/*
 * Contadores paralelos: sequencial, A (mutex) e B (contadores locais)
 *
 * Compilar: gcc -O2 -pthread contadores.c -o contadores
 * Usar:     ./contadores <seq|A|B> <num_threads> [repeticoes]
 * Exemplo:  ./contadores A 4 5
 *
 * Saida (CSV): versao,threads,execucao,tempo_s,total,correto
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

#define TOTAL 100000000LL   /* 100 milhoes de incrementos */

static long long contador_compartilhado = 0;
static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

typedef struct {
    long long inicio;   /* primeiro incremento desta thread */
    long long fim;      /* ultimo + 1 */
    long long local;    /* resultado parcial (versao B) */
} Arg;

/* Tempo em segundos, com relogio monotonico */
static double agora(void) {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec + ts.tv_nsec / 1e9;
}

/* ---------- Sequencial ---------- */
static double rodar_seq(long long *resultado) {
    double t0 = agora();
    volatile long long c = 0;   /* volatile: impede o compilador de eliminar o laco */
    for (long long i = 0; i < TOTAL; i++)
        c++;
    double t1 = agora();
    *resultado = c;
    return t1 - t0;
}

/* ---------- Versao A: contador compartilhado + mutex ---------- */
static void *thread_a(void *p) {
    Arg *a = (Arg *)p;
    long long inicio = a->inicio, fim = a->fim;   /* copia para variaveis locais */
    for (long long i = inicio; i < fim; i++) {
        pthread_mutex_lock(&mutex);
        contador_compartilhado++;
        pthread_mutex_unlock(&mutex);
    }
    return NULL;
}

/* ---------- Versao B: contador local por thread ---------- */
static void *thread_b(void *p) {
    Arg *a = (Arg *)p;
    long long inicio = a->inicio, fim = a->fim;
    volatile long long c = 0;     /* contador local (pilha da thread) */
    for (long long i = inicio; i < fim; i++)
        c++;
    a->local = c;                 /* escreve o resultado uma unica vez */
    return NULL;
}

/* Executa a versao paralela. modo: 'A' ou 'B'. Retorna o tempo em segundos. */
static double rodar_par(char modo, int nt, long long *resultado) {
    pthread_t th[nt];
    Arg args[nt];
    long long bloco = TOTAL / nt;

    contador_compartilhado = 0;

    double t0 = agora();   /* inclui criacao, distribuicao, join e soma final */

    for (int t = 0; t < nt; t++) {
        args[t].inicio = (long long)t * bloco;
        args[t].fim    = (t == nt - 1) ? TOTAL : (long long)(t + 1) * bloco;
        args[t].local  = 0;
        pthread_create(&th[t], NULL, (modo == 'A') ? thread_a : thread_b, &args[t]);
    }
    for (int t = 0; t < nt; t++)
        pthread_join(th[t], NULL);

    long long total = 0;
    if (modo == 'A') {
        total = contador_compartilhado;
    } else {
        for (int t = 0; t < nt; t++)
            total += args[t].local;
    }

    double t1 = agora();
    *resultado = total;
    return t1 - t0;
}

int main(int argc, char **argv) {
    if (argc < 3) {
        fprintf(stderr, "Uso: %s <seq|A|B> <num_threads> [repeticoes]\n", argv[0]);
        return 1;
    }

    const char *versao = argv[1];
    int nt = atoi(argv[2]);
    int reps = (argc >= 4) ? atoi(argv[3]) : 5;

    if (nt < 1 || reps < 1 || (strcmp(versao, "seq") != 0 && strcmp(versao, "A") != 0 && strcmp(versao, "B") != 0)) {
        fprintf(stderr, "Parametros invalidos.\n");
        return 1;
    }

    for (int r = 1; r <= reps; r++) {
        long long resultado = 0;
        double tempo;

        if (strcmp(versao, "seq") == 0)
            tempo = rodar_seq(&resultado);
        else
            tempo = rodar_par(versao[0], nt, &resultado);

        printf("%s,%d,%d,%.6f,%lld,%s\n",
               versao, strcmp(versao, "seq") == 0 ? 1 : nt, r, tempo, resultado,
               resultado == TOTAL ? "OK" : "ERRO");
    }
    return 0;
}
