#include "wasm4.h"
#include "background_jogo.h"
#include <stdint.h>
#include <stdlib.h>
#include "pony.h"
#include "ponyBehind.h"

#define GRAVITY 0

#define GOOD_APPLE 0
#define BAD_APPLE 1

int timer = 3600;
int score = 0;
int frame = 0;
struct point {
    int32_t x;
    int32_t y;
    int type;
    int stop;
};
struct point fruit;
int velocity_apple = 0;

// retorna cor do pixel (x,y)
int pget(int x, int y) {
    if (x < 0 || x > 159 || y < 0 || y > 159) { return 0; }
    int idx = (y*160 + x) >> 2;
    int shift = (x & 0b11) << 1;
    int mask = 0b11 << shift;
    return ((FRAMEBUFFER[idx] & mask) >> shift) + 1;
}

int x, y; // posição do jogador
int esquerda = 0;

void spawn_fruit() {
    fruit.x = rand()%153;
    fruit.y = (rand() % 41) + 30;
    fruit.type = rand()%2;
    fruit.stop = 40;
}

void start () {
    PALETTE[0] = 0x483852; //roxo escuro
    PALETTE[1] = 0x5b768d; //laranjinha
    PALETTE[2] = 0xe48959; //azul
    PALETTE[3] = 0xf6d7a8; //nude
   x = 80;
   y = 105; // posição inicial
   spawn_fruit();
   
}

void update () {
    frame++;
    
    if (fruit.stop>0) { //faz contagem regressiva para a maçã não cair
        fruit.stop--;
    } else {
        if (frame % 2 == 0) {
            fruit.y++;  
        }
    }
    if (fruit.y > 110) { //se a maçã cair no chão
        spawn_fruit(); //spawna outra fruta
    }

    //background
    *DRAW_COLORS = 0x1234;
    blit(background_jogo, 0, 0, background_jogoWidth, background_jogoHeight, background_jogoFlags);
    
    if (timer <= 0) {
        *DRAW_COLORS = 0x12;
        rect(35, 50, 92, 30);
        *DRAW_COLORS = 0x0001;
        text("GAME OVER", 45, 62);
        tone(100, 40, 100, 2);
        return;
    } else {
        timer--;
    }

    //parte do input
    srand((unsigned int)frame);
    int dx = 0; // deslocamento, permite mais de uma tecla pressionada
    
    uint8_t gamepad = *GAMEPAD1;
    if (gamepad & BUTTON_LEFT)  { 
        dx -= 1;
        esquerda = 1;
    }
    if (gamepad & BUTTON_RIGHT) { 
        dx += 1;
        esquerda = 0; 
    }
    x += dx;
    
    int frameAtual = 0;
    if (x < -2) {
        x = -2;
    }

    if (x > 150) {
        x = 150;
    }
    if (dx != 0) {
        frameAtual = ((frame / 12) % 2) + 1;
    }
    
    int spriteX = frameAtual * 16;
    int spriteY = 0;

    uint32_t flags_desenho = BLIT_2BPP;
    if (esquerda) {
        flags_desenho |= BLIT_FLIP_X;
    }
    
    //applejack (pony)
    *DRAW_COLORS = 0x0024;
    blitSub(ponyBehind, x, y, 16, 16, (uint32_t)spriteX, (uint32_t)spriteY, ponyBehindWidth, flags_desenho);

    *DRAW_COLORS = 0x0123;
    blitSub(pony, x, y, 16, 16, (uint32_t)spriteX, (uint32_t)spriteY, ponyWidth, flags_desenho);
    // *DRAW_COLORS = 0x0024;
    // blitSub(ponyBehind, x, y, 16, 16, (uint32_t)spriteX, (uint32_t)spriteY, ponyBehindWidth, BLIT_2BPP);
    // *DRAW_COLORS = 0x0123;
    // blitSub(pony, x, y, 16, 16, (uint32_t)spriteX, (uint32_t)spriteY, ponyWidth, BLIT_2BPP);
    
    if (x < fruit.x + 5 && x + 5 > fruit.x && y < fruit.y + 5 && y + 5 > fruit.y) { //se colidiu
        if (fruit.type == GOOD_APPLE) {
            score += 10;
            tone(800, 20, 100, TONE_PULSE1);
        } else {
            if (score > 0) {
                score -= 10;
            }
            
            tone(120, 25 << 16, 90, 3);
        }
        
        spawn_fruit();
    }

    if (fruit.type == BAD_APPLE) {
        *DRAW_COLORS = 0x0123;
        blitSub(ponyBehind, fruit.x, fruit.y, 16, 16, (uint32_t)64, (uint32_t)spriteY, ponyBehindWidth, BLIT_2BPP);
    } else {
        blitSub(ponyBehind, fruit.x, fruit.y, 16, 16, (uint32_t)48, (uint32_t)spriteY, ponyBehindWidth, BLIT_2BPP);
        blitSub(pony, fruit.x, fruit.y, 16, 16, (uint32_t)48, (uint32_t)spriteY, ponyWidth, BLIT_2BPP);
    }

    *DRAW_COLORS = 0x12;
    rect(0, 125, 160, 35);

    char textScore[11] = "SCORE:0000";
    textScore[9] = (char)('0' + score % 10);
    textScore[8] = (char)('0' + ((score / 10) % 10));
    textScore[7] = (char)('0' + ((score / 100) % 10));
    textScore[6] = (char)('0' + ((score / 1000) % 10));
    *DRAW_COLORS = 0x0001;
    text(textScore, 4, 140);

    int timerSec= timer / 60;
    char textTime[9] = "TIME:000";
    textTime[7] = (char)('0' + timerSec % 10); //calcula as unidades
    textTime[6] = (char)('0' + ((timerSec / 10) % 10));//calcula as dezenas
    textTime[5] = (char)('0' + ((timerSec / 100) % 10)); //calcula as centenas
    text(textTime, 90, 140);
}
