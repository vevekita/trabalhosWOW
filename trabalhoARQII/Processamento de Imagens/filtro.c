/*
 * Filtro de média 3x3 em imagem em escala de cinza: sequencial e paralelo (pthreads)
 * Compilar: gcc -pthread filtro.c -o filtro -lm
 * Usar: ./filtro <seq|par> <arquivo.png> <num_threads> [repetições] [saída.png]
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

static int largura, altura;
static unsigned char *imagem_entrada, *imagem_saida;

typedef struct {
    int linha_inicio;
    int linha_fim;
} DadosThread;

/* Retorna o tempo atual em segundos */
static double obter_tempo_atual(void) {
    struct timespec relogio;
    clock_gettime(CLOCK_MONOTONIC, &relogio);
    return relogio.tv_sec + relogio.tv_nsec / 1e9;
}

static void aplicar_filtro_linhas(int linha_inicio, int linha_fim) {
    for (int linha = linha_inicio; linha < linha_fim; linha++) {
        for (int coluna = 1; coluna < largura - 1; coluna++) {
            int soma_vizinhos = 0;
            for (int desloc_y = -1; desloc_y <= 1; desloc_y++) {
                for (int desloc_x = -1; desloc_x <= 1; desloc_x++) {
                    soma_vizinhos += imagem_entrada[(linha + desloc_y) * largura + (coluna + desloc_x)];
                }
            }
            imagem_saida[linha * largura + coluna] = (unsigned char)(soma_vizinhos / 9);
        }
    }
}

static void *tarefa_filtro(void *parametro) {
    DadosThread *dados = (DadosThread *)parametro;
    aplicar_filtro_linhas(dados->linha_inicio, dados->linha_fim);
    return NULL;
}

static double executar_sequencial(void) {
    double tempo_inicio = obter_tempo_atual();
    aplicar_filtro_linhas(1, altura - 1);
    double tempo_fim = obter_tempo_atual();
    return tempo_fim - tempo_inicio;
}

static double executar_paralelo(int num_threads) {
    pthread_t threads[num_threads];
    DadosThread dados_threads[num_threads];
    int linhas_internas = altura - 2; /* exclui primeira e última linha (bordas) */
    int linhas_base = linhas_internas / num_threads;
    int linhas_extras = linhas_internas % num_threads;
    int linha_atual = 1;

    double tempo_inicio = obter_tempo_atual();

    for (int id = 0; id < num_threads; id++) {
        int qtd_linhas = linhas_base + (id < linhas_extras ? 1 : 0);
        dados_threads[id].linha_inicio = linha_atual;
        dados_threads[id].linha_fim = linha_atual + qtd_linhas;
        linha_atual += qtd_linhas;
        pthread_create(&threads[id], NULL, tarefa_filtro, &dados_threads[id]);
    }

    for (int id = 0; id < num_threads; id++) {
        pthread_join(threads[id], NULL);
    }

    double tempo_fim = obter_tempo_atual();
    return tempo_fim - tempo_inicio;
}

int main(int argc, char **argv) {
    if (argc < 4) {
        fprintf(stderr, "Uso: %s <seq|par> <arquivo.png> <num_threads> [repeticoes] [saida.png]\n", argv[0]);
        return 1;
    }

    const char *versao = argv[1];
    const char *caminho_arquivo = argv[2];
    int num_threads = atoi(argv[3]);
    int repeticoes = (argc >= 5) ? atoi(argv[4]) : 5;
    const char *arquivo_saida = (argc >= 6) ? argv[5] : NULL;
    int modo_sequencial = (strcmp(versao, "seq") == 0);

    if (num_threads < 1 || repeticoes < 1 || (!modo_sequencial && strcmp(versao, "par") != 0)) {
        fprintf(stderr, "Parâmetros inválidos.\n");
        return 1;
    }

    /* Leitura da imagem */
    int canais_originais;
    imagem_entrada = stbi_load(caminho_arquivo, &largura, &altura, &canais_originais, 1);
    if (!imagem_entrada) {
        fprintf(stderr, "Erro ao ler %s: %s\n", caminho_arquivo, stbi_failure_reason());
        return 1;
    }
    if (largura < 3 || altura < 3) {
        fprintf(stderr, "Imagem pequena demais para o filtro 3x3.\n");
        return 1;
    }
    if (num_threads > altura - 2) {
        num_threads = altura - 2;
    }

    size_t total_pixels = (size_t)largura * altura;
    imagem_saida = malloc(total_pixels);
    if (!imagem_saida) {
        fprintf(stderr, "Sem memoria.\n");
        return 1;
    }

    /* Extrai apenas o nome do arquivo para exibir na tabela */
    const char *nome_arquivo = strrchr(caminho_arquivo, '/');
    nome_arquivo = nome_arquivo ? nome_arquivo + 1 : caminho_arquivo;

    for (int exec = 1; exec <= repeticoes; exec++) {
        memcpy(imagem_saida, imagem_entrada, total_pixels); /* copia as bordas da imagem original */
        double tempo_execucao = modo_sequencial ? executar_sequencial() : executar_paralelo(num_threads);
        int threads_usadas = modo_sequencial ? 1 : num_threads;

        printf("%s, %s, %d, %d, %d, %d, %.6f\n", versao, nome_arquivo, largura, altura, threads_usadas, exec, tempo_execucao);
    }

    if (arquivo_saida) {
        if (!stbi_write_png(arquivo_saida, largura, altura, 1, imagem_saida, largura)) {
            fprintf(stderr, "Erro ao gravar %s\n", arquivo_saida);
        }
    }

    stbi_image_free(imagem_entrada);
    free(imagem_saida);
    return 0;
}
