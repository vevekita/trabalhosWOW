/*
 * Multiplicacao de matrizes C = A x B: sequencial e paralela (pthreads)
 *
 * Compilar: gcc -O2 -pthread matriz.c -o matriz
 * Usar:     ./matriz <seq|par> <N> <num_threads> [repeticoes]
 * Exemplo:  ./matriz seq 512 1 5
 *           ./matriz par 512 4 5
 *
 * Saida (CSV): versao,N,threads,execucao,tempo_s
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

static int ordem_matriz;
static int *matriz_a, *matriz_b, *matriz_c;   /* matrizes N x N armazenadas em vetor linear */

typedef struct {
    int linha_inicio;   /* primeira linha de C desta thread */
    int linha_fim;      /* ultima linha + 1 */
} DadosThread;

/* Retorna o tempo atual em segundos */
static double obter_tempo_atual(void) {
    struct timespec relogio;
    clock_gettime(CLOCK_MONOTONIC, &relogio);
    return relogio.tv_sec + relogio.tv_nsec / 1e9;
}

/* Leitura de uma matriz de arquivo texto (fora da medicao) */
static int carregar_matriz(const char *nome_arquivo, int *matriz_destino) {
    FILE *arquivo = fopen(nome_arquivo, "r");
    if (!arquivo) {
        fprintf(stderr, "Erro ao abrir %s\n", nome_arquivo);
        return 0;
    }
    long total_elementos = (long)ordem_matriz * ordem_matriz;
    for (long idx = 0; idx < total_elementos; idx++) {
        if (fscanf(arquivo, "%d", &matriz_destino[idx]) != 1) {
            fprintf(stderr, "Erro de leitura em %s (elemento %ld)\n", nome_arquivo, idx);
            fclose(arquivo);
            return 0;
        }
    }
    fclose(arquivo);
    return 1;
}

/* Calcula as linhas [linha_inicio, linha_fim) da matriz C */
static void multiplicar_intervalo_linhas(int linha_inicio, int linha_fim) {
    for (int linha = linha_inicio; linha < linha_fim; linha++) {
        for (int coluna = 0; coluna < ordem_matriz; coluna++) {
            int soma_produto = 0;
            for (int k = 0; k < ordem_matriz; k++) {
                soma_produto += matriz_a[linha * ordem_matriz + k] * matriz_b[k * ordem_matriz + coluna];
            }
            matriz_c[linha * ordem_matriz + coluna] = soma_produto;
        }
    }
}

static void *tarefa_multiplicacao(void *parametro) {
    DadosThread *dados = (DadosThread *)parametro;
    multiplicar_intervalo_linhas(dados->linha_inicio, dados->linha_fim);
    return NULL;
}

static double executar_sequencial(void) {
    double tempo_inicio = obter_tempo_atual();
    multiplicar_intervalo_linhas(0, ordem_matriz);
    double tempo_fim = obter_tempo_atual();
    return tempo_fim - tempo_inicio;
}

static double executar_paralelo(int num_threads) {
    pthread_t threads[num_threads];
    DadosThread dados_threads[num_threads];
    int linhas_base = ordem_matriz / num_threads;
    int linhas_extras = ordem_matriz % num_threads;
    int linha_atual = 0;

    double tempo_inicio = obter_tempo_atual();

    for (int id = 0; id < num_threads; id++) {
        int qtd_linhas = linhas_base + (id < linhas_extras ? 1 : 0);
        dados_threads[id].linha_inicio = linha_atual;
        dados_threads[id].linha_fim    = linha_atual + qtd_linhas;
        linha_atual += qtd_linhas;
        pthread_create(&threads[id], NULL, tarefa_multiplicacao, &dados_threads[id]);
    }

    for (int id = 0; id < num_threads; id++) {
        pthread_join(threads[id], NULL);
    }

    double tempo_fim = obter_tempo_atual();
    return tempo_fim - tempo_inicio;
}

int main(int argc, char **argv) {
    if (argc < 4) {
        fprintf(stderr, "Uso: %s <seq|par> <N> <num_threads> [repeticoes]\n", argv[0]);
        return 1;
    }

    const char *versao = argv[1];
    ordem_matriz = atoi(argv[2]);
    int num_threads = atoi(argv[3]);
    int repeticoes = (argc >= 5) ? atoi(argv[4]) : 5;
    int modo_sequencial = (strcmp(versao, "seq") == 0);

    if (ordem_matriz < 1 || num_threads < 1 || repeticoes < 1 || (!modo_sequencial && strcmp(versao, "par") != 0)) {
        fprintf(stderr, "Parametros invalidos.\n");
        return 1;
    }
    if (num_threads > ordem_matriz) {
        num_threads = ordem_matriz;
    }

    size_t tamanho_bytes = (size_t)ordem_matriz * ordem_matriz * sizeof(int);
    matriz_a = malloc(tamanho_bytes);
    matriz_b = malloc(tamanho_bytes);
    matriz_c = malloc(tamanho_bytes);

    if (!matriz_a || !matriz_b || !matriz_c) {
        fprintf(stderr, "Sem memoria.\n");
        return 1;
    }

    char arquivo_a[64], arquivo_b[64];
    snprintf(arquivo_a, sizeof arquivo_a, "A_%d.txt", ordem_matriz);
    snprintf(arquivo_b, sizeof arquivo_b, "B_%d.txt", ordem_matriz);
    if (!carregar_matriz(arquivo_a, matriz_a) || !carregar_matriz(arquivo_b, matriz_b)) {
        return 1;
    }

    for (int exec = 1; exec <= repeticoes; exec++) {
        memset(matriz_c, 0, tamanho_bytes);
        double tempo_execucao = modo_sequencial ? executar_sequencial() : executar_paralelo(num_threads);
        int threads_usadas = modo_sequencial ? 1 : num_threads;

        printf("%s,%d,%d,%d,%.6f\n",
               versao, ordem_matriz, threads_usadas, exec, tempo_execucao);
    }

    free(matriz_a);
    free(matriz_b);
    free(matriz_c);
    return 0;
}
