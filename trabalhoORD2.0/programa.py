from sys import argv
from struct import pack, unpack, calcsize
from dataclasses import dataclass
import io
import os

# Variável Global
ORDEM: int = 0
NULO: int = -1
FORMATO_HEADER = 'i'
SIZEOF_HEADER = calcsize(FORMATO_HEADER)
MAX_CHAVES = ORDEM - 1
MAX_FILHOS = ORDEM
FORMATO_PAG = f'i {MAX_CHAVES}i {MAX_CHAVES}i {MAX_FILHOS}i'
SIZEOF_PAG = calcsize(FORMATO_PAG)

@dataclass
class Pagina:
    def __init__(self) -> None:
        self.numChaves: int = 0
        self.chaves: list[tuple[int, int]] = [(NULO, NULO)] * (ORDEM-1)
        self.filhos: list = [NULO] * ORDEM # Referência ao RRN das páginas filhas (esquerda e direita)

def ler_pagina(rrn: int, arq: io.BufferedReader) -> Pagina:
    '''Função auxiliar para a busca de uma página em um arquivo com uma árvore-B'''
    arq.seek(SIZEOF_HEADER + (rrn * SIZEOF_PAG))
    pag_bytes = arq.read(SIZEOF_PAG)
    pag_str = unpack(FORMATO_PAG, pag_bytes)
    num_chaves = pag_str[0]
    list_id = pag_str[1:(1 + MAX_CHAVES)]
    list_offset = pag_str[(1 + MAX_CHAVES):(1 + (MAX_CHAVES * 2))]
    list_filhos = pag_str[(1 + (MAX_CHAVES * 2)):]
    pag = Pagina() #cria uma página pag
    pag.numChaves = num_chaves

    for i in range(MAX_CHAVES):
        pag.chaves.append((list_id[i], list_offset[i])) #cada chave é uma tupla (id, offset). Aqui, vai inserindo cada uma das chaves da página
    
    for n in range(MAX_FILHOS):
        pag.filhos.append(list_filhos[n]) #insere cada página filho de determinada página

    return pag

def buscaNaArvore(chave: int, rrn: int, arq: io.BufferedReader):
    if rrn == NULO:
        return False, NULO, NULO
    else:
        pag = ler_pagina(rrn, arq)#leitura da página armazenada no rrn
        achou, pos = buscaNaPagina(chave, pag)
        if achou:
            return True, rrn, pos
        else:
            return buscaNaArvore(chave, pag.filhos[pos])

def buscaNaPagina(chave, pag):
    pos = 0
    while pos < pag.numChaves and chave > pag.chaves[pos]:
        pos += 1
    if pos < pag.numChaves and chave == pag.chaves[pos]:
        return True, pos
    else:
        return False, pos

def insereChave(chave, rrnAtual):
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
def lePagina(rrn):
    pass

def escrevePagina(rrn, pag):
    pass

def insereChavePromo(chave, filhoD, pag):
    pass

def divide(chave, filhoD, pag):
    pass

def constroi_indices():
    btree: list[Pagina] = [] # não sei como definir o tipo da lista
    with open('games.dat', 'rb') as entrada, open('btree,dat', 'wb') as saida:
        offset = 0 # ou poderia ser entrada.tell()?
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split('|')
            id = int(campos[0])
            #btree.append((id, offset)) # usar a função de inserção para inserir na árvore
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
    main()
