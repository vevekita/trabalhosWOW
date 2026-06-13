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

def constroi_indices():
    btree: list[tuple[int, int]] = []
    with open('games.dat', 'rb') as entrada:
        offset = 0
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split('|')
            id = int(campos[0])
            btree.append((id, offset))
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
