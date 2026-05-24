#include <stdio.h>
#include <stdlib.h>
#include "map.h"


/*colocar como as funções do map.h funcionam*/
Map *map_create(int rows, int cols){
    Map *m = malloc(sizeof(Map));
    m->rows = rows;
    m->cols = cols;
    m->mat = malloc(rows * cols * sizeof(char));
    for(int i = 0; i < rows*cols; i++){
        m->mat[i] = OPEN;
    }
    return m;
}

int map_rows(const Map *m){
    return m->rows;
}

int map_cols(const Map *m){
    return m->cols;
}

char map_get(const Map *m, int r, int c){
    int i = (r * m->cols) + c;
    return m->mat[i];
}

void map_set(Map *m, int r, int c, char ch){
    int i = (r * m->cols) + c;
    m->mat[i] = ch;
}

void map_print(const Map *m, FILE *out){
    // FILE *out = open(out, 'r')
    for (int r = 0; r < m->rows; r++) {
        for (int c = 0; c < m->cols; c++) {
            int i = (r * m->cols) + c;
        fprintf( out, "%c", m->mat[i]);
        }
    fprintf(out, "\n"); 
    }
} //usar o stdout na hora de testar para não precisar ficar lidando com arquivos de primeira


void map_free(Map *m){
    if (m == NULL)
        return;
    free(m->mat);
    free(m);
}

Map *map_load(const char *filename){
    FILE *arquivo = fopen(filename, "r");
    if (arquivo == NULL)
        return NULL;
 
    int rows, cols;
    if (fscanf(arquivo, "%d %d\n", &rows, &cols) != 2) {
        fclose(arquivo);
        return NULL;
    }
 
    Map *m = map_create(rows, cols);
    if (m == NULL) {
        fclose(arquivo);
        return NULL;
    }
 
    char buf[4096];
    for (int r = 0; r < rows; r++) {
        if (fgets(buf, sizeof(buf), arquivo) == NULL) {
            map_free(m);
            fclose(arquivo);
            return NULL;
        }
        /* copia apenas os `cols` primeiros caracteres da linha */
        for (int c = 0; c < cols; c++)
            map_set(m, r, c, buf[c]);
    }
 
    fclose(arquivo);
    return m;
}
