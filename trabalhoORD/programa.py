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
    
def cria_lista(nome_arq_entrada: str, nome_arq_prim: str, nome_arq_gen: str, nome_arq_pub: str, nome_arq_list: str):
    '''Lê o aquivo de entrada e adiciona os registros
    lidos em separações diferentes dependendo a partir
    de chaves (1 primária - id e 2 secundárias - gênero 
    e publicadora)'''
    list_chave_prim:list[tuple] = [] #lista da chave primária
    list_chave_gen:list[tuple[str, int]] = []  #lista da chave secundária: gênero
    list_chave_pub:list[tuple[str, int]] = [] #lista da chave secundária: publicadora
    list_encadeadas_gen: list[tuple[str, lista]] = [] #lista contendo indentificador "genero" e lista encadeada com as posições dos jogos associados
    list_encadeadas_pub: list[tuple[str, lista]] = [] #lista contendo indentificador "genero" e lista encadeada com as posições dos jogos associados
    lista_invertida:list[list] = [] #lista invertida
    posicao_reg = 0
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
            while n < len(list_encadeadas_gen) and genero_encontrado == False:
                if genero == list_encadeadas_gen[n][0]:
                    list_encadeadas_gen[n][1].insere_fim(chave(posicao_reg))
                    genero_encontrado = True
                n += 1
            if genero_encontrado == False:
                nova_lista_gen = lista()
                nova_lista_gen.insere_fim(chave(posicao_reg)) #ao invés de insere_fim(chave(int(id))). Seu eu fizer isso, a lista encadeada vira
                list_encadeadas_gen.append([genero, nova_lista_gen])
                list_chave_gen.append([genero, posicao_reg])

            #parte da criação dos índices de publicadora
            publicadora_encontrada = False
            m = 0
            while m < len(list_encadeadas_pub) and publicadora_encontrada == False:
                if publicadora == list_encadeadas_pub[m][0]:
                    list_encadeadas_pub[m][1].insere_fim(chave(posicao_reg))
                    publicadora_encontrada = True
                m += 1
            if publicadora_encontrada == False:
                nova_lista_pub = lista()
                nova_lista_pub.insere_fim(chave(posicao_reg))
                list_encadeadas_pub.append([publicadora, nova_lista_pub])
                list_chave_pub.append([publicadora, posicao_reg])
                
            posicao_reg += 1
            tam_bytes = entrada.read(2)
            tam_int = int.from_bytes(tam_bytes, "little")

        #criação da lista invertida
        for a in range(posicao_reg):
            proximo_gen = -1
            proximo_pub = -1
            id_atual = list_chave_prim[a][0]
            b = 0
            c = 0
            while b < len(list_encadeadas_gen):
                no_atual_gen = list_encadeadas_gen[b][1].busca(a)
                if no_atual_gen and no_atual_gen.prox:
                    proximo_gen = no_atual_gen.prox.dado.valor #determina o nó onde se encontra o  registro na lista encadeada de gênero
                b += 1
            while c < len(list_encadeadas_pub):
                no_atual_pub = list_encadeadas_pub[c][1].busca(a) #determina o nó onde se encontra o  registro na lista encadeada de publicador
                if no_atual_pub and no_atual_pub.prox:
                    proximo_pub = no_atual_pub.prox.dado.valor
                c += 1
            lista_invertida.append([id_atual, proximo_gen, proximo_pub])
        
    list_chave_prim.sort()
    list_chave_gen.sort()
    list_chave_pub.sort()

    with open(nome_arq_prim, 'wb') as primario:
        for prim in list_chave_prim:
            elem_p = pack('2i', prim[0], prim[1])
            primario.write(elem_p)
    
    with open(nome_arq_gen, 'wb') as genero:
        for gen in list_chave_gen:
            gen_bytes = gen[0].encode()
            elem_g = pack('50si', gen_bytes, gen[1])
            genero.write(elem_g)

    with open(nome_arq_pub, 'wb') as publicadora:
        for pub in list_chave_pub:
            pub_bytes = pub[0].encode()
            elem_pb = pack('50si', pub_bytes, pub[1])
            publicadora.write(elem_pb)

    with open(nome_arq_list, 'wb') as lista_inv:
        for lst in lista_invertida:
            elem_lst = pack('3i', lst[0], lst[1], lst[2])
            lista_inv.write(elem_lst)           
                
def main():
    if len(argv) < 2:
        raise TypeError('Número incorreto de argumentos\nModo de uso: nome_arq_entrada nome_arq_saida')
    cria_lista(argv[1], 'primario.ind', 'genero.ind', 'publicadora.ind', 'listaInvertida.lst')



if __name__ == '__main__':
    main()
