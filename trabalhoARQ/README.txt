SIMULADOR DO CICLO DE INSTRUÇÃO

Este projeto consiste em um simulador funcional de um processador programável,
desenvolvido em Python. O simulador implementa o Ciclo de Instrução completo
(Busca, Decodificação e Execução), manipulando uma memória RAM compartilhada,
registradores gerais, registradores de controle, uma Unidade Lógica e Aritmética
(ULA) e Flags de condição.

O simulador é genérico e suporta as seguintes operações:
* LOAD -> Carrega o valor da RAM no registrador geral.
* STORE -> Salva o valor do registrador geral na RAM.
* ADD -> Soma o valor da RAM ao registrador. Atualiza AC e Flags.
* SUB -> Subtrai o valor da RAM do registrador. Atualiza AC e Flags.
* MULT -> Multiplica o registrador pelo valor da RAM. Atualiza M e AC.
* DIV -> Divide o registrador pelo valor da RAM. Quociente em AC, Resto em R.
* MOV -> Move dados entre registradores (incluindo o especial M).
* JUMP -> Desvio incondicional de fluxo (altera o PC).
* JUMP+ -> Desvio condicional (desvia se AC >= 0).
* LOADI -> Carregamento indireto (busca o endereço real na RAM).
* STORI -> Escrita indireta através de ponteiro na RAM.

Divisão da memória RAM:
* 0x00 a 0x0F (0 a 15) -> Setor Reservado para Variáveis e Dados.
* 0x10 em diante (16+) -> Setor Reservado para Vetores/Arrays.
* 0xA0 em diante (160+) -> Setor de Código (Onde as instruções iniciam).

Como executar o simulador:
1. Certifique-se de que o arquivo de algoritmo desejado (ex: `selectionsort.txt`)
   esteja na mesma pasta do arquivo principal (`arquivo.py`) e que esteja definido 
   ao fim do código (na main) onde na função carregar_arquivo() contenha o nome do arquivo
   .txt desejado para execução.
2. No terminal, execute o comando:
   python arquivo.py
3. O simulador exibirá o painel visual com o estado inicial do hardware.
4. Pressione a tecla <ENTER> para avançar passo a passo (ciclo por ciclo).
5. O programa encerrará automaticamente até encontrar o fim das instruções na memória.
