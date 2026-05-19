#ifndef MAP_H
#define MAP_H

#include <stdio.h>

/* Estrutura do mapa. */
typedef struct {
        int rows;
        int cols;
        char *mat; /*ponteiro para alocação (provável que é porque vai utilizar malloc*/
 } Map;

#define OPEN '.'
#define WALL '#'

/* Cria um mapa vazio (todas células com OPEN) com o tamanho fornecido. */
Map *map_create(int rows, int cols);

/* Obtém dimensões. */
int map_rows(const Map *m);
int map_cols(const Map *m);

/* Consulta/modifica uma célula (qualquer caractere é válido). */
char map_get(const Map *m, int r, int c);
void map_set(Map *m, int r, int c, char ch);

/* Imprime o mapa em `out` (por exemplo, `stdout`). */
void map_print(const Map *m, FILE *out);

/* Libera todos os recursos. */
void map_free(Map *m);

/* Carrega um mapa de um arquivo de texto. Formato:
* Linha 1: <l> <c>
* Para as próximas `l` linhas: `c` caracteres copiados para a grade.
* Retorna NULL em caso de falha. */
Map *map_load(const char *filename);

#endif /* LIBMAP_H */
