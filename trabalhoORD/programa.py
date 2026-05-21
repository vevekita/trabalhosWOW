from sys import argv, exit
from struct import pack, unpack, calcsize
import io
import os
from dataclasses import dataclass
from copy import deepcopy

# CONSTANTES
FORMATO_CHAVE_SEC = '50si'   # string/bytes de 50 caracteres e um inteiro de 4 bytes
FORMATO_ELEMLISTA = '2i'    # dois inteiros de 4 bytes
FORMATO_TAMREG = 'h'        # um inteiro de 2 bytes
FORMATO_LISTA_INV = '3i'    # três inteiros de 4 bytes
SIZEOF_CHAVE_SEC = calcsize(FORMATO_CHAVE_SEC)      # 50 bytes
SIZEOF_ELEMLISTA = calcsize(FORMATO_ELEMLISTA)      # 8 bytes
SIZEOF_TAMREG = calcsize(FORMATO_TAMREG)            # 2 bytes
SIZEOF_LISTA_INV = calcsize(FORMATO_LISTA_INV)

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
    list_chave_prim:list[tuple[int, int]] = [] #lista da chave primária
    list_chave_gen:list[tuple[str, int]] = []  #lista da chave secundária: gênero
    list_chave_pub:list[tuple[str, int]] = [] #lista da chave secundária: publicadora
    list_encadeadas_gen: list[tuple[str, lista]] = [] #lista contendo indentificador "genero" e lista encadeada com as posições dos jogos associados
    list_encadeadas_pub: list[tuple[str, lista]] = [] #lista contendo indentificador "genero" e lista encadeada com as posições dos jogos associados
    lista_invertida:list[list] = [] #lista invertida
    lista_aux_chaves:list[tuple[int, str, str]] = []
    with open('games.dat', 'rb') as entrada:
        offset = 0
        tam_bytes = entrada.read(FORMATO_TAMREG)
        tam_int = int.from_bytes(tam_bytes, 'little')
        while tam_int > 0:
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campos = reg_str.split('|')
            id = int(campos[0])
            genero = campos[3]
            publicadora = campos[4]
            list_chave_prim.append((id, offset)) #adicona na lista de índeces secundários
            gen_prox =-1
            pub_prox =-1
            lista_invertida.append([id, gen_prox, pub_prox]) #adiciona na da lista invertida
            lista_aux_chaves.append([id, genero, publicadora]) #lista auxiliar para armazenar os gêneros e publicadoras de cada id de jogo
            offset = entrada.tell()
            tam_bytes = entrada.read(FORMATO_TAMREG)
            tam_int = int.from_bytes(tam_bytes, "little")
        list_chave_prim.sort() 
        lista_aux_chaves.sort()
        
        #criação dos índices secundários
        for n in range(len(lista_aux_chaves)):
            genero_encontrado = False
            publicadora_encontrada = False
            id = lista_aux_chaves[n][0]
            genero = lista_aux_chaves[n][1]
            publicadora = lista_aux_chaves[n][2]
            p = 0
            while p < len(lista_invertida): #procura posição
                if lista_invertida[p][0] == id:
                    pos = p
                p += 1

            m = 0
            while m < len(list_encadeadas_gen) and genero_encontrado == False: #parte da criação dos índices de gênero
                chave_sec = list_encadeadas_gen[m][0]
                if chave_sec == genero:
                    list_encadeadas_gen[m][1].insere_fim(chave(pos))
                    genero_encontrado = True
                m += 1
            if genero_encontrado == False: #se o gênero em questão ainda não existe na lista_endaceadas_gen:
                nova_lista = lista()
                nova_lista.insere_fim(chave(pos)) 
                list_encadeadas_gen.append([genero, nova_lista])
                list_chave_gen.append([genero, pos])
                
            m = 0
            while m < len(list_encadeadas_pub) and publicadora_encontrada == False: #parte da criação dos índices de publicadora
                chave_sec = list_encadeadas_pub[m][0]
                if chave_sec == publicadora: #se a publicadora em questão ainda não existena lista_encadeadas_pub:
                    list_encadeadas_pub[m][1].insere_fim(chave(pos))
                    publicadora_encontrada = True
                m += 1
            if publicadora_encontrada == False:
                nova_lista = lista()
                nova_lista.insere_fim(chave(pos))
                list_encadeadas_pub.append([publicadora, nova_lista])
                list_chave_pub.append([publicadora, pos])

        #atualização da lista invertida
        for pos in range(len(lista_invertida)):
            id = lista_invertida[pos][0]
            a = 0
            gen_prox = -1
            
            while a < len(list_encadeadas_gen): #acessa os gêneros com suas suas listas encadeadas
                no_atual = list_encadeadas_gen[a][1].busca(pos)
                if no_atual and no_atual.prox:
                    gen_prox = no_atual.prox.dado.valor
                a += 1

            pub_prox = -1
            b = 0
            while b < len(list_encadeadas_pub): #acessa as publicadoras com suas listas encadeadas
                no_atual = list_encadeadas_pub[b][1].busca(pos)
                if no_atual and no_atual.prox:
                    pub_prox = no_atual.prox.dado.valor
                b += 1
            
            lista_invertida[pos] = [id, gen_prox, pub_prox] #atualiza os elementos da posição da lista invertida

    list_chave_gen.sort()
    list_chave_pub.sort()

    with open('primario.ind', 'wb') as primario:
        for prim in list_chave_prim:
            elem_p = pack(FORMATO_ELEMLISTA, prim[0], prim[1])
            primario.write(elem_p)
    
    with open('genero.ind', 'wb') as genero:
        for gen in list_chave_gen:
            gen_bytes = gen[0].encode()
            elem_g = pack(FORMATO_CHAVE_SEC, gen_bytes, gen[1])
            genero.write(elem_g)

    with open('publicadora.ind', 'wb') as publicadora:
        for pub in list_chave_pub:
            pub_bytes = pub[0].encode()
            elem_pb = pack(FORMATO_CHAVE_SEC, pub_bytes, pub[1])
            publicadora.write(elem_pb)

    with open('listaInvertida.lst', 'wb') as lista_inv:
        for lst in lista_invertida:
            elem_lst = pack(FORMATO_LISTA_INV, lst[0], lst[1], lst[2])
            lista_inv.write(elem_lst)    

def carregar_indices() -> tuple[list[tuple[int,int]], list[tuple[str, int]], list[tuple[str,int]], list[list[int]]]:
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
    lista_invertida: list[list[int]] = []
    with open('primario.ind', 'rb') as primario:
        reg = primario.read(SIZEOF_ELEMLISTA)
        while reg: #enquanto reg existir:
            tupla = unpack(FORMATO_ELEMLISTA, reg)
            list_chave_prim.append(tupla) 
            reg = primario.read(SIZEOF_ELEMLISTA)

    with open('genero.ind', 'rb') as genero:
        reg = genero.read(SIZEOF_CHAVE_SEC)
        while reg:
            tupla = unpack(FORMATO_CHAVE_SEC, reg)
            gen: bytes = tupla[0]
            pos: int = tupla[1]
            chave = gen.decode().split('\x00')[0] #retira os bytes nulos
            list_chave_gen.append((chave, pos))
            reg = genero.read(SIZEOF_CHAVE_SEC)

    with open('publicadora.ind', 'rb') as publicadora:
        reg = publicadora.read(SIZEOF_CHAVE_SEC)
        while reg:
            tupla = unpack(FORMATO_CHAVE_SEC, reg)
            gen: bytes = tupla[0]
            pos: int = tupla[1]
            chave = gen.decode().split('\x00')[0] #retira os bytes nulos
            list_chave_pub.append((chave, pos))
            reg = publicadora.read(SIZEOF_CHAVE_SEC)
    
    with open('listaInvertida.lst', 'rb') as lista_inv:
        reg = lista_inv.read(SIZEOF_LISTA_INV)
        while reg:
            tupla = unpack(FORMATO_LISTA_INV, reg)
            lista_invertida.append(list(tupla))
            reg = lista_inv.read(SIZEOF_LISTA_INV)

    return list_chave_prim, list_chave_gen, list_chave_pub, lista_invertida 

def busca_binaria(x: int, lista: list) -> int:
    esq = 0
    dir = len(lista) - 1
    while esq <= dir:
        meio = (esq + dir) // 2
        id, offset = lista[meio]
        if id == x:
            return offset
        elif id < x:
            esq = meio + 1
        else: 
            dir = meio - 1
    return -1
  
def busca_binaria(x: int, lista: list) -> int:
    '''Função auxiliar para realizar a busca binária de uma lista.'''
    esq = 0
    dir = len(lista) - 1
    while esq <= dir:
        meio = (esq + dir) // 2
        id, offset = lista[meio]
        if id == x:
            return offset 
        elif id < x:
            esq = meio + 1
        else: 
            dir = meio - 1
    return -1

def busca_prim(id: int, list_prim: list[tuple[int, int]], arq: io.BufferedRandom ) -> str: 
    ''' O arquivo de índices primários é aberto e é percorrido em procura de
    determinado índice. A partir do byte-offset encontrado no índice, 
    fazemos o acesso direto ao arquivo *games.dat* para o registro do *id*.'''

    offset = busca_binaria(id, list_prim)

    if offset == -1:
        return 'Registro não encontrado!'
    
    arq.seek(offset)
    tam_bytes = arq.read(SIZEOF_TAMREG)
    tam = int.from_bytes(tam_bytes, 'little')
    reg = arq.read(tam).decode() #transforma o registro todo em string
    registro = reg

    return registro

def busca_genero(chave:str, list_prim: list[tuple[int, int]], lista_indice_genero: list[tuple[str, int]], lista_invertida: list[list[int]], arq: io.BufferedRandom):
    '''Procura-se o gênero (chave) necessário na lista_indice_genero. Se encontrar, guarda o ID e passa pra lista_invertida.
    Verifica na lista_invertida se existe um próximo do mesmo gênero (diferente de -1), se existir vai pra posição em que está
    o próximo e guarda o ID. Assim, vai encontrando todos os próximos do mesmo gênero até que não tenha mais.
    Depois de encontrar todos os IDs que pertencem ao mesmo gênero, procura na list_prim o offset de cada um dos IDs e guarda.
    Depois abre o arquivo de registros e procura diretamente de acordo com o byte_offset para conseguir os registros completos e armazena.
    Após isso, faz print pra cada registro armazenado.'''

    n = 0
    encontrado = False
    lista_id: list[int] = []
    lista_offset: list[int] = []
    registros: list[str] = []

    while n < len(lista_indice_genero) and encontrado == False:
        if lista_indice_genero[n][0] == chave:
            posicao = lista_indice_genero[n][1]  # posição inicial na lista invertida
            while posicao != -1:
                lista_id.append(lista_invertida[posicao][0])
                posicao = lista_invertida[posicao][1]
            encontrado = True
        n += 1


    for id in lista_id:
        for i in range(len(list_prim)):
            if id == list_prim[i][0]:
                lista_offset.append(list_prim[i][1])

    for offset in lista_offset:
        arq.seek(offset)
        tam_bytes = arq.read(SIZEOF_TAMREG)
        tam = int.from_bytes(tam_bytes, 'little')
        reg = arq.read(tam).decode()
        registros.append(reg)
    print(f'Busca por registros do gênero "{chave}" ({len(registros)} registros)')
    for registro in registros:
        print(registro)

def busca_publicadora(chave:str, list_prim: list[tuple[int, int]], lista_indice_pub: list[tuple[str, int]], lista_invertida: list[list[int]], arq: io.BufferedRandom):
    '''Procura-se a publicadora (chave) necessária na lista_indice_pub. Se encontrar, guarda o ID e passa pra lista_invertida.
    Verifica na lista_invertida se existe um próximo da mesma publicadora (diferente de -1), se existir vai pra posição em que está
    o próximo e guarda o ID. Assim, vai encontrando todos os próximos da mesma publicadora até que não tenha mais.
    Depois de encontrar todos os IDs que pertencem  a mesma publicadora, procura na list_prim o offset de cada um dos IDs e guarda.
    Depois abre o arquivo de registros e procura diretamente de acordo com o byte_offset para conseguir os registros completos e armazena.
    Após isso, faz print pra cada registro armazenado.'''
    n = 0
    encontrado = False
    lista_id: list[int] = []
    lista_offset: list[int] = []
    registros: list[str] = []

    while n < len(lista_indice_pub) and encontrado == False:
        if lista_indice_pub[n][0] == chave:
            posicao = lista_indice_pub[n][1]  # posição inicial na lista invertida
            while posicao != -1:
                lista_id.append(lista_invertida[posicao][0])
                posicao = lista_invertida[posicao][2]
            encontrado = True
        n += 1
    for id in lista_id:
        for i in range(len(list_prim)):
            if id == list_prim[i][0]:
                 lista_offset.append(list_prim[i][1])
    for offset in lista_offset:
        arq.seek(offset)
        tam_bytes = arq.read(2)
        tam = int.from_bytes(tam_bytes, 'little')
        reg = arq.read(tam).decode()
        registros.append(reg)
    print(f'Busca por registros de publicadora "{chave}" ({len(registros)} registros)')
    for registro in registros:
        print(registro)

def insercao(registro: str, list_prim: list[tuple[int, int]], lista_indice_genero: list[tuple[str, int]], lista_indice_pub: list[tuple[str, int]], lista_invertida: list[list[int]], arq: io.BufferedRandom):
    campos = registro.split(sep='|')
    id = int(campos[0])
    genero = campos[3]
    publicadora = campos[4]

    if busca_binaria(id, list_prim) != -1:
        print('ID duplicado!')
        return ''
    else:
        reg_bytes = registro.encode('utf-8')
        tam_reg = len(reg_bytes)

        arq.seek(0, os.SEEK_END)
        offset_novo = arq.tell()
        arq.write(tam_reg.to_bytes(SIZEOF_TAMREG, 'little'))
        arq.write(reg_bytes)

        list_prim.append((id, offset_novo))
        list_prim.sort()

        info = [id, -1, -1]
        lista_invertida.append(info)
        posicao_id_inserido = len(lista_invertida) - 1

        #atualização da lista de gênero e invertida na parte de gênero
        n = 0
        genero_encontrado = False
        while n < len(lista_indice_genero) and genero_encontrado == False:
            if lista_indice_genero[n][0] == genero:
                genero_encontrado = True
            n += 1

        if genero_encontrado:
            indice_genero = n - 1
            posicao = lista_indice_genero[indice_genero][1]
            anterior = -1
            adicionado = False
            while posicao != -1 and not adicionado:
                if lista_invertida[posicao][0] < id:
                    anterior = posicao
                    posicao = lista_invertida[posicao][1]
                else:
                    if anterior == -1:
                        lista_indice_genero[indice_genero] = (genero, posicao_id_inserido)
                    else:
                        lista_invertida[anterior][1] = posicao_id_inserido
                    lista_invertida[posicao_id_inserido][1] = posicao
                    adicionado = True
            if not adicionado:
                if anterior == -1:
                    lista_indice_genero[indice_genero] = (genero, posicao_id_inserido)
                else:
                    lista_invertida[anterior][1] = posicao_id_inserido
                lista_invertida[posicao_id_inserido][1] = -1
        else:
            lista_indice_genero.append((genero, posicao_id_inserido))
            lista_indice_genero.sort()

        #atualização da lista de publicadora e invertida na parte de publicadora
        m = 0
        publicadora_encontrada = False
        while m < len(lista_indice_pub) and publicadora_encontrada == False:
            if lista_indice_pub[m][0] == publicadora:
                publicadora_encontrada = True
            m += 1

        if publicadora_encontrada:
            indice_publicadora = m - 1
            posicao = lista_indice_pub[indice_publicadora][1]
            anterior = -1
            adicionado = False
            while posicao != -1 and not adicionado:
                if lista_invertida[posicao][0] < id:
                    anterior = posicao
                    posicao = lista_invertida[posicao][2]
                else:
                    if anterior == -1:
                        lista_indice_pub[indice_publicadora] = (publicadora, posicao_id_inserido)
                    else:
                        lista_invertida[anterior][2] = posicao_id_inserido
                    lista_invertida[posicao_id_inserido][2] = posicao
                    adicionado = True
            if not adicionado:
                if anterior == -1:
                    lista_indice_pub[indice_publicadora] = (publicadora, posicao_id_inserido)
                else:
                    lista_invertida[anterior][2] = posicao_id_inserido
                lista_invertida[posicao_id_inserido][2] = -1
        else:
            lista_indice_pub.append((publicadora, posicao_id_inserido))
            lista_indice_pub.sort()

        print(f'Inserção do registro de chave "{id}" ({tam_reg} bytes)')


def remocao(chave: int, list_prim: list[tuple[int, int]], lista_invertida: list[list[int]], lista_indice_genero: list[tuple[str, int]], lista_indice_pub: list[tuple[str, int]], arq: io.BufferedRandom) -> bool:
    '''Realiza a remoção lógica de um registro a partir de uma determinada chave(ID) na função de execução de operações. Essa função atualiza os dados dos arquivos'''
    offset = busca_binaria(chave, list_prim)
    
    if offset == -1:
        print(f'Remoção do registro de chave "{chave}"')
        print('Registro não encontrado!')
        return False
    else:
        print(f'Remoção do registro de chave "{chave}" (offset = {offset})')
        arq.seek(offset + 2) #começo do registro (pulando o indicador de tamanho)
        indica_rem = '*'.encode()
        arq.write(indica_rem)

        #atualização dos arranjos
        pos_rem = -1
        n = 0
        while n < len(lista_invertida):
            if lista_invertida[n][0] == chave:
                pos_rem = n #encontra a posição do jogo a ser removido na lista invertida (logo, no arquivo geral também)
            n += 1

        removido = False
        m = 0
        while m < len(lista_indice_genero): #atualização em relação à chave secundária de gênero
            pos_atual = lista_indice_genero[m][1]
            prox_pos = lista_invertida[pos_atual][1]
            if pos_atual == pos_rem:
                lista_invertida[pos_atual][1] = -1 #jogo sendo o primeiro da lista de um gênero: desvincula o jogo da organização da chave secundária
                if prox_pos == -1: #se o registro a ser removido é o ÚNICO elemento de determinado gênero:
                    lista_indice_genero.pop(m) #remove o gênero para não ser lido na lista posteriormente
            else: #se o registro a ser removido NÃO é o primeiro (estando no meio ou final da lista invertida)
                while prox_pos> -1 and removido == False:
                    if prox_pos == pos_rem:
                        ant_pos = pos_atual
                        pos_atual = prox_pos
                        prox_pos = lista_invertida[pos_atual][1]
                        lista_invertida[ant_pos][1] = prox_pos
                        lista_invertida[pos_atual][1] = -1 #desvincula o índice do registro removido da lista de índices secundários de gênero
                        removido = True
                    pos_atual = prox_pos
                    prox_pos = lista_invertida[pos_atual][1]
            m += 1
        
        removido = False
        m = 0
        while m <len(lista_indice_pub): #atualização em relação à chave secundária de publicadora
            pos_atual = lista_indice_pub[m][1]
            prox_pos = lista_invertida[pos_atual][2]
            if pos_atual == pos_rem:
                lista_invertida[pos_atual][2] = -1
                if prox_pos == -1:
                    lista_indice_pub.pop(m)
            else:
                while prox_pos > -1 and removido == False:
                    if prox_pos == pos_rem:
                        ant_pos = pos_atual
                        pos_atual = prox_pos
                        prox_pos = lista_invertida[pos_atual][2]
                        lista_invertida[ant_pos][2] = prox_pos
                        lista_invertida[pos_atual][2] = -1
                        removido = True
                    pos_atual = prox_pos
                    prox_pos = lista_invertida[pos_atual][2]
            m += 1
        return True

def compactar():
    with open('games.dat', 'rb') as entrada, open('games_sem_frag.dat', 'wb') as saida:
        tam_bytes = entrada.read(2)
        while tam_bytes:
            tam = int.from_bytes(tam_bytes, 'little')
            verificacao = entrada.read(1)
            verificacao_str = verificacao.decode()
            if verificacao_str == '*':
                entrada.seek(tam - 1, 1) #pula o resto do registro
            else:
                restante = entrada.read(tam - 1)
                saida.write(tam_bytes)
                saida.write(verificacao + restante)
            tam_bytes = entrada.read(2)
            
    list_chave_prim = []
    with open('games_sem_fragmentacao.dat', 'rb') as arq:
        while True:
            offset = arq.tell()        
            tam_bytes = arq.read(2)
            if not tam_bytes:
                break
            tam = int.from_bytes(tam_bytes, 'little')
            reg = arq.read(tam).decode()
            campos = reg.split('|')
            id_jogo = int(campos[0])
            list_chave_prim.append((id_jogo, offset))

    list_chave_prim.sort()

    # salva o índice primário atualizado
    with open('primario.ind', 'wb') as saida:
        for (id_jogo, offset) in list_chave_prim:
            saida.write(pack('2i', id_jogo, offset))
    
def executar_operacoes(arq_operacoes: str):
    indices = carregar_indices()
    lista_prim = indices[0]
    lista_indice_genero = indices[1]
    lista_indice_pub = indices[2]
    lista_invertida = indices[3]

    with open('games.dat', 'r+b') as arq, open(arq_operacoes, 'r') as entrada:
        for linha in entrada:
            linha = linha.strip() #retira o \n no final da linha
            partes = linha.split(' ', 1) #vai separar somente do primeiro espaço vazio que aparecer
            operacao = partes[0]
            argumento = partes[1]

            if operacao == 'bp':
                print(f'Busca pelo registro de ID "{argumento}"')
                print(busca_prim(int(argumento), lista_prim, arq))
                print()
            elif operacao == 'bs1':
                busca_genero(argumento, lista_prim, lista_indice_genero, lista_invertida, arq)
                print()
            elif operacao == 'bs2':
                busca_publicadora(argumento, lista_prim, lista_indice_pub, lista_invertida, arq)
                print()
            elif operacao == 'i':
                insercao(argumento, lista_prim, lista_indice_genero, lista_indice_pub, lista_invertida, arq)

            elif operacao == 'r':
                remocao(int(argumento), lista_prim, lista_invertida, lista_indice_genero, lista_indice_pub, arq)

def main():
    if len(argv) < 2:
        raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py [ -b | -e arquivo_operacoes | -c')
    
    flag = argv[1]

    if flag == "-b":
        constroi_indices()
    elif flag == "-e":
        if len(argv) < 3:
            raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py -e arquivo_operacoes')
        executar_operacoes(argv[2])
    elif flag == "-c":
        # compactar()
        pass
if __name__ == '__main__':
    main()
