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
    
def constroi_indices():
    '''Lê o aquivo de entrada e adiciona os registros
    lidos em separações diferentes dependendo a partir
    de chaves (1 primária - id e 2 secundárias - gênero 
    e publicadora)'''
    nome_arq_entrada = "games.dat" #coloquei aqui porque o arquivo vai ser fixo "games.dat", daí pra não precisar receber um arquivo por parâmetro
    list_chave_prim = [] #lista da chave primária
    list_chave_gen = []  #lista da chave secundária: gênero
    list_chave_pub = [] #lista da chave secundária: publicadora
    with open(nome_arq_entrada, 'rb') as entrada:
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            offset = entrada.tell()
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split(sep='|')
            id = campos[0]
            genero = campos[3]
            publicadora = campos[4]
            list_chave_prim.append((int(id), offset))
            
            #parte da criação dos índices do gênero
            genero_encontrado = False
            n = 0
            while n < len(list_chave_gen) and genero_encontrado == False:
                if genero == list_chave_gen[n][0]:
                    list_chave_gen[n][1].insere_fim(int(id))
                    genero_encontrado = True
                n += 1
            if genero_encontrado == False:
                nova_lista = lista()
                nova_lista.insere_fim(chave(int(id)))
                list_chave_gen.append([genero, nova_lista])

            #parte da criação dos índices de publicadora
            publicadora_encontrada = False
            m = 0
            while m < len(list_chave_pub) and publicadora_encontrada == False:
                if publicadora == list_chave_pub[m][0]:
                    list_chave_pub[m][1].insere_fim(int(id))
                    publicadora_encontrada = True
                m += 1
            if publicadora_encontrada == False:
                nova_lista_pub = lista()
                nova_lista_pub.insere_fim(chave(int(id)))
                list_chave_pub.append([publicadora, nova_lista_pub])
                
            tam_bytes = entrada.read(2)
            tam_int = int.from_bytes(tam_bytes, 'little')

def main():
    if len(argv) < 2:
        raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py [ -b | -e arquivo_operacoes | -c')
    
    flag = argv[1]

    if flag == "-b":
        constroi_indices()
    elif flag == "-e":
        if len(argv) < 3:
            raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py -e arquivo_operacoes')
        #executar_operacoes()
    elif flag == "-c":
        # compactar()
        pass
if __name__ == '__main__':
    main()
