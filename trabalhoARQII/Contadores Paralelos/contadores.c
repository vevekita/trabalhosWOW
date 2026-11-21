/*
 * Contadores paralelos: sequencial, A (mutex) e B (contador local)
 *
 * Compilar: gcc -pthread contadores.c -o contadores
 * Usar: ./contadores <seq|A|B> <num_threads> [repetições]
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <time.h>

#define TOTAL_INCREMENTOS 100000000LL   /* 100 milhões de incrementos */

static long long contador_compartilhado = 0;
static pthread_mutex_t trava_contador = PTHREAD_MUTEX_INITIALIZER;

typedef struct {
    long long indice_inicio;
    long long indice_fim;
    long long resultado_local;
} DadosThread;

/* Retorna o tempo atual em segundos */
static double obter_tempo_atual(void) {
    struct timespec relogio;
    clock_gettime(CLOCK_MONOTONIC, &relogio);
    return relogio.tv_sec + relogio.tv_nsec / 1e9;
}

/* Contador para execução sequencial */
static double executar_sequencial(long long *total_final) {
    double tempo_inicio = obter_tempo_atual();
    long long contador_local = 0;
    for (long long i = 0; i < TOTAL_INCREMENTOS; i++) {
        contador_local++;
    }
    double tempo_fim = obter_tempo_atual();
    *total_final = contador_local;
    return tempo_fim - tempo_inicio;
}

/* Contador com mutex */
static void *tarefa_contador_mutex(void *parametro) {
    DadosThread *dados = (DadosThread *)parametro;
    long long inicio = dados->indice_inicio;
    long long fim = dados->indice_fim;

    for (long long i = inicio; i < fim; i++) {
        pthread_mutex_lock(&trava_contador);
        contador_compartilhado++;
        pthread_mutex_unlock(&trava_contador);
    }
    return NULL;
}

/* Contador local */
static void *tarefa_contador_local(void *parametro) {
    DadosThread *dados = (DadosThread *)parametro;
    long long inicio = dados->indice_inicio;
    long long fim = dados->indice_fim;
    long long contador_local = 0;

    for (long long i = inicio; i < fim; i++) {
        contador_local++;
    }
    dados->resultado_local = contador_local;
    return NULL;
}

/* Executa a versão paralela ('A' ou 'B') e retorna o tempo total em segundos */
static double executar_paralelo(char modo, int num_threads, long long *total_final) {
    pthread_t threads[num_threads];
    DadosThread dados_threads[num_threads];
    long long tamanho_bloco = TOTAL_INCREMENTOS / num_threads;

    contador_compartilhado = 0;

    double tempo_inicio = obter_tempo_atual();

    for (int id = 0; id < num_threads; id++) {
        dados_threads[id].indice_inicio = (long long)id * tamanho_bloco;
        dados_threads[id].indice_fim = (id == num_threads - 1) ? TOTAL_INCREMENTOS : (long long)(id + 1) * tamanho_bloco;
        dados_threads[id].resultado_local = 0;

        void *(*funcao_thread)(void *) = (modo == 'A') ? tarefa_contador_mutex : tarefa_contador_local;
        pthread_create(&threads[id], NULL, funcao_thread, &dados_threads[id]);
    }

    for (int id = 0; id < num_threads; id++) {
        pthread_join(threads[id], NULL);
    }

    long long soma_total = 0;
    if (modo == 'A') {
        soma_total = contador_compartilhado;
    } else {
        for (int id = 0; id < num_threads; id++) {
            soma_total += dados_threads[id].resultado_local;
        }
    }

    double tempo_fim = obter_tempo_atual();
    *total_final = soma_total;
    return tempo_fim - tempo_inicio;
}

int main(int argc, char **argv) {
    if (argc < 3) {
        fprintf(stderr, "Uso: %s <seq|A|B> <num_threads> [repeticoes]\n", argv[0]);
        return 1;
    }

    const char *versao = argv[1];
    int num_threads = atoi(argv[2]);
    int repeticoes = (argc >= 4) ? atoi(argv[3]) : 5;

    int versao_valida = (strcmp(versao, "seq") == 0 || strcmp(versao, "A") == 0 || strcmp(versao, "B") == 0);
    if (num_threads < 1 || repeticoes < 1 || !versao_valida) {
        fprintf(stderr, "Parâmetros inválidos.\n");
        return 1;
    }

    for (int exec = 1; exec <= repeticoes; exec++) {
        long long total_obtido = 0;
        double tempo_execucao;

        if (strcmp(versao, "seq") == 0) {
            tempo_execucao = executar_sequencial(&total_obtido);
        } else {
            tempo_execucao = executar_paralelo(versao[0], num_threads, &total_obtido);
        }

        int threads_usadas = (strcmp(versao, "seq") == 0) ? 1 : num_threads;
        printf("%s, %d, %d, %.6f, %lld\n", versao, threads_usadas, exec, tempo_execucao, total_obtido);
    }
    return 0;
}
