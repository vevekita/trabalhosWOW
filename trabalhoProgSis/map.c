#include <stdio.h>
#include <stdlib.h>
#include "map.h"


/*colocar como as funções do map.h funcionam*/
Map *map_create(int rows, int cols){
    Map *m = malloc(sizeof(Map));
    //if(m == NULL){
    //  return NULL;
    //}
    m->rows = rows;
    m->cols = cols;
    m->mat = malloc(rows * cols * sizeof(char));
    // if(m->mat == NULL){
    //     free(m);
    //     return NULL;
    // }
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
    int i = r * m->cols + c;
    return m->mat[i];
}

void map_set(Map *m, int r, int c, char ch){
    
}

void map_print(const Map *m, FILE *out){
}

void map_free(Map *m){
}

Map *map_load(const char *filename){
}
