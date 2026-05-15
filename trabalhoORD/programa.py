from sys import argv, exit
from struct import pack, unpack, calcsize
import io
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
    list_chave_prim:list[tuple] = [] #lista da chave primária
    list_chave_gen:list[tuple[str, int]] = []  #lista da chave secundária: gênero
    list_chave_pub:list[tuple[str, int]] = [] #lista da chave secundária: publicadora
    list_encadeadas_gen: list[tuple[str, lista]] = [] #lista contendo indentificador "genero" e lista encadeada com as posições dos jogos associados
    list_encadeadas_pub: list[tuple[str, lista]] = [] #lista contendo indentificador "genero" e lista encadeada com as posições dos jogos associados
    lista_invertida:list[list] = [] #lista invertida
    posicao_reg = 0
    with open('games.dat', 'rb') as entrada:
        tam_bytes = entrada.read(2)
        tam_int = unpack('h', tam_bytes)[0]
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

    with open('primario.ind', 'wb') as primario:
        for prim in list_chave_prim:
            elem_p = pack('2i', prim[0], prim[1])
            primario.write(elem_p)
    
    with open('genero.ind', 'wb') as genero:
        for gen in list_chave_gen:
            gen_bytes = gen[0].encode()
            elem_g = pack('50si', gen_bytes, gen[1])
            genero.write(elem_g)

    with open('publicadora.ind', 'wb') as publicadora:
        for pub in list_chave_pub:
            pub_bytes = pub[0].encode()
            elem_pb = pack('50si', pub_bytes, pub[1])
            publicadora.write(elem_pb)

    with open('listaInvertida.lst', 'wb') as lista_inv:
        for lst in lista_invertida:
            elem_lst = pack('3i', lst[0], lst[1], lst[2])
            lista_inv.write(elem_lst)    

def carregar_indices() -> tuple[list[tuple[int,int]], list[tuple[str, int]], list[tuple[str,int]], list[tuple[int]]]:
    # verifica se todos os arquivos existem
    try:
        open('primario.ind', 'rb').close()
        open('genero.ind', 'rb').close()
        open('publicadora.ind', 'rb').close()
        open('listaInvertida.lst', 'rb').close()
        open('games.dat', 'rb').close()
    except FileNotFoundError as e:
        print(f'Erro: {e}')

    list_chave_prim: list[tuple[int, int]] = []
    list_chave_gen: list[tuple[str, int]] = []
    list_chave_pub: list[tuple[str, int]] = []
    lista_invertida: list[tuple[int]] = [] #acho que aqui tá errado rsrs
    with open('primario.ind', 'rb') as primario:
        reg = primario.read(calcsize('2i'))
        while reg:
            tupla = unpack('2i', reg)
            list_chave_prim.append(tupla)
            reg = primario.read(calcsize('2i'))

    with open('genero.ind', 'rb') as genero:
        reg = genero.read(calcsize('50si'))
        while reg:
            tupla = unpack('50si', reg)
            gen: bytes = tupla[0]
            pos: int = tupla[1]
            chave = gen.decode().split('\x00')[0] #retira os bytes nulos
            list_chave_gen.append((chave, pos))
            reg = genero.read(calcsize('50si'))

    with open('publicadora.ind', 'rb') as publicadora:
        reg = publicadora.read(calcsize('50si'))
        while reg:
            tupla = unpack('50si', reg)
            gen: bytes = tupla[0]
            pos: int = tupla[1]
            chave = gen.decode().split('\x00')[0] #retira os bytes nulos
            list_chave_pub.append((chave, pos))
            reg = publicadora.read(calcsize('50si'))
    
    with open('listaInvertida.lst', 'rb') as lista_inv:
        reg = lista_inv.read(calcsize('3i'))
        while reg:
            tupla = unpack('3i', reg)
            lista_invertida.append(tupla)
            reg = lista_inv.read(calcsize('3i'))

    return list_chave_prim, list_chave_gen, list_chave_pub, lista_invertida
        
print(carregar_indices()) 

def busca_prim(id: int, list_prim: list[tuple[int, int]] ) -> str: 
    ''' O arquivo de índices primários é aberto e é percorrido em procura de
    determinado índice. A partir do byte-offset encontrado no índice, 
    fazemos o acesso direto ao arquivo *games.dat* para o registro do *id*.'''

    n = 0
    encontrado = False
    offset = 0
    registro = ''
    while n < len(list_prim) and encontrado == False: #aqui não tá errado fazer assim, mas como a lista está ordenada, usando bb seria mais eficiente
        if list_prim[n][0] == id: #Se o primeiro elemento do índice primário (posição) for igual ao id que está sendo usado de chave:
            offset = list_prim[n][1]
            encontrado = True #pra sair do while assim que encontrar, desse jeito ele funciona igual um for sem break
        n += 1

    if not encontrado:
        print('Registro não encontrado!')
        return ''
    
    with open('games.dat', 'rb') as arq:
        arq.seek(offset)
        tam_bytes = arq.read(2)
        tam = int.from_bytes(tam_bytes, 'little')
        reg = arq.read(tam).decode() #transforma o registro todo em string
        registro = reg #LILI: precisamos fazer essa igualdade? siii

    return registro

def busca_genero(chave:str, lista_indice_genero: list[tuple[str, int]], lista_invertida: list[tuple[int, int, int]]):
    pass

def busca_publicadora(chave:str, lista_indice_pub: list[tuple[str, int]], lista_invertida: list[tuple[int, int, int]]):
    pass

def insercao(registro: str):
    pass

def remocao():
    pass

def executar_operacoes(arq_operacoes: str):
    indices = carregar_indices()
    list_prim = indices[0]
    lista_indice_genero = indices[1]
    lista_indice_pub = indices[2]
    lista_invertida = indices[3]

    # if _ == 'bp':
    #     busca_prim(int(_), list_prim)
    # elif _ == 'bs1':
    #     busca_genero(_, lista_indice_genero, lista_invertida)
    # elif _ == 'bs2':
    #     busca_publicadora(_, lista_indice_pub, lista_invertida)
    # elif _ == 'i':
    #     insercao()
    # elif _ == 'r':
    #     remocao()

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
