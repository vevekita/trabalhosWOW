from sys import argv
from struct import pack, unpack, calcsize
from dataclasses import dataclass
import io
import os

# Variáveis Globais
ORDEM: int = 0
NULO: int = -1
FORMATO_HEADER = 'i'
SIZEOF_HEADER = calcsize(FORMATO_HEADER)
MAX_CHAVES = ORDEM - 1
MAX_FILHOS = ORDEM
FORMATO_PAG = f'i {MAX_CHAVES*2}i {MAX_FILHOS}i'
SIZEOF_PAG = calcsize(FORMATO_PAG)

@dataclass
class Pagina:
    def __init__(self) -> None:
        self.numChaves: int = 0
        self.chaves: list[tuple[int, int]] = [(NULO, NULO)] * (ORDEM-1)
        self.filhos: list = [NULO] * ORDEM # Referência ao RRN das páginas filhas (esquerda e direita)

def ler_pagina(rrn: int, arq: io.BufferedReader) -> Pagina:
    '''Função auxiliar para a busca de uma página em um arquivo com uma árvore-B'''
    offset: int = SIZEOF_HEADER + (rrn * SIZEOF_PAG)
    arq.seek(offset)
    pag_bytes = arq.read(SIZEOF_PAG)
    pag_str = unpack(FORMATO_PAG, pag_bytes)
    num_chaves = pag_str[0]
    list_filhos = pag_str[(1 + (MAX_CHAVES * 2)):]
    chaves: tuple = pag_str[1:(1 + (MAX_CHAVES * 2))]
    pag = Pagina() #cria uma página pag
    pag.numChaves = num_chaves

    i = 0
    id_atual = NULO
    offset_atual = NULO
    while i < (MAX_CHAVES * 2): #cada tipo de chave (id e offset) tem tamanho de MAX_CHAVES, logo o tamanho de chaves no total é (2 * MAX_CHAVES)
        id_atual: int = chaves[i]
        offset_atual: int = chaves[i + 1]
        indice: int = i // 2 #inclui um id e um offset respectivos como parte de um único índice, então define-se o índice como a metade inteira (arredondada para baixo) do indice da chave tratada individualmente
        pag.chaves[indice] = (id_atual, offset_atual)
        i += 2 #pula de 2 em 2 pois cada elemento vai ser uma tupla
    
    for n in range(MAX_FILHOS):
        pag.filhos[n] = list_filhos[n] #substitui valor vazio inicialmente definido pela criação do objeto Página em um indice i por um elemento da lista de filhos remetente ao mesmo índice

    return pag

def buscaNaArvore(chave: tuple[int, int], rrn: int, arq: io.BufferedReader):
    if rrn == NULO:
        return False, NULO, NULO
    else:
        pag = ler_pagina(rrn, arq)#leitura da página armazenada no rrn
        achou, pos = buscaNaPagina(chave, pag)
        if achou:
            return True, rrn, pos
        else:
            return buscaNaArvore(chave, pag.filhos[pos])

def buscaNaPagina(chave: tuple[int, int], pag: Pagina) -> tuple[bool, int]:
    pos = 0
    while pos < pag.numChaves and chave > pag.chaves[pos]:
        pos += 1
    if pos < pag.numChaves and chave == pag.chaves[pos]:
        return True, pos
    else:
        return False, pos

def insereChave(chave: tuple[int, int], rrnAtual: int):
    if rrnAtual == NULO:
        chavePro = chave
        filhoDpro = NULO
        return chavePro, filhoDpro, True
    else:
        pag = #leitura página armazenada em rrnAtual
        achou, pos = buscaNaPagina(chave, pag)
    if achou:
        raise ValueError("Chave duplicada!")
    
    chavePro, filhoDpro, promo = insereChave(chave, pag.filhos[pos])

    if not promo:
        return NULO, NULO, False
    else:
        if #existe espaço em pag para inserir chavePro:
            # não terminado!

# FUNÇÕES AUXILIARES PARA A FUNÇÃO INSERE (tem mais a buscaNaPagina())
def escrevePagina(rrn: int, pag: Pagina, arq: io.BufferedWriter):
    '''Registra os dados presentes em determinada página no arquivo 'btree.dat' '''
    offset: int = SIZEOF_HEADER + (rrn * SIZEOF_PAG)
    arq.seek(offset)
    pag_bytes = pack(FORMATO_PAG, pag.numChaves)

    for i in range(MAX_CHAVES): #transforma a lista de tuplas em só uma lista de inteiros
        chave_tupla = pag.chaves[i]
        pag_bytes += pack('2i', chave_tupla[0], chave_tupla[1])

    for n in range(MAX_FILHOS):
        pag_bytes += pack('i', pag.filhos[n])

    arq.write(pag_bytes)

    
def insereChavePromo(chave: tuple[int, int], filhoD: int, pag: Pagina):
    pass

def divide(chave: tuple[int, int], filhoD: int, pag: Pagina):
    pass

def constroi_indices():
    with open('games.dat', 'rb') as entrada, open('btree,dat', 'r + b') as saida:
        offset = 0 # ou poderia ser entrada.tell()?
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split('|')
            id = int(campos[0])
            offset = entrada.tell()
            tam_bytes = entrada.read(2)
            tam_int = int.from_bytes(tam_bytes, 'little')
            
def executa_operacoes(arquivo_operacoes: str):
    pass

def imprime():
    pass

def main():
    if len(argv) < 2:
        raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py [ -b | -e arquivo_operacoes | -p')
    
    flag = argv[1]

    if flag == "-b":
        constroi_indices()
    elif flag == "-e":
        if len(argv) < 3:
            raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py -e arquivo_operacoes')
        executa_operacoes(argv[2])
    elif flag == "-p":
        imprime()

if __name__ == '__main__':
    main
