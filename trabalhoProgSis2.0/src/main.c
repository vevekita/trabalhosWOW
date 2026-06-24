#include "wasm4.h"
#include "background_jogo.h"
#include <stdint.h>
#include "pony.h"
#include "ponyBehind.h"


int frame = 0;
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
    PALETTE[1] = 0x5b768d; //laranjinha
    PALETTE[2] = 0xe48959; //azul
    PALETTE[3] = 0xf6d7a8; //nude
   x = 80;
   y = 105; // posição inicial
}

void update () {
    frame += 1;
    //ordem de cores do fundo
    *DRAW_COLORS = 0x1234;
    blit(background_jogo, 0, 0, background_jogoWidth, background_jogoHeight, background_jogoFlags);
    
    int dx = 0; // deslocamento, permite mais de uma tecla pressionada
    uint8_t gamepad = *GAMEPAD1;
    if (gamepad & BUTTON_LEFT)  { 
        dx -= 1;
    }
    if (gamepad & BUTTON_RIGHT) { 
        dx += 1; 
    }
    x += dx;
    
    int frameAtual = 0; //vai representar a pose estática da personagem
    //bara a applejack para o limite da esquerda
    if (x < 0) {
        x = 0;
    }
    //barra a applejack para o limite da direita
    if (x > 144) {
        x = 144;
    }
    if (dx != 0) {
        frameAtual = ((frame / 12) % 2) + 1; //oscila entre sprite 1 e spite 2 (sprites da applejack correndo)
    }
    
    int spriteX = frameAtual * 16; //operação para pegar cada sprite (se frameAtual = 0, pega o primeiro srite. Se for 1, pega o segundo sprite (que começa no pixel 16))
    int spriteY = 0; //mesma coisa que o frameAtual, mas não é relevante porque o arquivo de sprites só tem uma linha

    *DRAW_COLORS = 0x0024;
    blitSub(ponyBehind, x, y, 16, 16, (uint32_t)spriteX, (uint32_t)spriteY, ponyBehindWidth, BLIT_2BPP);
    *DRAW_COLORS = 0x0123;
    blitSub(pony, x, y, 16, 16, (uint32_t)spriteX, (uint32_t)spriteY, ponyWidth, BLIT_2BPP);
    
}

