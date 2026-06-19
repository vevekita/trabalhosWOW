# ALUNAS: Ana Julia Thibes, RA: 145108; Anna Lísia Yaguti, RA: 145093; Verônica Kitamura, RA: 145096
#---------------------------------------------------------------------------------------------------

RAM = [0] * 256

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

    'C': 0, # Carry
    'N': 0, # Negativo (0 ou 1)
    'Z': 0 # Zero (0 ou 1)
}

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

def executar_comando(instrucao: str): #LOAD A, 0x05
    partes = instrucao.replace(',', ' ').split()
    opcode = partes[0]

    if opcode == 'LOAD':
        # lógica
        pass
    elif opcode == 'ADD':
        # lógica
        pass
    elif opcode == 'SUB':
        # lógica
        pass
    # pode ter mais operações, só lembrei dessas de cabeça

def ciclo_instrucao():
    exibir_registradores()
    while True:
        indice_atual = REGS_ESPECIAIS['PC']
        # Condição de parada
        if indice_atual >= 256 or RAM[indice_atual] == 0 or RAM[indice_atual] == '':
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
