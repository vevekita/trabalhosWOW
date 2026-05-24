#include <stdio.h>
#include <stdlib.h>

#include "map.h"
#include "rpng.h"

#define TILE_SIZE 8
#define TILES_COLS 16  /* blocos por linha em tiles.png */
#define CHAVE 34
#define BAU 36
#define YELLOW_DIAM 45
#define HERO_BACK 99
#define MONSTER 113
#define DOOR 40
#define SPIKES 39

int main() {
    /* 1. cria um mapa 6x6 com um retângulo vazado representando as paredes */
    Map *m = map_create(20, 12);
    if (!m) {
        fprintf(stderr, "map_create falhou\n");
        return 1;
    }
    for (int c = 0; c < 12; c++) {
        if (c < 5) {
            map_set(m, 0, c, WALL);
            map_set(m, 19, c, WALL);
        }
        else if (c > 6) {
            map_set(m, 0, c, WALL);
            map_set(m, 19, c, WALL);
        }
    }
    for (int r = 1; r < 19; r++) {
        map_set(m, r, 0, WALL);
        map_set(m, r, 11, WALL);
        if (r > 14 && r < 19) {
            map_set(m, r, 9, WALL);
        }
        if (r < 5) {
            map_set(m, r, 2, WALL);
            map_set(m, r, 4, WALL);
        }
        if (r % 2 == 0) {
            map_set(m, r, 5, YELLOW_DIAM);
        }
        else if (r % 2 == 1) {
            map_set(m, r, 6, YELLOW_DIAM);
        }
        
    }
    map_set(m, 1, 3, SPIKES);
    map_set(m, 18, 10, CHAVE);
    map_set(m, 1, 1, CHAVE);
    map_set(m, 15, 10, DOOR);
    map_set(m, 4, 1, DOOR);
    map_set(m, 4, 3, DOOR);
    map_set(m, 13, 10, MONSTER);
    map_set(m, 6, 2, MONSTER);
    map_set(m, 10, 6, BAU);
    map_set(m, 10, 5, BAU);
    map_set(m, 18, 6, HERO_BACK);

    map_print(m, stdout);

    /* 2. carrega os blocos */
    int tw, th, tc, tb;
    char *tiles = rpng_load_image("tiles.png", &tw, &th, &tc, &tb);
    if (!tiles) {
        fprintf(stderr, "falhou ao carregar tiles.png\n");
        map_free(m);
        return 1;
    }
    printf("carregado com w=%i h=%i c=%i b=%i \n", tw, th, tc, tb);

    /* 3. constroi a imagem RGB de saída, copiando um bloco por célula */
    int linhas = map_rows(m);
    int colunas = map_cols(m);
    int out_w = colunas * TILE_SIZE;
    int out_h = linhas * TILE_SIZE;
    char *out = calloc(out_w * out_h * 3 /* R G B */, sizeof(char)); // vetor zerado
    if (!out) {
        fprintf(stderr, "calloc falhou\n");
        free(tiles);
        map_free(m);
        return 1;
    }
    for (int r = 0; r < linhas; r++) {
        for (int c = 0; c < colunas; c++) {
            unsigned char ch = (unsigned char)map_get(m, r, c);
            int tile_col = ch % TILES_COLS;
            int tile_row = ch / TILES_COLS;
            for (int py = 0; py < TILE_SIZE; py++) {
                for (int px = 0; px < TILE_SIZE; px++) {
                    int sx = tile_col * TILE_SIZE + px;
                    int sy = tile_row * TILE_SIZE + py;
                    int dx = c * TILE_SIZE + px;
                    int dy = r * TILE_SIZE + py;
                    int si = (sy * tw + sx) * tc;
                    int di = (dy * out_w + dx) * 3;
                    out[di + 0] = tiles[si + 0];
                    out[di + 1] = tiles[si + 1];
                    out[di + 2] = tiles[si + 2];
                }
            }
        }
    }

    /* 4. Grava o resultado */
    if (rpng_save_image("drawing.png", out, out_w, out_h, 3, 8) != 0)
        fprintf(stderr, "falhou ao salvar imagem\n");
    else
        printf("imagem criada (%d x %d)\n", out_w, out_h);

    free(out);
    free(tiles);
    map_free(m);
    return 0;
}
