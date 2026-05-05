from dataclasses import dataclass
from copy import deepcopy

@dataclass
class chave:
    valor: int

class no:
    def __init__(self, x:chave):
        self.dado: chave = x
        self.prox: no | None = None

class lista:
    def __init__(self):
        self.primeiro: no | None = None
        self.ultimo: no | None = None

    def vazia(self):
        '''Verifica se a lista está vazia.
        False'''
        return self.primeiro == None

    def busca(self, chave:int) -> no:
        '''Busca um número na lista pela chave. Se ele não estiver na lista,
        retorna None.'''
        ptr = self.primeiro
        while (ptr != None) and (ptr.dado.valor != chave):
            ptr = ptr.prox
        return ptr

    # def busca_item(self, chave:int) -> chave | None:
    #         '''Busca um item com base na função *busca*, se ela não estiver na lista,
    #         retorna None.'''
    #         ptr = self.busca(chave)
    #         if ptr != None:
    #             return deepcopy(ptr.dado)
    #         else:
    #             return None

    def insere_fim(self, x: chave) -> bool:
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
