from sys import argv
from struct import pack, unpack, calcsize
from dataclasses import dataclass
import io
import os

# Variável Global
ORDEM: int = 5
NULO: int = -1
FORMATO_HEADER = 'i'
SIZEOF_HEADER = calcsize(FORMATO_HEADER)
MAX_CHAVES = ORDEM - 1
MAX_FILHOS = ORDEM
FORMATO_PAG = f'i {MAX_CHAVES*2}i {MAX_FILHOS}i'
SIZEOF_PAG = calcsize(FORMATO_PAG)
FORMATO_TAMREG = 'h' #um inteiro de 2 bytes
SIZEOF_TAMEG = calcsize(FORMATO_TAMREG)

@dataclass
class Pagina:
    def __init__(self) -> None:
        self.numChaves: int = 0
        self.chaves: list[tuple[int, int]] = [(NULO, NULO)] * (ORDEM-1)
        self.filhos: list[int] = [NULO] * ORDEM # Referência ao RRN das páginas filhas (esquerda e direita)

def buscaNaArvore(chave: tuple[int, int], rrn: int, btree: io.BufferedRandom):
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
            return buscaNaArvore(chave, pag.filhos[pos], btree)

def buscaNaPagina(chave: tuple[int, int], pag: Pagina) -> tuple[bool, int]:
    '''Realiza a busca de uma chave em determinada página.'''
    pos = 0
    while pos < pag.numChaves and chave > pag.chaves[pos]:
        pos += 1
    if pos < pag.numChaves and chave == pag.chaves[pos]:
        return True, pos
    else:
        return False, pos

def insereChave(chave: tuple[int, int], rrnAtual: int, btree: io.BufferedRandom):
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
        if pag.numChaves < MAX_CHAVES: #se existe espaço para inserir a chave
            insereChavePromo(chavePro, filhoDpro, pag)
            escrevePagina(rrnAtual, pag, btree)
            return NULO, NULO, False
        else: #se não existe espaço na página
            chavePro, filhoDpro, pag, novaPag = divide(chavePro, filhoDpro, pag, btree)
            escrevePagina(rrnAtual, pag, btree) #escreve a página atual no arquivo
            escrevePagina(filhoDpro, novaPag, btree) #escreve a nova página no arquivo
            return chavePro, filhoDpro, True
    
def insereNaArvore(chave: tuple[int, int], raiz: int, btree: io.BufferedRandom): # Adicionado parametro btree
    '''Função gerenciadora. Ela le as chaves a serem armazenadas na árvore-B e chama a função de insereChave()
    Se houver divisão na raiz atual, ela cria a página que será a nova raiz, inserindo a chave promovida e 
    atualizando o seus filhos, além do RRN da raiz'''
    
    chavePro, filhoDpro, promocao = insereChave(chave, raiz, btree) # Adicionado parametro btree
    if promocao:
        pNova = Pagina()
        pNova.chaves[0] = chavePro # nova chave raiz
        pNova.filhos[0] = raiz # filho esquerdo
        pNova.filhos[1] = filhoDpro # filho direito
        pNova.numChaves += 1

        # Novo RRN para escrever na página raiz
        nRaizRrn = novoRRN(btree)
        escrevePagina(nRaizRrn, pNova, btree)

        # Atualização do cabeçalho do arquivo com o RRN da raiz nova
        btree.seek(0)
        btree.write(pack(FORMATO_HEADER, nRaizRrn))
        return nRaizRrn
    
    return raiz

#funções auxiliares da função *insere*
def ler_pagina(rrn: int, btree: io.BufferedRandom) -> Pagina:
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
        pag.chaves[indice] = (id_atual, offset_atual) #colocando os valores das chaves na página no formato certo
        i += 2 #pula de 2 em 2 pois cada elemento vai ser uma tupla
    for n in range(MAX_FILHOS): #colocando os valores dos ids das páginas filhas na página atual
        pag.filhos[n] = list_filhos[n] #substitui valor vazio inicialmente definido pela criação do objeto Página em um indice i por um elemento da lista de filhos remetente ao mesmo índice

    return pag

def escrevePagina(rrn: int, pag: Pagina, btree: io.BufferedRandom):
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
    empurrandos os elementos maiores para a direita para abrir espaço à chave a ser inserida em seu 
    devido lugar.'''
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
    '''Divide uma página em duas páginas(uma delas sendo a página em si). 
    Ela lida com páginas com  overflow (estouradas)'''
    insereChavePromo(chave, filhoD, pag) #insee temporarioamente (a página está com overflow agora)
    meio = ORDEM // 2
    chavePro = pag.chaves[meio]
    filhoDpro = novoRRN(btree)

    pAtual = Pagina()
    pNova = Pagina()
    novaQtdChaves: int = 0
    #Recebimento de valores de pag no pAtual
    for n in range(meio):
        pAtual.chaves[n] = pag.chaves[n]#atribui as chaves da primeira metade às chaves do pAtual
        pAtual.filhos[n] = pag.filhos[n]#atribui os filhos da primeira metade aos filhos do pAtual
    pAtual.filhos[meio] = pag.filhos[meio] #antribui o filho à mais direita, que estava fora do for
    pAtual.numChaves = meio

    tam_novaPag: int = pag.numChaves - meio - 1 #tamanho da nova página criada
    #Recebimento de valores de pag na pNova
    for m in range(tam_novaPag):
        pNova.chaves[m] = pag.chaves[meio + 1 + m]
        pNova.filhos[m] = pag.filhos[meio + 1 + m]
        novaQtdChaves += 1
    pNova.filhos[novaQtdChaves] = pag.filhos[pag.numChaves]
    pNova.numChaves = novaQtdChaves

    return chavePro, filhoDpro, pAtual, pNova

def novoRRN(btree: io.BufferedRandom) -> int:
    '''Função auxiliar que encontra o RRN de uma nova página adicionada na árvore-B'''
    btree.seek(0, os.SEEK_END)
    offset = btree.tell()
    return (offset - SIZEOF_HEADER) // SIZEOF_PAG

def principal():
    '''Função com caráter gerenciador de arquivos, ela é responsável por abrir ou 
    criar o arquivo da árvore-B e chamar a inserção'''  
    try:
        arqArvb = open('btree.dat', 'r+b')
        bytes_raiz = arqArvb.read(SIZEOF_HEADER)
        raiz = unpack(FORMATO_HEADER, bytes_raiz)[0]
    except FileNotFoundError:
        arqArvb = open('btree.dat', 'w+b')
        raiz = 0
        bytes_raiz = pack(FORMATO_HEADER, raiz)
        arqArvb.write(bytes_raiz)
        pag = Pagina()
        escrevePagina(raiz, pag, arqArvb)
        
    with open('games.dat', 'rb') as entrada: #processo de construir os índices
        offset = 0
        tam_bytes = entrada.read(SIZEOF_TAMEG) #lê o indicador de tamanho de um registro de jogo
        tam_int = unpack(FORMATO_TAMREG, tam_bytes)[0]
        while tam_int > 0:
            reg_bytes = entrada.read(tam_int)
            reg_str = reg_bytes.decode()
            campos = reg_str.split('|')
            id = int(campos[0])
            chave_reg = (id, offset)
            raiz = insereNaArvore(chave_reg, raiz, arqArvb) #colocando a chave (no formato de tupla) na árvore-B
            offset = entrada.tell()
            tam_bytes = entrada.read(SIZEOF_TAMEG)
            tam_int = unpack(FORMATO_TAMREG, tam_bytes)[0]
    raiz_bytes = pack(SIZEOF_HEADER, raiz)
    arqArvb.seek(0) #vai para o começo do aquivo da arvore-B 
    arqArvb.write(raiz_bytes) #escreve a raiz no cabeçario
    arqArvb.close()

def executa_operacoes(arquivo_operacoes: str):
    try:
        open('games.dat', 'rb').close()
        open('btree.dat', 'rb').close()
    except FileNotFoundError as e:
        print(f'Erro: {e}')

    with open('games.dat', 'r+b') as arq_jogos, open('btree.dat', 'r+b') as arq_btree, open(arquivo_operacoes, 'r') as arq_operacoes:
        arq_btree.seek(0)
        raiz = unpack(FORMATO_HEADER, arq_btree.read(SIZEOF_HEADER))[0]
        for linha in arq_operacoes:
            linha = linha.strip()
            partes = linha.split(' ', 1)
            operacao = partes[0]
            argumento = partes[1]

            if operacao == 'b':
                id_buscado = int(argumento)
                print(f'Busca pelo registro de chave "{id_buscado}"')
                achou, rrn_pag, pos = buscaNaArvore((id_buscado, 0), raiz, arq_btree)

                if achou:
                    pag = ler_pagina(rrn_pag, arq_btree)
                    offset_registro = pag.chaves[pos][1]

                    arq_jogos.seek(offset_registro)
                    tam_bytes = arq_jogos.read(2)
                    tam_int = int.from_bytes(tam_bytes,'little')
                    registro = arq_jogos.read(tam_int).decode()

                    print(f'{id_buscado} | {registro} ({tam_int} bytes - offset {offset_registro})')
                else:
                    print(f'Erro: chave {id_buscado} não encontrada')
                print()

            elif operacao == 'i':
                campos = argumento.split(sep='|')
                id_insercao = int(campos[0])
                print(f'Inserção do registro de chave "{id_insercao}"')
                achou, _, _ = buscaNaArvore((id_insercao, 0), raiz, arq_btree)
                if achou:
                    print(f'Erro: chave "{id_insercao}" duplicada')
                    print()
                else:
                    arq_jogos.seek(0, os.SEEK_END)
                    offset_novo_registro = arq_jogos.tell()
                    registro_bytes = argumento.encode()
                    tam_registro = len(registro_bytes)

                    arq_jogos.write(tam_registro.to_bytes(2, 'little'))
                    arq_jogos.write(registro_bytes)

                    raiz = insereNaArvore((id_insercao, offset_novo_registro), raiz, arq_btree)

                    print(f'{argumento} ({tam_registro} bytes - offset {offset_novo_registro})')
                    print()
    print(f'As operações do arquivo "{arquivo_operacoes}" foram executadas com sucesso!')

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
        principal()
    elif flag == "-e":
        if len(argv) < 3:
            raise TypeError('Número incorreto de argumentos\nModo de uso: python programa.py -e arquivo_operacoes')
        executa_operacoes(argv[2])
    elif flag == "-p":
        imprime()

if __name__ == '__main__':
    main
