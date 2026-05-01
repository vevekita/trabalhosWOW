from sys import argv
from struct import pack, unpack, calcsize
import os
        
def cria_lista(nome_arq_entrada: str) -> list: #construção do índice principal :)
    '''vai criar uma lista e armazenar TODOS os
    registros do arquivo de entrada.'''
    registros = []
    with open(nome_arq_entrada, 'rb') as entrada:
        tam = entrada.read(2)
        tam_int = int.from_bytes(tam, 'little')
        v

def cria_ind(nome_arq_entrada: str):
    '''Lê o aquivo de entrada e adiciona os registros
    lidos em separações diferentes dependendo a partir
    de chaves (1 primária - id e 2 secundárias - gênero 
    e publicadora)'''
    registros = []
    list_chave_gen = []
    list_
    with open(nome_arq_entrada, 'rb') as entrada:
        offset = 0
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            offset = offset + tam_int + 2
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split(sep='|')
            id = campos[0]
            campos.pop() #elimina o último campo criado pelo split('|'), já que cada bloco termina com '|'
            registros.append(int(id), tam + reg)
            tam = entrada.read(2)
            tam_int = int.from_bytes(tam, "little")
        registros.sort() #já organiza as listas de jogos pela numeraçã
        for #a ideia é percorrer a lista gerada para ir adicionando nas outras listas definidas no começo da função (em mente de que essas listas armazenarão outras listas dentro delas)

def main():
    if len(argv) < 2:
        raise TypeError('Número incorreto de argumentos\nModo de uso: nome_arq_entrada nome_arq_saida')
    o = cria_lista(argv[1])
    print(o)


if __name__ == '__main__':
    main()
