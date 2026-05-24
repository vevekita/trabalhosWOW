CC = gcc
CFLAGS = -Wall

all: main

main: par.o main.o
        $(CC) par.o main.o -o main

par: par.c par.h
        $(CC) $(CFLAGS) par.c -c

main.o: main.c par.h
        $(CC) $(CFLAGS) main.c -c

clean:
        rm main par.o main.o
