#include "wasm4.h"
#include "background_jogo.h"
#include <stdint.h>

// retorna cor do pixel (x,y)
int pget(int x, int y) {
    if (x < 0 || x > 159 || y < 0 || y > 159) { return 0; }
    int idx = (y*160 + x) >> 2;
    int shift = (x & 0b11) << 1;
    int mask = 0b11 << shift;
    return ((FRAMEBUFFER[idx] & mask) >> shift) + 1;
}

int x, y; // posição do jogador

void start () {
    PALETTE[0] = 0x483852; //roxo escuro
    PALETTE[1] = 0xe48959; //azul
    PALETTE[2] = 0x5b768d; //laranjinha
    PALETTE[3] = 0xf6d7a8; //nude
   x = y = 50; // posição inicial
}

void update () {
    *DRAW_COLORS = 0x1324;
    blit(background_jogo, 0, 0, background_jogoWidth, background_jogoHeight, background_jogoFlags);
    int dx = 0, dy = 0; // deslocamento, permite mais de uma tecla pressionada
    uint8_t gamepad = *GAMEPAD1;
    if (gamepad & BUTTON_LEFT)  { dx -= 2; }
    if (gamepad & BUTTON_RIGHT) { dx += 2; }
    if (gamepad & BUTTON_UP)    { dy -= 2; }
    if (gamepad & BUTTON_DOWN)  { dy += 2; }
    x += dx;
    y += dy;
    
    *DRAW_COLORS = 3; rect(x, y, 2, 2); // desenha jogador
}
