CREATE SCHEMA empresa;
CREATE TABLE piloto(
	id_piloto INTEGER NOT NULL PRIMARY KEY,
    nome_piloto VARCHAR(50) NOT NULL,
    nacionalidade VARCHAR(30) NOT NULL,
    data_nasc DATE NOT NULL,
    num_carro INTEGER NOT NULL,
    UNIQUE (num_carro)
    );

CREATE TABLE equipe(
	id_equipe INTEGER NOT NULL PRIMARY KEY,
    nome_equipe VARCHAR(50) NOT NULL,
    pais_origem VARCHAR(45) NOT NULL,
    chefe_equipe VARCHAR(50) NOT NULL
    );

CREATE TABLE contrato(
	idPiloto INTEGER NOT NULL,
    idEquipe INTEGER NOT NULL,
    FOREIGN KEY (idPiloto) references piloto(id_piloto),
    FOREIGN KEY (idEquipe) references equipe(id_equipe)
    );
    
CREATE TABLE colecao(
	id_colecao INTEGER NOT NULL PRIMARY KEY,
    nome_colecao VARCHAR(45) NOT NULL,
    ano_lancamento INTEGER NOT NULL,
    tipo_acabamento ENUM('fosco', 'foil', 'rainbow', 'gold')
    );

CREATE TABLE lote(
	id_lote INTEGER NOT NULL PRIMARY KEY,
	idColecao INTEGER NOT NULL,
	data_fabricacao DATE NOT NULL,
	qtd_impressa INT NOT NULL,
	custo_producao DECIMAL(10,2),
	FOREIGN KEY (idColecao) references colecao(id_colecao)
	);

CREATE TABLE card(
	id_card INTEGER NOT NULL PRIMARY KEY,
    idColecao INTEGER NOT NULL,
    idPiloto INTEGER NOT NULL,
    nivel_raridade ENUM('comum', 'raro', 'super-raro', 'especial'),
    tiragem_maxima INTEGER  NOT NULL,
    preco_base DECIMAL(10,2),
    FOREIGN KEY (idColecao) references colecao(id_colecao),
	FOREIGN KEY (idPiloto) references piloto(id_piloto)
    );
    
CREATE TABLE cliente(
	id_cliente INTEGER NOT NULL PRIMARY KEY,
    cpf CHAR(11) NOT NULL,
    nome_cliente VARCHAR(50) NOT NULL,
    email_cliente VARCHAR(100),
    telefone VARCHAR(14) NOT NULL
    );

CREATE TABLE endereco(
	);

CREATE TABLE pedido(
	id_pedido INTEGER NOT NULL PRIMARY KEY,
    idCliente INTEGER NOT NULL,
    idEndereco INTEGER NOT NULL,
    data_pedido DATE NOT NULL,
    status_pedido ENUM('recebido','em-producao', 'enviado'),
    valor_total DECIMAL(10,2),
    FOREIGN KEY (idCliente) references cliente(id_cliente),
    FOREIGN KEY (idEndereco) references endereco(id_endereco)
    );
	
CREATE TABLE contem(
	idPedido INTEGER NOT NULL,
	idCard INTEGER NOT NULL,
	quantidade INTEGER NOT NULL,
	FOREIGN KEY (idPedido) references pedido(id_pedido),
	FOREIGN KEY (idCard) references card(id_card)
	);
