#include <assert.h>
#include <stdio.h>

#include "map.h"

int main(){
    Map *m = map_create(5, 5);
    assert(m != NULL);

    assert(map_rows(m) == 5);

    assert(map_cols(m) == 5);

    map_set(m, 2, 3, WALL);
    assert(map_get(m, 2, 3) == WALL);
    
    map_set(m, 2, 3, OPEN);
    assert(map_get(m, 2, 3) == OPEN);

    map_load()
    
    map_print(map_get(m, 2, 3), )

    map_free(m);

    printf("OK!\n");
    return 0;
}

