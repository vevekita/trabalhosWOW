/*
 * Multiplicacao de matrizes C = A x B: sequencial e paralela (pthreads)
 *
 * Compilar: gcc -pthread matriz.c -o matriz
 * Usar:     ./matriz <seq|par> <N> <num_threads> [repeticoes]
 * Exemplo:  ./matriz seq 512 1 5
 *           ./matriz par 512 4 5
 *
 * Le os arquivos A_<N>.txt e B_<N>.txt gerados pelo codigo da professora
 * (precisam estar na pasta onde o programa e executado).
 *
 * Saida (CSV): versao,N,threads,execucao,tempo_s
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

static int N;
static int *A, *B, *C;   /* matrizes N x N armazenadas em vetor (linha a linha) */

typedef struct {
    int linha_ini;   /* primeira linha de C desta thread */
    int linha_fim;   /* ultima linha + 1 */
} Arg;

/* Tempo em segundos, com relogio monotonico */
static double agora(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec + ts.tv_nsec / 1e9;
}

/* Leitura de uma matriz de arquivo texto (nao entra na medicao) */
static int ler_matriz(const char *nome, int *M) {
    FILE *f = fopen(nome, "r");
    if (!f) {
        fprintf(stderr, "Erro ao abrir %s\n", nome);
        return 0;
    }
    for (long i = 0; i < (long)N * N; i++) {
        if (fscanf(f, "%d", &M[i]) != 1) {
            fprintf(stderr, "Erro de leitura em %s (elemento %ld)\n", nome, i);
            fclose(f);
            return 0;
        }
    }
    fclose(f);
    return 1;
}

/* Calcula as linhas [ini, fim) de C. Serve para a versao sequencial (todas as
 * linhas) e para cada thread da versao paralela (so as suas linhas). */
static void multiplicar_linhas(int ini, int fim) {
    for (int i = ini; i < fim; i++) {
        for (int j = 0; j < N; j++) {
            int soma = 0;
            for (int k = 0; k < N; k++)
                soma += A[i * N + k] * B[k * N + j];
            C[i * N + j] = soma;
        }
    }
}

static void *thread_mult(void *p) {
    Arg *a = (Arg *)p;
    multiplicar_linhas(a->linha_ini, a->linha_fim);
    return NULL;
}

static double rodar_seq(void) {
    double t0 = agora();
    multiplicar_linhas(0, N);
    double t1 = agora();
    return t1 - t0;
}

static double rodar_par(int nt) {
    pthread_t th[nt];
    Arg args[nt];
    int base = N / nt, resto = N % nt;
    int linha = 0;

    double t0 = agora();   /* inclui criacao, distribuicao e join */

    for (int t = 0; t < nt; t++) {
        int qtd = base + (t < resto ? 1 : 0);   /* reparte tambem o resto */
        args[t].linha_ini = linha;
        args[t].linha_fim = linha + qtd;
        linha += qtd;
        pthread_create(&th[t], NULL, thread_mult, &args[t]);
    }
    for (int t = 0; t < nt; t++)
        pthread_join(th[t], NULL);

    double t1 = agora();
    return t1 - t0;
}

int main(int argc, char **argv) {
    if (argc < 4) {
        fprintf(stderr, "Uso: %s <seq|par> <N> <num_threads> [repeticoes]\n", argv[0]);
        return 1;
    }

    const char *versao = argv[1];
    N = atoi(argv[2]);
    int nt = atoi(argv[3]);
    int reps = (argc >= 5) ? atoi(argv[4]) : 5;
    int eh_seq = (strcmp(versao, "seq") == 0);

    if (N < 1 || nt < 1 || reps < 1 || (!eh_seq && strcmp(versao, "par") != 0)) {
        fprintf(stderr, "Parametros invalidos.\n");
        return 1;
    }
    if (nt > N) nt = N;   /* nao faz sentido ter mais threads que linhas */

    size_t bytes = (size_t)N * N * sizeof(int);
    A = malloc(bytes);
    B = malloc(bytes);
    C = malloc(bytes);
    if (!A || !B || !C) {
        fprintf(stderr, "Sem memoria.\n");
        return 1;
    }
    memset(C, 0, bytes);   /* ja "toca" as paginas de C fora da medicao */

    char nomeA[64], nomeB[64];
    snprintf(nomeA, sizeof nomeA, "A_%d.txt", N);
    snprintf(nomeB, sizeof nomeB, "B_%d.txt", N);
    if (!ler_matriz(nomeA, A) || !ler_matriz(nomeB, B))
        return 1;

    for (int r = 1; r <= reps; r++) {
        if (eh_seq) {
            double tempo = rodar_seq();
            printf("seq,%d,1,%d,%.6f\n", N, r, tempo);
        } else {
            double tempo = rodar_par(nt);
            printf("par,%d,%d,%d,%.6f\n", N, nt, r, tempo);
        }
    }

    free(A); free(B); free(C);
    return 0;
}
