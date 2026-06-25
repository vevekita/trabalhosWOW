# ALUNAS: Ana Julia Thibes, RA: 145108; Anna Lísia Yaguti, RA: 145093; Verônica Kitamura, RA: 145096
#---------------------------------------------------------------------------------------------------

RAM = [int, str] * 256 # Armazena valor inteiro ou instrução (String)


REGS_GERAIS = {
    'A': 0,
    'B': 0
}

REGS_ESPECIAIS ={
    'PC': 0, # Program Counter (ponteiro de instruções)
    'IR': '', # Instruction Register (guarda a instrução atual)
    'MAR': 0, # Memory Adress Register
    'MBR': None, # Memory Buffer Register
    'AC': 0, # Acumulador
    'M': 0, # Multiplicador
    'R': 0, # Resto da divisão

    'C': 0, # Carry (0 ou 1)
    'N': 0, # Negativo 0 (positivo) e 1 (negativo) 
    'Z': 0 # Zero (diferente de 0) ou 1 (zero)
}

def carregar_arquivo(nome_arquivo) -> bool:
    '''Lê o arquivo de entrada .txt e preenche a lista RAM com os dados e instruções.
    Também define o valor inicial do PC.'''

    global RAM, REGS_ESPECIAIS
    try:
        with open(nome_arquivo, 'r') as f:
            linhas = f.readlines()
            
        ponteiro_dados = 0
        ponteiro_instr = 160  # Padrão para 0xA0
        lendo_instrucoes = False

        for linha in linhas:
            linha_limpa = linha.split('#')[0].strip() # Remove espaços nas pontas e separa o comentário se houver
            
            if not linha_limpa: # Pula linhas vazias
                continue

            if linha_limpa.upper() == "0XA0":
                lendo_instrucoes = True
                REGS_ESPECIAIS['PC'] = 160
                continue # Pula para a próxima linha do arquivo (não salva o '0xA0' na RAM)

            # Armazenamento na RAM
            if not lendo_instrucoes:
                # Armazena na região de dados
                if linha_limpa[:2].upper() == "0X":
                    RAM[ponteiro_dados] = linha_limpa # Mantém como string se for endereço hexadecimal
                else:
                    RAM[ponteiro_dados] = int(linha_limpa) # Converte para inteiro se for dado numérico
                ponteiro_dados += 1
            else:
                # Armazena na região de instruções
                RAM[ponteiro_instr] = linha_limpa
                ponteiro_instr += 1
        return True
    
    except FileNotFoundError:
        print(f"Erro: Arquivo '{nome_arquivo}' não encontrado.")
        return False
    
def exibir_registradores():
    # Função para exibir o estado atual dos registradores na tela inicial do usuário
    print('ESTADO ATUAL DOS REGISTRADORES')
    print(f"Gerais -> A: {REGS_GERAIS['A']} | B: {REGS_GERAIS['B']}")
    print(f"Controle -> PC: 0x{REGS_ESPECIAIS['PC']:02X} | IR: '{REGS_ESPECIAIS['IR']}'")
    print(f"Memória -> MAR: 0x{REGS_ESPECIAIS['MAR']:02X} | MBR: {REGS_ESPECIAIS['MBR']}")
    print(f"ULA -> AC: {REGS_ESPECIAIS['AC']} | M: {REGS_ESPECIAIS['M']} | R: {REGS_ESPECIAIS['R']}")
    print(f"Flags -> C: {REGS_ESPECIAIS['C']} | N: {REGS_ESPECIAIS['N']} | Z: {REGS_ESPECIAIS['Z']}")
    print()

    input('Pressione <ENTER> para avançar para o próximo ciclo...')

def executar_comando(instrucao: str):
    '''Decodifica a string da instrução e altera os registradores ou a RAM.'''
     
    global RAM, REGS_GERAIS, REGS_ESPECIAIS
 
    partes = instrucao.replace(',', ' ').split()
    opcode = partes[0].upper()
 
    if opcode == 'LOAD':
        reg_destino = partes[1].upper()
        endereco_hex = partes[2]
         
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        if isinstance(valor_ram, str) and valor_ram[:2].upper() == "0X":
            valor_ram = int(valor_ram, 16)
            
        REGS_GERAIS[reg_destino] = int(valor_ram)
        REGS_ESPECIAIS['AC'] = valor_ram
        print(f"[ULA] Sucesso: Registrador {reg_destino} recebeu o valor {valor_ram}")

    elif opcode == 'ADD':
        reg_atual = partes[1].upper()
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]

        if isinstance(valor_ram, str) and valor_ram.upper().startswith("0X"):
            valor_ram = int(valor_ram, 16)

        valor_reg = REGS_GERAIS[reg_atual]
        if isinstance(valor_reg, str) and valor_reg.upper().startswith("0X"):
            valor_reg = int(valor_reg, 16)
        
        resultado = int(valor_reg) + int(valor_ram)
        REGS_GERAIS[reg_atual] = resultado
        REGS_ESPECIAIS['AC'] = resultado
        
        REGS_ESPECIAIS['Z'] = 1 if resultado == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if resultado < 0 else 0
        print(f"[ULA] Sucesso: Somado {valor_ram} ao Registrador {reg_atual}. AC = {resultado}")
     
    elif opcode == 'MOV':
        reg_destino = partes[1].upper()
        reg_origem = partes[2].upper()

        if reg_origem == 'M':
            valor_origem = REGS_ESPECIAIS['M']
        else:
            valor_origem = REGS_GERAIS[reg_origem]

        REGS_GERAIS[reg_destino] = valor_origem
        print(f"[ULA] MOV: Registrador {reg_destino} recebeu o valor de {reg_origem} ({valor_origem})")
     
    elif opcode == 'MULT':
        reg_atual = partes[1].upper()
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        if isinstance(valor_ram, str) and valor_ram.upper().startswith("0X"):
            valor_ram = int(valor_ram, 16)
        
        valor_reg = REGS_GERAIS[reg_atual]
        if isinstance(valor_reg, str) and valor_reg.upper().startswith("0X"):
            valor_reg = int(valor_reg, 16)
            
        resultado = valor_reg * valor_ram
        REGS_GERAIS[reg_atual] = int(resultado)
        
        REGS_ESPECIAIS['M'] = resultado
        REGS_ESPECIAIS['AC'] = resultado
        
        REGS_ESPECIAIS['Z'] = 1 if resultado == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if int(resultado) < 0 else 0
        print(f"[ULA] MULT: Multiplicado {reg_atual} por RAM[{endereco_hex}] ({valor_ram}). M/AC = {resultado}")

    elif opcode == 'STORE':
        reg_origem = partes[1].upper()
        endereco_hex = partes[2]
         
        indice_ram = int(endereco_hex, 16)
    
        RAM[indice_ram] = REGS_GERAIS[reg_origem]
        print(f"[ULA] Sucesso: RAM[{endereco_hex}] agora guarda o valor {REGS_GERAIS[reg_origem]}")
 
    elif opcode == 'SUB':
        reg_atual = partes[1].upper()
        endereco_hex = partes[2]
        
        indice_ram = int(endereco_hex, 16)
        valor_ram = RAM[indice_ram]
        
        if isinstance(valor_ram, str) and valor_ram.upper().startswith("0X"):
            valor_ram = int(valor_ram, 16)
        
        valor_reg = REGS_GERAIS[reg_atual]
        if isinstance(valor_reg, str) and valor_reg.upper().startswith("0X"):
            valor_reg = int(valor_reg, 16)
            
        resultado = int(valor_reg) - int(valor_ram)
        REGS_ESPECIAIS['AC'] = resultado
        REGS_GERAIS[reg_atual] = resultado
        
        REGS_ESPECIAIS['Z'] = 1 if resultado == 0 else 0
        REGS_ESPECIAIS['N'] = 1 if resultado < 0 else 0
        print(f"[ULA] SUB: Subtraído {valor_ram} do Registrador {reg_atual}. AC = {resultado}")
 
    elif opcode == 'DIV':
        reg_atual = partes[1].upper()
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
        endereco_hex = partes[1].upper()
        indice_destino = int(endereco_hex, 16)
     
        REGS_ESPECIAIS['PC'] = indice_destino
        print(f"[Controle] JUMP: Fluxo desviado incondicionalmente para 0x{indice_destino:02X}")
 
    elif opcode == "JUMP+":
        endereco_hex = partes[1].upper()
        indice_destino = int(endereco_hex, 16)
         
        if REGS_ESPECIAIS['AC'] >= 0:
            REGS_ESPECIAIS['PC'] = indice_destino
            print(f"[Controle] JUMP+: AC é {REGS_ESPECIAIS['AC']} (positivo). Desviando para 0x{indice_destino:02X}")
        else:
            print(f"[Controle] JUMP+: AC é {REGS_ESPECIAIS['AC']} (negativo). Desvio NÃO realizado.")
 
    elif opcode == "LOADI": # Endereçamento indireto
        reg_destino = partes[1].upper()
        endereco_ponteiro_hex = partes[2]
 
        indice_ponteiro = int(endereco_ponteiro_hex, 16)
        endereco_real = RAM[indice_ponteiro]
         
        if isinstance(endereco_real, str): # Verificação pra saber se o tipo do dado é de fato o tipo que se espera para o dado
            indice_real = int(endereco_real, 16)
        else:
            indice_real = int(endereco_real)
             
        valor_final = RAM[indice_real]
        
        REGS_GERAIS[reg_destino] = valor_final
        print(f"[ULA] LOADI: Ponteiro 0x{indice_ponteiro:02X} apontou para 0x{indice_real:02X}. Valor {valor_final} carregado em {reg_destino}")
 
    elif opcode == 'STORI':
        reg_origem = partes[1].upper()
        endereco_ponteiro_hex = partes[2]
 
        indice_ponteiro = int(endereco_ponteiro_hex, 16)
        endereco_real = RAM[indice_ponteiro]
 
        if isinstance(endereco_real, str):
            indice_real = int(endereco_real, 16)
        else:
            indice_real = int(endereco_real)
             
        valor_para_salvar = REGS_GERAIS[reg_origem]
        RAM[indice_real] = valor_para_salvar
        print(f"[ULA] STORI: Ponteiro 0x{indice_ponteiro:02X} apontou para 0x{indice_real:02X}. Salvo o valor {valor_para_salvar} na RAM.")

    

def ciclo_instrucao():
    while True:
        indice_atual = REGS_ESPECIAIS['PC']
        
        if indice_atual >= 256 or RAM[indice_atual] in (None, 0, '', int, str):
            print('Execução finalizada ou fim da memória alcançado!')
            break
            
        # MAR <- PC
        REGS_ESPECIAIS['MAR'] = REGS_ESPECIAIS['PC']
        
        # MBR <- RAM[MAR]
        REGS_ESPECIAIS['MBR'] = RAM[REGS_ESPECIAIS['MAR']]
        
        # IR <- MBR
        REGS_ESPECIAIS['IR'] = str(REGS_ESPECIAIS['MBR'])
        
        # PC <- PC + 1
        REGS_ESPECIAIS['PC'] += 1

        exibir_registradores()
        input("Pressione <ENTER> para avançar para o próximo ciclo...")
        
        comando = REGS_ESPECIAIS['IR']
        print(f"Executando comando {comando}...")
        executar_comando(comando)

if __name__ == "__main__":
    print("Iniciando o simulador...") # Só para testar se o terminal responde
    
    if carregar_arquivo("selectionsort.txt"): # Aqui muda o nome do arquivo que for fazer a o teste
        ciclo_instrucao()
