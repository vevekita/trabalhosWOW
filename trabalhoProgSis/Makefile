CC = gcc
CFLAGS = -Wall

all: test_map draw_map

test_map: map.o test_map.o
	$(CC) map.o test_map.o -o test_map

draw_map: map.o rpng.o draw_map.o
	$(CC) map.o rpng.o draw_map.o -o draw_map

map.o: map.c map.h
	$(CC) $(CFLAGS) map.c -c

rpng.o: rpng.c rpng.h
	$(CC) $(CFLAGS) rpng.c -c

test_map.o: test_map.c map.h
	$(CC) $(CFLAGS) test_map.c -c

draw_map: draw_map.c map.h
	$(CC) $(CFLAGS) draw_map.c -c
clean:
	rm test_map draw_map *.o
