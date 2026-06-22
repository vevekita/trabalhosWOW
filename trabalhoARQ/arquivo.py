# ALUNAS: Ana Julia Thibes, RA: 145108; Anna Lísia Yaguti, RA: 145093; Verônica Kitamura, RA: 145096
#---------------------------------------------------------------------------------------------------

RAM = [int, ''] * 256 # Armazena valor inteiro ou instrução (String)

REGS_GERAIS = {
    'A': 0,
    'B': 0
}

REGS_ESPECIAIS ={
    'PC': 0, # Program Counter (ponteiro de instruções)
    'IR': "", # Instruction Register (guarda a instrução atual)
    'MAR': 0, # Memory Adress Register
    'MBR': None, # Memory Buffer Register
    'AC': 0, # Acumulador
    'M': 0, # Multiplicador
    'R': 0, # Resto da divisão

    'C': 0, # Carry (0 ou 1)
    'N': 0, # Negativo 0 (positivo) e 1 (negativo) 
    'Z': 0 # Zero (diferente de 0) ou 1 (zero)
}

def carregar_arquivo(nome_arquivo):
    '''Lê o arquivo de entrada .txt e preenche a lista RAM com os dados e instruções.
    Também define o valor inicial do PC.'''

    global RAM, REGS_ESPECIAIS
    
    try:
        with open(nome_arquivo, 'r') as arquivo:
            linhas = arquivo.readlines()
    except FileNotFoundError:
        print(f"Erro: O arquivo '{nome_arquivo}' não foi encontrado.")
        return False
    
    modo = None
    ponteiro_dados = 0
    ponteiro_instr = 0 # Será definido pelo arquivo (ex: 0xA0)

    for linha in linhas:
        linha_limpa = linha.strip()

        if not linha_limpa:
            continue
        if linha_limpa[0] == '#':
            if linha_limpa[0:6] == '#Dados':
                modo = 'Dados'
            elif 'Endereço inicial' in linha_limpa:
                modo = 'ENDERECO INICIAL'
            
            continue
        if modo == 'DADOS':
            parte_util = linha_limpa.split('#')[0].strip()
            if parte_util:
                RAM[ponteiro_dados] = int(parte_util)
                ponteiro_dados += 1
                
        elif modo == "ENDERECO_INICIAL":
            endereco_inteiro = int(linha_limpa, 16)
            ponteiro_instr = endereco_inteiro
            REGS_ESPECIAIS['PC'] = endereco_inteiro
            modo = "INSTRUCOES"
            
        elif modo == "INSTRUCOES":
            parte_util = linha_limpa.split('#')[0].strip()
            if parte_util:
                RAM[ponteiro_instr] = int(parte_util)
                ponteiro_instr += 1

def exibir_registradores():
    # Função para exibir o estado atual dos registradores na tela inicial do usuário
    print('ESTADO ATUAL DOS REGISTRADORES')
    print(f"Gerais   -> A: {REGS_GERAIS['A']} | B: {REGS_GERAIS['B']}")
    print(f"Controle -> PC: 0x{REGS_ESPECIAIS['PC']:02X} | IR: '{REGS_ESPECIAIS['IR']}'")
    print(f"Memória  -> MAR: 0x{REGS_ESPECIAIS['MAR']:02X} | MBR: {REGS_ESPECIAIS['MBR']}")
    print(f"ULA      -> AC: {REGS_ESPECIAIS['AC']} | M: {REGS_ESPECIAIS['M']} | R: {REGS_ESPECIAIS['R']}")
    print(f"Flags    -> C: {REGS_ESPECIAIS['C']} | N: {REGS_ESPECIAIS['N']} | Z: {REGS_ESPECIAIS['Z']}")
    print()

    input('Pressione <ENTER> para avançar para o próximo ciclo...')

def executar_comando(instrucao: str):
    '''Decodifica a string da instrução e altera os registradores ou a RAM.'''
    
    global RAM, REGS_GERAIS, REGS_ESPECIAIS

    partes = instrucao.replace(',', ' ').split()
    opcode = partes[0]

    if opcode == 'LOAD':
        reg_destino = partes[1]
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        
        REGS_GERAIS[reg_destino] = RAM[indice_ram]
        print(f"[ULA] Sucesso: Registrador {reg_destino} recebeu o valor {RAM[indice_ram]}")

    elif opcode == 'ADD':
        reg_atual = partes[1]
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        resultado = REGS_GERAIS[reg_atual] + valor_ram
        
        REGS_ESPECIAIS['AC'] = resultado

        REGS_GERAIS[reg_atual] = resultado
        
        REGS_ESPECIAIS['Z'] = 1 if resultado == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if resultado < 0 else 0
        
        print(f"[ULA] Sucesso: Somado {valor_ram} ao Registrador {reg_atual}. AC = {resultado}")
    
    elif opcode == 'MOV':
        reg_destino = partes[1]
        reg_origem = partes[2]

        REGS_GERAIS[reg_destino] = REGS_GERAIS[reg_origem]
        print(f"[ULA] MOV: Registrador {reg_destino} recebeu o valor de {reg_origem} ({REGS_GERAIS[reg_origem]})")
    
    elif opcode == 'MULT':
        reg_atual = partes[1]
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        resultado = REGS_GERAIS[reg_atual] * valor_ram
        
        REGS_ESPECIAIS['M'] = resultado
        REGS_ESPECIAIS['AC'] = resultado
        REGS_GERAIS[reg_atual] = resultado
        
        REGS_ESPECIAIS['Z'] = 1 if resultado == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if resultado < 0 else 0
        
        print(f"[ULA] MULT: Multiplicado {reg_atual} por RAM[{endereco_hex}] ({valor_ram}). M/AC = {resultado}")
    
    elif opcode == 'STORE':
        reg_origem = partes[1]
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
   
        RAM[indice_ram] = REGS_GERAIS[reg_origem]
        print(f"[ULA] Sucesso: RAM[{endereco_hex}] agora guarda o valor {REGS_GERAIS[reg_origem]}")

    elif opcode == 'SUB':
        reg_atual = partes[1]
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        resultado = REGS_GERAIS[reg_atual] - valor_ram
        REGS_ESPECIAIS['AC'] = resultado
        REGS_GERAIS[reg_atual] = resultado
        
        REGS_ESPECIAIS['Z'] = 1 if resultado == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if resultado < 0 else 0
        print(f"[ULA] SUB: Subtraído {valor_ram} do Registrador {reg_atual}. AC = {resultado}")

    elif opcode == 'DIV':
        reg_atual = partes[1]
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        if valor_ram == 0:
            print("[Erro ULA] Divisão por zero!")
            return
            
        quociente = REGS_GERAIS[reg_atual] // valor_ram
        resto = REGS_GERAIS[reg_atual] % valor_ram
        
        REGS_ESPECIAIS['AC'] = quociente
        REGS_ESPECIAIS['R'] = resto
        REGS_GERAIS[reg_atual] = quociente
        
        REGS_ESPECIAIS['Z'] = 1 if quociente == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if quociente < 0 else 0
        print(f"[ULA] DIV: {reg_atual} / {valor_ram}. AC (Quociente) = {quociente}, R (Resto) = {resto}")

    elif opcode == 'JUMP':
        endereco_hex = partes[1]
        indice_destino = int(endereco_hex, 16)
        
        REGS_ESPECIAIS['PC'] = indice_destino
        print(f"[Controle] JUMP: Fluxo desviado incondicionalmente para 0x{indice_destino:02X}")

    elif opcode == "JUMP+":
        endereco_hex = partes[1]
        indice_destino = int(endereco_hex, 16)
        
        if REGS_ESPECIAIS['AC'] >= 0:
            REGS_ESPECIAIS['PC'] = indice_destino
            print(f"[Controle] JUMP+: AC é {REGS_ESPECIAIS['AC']} (positivo). Desviando para 0x{indice_destino:02X}")
        else:
            print(f"[Controle] JUMP+: AC é {REGS_ESPECIAIS['AC']} (negativo). Desvio NÃO realizado.")

def ciclo_instrucao():
    exibir_registradores()
    while True:
        indice_atual = REGS_ESPECIAIS['PC']
        # Condição de parada
        if indice_atual >= 256 or RAM[indice_atual] in (None, 0, ''):
            print('Execução finalizada ou fim da memória alcançado!')
            break

        # MAR <- PC
        REGS_ESPECIAIS['MAR'] = REGS_ESPECIAIS['PC']
        # MBR <- RAM[MAR]
        REGS_ESPECIAIS['MBR'] = RAM[REGS_ESPECIAIS['MAR']]
        # IR <- MBR
        REGS_ESPECIAIS['IR'] = REGS_ESPECIAIS['MBR']
        # PC <- PC + 1
        REGS_ESPECIAIS['PC'] += 1
        # Executar instrução
        comando = REGS_ESPECIAIS['IR']

        print(f"Executando comando {comando}...")
        executar_comando(comando)

        exibir_registradores()
