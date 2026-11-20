/*
 * Filtro de media 3x3 em imagem em escala de cinza: sequencial e paralelo (pthreads)
 *
 * Depende de duas bibliotecas de um unico arquivo .h (colocar na mesma pasta):
 *   stb_image.h        (leitura de PNG)
 *   stb_image_write.h  (gravacao do PNG de saida, opcional)
 *   Download: https://github.com/nothings/stb
 *
 * Compilar: gcc -O2 -pthread imagem.c -o imagem -lm
 * Usar:     ./imagem <seq|par> <arquivo.png> <num_threads> [repeticoes] [saida.png]
 * Exemplo:  ./imagem seq imagens/foto.png 1 5
 *           ./imagem par imagens/foto.png 4 5 resultado.png
 *
 * Saida (CSV): versao,arquivo,largura,altura,threads,execucao,tempo_s,resultado
 *   resultado = "ref" (sequencial) ou "IGUAL"/"DIFERENTE" (paralelo vs sequencial)
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

#define STBI_NO_HDR
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

static int W, H;                 /* largura e altura */
static unsigned char *in, *out;  /* imagem de entrada e de saida (W*H bytes) */

typedef struct {
    int linha_ini;   /* primeira linha da imagem desta thread */
    int linha_fim;   /* ultima linha + 1 */
} Arg;

/* Tempo em segundos, com relogio monotonico */
static double agora(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec + ts.tv_nsec / 1e9;
}

/* Aplica o filtro de media 3x3 nas linhas [ini, fim) da imagem.
 * Le de 'in' (incluindo linhas vizinhas) e escreve so em 'out' nas proprias linhas.
 * Linhas/colunas de borda nao sao calculadas (permanecem iguais a entrada). */
static void filtrar_linhas(int ini, int fim) {
    for (int y = ini; y < fim; y++) {
        for (int x = 1; x < W - 1; x++) {
            int soma = 0;
            for (int dy = -1; dy <= 1; dy++)
                for (int dx = -1; dx <= 1; dx++)
                    soma += in[(y + dy) * W + (x + dx)];
            out[y * W + x] = (unsigned char)(soma / 9);   /* divisao inteira */
        }
    }
}

static void *thread_filtro(void *p) {
    Arg *a = (Arg *)p;
    filtrar_linhas(a->linha_ini, a->linha_fim);
    return NULL;
}

static double rodar_seq(void) {
    double t0 = agora();
    filtrar_linhas(1, H - 1);
    double t1 = agora();
    return t1 - t0;
}

static double rodar_par(int nt) {
    pthread_t th[nt];
    Arg args[nt];
    int interiores = H - 2;            /* linhas que realmente sao filtradas: 1 .. H-2 */
    int base = interiores / nt, resto = interiores % nt;
    int linha = 1;

    double t0 = agora();   /* inclui criacao, distribuicao e join */

    for (int t = 0; t < nt; t++) {
        int qtd = base + (t < resto ? 1 : 0);
        args[t].linha_ini = linha;
        args[t].linha_fim = linha + qtd;
        linha += qtd;
        pthread_create(&th[t], NULL, thread_filtro, &args[t]);
    }
    for (int t = 0; t < nt; t++)
        pthread_join(th[t], NULL);

    double t1 = agora();
    return t1 - t0;
}

int main(int argc, char **argv) {
    if (argc < 4) {
        fprintf(stderr, "Uso: %s <seq|par> <arquivo.png> <num_threads> [repeticoes] [saida.png]\n", argv[0]);
        return 1;
    }

    const char *versao = argv[1];
    const char *arquivo = argv[2];
    int nt = atoi(argv[3]);
    int reps = (argc >= 5) ? atoi(argv[4]) : 5;
    const char *saida = (argc >= 6) ? argv[5] : NULL;
    int eh_seq = (strcmp(versao, "seq") == 0);

    if (nt < 1 || reps < 1 || (!eh_seq && strcmp(versao, "par") != 0)) {
        fprintf(stderr, "Parametros invalidos.\n");
        return 1;
    }

    /* Leitura (fora da medicao). O ultimo argumento (1) forca 1 canal = escala de cinza. */
    int canais;
    in = stbi_load(arquivo, &W, &H, &canais, 1);
    if (!in) {
        fprintf(stderr, "Erro ao ler %s: %s\n", arquivo, stbi_failure_reason());
        return 1;
    }
    if (W < 3 || H < 3) {
        fprintf(stderr, "Imagem pequena demais para o filtro 3x3.\n");
        return 1;
    }
    if (nt > H - 2) nt = H - 2;   /* nao faz sentido ter mais threads que linhas */

    size_t bytes = (size_t)W * H;
    out = malloc(bytes);
    unsigned char *ref = eh_seq ? NULL : malloc(bytes);
    if (!out || (!eh_seq && !ref)) {
        fprintf(stderr, "Sem memoria.\n");
        return 1;
    }

    /* nome do arquivo sem o caminho, para a coluna do CSV */
    const char *base = strrchr(arquivo, '/');
    base = base ? base + 1 : arquivo;

    if (eh_seq) {
        for (int r = 1; r <= reps; r++) {
            memcpy(out, in, bytes);   /* bordas = entrada; fora da medicao */
            double tempo = rodar_seq();
            printf("seq,%s,%d,%d,1,%d,%.6f,ref\n", base, W, H, r, tempo);
        }
    } else {
        /* Referencia sequencial calculada uma vez, fora da medicao */
        memcpy(out, in, bytes);
        filtrar_linhas(1, H - 1);
        memcpy(ref, out, bytes);

        for (int r = 1; r <= reps; r++) {
            memcpy(out, in, bytes);
            double tempo = rodar_par(nt);
            int igual = (memcmp(out, ref, bytes) == 0);
            printf("par,%s,%d,%d,%d,%d,%.6f,%s\n", base, W, H, nt, r, tempo, igual ? "IGUAL" : "DIFERENTE");
        }
    }

    if (saida) {   /* grava a ultima imagem filtrada (util para "prints" no relatorio) */
        if (!stbi_write_png(saida, W, H, 1, out, W))
            fprintf(stderr, "Erro ao gravar %s\n", saida);
    }

    stbi_image_free(in);
    free(out);
    free(ref);
    return 0;
}
