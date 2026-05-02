from sys import argv
from struct import pack, unpack, calcsize
import os
from dataclasses import dataclass
from copy import deepcopy

@dataclass
class chave:
    valor: int
class no:
    def __init__(self, x:chave):
        self.dado: chave = x
        self.prox: no | None = None

    def vazia(self):
        '''Verifica se a lista está vazia.
        False'''
        return self.primeiro == None
    
class lista:
    def __init__(self):
        self.primeiro: no | None = None
        self.ultimo: no | None = None
    
    def busca(self, chave:int) -> no:
        '''Busca um número na lista pela chave. Se ele não estiver na lista,
        retorna None.
        '''
        ptr = self.primeiro
        while (ptr != None) and (ptr.dado.valor != chave):
            ptr = ptr.prox
        return ptr
    
    def busca_item(self, chave:int) -> chave | None:
            '''Busca um item com base na função *busca*, se ela não estiver na lista,
            retorna None.'''
            ptr = self.busca(chave)
            if ptr != None:
                return deepcopy(ptr.dado)
            else:
                return None
        
    def insere_fim(self, x:chave) -> bool:
        '''Insere um novo item no final da lista. Se esse item já estiver na
        lista, retorna False.'''
        if self.busca(x.valor) == None:
            novo = no(x)
            if self.vazia():
                self.primeiro = novo
            else:
                self.ultimo.prox = novo
            self.ultimo = novo
            return True
        else:
            return False
    
def cria_ind(nome_arq_entrada: str):
    '''Lê o aquivo de entrada e adiciona os registros
    lidos em separações diferentes dependendo a partir
    de chaves (1 primária - id e 2 secundárias - gênero 
    e publicadora)'''
    registros = []
    list_chave_gen = lista()
    list_chave_prod = lista()
    with open(nome_arq_entrada, 'rb') as entrada:
        offset = 0
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split(sep='|')
            campos[0] = int(campos[0])
            campos.pop() #elimina o último campo criado pelo split('|'), já que cada bloco termina com '|'
            registros.append(campos)
            offset = offset + tam_int + 2
            tam_bytes = entrada.read(2)
            tam_int = int.from_bytes(tam_bytes, "little")
        print(registros)
def main():
    if len(argv) < 2:
        raise TypeError('Número incorreto de argumentos\nModo de uso: nome_arq_entrada nome_arq_saida')
    cria_ind(argv[1])



if __name__ == '__main__':
    main()
