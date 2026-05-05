from sys import argv
from TAD_ListaEncadeada import *

def le_registro(nome_arq_entrada: str) -> list:
    '''
    Aqui ele vai ler um registro do arquivo de entrada e retornar esse registro em forma de lista.
    Ex.: registro=[id, nome, ano, genero, publicadora, plataforma]
    '''
    with open(nome_arq_entrada, 'rb') as entrada:
        tam_bytes = entrada.read(2)
        tam_int = int.from_bytes(tam_bytes, 'little')
        reg = entrada.read(tam_int)
        reg_str = reg.decode()
        campo = reg_str.split(sep='|')
    return campo

def constroi_indice_primario():
    arq_entrada = "games.dat"
    chave_indice_primario = []
    with open(arq_entrada, 'rb') as entrada:
        while True:
            offset = entrada.tell()
            tam_bytes = entrada.read(2)
            if not tam_bytes: #verificação para ver se o arquivo já acabou
                break
            tam_int = int.from_bytes(tam_bytes, 'little')
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campo = []
            campo = reg_str.split(sep='|')
            id = campo[0] 
            chave_indice_primario.append((int(id), offset))
            #até aqui ele constrói a lista de índice primário com (id, byte-offset)

def constroi_indice_secundario1():
    arq_entrada = "games.dat"
    chave_indice_secundario = []
    with open(arq_entrada, 'rb') as entrada:
        while True:
            tam_bytes = entrada.read(2)
            if not tam_bytes: #verificação para ver se o arquivo já acabou
                break
            tam_int = int.from_bytes(tam_bytes, 'little')
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campo = []
            campo = reg_str.split(sep='|')
            id = campo[0]
            genero = campo[3]

            encontrado = False
            a = 0
            while encontrado == False and a < len(chave_indice_secundario):
                if chave_indice_secundario[a][0] == genero:
                    chave_indice_secundario[a][1].insere_fim(chave(int(id)))
                    encontrado = True
                a += 1
            if encontrado == False:
                #essa parte pra se NÃO existir o gênero na chave_indice_secundario
                lista_id = lista()
                lista_id.insere_fim(chave(int(id)))
                chave_indice_secundario.append((genero, lista_id))
    return chave_indice_secundario

print(constroi_indice_secundario1())

def constroi_indice_secundario2():
    arq_entrada = "games.dat"
    chave_indice_secundario = []
    with open(arq_entrada, 'rb') as entrada:
        while True:
            tam_bytes = entrada.read(2)
            if not tam_bytes: #verificação para ver se o arquivo já acabou
                break
            tam_int = int.from_bytes(tam_bytes, 'little')
            reg = entrada.read(tam_int)
            reg_str = reg.decode()
            campo = []
            campo = reg_str.split(sep='|')
            id = campo[0]
            publicadora = campo[4]

            encontrado = False
            b = 0
            while encontrado == False and b < len(chave_indice_secundario):
                if chave_indice_secundario[b][0] == publicadora:
                    chave_indice_secundario[b][1].insere_fim(chave(int(id)))
                    encontrado = True
                b += 1
            if encontrado == False:
                #essa parte pra se NÃO existir a publicadora na chave_indice_secundario
                lista_id = lista()
                lista_id.insere_fim(chave(int(id)))
                chave_indice_secundario.append((publicadora, lista_id))
    return chave_indice_secundario

def busca_primaria(id, indice_primario):
    '''
    Procura pelo id no arquivo de índice primário por busca binária. 
    Se encontrar, pega o offset, acessa o arquivo games.dat, procura e retorna o registro.
    '''

#fragmentação é só a estática na parte de compactação
