CC = gcc
CFLAGS = -Wall

all: test_map

default: all

test_map: map.o test_map.o
	$(CC) map.o test_map.o -o test_map

map.o: map.c map.h
	$(CC) $(CFLAGS) map.c -c

test_map.o: test_map.c map.h
	$(CC) $(CFLAGS) test_map.c -c

clean:
	rm -f test_map map.o test_map.o
