CC = gcc
CFLAGS = -Wall

all: main test_map

default: all

main: par.o main.o
	$(CC) par.o main.o -o main

test_map: map.o test_map.o
	$(CC) map.o test_map.o -o test_map

par.o: par.c par.h
	$(CC) $(CFLAGS) par.c -c

main.o: main.c par.h
	$(CC) $(CFLAGS) main.c -c

map.o: map.c map.h
	$(CC) $(CFLAGS) map.c -c

test_map.o: test_map.c map.h
	$(CC) $(CFLAGS) test_map.c -c

clean:
	rm -f main par.o main.o test_map map.o test_map.o
