#include "wasm4.h"
#include "background_jogo.h"
#include <stdint.h>
#include <stdlib.h>
#include "pony.h"
#include "ponyBehind.h"

#define GRAVITY 0
#define GOOD_APPLE 0
#define BAD_APPLE 1
#define MENU 0
#define JOGO 1
#define FIM 2

int jogo = MENU;
int timer = 2400;
int score = 0;
int frame = 0;
struct point {
    int32_t x;
    int32_t y;
    int type;
    int stop;
    int speed;
};
struct point fruit;
int velocity_apple = 0;
char textScore[11] = "SCORE:0000";
char textTime[9] = "TIME:000";

// retorna cor do pixel (x,y)
int pget(int x, int y) {
    if (x < 0 || x > 159 || y < 0 || y > 159) { return 0; }
    int idx = (y*160 + x) >> 2;
    int shift = (x & 0b11) << 1;
    int mask = 0b11 << shift;
    return ((FRAMEBUFFER[idx] & mask) >> shift) + 1;
}

int x, y; // posição do jogador
int esquerda;

void spawn_fruit() {
    fruit.x = rand()%153;
    fruit.y = (rand() % 41) + 30;
    fruit.type = rand()%2;
    fruit.stop = 40;
    if (fruit.y <= 42) {
        fruit.speed = 1;
    }
    else if (fruit.y <= 60) {
        fruit.speed = 2;
    }
    else {
        fruit.speed = 3;
    }
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
    
    if (jogo == MENU) {
        *DRAW_COLORS = 0x3;
        rect(0, 0, 160, 160);
        *DRAW_COLORS = 0x0001;
        text("PONY GAME", 45, 20);

        *DRAW_COLORS = 0x4;
        rect(10, 40, 140, 45);
        *DRAW_COLORS = 0x2;
        line(150, 85, 150, 40);
        line(10, 85, 150, 85);

        *DRAW_COLORS = 0x0001;
        text("-> bad apple", 40, 70);
        *DRAW_COLORS = 0x0123;
        blitSub(ponyBehind, 20, 66, 16, 16, (uint32_t)64, 0, ponyBehindWidth, BLIT_2BPP);

        *DRAW_COLORS = 0x0001;
        text("-> good apple", 40, 45);
        *DRAW_COLORS = 0x0124; 
        blitSub(ponyBehind, 20, 42, 16, 16, (uint32_t)48, 0, ponyBehindWidth, BLIT_2BPP);
        *DRAW_COLORS = 0x0123;
        blitSub(pony, 20, 42, 16, 16, (uint32_t)48, 0, ponyWidth, BLIT_2BPP);

        *DRAW_COLORS = 0x0001;
        text("Try to catch all the", 0, 95);
        text("good apples in the time limit", 10, 105);
        text("time limit", 40, 115);

        *DRAW_COLORS = 0x4;
        rect(5, 127, 150, 14);
        *DRAW_COLORS = 0x2;
        line(5, 141, 155, 141);
        line(155, 127, 155, 141);

        *DRAW_COLORS = 0x0001;
        text("press [X] to start the game", 10, 130);
        uint8_t gamepad = *GAMEPAD1;
        if (gamepad & BUTTON_1) {
            jogo = JOGO;
        }

    }
    else if (jogo == JOGO) {
        if (fruit.stop>0) { //faz contagem regressiva para a maçã não cair
            fruit.stop--;
        } else {
            if (frame % fruit.speed == 0) {
                fruit.y++;  
            }
        }
        if (fruit.y > 110) { //se a maçã cair no chão
            if (fruit.type == 0) {
                timer = timer - 120;
            }
            spawn_fruit(); //spawna outra fruta
        }

        //background
        *DRAW_COLORS = 0x1234;
        blit(background_jogo, 0, 0, background_jogoWidth, background_jogoHeight, background_jogoFlags);
        
        if (timer <= 0) {
            jogo = FIM;
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
            *DRAW_COLORS = 0x0124; 
            blitSub(ponyBehind, fruit.x, fruit.y, 16, 16, (uint32_t)48, (uint32_t)spriteY, ponyBehindWidth, BLIT_2BPP);
            *DRAW_COLORS = 0x0123;
            blitSub(pony, fruit.x, fruit.y, 16, 16, (uint32_t)48, (uint32_t)spriteY, ponyWidth, BLIT_2BPP);
        }

        *DRAW_COLORS = 0x12;
        rect(0, 125, 160, 35);

        textScore[9] = (char)('0' + score % 10);
        textScore[8] = (char)('0' + ((score / 10) % 10));
        textScore[7] = (char)('0' + ((score / 100) % 10));
        textScore[6] = (char)('0' + ((score / 1000) % 10));
        *DRAW_COLORS = 0x0001;
        text(textScore, 4, 140);

        int timerSec= timer / 60;
        textTime[7] = (char)('0' + timerSec % 10); //calcula as unidades
        textTime[6] = (char)('0' + ((timerSec / 10) % 10));//calcula as dezenas
        textTime[5] = (char)('0' + ((timerSec / 100) % 10)); //calcula as centenas
        text(textTime, 90, 140);

    }
    else if (jogo == FIM) {
        *DRAW_COLORS = 0x1234;
        blit(background_jogo, 0, 0, background_jogoWidth, background_jogoHeight, background_jogoFlags);

        *DRAW_COLORS = 0x13;
        rect(20, 50, 120, 100);
        *DRAW_COLORS = 0x2;
        text("GAME OVER", 46, 64);
        *DRAW_COLORS = 0x0004;
        text("GAME OVER", 45, 62);
        *DRAW_COLORS = 0x0001;
        text(textScore, 42, 85);

        *DRAW_COLORS = 0x4;
        rect(27, 105, 106, 35);
        *DRAW_COLORS = 0x2;
        line(27, 140, 133, 140);
        line(133, 105, 133, 140);
        *DRAW_COLORS = 0x0001;
        text("PRESS [Z] TO", 33, 110);
        text("GO BACK TO", 42, 120);
        text("MENU", 65, 130);

        uint8_t gamepad = *GAMEPAD1;
        if (gamepad & BUTTON_2) {
            jogo = MENU;
            timer = 2400;
            score = 0;
            spawn_fruit();
        }
    }
    
}
