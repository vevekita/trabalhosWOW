# ALUNAS: Ana Julia Thibes, RA: 145108; Anna Lísia Yaguti, RA: 145093; Verônica Kitamura, RA: 145096
# --------------------------------------------------------------------------------------------------

RAM = [0] * 256

REGS_GERAIS = {
    'A': 0,
    'B': 0
}

REGS_ESPECIAIS ={
    'PC': 0x00, # Program Counter (ponteiro de instruções)
    'IR': "", # Instruction Register (guarda a instrução atual)
    'MAR': 0x00, # Memory Adress Register
    'MBR': None, # Memory Buffer Register
    'AC': 0, # Acumulador
    'M': 0, # Multiplicador
    'R': 0, # Resto da divisão

    'C': 0, # Carry
    'N': 0, # Negativo (0 ou 1)
    'Z': 0 # Zero (0 ou 1)
}

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
    while True:
        if RAM[REGS_ESPECIAIS['PC']] == 0 or RAM[REGS_ESPECIAIS['PC']] == '':
            print('Execução finalizada ou fim da memória alcançado!')
            break

        # não terminei hehe
