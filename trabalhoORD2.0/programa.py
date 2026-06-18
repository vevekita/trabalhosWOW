from sys import argv
from struct import pack, unpack, calcsize
from dataclasses import dataclass

# Variável Global
ORDEM: int = 0
NULO: int = -1

@dataclass
class Pagina:
    def __init__(self) -> None:
        self.numChaves: int = 0
        self.chaves: list = [NULO] * (ORDEM-1)
        self.filhos: list = [NULO] * ORDEM # Referência ao RRN das páginas filhas (esquerda e direita) 

# isso aqui é uma ideia, mas pode ignorar por enquanto
# MAX_CHAVES = ORDEM - 1
# MAX_FILHOS = ORDEM

# FORMATO_PAGINA = f'i{MAX_CHAVES}i{MAX_CHAVES}i{MAX_FILHOS}i' # n + chave + offset + filhos
# TAMANHO_PAGINA = calcsize(FORMATO_PAGINA)

def buscaNaArvore(chave, rrn):
    if rrn == NULO:
        return False, NULO, NULO
    else:
        pag = #leitura da página armazenada no rrn
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
