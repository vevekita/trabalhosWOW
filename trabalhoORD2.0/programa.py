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
FORMATO_PAG = f'i {MAX_CHAVES*2}i {MAX_FILHOS}i'
SIZEOF_PAG = calcsize(FORMATO_PAG)

@dataclass
class Pagina:
    def __init__(self) -> None:
        self.numChaves: int = 0
        self.chaves: list[tuple[int, int]] = [(NULO, NULO)] * (ORDEM-1)
        self.filhos: list[int] = [NULO] * ORDEM # Referência ao RRN das páginas filhas (esquerda e direita)

def ler_pagina(rrn: int, btree: io.BufferedReader) -> Pagina:
    '''Função auxiliar para a busca e inserção de uma chave em um arquivo com uma árvore-B. 
    Ela busca pegar os registros de determinado rrn no arquivo onde fica a árvore-B e colocar eles conforme as propriedades de uma Pagina'''
    offset: int = SIZEOF_HEADER + (rrn * SIZEOF_PAG)
    btree.seek(offset)
    pag_bytes = btree.read(SIZEOF_PAG)
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

def buscaNaArvore(chave: tuple[int, int], rrn: int, btree: io.BufferedReader):
    '''Busca uma chave na árvore-B. Retorna se a ocorrência acontece ou não (True | False). 
    Se ocorre, retorna também o rrn e a posição na página do rrn'''
    if rrn == NULO:
        return False, NULO, NULO
    else:
        pag = ler_pagina(rrn, btree)#leitura da página armazenada no rrn
        achou, pos = buscaNaPagina(chave, pag)
        if achou:
            return True, rrn, pos
        else:
            return buscaNaArvore(chave, pag.filhos[pos])

def buscaNaPagina(chave: tuple[int, int], pag: Pagina) -> tuple[bool, int]:
    '''Realiza a busca de uma chave em determinada página.'''
    pos = 0
    while pos < pag.numChaves and chave > pag.chaves[pos]:
        pos += 1
    if pos < pag.numChaves and chave == pag.chaves[pos]:
        return True, pos
    else:
        return False, pos

def insereChave(chave: tuple[int, int], rrnAtual: int, btree: io.BufferedReader):
    '''Função que insere uma chave em uma página, percorrendo recursivamente pelas páginas 
    filhas até encontrar um ponteiro nulo e insere, de forma válida (utilizando métodos de 
    divisão e promoção se necessários), a chave'''
    if rrnAtual == NULO: #condição de parada da recursão
        chavePro = chave
        filhoDpro = NULO
        return chavePro, filhoDpro, True
    else:
        pag = ler_pagina(rrnAtual, btree)
        achou, pos = buscaNaPagina(chave, pag)
    if achou:
        raise ValueError("Chave duplicada!")
    
    chavePro, filhoDpro, promo = insereChave(chave, pag.filhos[pos], btree)

    if not promo: #se não houver promoção
        return NULO, NULO, False
    else: #se houver promoção
        chavePro, filhoDpro, pag, novaPag = divide(chavePro, filhoDpro, pag)
        escrevePagina(rrnAtual, pag, btree)
        escrevePagina(filhoDpro, novaPag, btree)
        return chavePro, filhoDpro, True

# FUNÇÕES AUXILIARES PARA A FUNÇÃO INSERE (tem mais a buscaNaPagina())
def escrevePagina(rrn: int, pag: Pagina, btree: io.BufferedWriter):
    '''Registra os dados presentes em determinada página no arquivo 'btree.dat' '''
    offset: int = SIZEOF_HEADER + (rrn * SIZEOF_PAG)
    btree.seek(offset)
    pag_bytes = pack(FORMATO_PAG, pag.numChaves)

    for i in range(MAX_CHAVES): #transforma a lista de tuplas em só uma lista de inteiros
        chave_tupla = pag.chaves[i]
        pag_bytes += pack('2i', chave_tupla[0], chave_tupla[1])
    for n in range(MAX_FILHOS):
        pag_bytes += pack('i', pag.filhos[n])
    btree.write(pag_bytes)

def insereChavePromo(chave: tuple[int, int], filhoD: int, pag: Pagina):
    '''Função auxiliar que insere uma chave (e seu filho direito) em determinada página da árvore-B,
    empurrandos os elementos maiores para a direita para abrir espaço à chave a ser inserida.'''
    if pag.numChaves == MAX_CHAVES:
        pag.chaves.append((NULO, NULO))
        pag.filhos.append(NULO)
    i = pag.numChaves
    while i > 0 and chave[0] < pag.chaves[i-1][0]:
        pag.chaves[i] = pag.chaves[i-1]
        pag.filhos[i+1] = pag.filhos[i]
        i -= 1
    pag.chaves[i] = chave
    pag.filhos[i+1] = filhoD
    pag.numChaves += 1

def divide(chave: tuple[int, int], filhoD: int, pag: Pagina, btree: io.BufferedRandom):
    '''Divide uma página em duas páginas(uma delas sendo a página em si)'''
    insereChavePromo(chave, filhoD, pag)
    meio = ORDEM // 2
    chavePro = pag.chaves[meio]
    filhoDpro = novoRRN(btree)

    pAtual = Pagina()
    pNova = Pagina()
    qtdChaves: int = 0
    novaQtdChaves: int = 0
    #Recebimento de valores de pag no pAtual
    for n in range(meio):
        if pag.chaves[n] != (NULO, NULO):
            pAtual.chaves[n] = pag.chaves[n]#atribui as chaves da primeira metade às chaves do pAtual
            qtdChaves += 1
        if pag.filhos[n] != NULO:
            pAtual.filhos[n] = pag.filhos[n]#atribui os filhos da primeira metade aos filhos do pAtual
    if pag.filhos[meio] != NULO:
        pAtual.filhos[meio] = pag.filhos[meio]
    pAtual.numChaves = qtdChaves      

    #Recebimento de valores de pag na pNova
    for m in range(meio):
        if pag.chaves[meio + 1 + m] != (NULO, NULO):
            pNova.chaves[m] = pag.chaves[meio + 1 + m]
            novaQtdChaves += 1
        if pag.filhos[meio + 1 + m] != NULO:
            pNova.filhos[m] = pag.filhos[meio + 1 + m]
    if pag.filhos[MAX_FILHOS] != NULO: #se o filho da extremidade direita da página não fo nulo, transferimos ele para a pNova
        pNova.filhos[novaQtdChaves] = pag.filhos[MAX_FILHOS]
    pNova.numChaves = novaQtdChaves

    return chavePro, filhoDpro, pAtual, pNova

def novoRRN(btree: io.BufferedReader) -> int:
    '''Função auxiliar que encontra o RRN de uma nova página adicionada na árvore-B'''
    btree.seek(0, os.SEEK_END)
    offset = btree.tell()
    return (offset - SIZEOF_HEADER) // SIZEOF_PAG

def insereNaArvore(chave: tuple[int, int], raiz: int, btree): # Adicionado parametro btree
    chavePro, filhoDpro, promocao = insereChave(chave, raiz, btree) # Adicionado parametro btree
    if promocao:
        pNova = Pagina()
        pNova.numChaves = 1
        pNova.chaves[0] = chavePro # nova chave raiz
        pNova.filhos[0] = raiz # filho esquerdo
        pNova.filhos[1] = filhoDpro # filho direito

        # Novo RRN para escrever na página raiz
        nRaizRrn = novoRRN(btree)
        escrevePagina(nRaizRrn, pNova, btree)

        # Atualização do cabeçalho do arquivo com o RRN da raiz nova
        btree.seek(0)
        btree.write(pack(FORMATO_HEADER, nRaizRrn))
        return nRaizRrn
    
    return raiz

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
    try:
        open('games.dat', 'rb').close()
        open('btree.dat', 'rb').close()
    except FileNotFoundError as e:
        print(f'Erro: {e}')

    # As buscas deverão ser realizadas no índice que está armazenado no arquivo btree.dat. Uma vez localizada a chave
    # no índice, o byte-offset do registro correspondente deverá ser recuperado e o registro deverá ser acessado de forma
    # direta no arquivo games.dat.
    # As inserções sempre acontecerão no fim do arquivo games.dat e, complementarmente, as informações do novo
    # registro (chave e byte-offset) deverão ser inseridas no índice do arquivo btree.dat. Não será permitida a inserção de
    # registros com chave duplicada. Dessa forma, antes de realizar uma inserção, o índice deverá ser consultado e uma
    # mensagem de erro deverá ser dada caso se identifique a duplicação.

def imprime():
    try:
        open('btree.dat', 'r').close()
    except FileNotFoundError as e:
        print(f'Erro: {e}')

    # Sempre que ativada, essa funcionalidade apresentará na tela o conteúdo de todas as páginas da árvore-B armazenada
    # no arquivo btree.dat. Para cada página da árvore deverá ser informado: (a) o seu RRN; (b) os valores das chaves; (c)
    # os valores dos byte-offsets dos registros associados às chaves; e (d) os RRNs das páginas filhas. As páginas devem ser
    # apresentadas pela ordem do seu RRN e a página raiz deve ser devidamente identificada.

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
