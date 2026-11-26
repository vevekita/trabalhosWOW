CREATE SCHEMA empresa_cards;
USE empresa_cards;

CREATE TABLE piloto (
	id_piloto INTEGER NOT NULL PRIMARY KEY,
    nome_piloto VARCHAR(50) NOT NULL,
    nacionalidade VARCHAR(30) NOT NULL,
    data_nasc DATE NOT NULL,
    num_carro INTEGER NOT NULL,
    UNIQUE (num_carro) -- não pode dois pilotos com o mesmo número
);

CREATE TABLE equipe (
	id_equipe INTEGER NOT NULL PRIMARY KEY,
    nome_equipe VARCHAR(50) NOT NULL,
    pais_origem VARCHAR(50) NOT NULL,
    chefe_equipe VARCHAR(50) NOT NULL
);

CREATE TABLE contrato (
	idPiloto INTEGER NOT NULL,
    idEquipe INTEGER NOT NULL,
    FOREIGN KEY (idPiloto) REFERENCES piloto(id_piloto),
    FOREIGN KEY (idEquipe) REFERENCES equipe(id_equipe)
);
    
CREATE TABLE colecao (
	id_colecao INTEGER NOT NULL PRIMARY KEY,
    nome_colecao VARCHAR(45) NOT NULL,
    ano_lancamento INTEGER NOT NULL,
    tipo_acabamento ENUM('fosco', 'foil', 'rainbow', 'gold')
);

CREATE TABLE lote (
	id_lote INTEGER NOT NULL PRIMARY KEY,
	idColecao INTEGER NOT NULL,
	data_fabricacao DATE NOT NULL,
	qtd_impressa INT NOT NULL,
	custo_producao DECIMAL(10,2),
	FOREIGN KEY (idColecao) REFERENCES colecao(id_colecao)
);

CREATE TABLE card (
	id_card INTEGER NOT NULL PRIMARY KEY,
    idColecao INTEGER NOT NULL,
    idPiloto INTEGER NOT NULL,
    nivel_raridade ENUM('comum', 'raro', 'super-raro', 'especial'),
    tiragem_maxima INTEGER NOT NULL,
    preco_base DECIMAL(10,2),
    FOREIGN KEY (idColecao) REFERENCES colecao(id_colecao),
	FOREIGN KEY (idPiloto) REFERENCES piloto(id_piloto)
);
    
CREATE TABLE cliente (
	id_cliente INTEGER NOT NULL PRIMARY KEY,
    cpf CHAR(11) NOT NULL,
    nome_cliente VARCHAR(50) NOT NULL,
    email_cliente VARCHAR(100),
    telefone VARCHAR(14) NOT NULL
);

CREATE TABLE endereco (
    idCliente INTEGER NOT NULL,
    numero VARCHAR(10) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado CHAR(2) NOT NULL,
    cep CHAR(8) NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE pedido (
    id_pedido INTEGER NOT NULL PRIMARY KEY,
    idCliente INTEGER NOT NULL,
    data_pedido DATE NOT NULL,
    status_pedido ENUM('recebido', 'em-producao', 'enviado') NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE pagamento (
    id_pagamento INTEGER NOT NULL PRIMARY KEY,
    idPedido INTEGER NOT NULL,
    forma_pagamento ENUM('cartao', 'pix', 'boleto') NOT NULL,
    codigo_transacao VARCHAR(30) NOT NULL,
    status_pagamento ENUM('pendente', 'confirmado', 'recusado') NOT NULL,
    UNIQUE (idPedido),
    FOREIGN KEY (idPedido) REFERENCES pedido(id_pedido)
);
 
CREATE TABLE contem (
    idPedido INTEGER NOT NULL,
    idCard INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    FOREIGN KEY (idPedido) REFERENCES pedido(id_pedido),
    FOREIGN KEY (idCard) REFERENCES card(id_card)
);

INSERT INTO piloto (id_piloto, nome_piloto, nacionalidade, data_nasc, num_carro) VALUES
(1, 'Max Verstappen', 'Holandês', '1997-09-30', 3),
(2, 'Lando Norris', 'Britânico', '1999-11-13', 1),
(3, 'Charles Leclerc', 'Monegasco', '1997-10-16', 16),
(4, 'Lewis Hamilton', 'Britânico', '1985-01-07', 44),
(5, 'Oscar Piastri', 'Australiano', '2001-04-06', 81),
(6, 'George Russell', 'Britânico', '1998-02-15', 63),
(7, 'Fernando Alonso', 'Espanhol', '1981-07-29', 14),
(8, 'Gabriel Bortoleto', 'Brasileiro', '2004-10-14', 5),
(9, 'Kimi Antonelli', 'Italiano', '2006-08-25', 12),
(10, 'Rafael Camara', 'Brasileiro', '2005-05-05', 51);

INSERT INTO equipe (id_equipe, nome_equipe, pais_origem, chefe_equipe) VALUES
(1, 'Red Bull Racing', 'Áustria', 'Laurent Mekies'),
(2, 'McLaren', 'Reino Unido', 'Andrea Stella'),
(3, 'Ferrari', 'Itália', 'Frédéric Vasseur'),
(4, 'Mercedes', 'Alemanha', 'Toto Wolff'),
(5, 'Aston Martin', 'Reino Unido', 'Mike Krack'),
(6, 'Williams', 'Reino Unido', 'James Vowles'),
(7, 'Haas', 'Estados Unidos', 'Ayao Komatsu'),
(8, 'Audi', 'Alemanha', 'Mattia Binotto');

INSERT INTO contrato (idPiloto, idEquipe) VALUES
(1, 1), -- Max Verstappen - Red Bull Racing
(2, 2), -- Lando Norris - McLaren
(3, 3), -- Charles Leclerc - Ferrari
(4, 4), -- Lewis Hamilton - Mercedes
(4, 3), -- Lewis Hamilton - Ferrari
(4, 2), -- Lewis Hamilton - McLaren
(5, 2), -- Oscar Piatri - McLaren
(6, 4), -- George Russel - Mercedes
(7, 5), -- Fernado Alonso - Aston Martin
(7, 3),	-- Fernado Alonso - Ferrari
(8, 8), -- Gabriel Bortoleto - Audi
(9, 4), -- Kimi Antonelli - Mercedes
(10, 7); -- Rafael Camara - Haas

INSERT INTO colecao (id_colecao, nome_colecao, ano_lancamento, tipo_acabamento) VALUES
(1, 'Grid 2023', 2023, 'fosco'),
(2, 'Campeões', 2024, 'foil'),
(3, 'Rookies 2025', 2025, 'rainbow'),
(4, 'Lendas Douradas', 2025, 'gold'),
(5, 'Temporada 2026', 2026, 'foil');
 
INSERT INTO lote (id_lote, idColecao, data_fabricacao, qtd_impressa, custo_producao) VALUES
(1, 1, '2023-03-10', 5000, 12000.00),
(2, 1, '2023-09-15', 3000, 7500.00),
(3, 2, '2024-04-20', 4000, 15000.00),
(4, 3, '2025-02-12', 2500, 11000.00),
(5, 4, '2025-06-30', 1000, 9000.00);
 
INSERT INTO card (id_card, idColecao, idPiloto, nivel_raridade, tiragem_maxima, preco_base) VALUES
(1, 1, 1, 'comum', 2000, 5.00),
(2, 1, 2, 'comum', 2000, 5.00),
(3, 1, 3, 'raro', 800, 15.00),
(4, 2, 1, 'super-raro', 300, 80.00),
(5, 2, 4, 'raro', 600, 20.00),
(6, 3, 5, 'comum', 1500, 6.00),
(7, 3, 8, 'especial', 100, 150.00),
(8, 3, 9, 'raro', 500, 18.00),
(9, 4, 4, 'especial', 50, 300.00),
(10, 4, 7, 'super-raro', 150, 120.00),
(11, 2, 2, 'raro', 700, 22.00),
(12, 1, 6, 'comum', 2000, 5.00),
(13, 5, 3, 'comum', 3000, 5.50),
(14, 5, 2, 'raro', 1000, 25.00);
 
INSERT INTO cliente (id_cliente, cpf, nome_cliente, email_cliente, telefone) VALUES
(1, '11122233344', 'Ana Souza', 'ana.souza@email.com', '44999990001'),
(2, '22233344455', 'Bruno Lima', 'bruno.lima@email.com', '44999990002'),
(3, '33344455566', 'Carla Mendes', 'carla.mendes@email.com', '11999990003'),
(4, '44455566677', 'Diego Alves', 'diego.alves@email.com', '44999990004'),
(5, '55566677788', 'Elisa Costa', 'elisa.costa@email.com', '43999990005'),
(6, '66677788899', 'Felipe Rocha', 'felipe.rocha@email.com', '21999990006');

INSERT INTO endereco (idCliente, numero, cidade, estado, cep) VALUES
(1, '120', 'Maringá', 'PR', '87020000'),
(1, '45', 'Londrina', 'PR', '86010000'),
(2, '300', 'Curitiba', 'PR', '80010000'),
(3, '88', 'São Paulo', 'SP', '01001000'),
(4, '15', 'Maringá', 'PR', '87013000'),
(6, '77', 'Rio de Janeiro', 'RJ', '20040000'),
(5, '210', 'Arapongas', 'PR', '86700000');
 
INSERT INTO pedido (id_pedido, idCliente, data_pedido, status_pedido, valor_total) VALUES
(1, 1, '2026-01-10', 'enviado', 30.00),
(2, 1, '2026-03-05', 'enviado', 120.00),
(3, 2, '2026-03-20', 'enviado', 25.00),
(4, 3, '2026-05-02', 'em-producao', 450.00),
(5, 4, '2026-06-11', 'recebido', 60.00),
(6, 2, '2026-08-14', 'enviado', 222.00),
(7, 6, '2026-09-01', 'recebido', 35.00),
(8, 1, '2026-10-02', 'em-producao', 270.00);
 
INSERT INTO pagamento (id_pagamento, idPedido, forma_pagamento, codigo_transacao, status_pagamento) VALUES
(1, 1, 'pix', 'TX0001', 'confirmado'),
(2, 2, 'cartao', 'TX0002', 'confirmado'),
(3, 3, 'boleto', 'TX0003', 'confirmado'),
(4, 4, 'cartao', 'TX0004', 'confirmado'),
(5, 5, 'pix', 'TX0005', 'pendente'),
(6, 6, 'cartao', 'TX0006', 'confirmado'),
(7, 7, 'boleto', 'TX0007', 'pendente'),
(8, 8, 'pix', 'TX0008', 'confirmado');
 
INSERT INTO contem (idPedido, idCard, quantidade) VALUES
(1, 1, 3), 
(1, 3, 1),
(2, 4, 1), 
(2, 5, 2),
(3, 1, 2), 
(3, 2, 2), 
(3, 12, 1),
(4, 9, 1), 
(4, 7, 1),
(5, 6, 4), 
(5, 8, 2),
(6, 4, 1), 
(6, 10, 1), 
(6, 11, 1),
(7, 3, 1), 
(7, 5, 1),
(8, 7, 1), 
(8, 10, 1);

-- Consultas
-- 1: projeção: Exibe o nome_piloto, nacionalidade e num_carro da tabela de piloto.
SELECT nome_piloto, nacionalidade, num_carro
FROM piloto;

-- 2: seleção e projeção: Exibe os cards e seus dados (id_card, nivel_raridade, tiragem_maxima e preco_base) 
-- de uma determinada coleção onde o nivel_raridade é 'super-raro'.
SELECT id_card, nivel_raridade, tiragem_maxima, preco_base
FROM card 
WHERE nivel_raridade = 'super-raro';

-- 3: UNION entre 2 relações: União das tabelas de lote e de pedido, 
-- no sentido de fluxo de caixa (a fim de analisar o movimento de compra e fabricação).
SELECT data_pedido, valor_total FROM pedido
UNION
SELECT data_fabricacao, custo_producao FROM lote;

-- 4: EXCEPT entre 2 relações: Exibe o id dos pilotos que ainda não possuem nenhum card lançado.
SELECT id_piloto FROM piloto
EXCEPT
SELECT idPiloto FROM card;

-- 5: INTERSECT entre 2 relações: Exibe o id dos clientes cadastrados que já compraram pelo menos uma vez.
SELECT id_cliente FROM cliente
INTERSECT
SELECT idCliente FROM pedido;

-- 6: subconsulta, retorna 1 valor. Localiza todas as compras de um comprador de CPF 44455566677 (cliente Diego)
SELECT id_pedido, data_pedido, status_pedido, valor_total
FROM pedido
WHERE idCliente = (
    SELECT id_cliente
    FROM cliente
    WHERE cpf = '44455566677'
);

-- 7: subconsultas, retorna conjunto de valores
    -- 7.a. IN ou NOT IN. Exibe clientes que ainda não fizeram nenhum pedido.
SELECT id_cliente, nome_cliente, email_cliente, telefone
FROM cliente
WHERE id_cliente NOT IN (
    SELECT idCliente
    FROM pedido
);

    -- 7.b. ANY.  Exibe os cards com preço-base menor que o maior preço entre todos os cards raros.
SELECT id_card, idPiloto, idColecao, nivel_raridade, preco_base
FROM card
WHERE preco_base < ANY (
    SELECT preco_base
    FROM card
    WHERE nivel_raridade = 'raro'
);

    -- 7.c. EXISTS ou NOT EXISTS. Exibe cards e dados desses cards, que ainda não chegaram a ser vendidos
SELECT cd.id_card, cd.idPiloto, cd.idColecao, cd.nivel_raridade, cd.preco_base
FROM card cd
WHERE NOT EXISTS (
    SELECT *
    FROM contem ct
    WHERE ct.idCard = cd.id_card
);

    -- 7.d. ALL. Compara cards de qualquer piloto que custam mais caro que os cards do piloto de id=1 ("Max Verstappen")
SELECT id_card, idPiloto, idColecao, nivel_raridade, preco_base
FROM card
WHERE preco_base > ALL (
    SELECT preco_base FROM card
    WHERE idPiloto = 1
);

-- 8: subconsulta, retorna 2 colunas. Exibe os cards que possuem a mesma combinação de piloto e raridade dos cards com preço acima de 10.00
SELECT id_card, idPiloto, idColecao, nivel_raridade, preco_base
FROM card
WHERE (idPiloto, nivel_raridade) IN (
    SELECT idPiloto, nivel_raridade
    FROM card
    WHERE preco_base > 10.00
);

--9: junção externa à direita. Exibe todas as equipes e os IDs de seus pilotos contratados, incluindo equipes sem pilotos (Williams)
SELECT ct.idPiloto, e.id_equipe, e.nome_equipe, e.pais_origem, e.chefe_equipe
FROM contrato ct
RIGHT JOIN equipe e ON ct.idEquipe = e.id_equipe;

--10: junção externa à esquerda. Exibe todos os pilotos e seus cards cadastrados, inclusive os pilotos sem cards atualmente
SELECT p.id_piloto, p.nome_piloto, p.num_carro, c.id_card, c.nivel_raridade, c.preco_base
FROM piloto p
LEFT JOIN card c ON p.id_piloto = c.idPiloto;

-- 11: uma ou mais funções de agregação: Exibe a quantidade de pagamentos que estão com o status 'confirmado'. 
SELECT COUNT(*)
FROM pagamento
WHERE status_pagamento = 'confirmado';

-- 12: função de agregação com GROUP BY: combina a tabela por idCliente e status_pedido. 
-- Faz a soma dos valores totais pertencentes aquele grupo.
SELECT idCliente, status_pedido, SUM(valor_total)
FROM pedido
GROUP BY idCliente, status_pedido;

-- 13: 2 funções de agregação com GROUP BY: 
-- Exibe a quantidade de cards que um cliente comprou e o valor total que ele gastou.
SELECT idCliente, COUNT(*), SUM(valor_total)
FROM pedido
GROUP BY idCliente;

-- 14: função agregada com GROUP BY e HAVING: 
-- Exibe o nome do piloto que já fez contrato com mais de uma equipe durante a carreira.
SELECT p.nome_piloto, COUNT(*)
FROM contrato c
JOIN piloto p ON c.idPiloto =  p.id_piloto
GROUP BY nome_piloto
HAVING COUNT(*) > 1;

-- 15: junção interna (2 relações): 
-- Exibe o número, o id do cliente e o status de um determinado pedido se o status do pagamento for 'pendente'.
SELECT id_pedido, idCliente, status_pedido
FROM pedido
JOIN pagamento
ON id_pedido = idPedido
WHERE status_pagamento = 'pendente';

-- 16: junção interna (3 relações). Exibe todos os cards do piloto Gabriel Bortoleto e seus dados.
SELECT c.id_card, c.nivel_raridade, c.tiragem_maxima, c.preco_base, cl.nome_colecao
FROM card c
JOIN piloto p ON c.idPiloto = p.id_piloto
JOIN colecao cl ON c.idColecao = cl.id_colecao
WHERE p.nome_piloto = 'Gabriel Bortoleto';

-- 17: junção interna (4 relações)
