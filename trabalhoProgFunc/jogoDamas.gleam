// Alunas: 
// Ana Julia Thibes dos Santos - RA 145108
// Verônica Manami Kitamura - RA 145096
//
// Jogo de Damas
//
// Regras
//
// - De 2 jogadores. Um jogador joga com as peças brancas e o outro com as peças
//   pretas.
// - se há uma peça adversária em alguma das diagonais da frente e a casa logo depois 
//   dela está livre, a peça pula e remove a peça adversária.
//    - Só pode capturar para a frente.
//    - Só é realizada uma captura por jogada: a jogada acaba depois do pulo, 
//      sem sequência de capturas.
//    - A captura não é obrigatória.
// - Dois jogadores não podem ocupar a mesma casa.
// - A partida dura 180 segundos. Vence quem pegou mais peças, e pode haver
//   empate.
// - a peça que chega à última linha do adversário fica parada ali e não se move mais. 
//   Como a captura é só para a frente, ela não pode ser capturada.
//
// Controles
//
// - Enter: começa a partida (na abertura), joga novamente (no resultado) ou insere um peça
//   na posição escolhida.
// - Setas: movem o cursor pelo tabuleiro.

import gleam/int
import gleam/list
import gleam/option.{type Option, None, Some}
import sgleam/check
import sgleam/color.{type Color}
import sgleam/env.{type Env}
import sgleam/fill
import sgleam/image.{type Image}
import sgleam/key
import sgleam/mworld
import sgleam/random.{type Generator}
import sgleam/style

// ----------------------------------------------------------------------
// Constantes
// ----------------------------------------------------------------------

/// Quantidade de casas em cada lado da grade.
const tamanho = 8

/// Lado de uma casa, em pixels.
const lado_casa = 40

/// Quantidade de tiques por segundo.
const tiques_por_segundo = 10

/// Duração da partida, em tiques (60 segundos).
const duracao = 1800

/// Largura da tela, em pixels.
const largura = 400

/// Altura da tela, em pixels.
const altura = 480

// ----------------------------------------------------------------------
// Tipos de dados
// ----------------------------------------------------------------------

pub type Cor {
   Brancas
   Pretas
}

/// Uma casa da grade. *lin* e *col* estão entre 0 e `tamanho - 1`; a casa
/// Posicao(0, 0) fica no canto superior esquerdo.
pub type Posicao {
   Posicao(linha: Int, coluna: Int)
}

// Uma peça do jogo. *jogador* define a cor da peça e posição define a posição 
// que ela está.
pub type Peca {
   Peca(jogador: Cor, posicao: Posicao)
}

// Um jogador do jogo. *contador* representa a quantidade de peças que esse jogador comeu,
// começando sempre em 0, *cor* define a cor das peças do jogador, *cursor* define em que 
// posição está o cursor do jogador e *selecionada* define a casa que o jogador selecionou 
// para se mover
pub type Jogador {
   Jogador(contador: Int, cor: Cor, cursor: Posicao, selecionada: Option(Posicao))
}

// Uma partida em andamento. *tiques_restantes* é a quantidade de tiques restantes
// para o final da partida  

pub type Partida {
   Partida(jogadores: List(Jogador), pecas: List(Peca), vez: Cor, tiques_restantes: Int)
}

// Resultado de uma partida
pub type Resultado {
   Vitoria
   Derrota
   Empate
}

pub type Estado{
  Abertura(jogadores: List(Jogador))
  EmPartida(partida: Partida)
  Resultado(jogadores: List(Jogador), resultado: Resultado)
}

/// A direção de um movimento.
pub type Direcao {
  Esquerda
  Direita
}

// ----------------------------------------------------------------------
// Mundo
// ----------------------------------------------------------------------

pub fn smain() {
  mworld.create_from(inicio, min: 2, max: 2, to_image: desenha)
  |> mworld.tick_rate(tiques_por_segundo)
  |> mworld.on_tick(tique)
  |> mworld.on_key_down(tecla)
}

/// Cria o estado inicial com o gerador de *env*, as imagens das frutas e a
/// quantidade de *jogadores*.
fn inicio(env: Env, jogadores: List(mworld.Player)) -> Estado {
  Abertura(env.random(env), carrega_imagens(), list.length(jogadores))
}

/// Desenha o *estado* do ponto de vista de *jogador*.
fn desenha(estado: Estado, jogador: mworld.Player) -> Image {
  tela(estado, mworld.player_index(jogador))
}

/// Trata o *evento* de tecla pressionada por *jogador*.
fn tecla(estado: Estado, jogador: mworld.Player, evento: key.Event) -> Estado {
  trata_tecla(estado, mworld.player_index(jogador), evento)
}

// ----------------------------------------------------------------------
// Início da partida
// ----------------------------------------------------------------------

/// Produz a lista dos números de *inicio* até *fim*, inclusive. Produz a
/// lista vazia se *inicio* é maior que *fim*.
pub fn sequencia(inicio: Int, fim: Int) -> List(Int) {
  case inicio > fim {
    True -> []
    False -> [inicio, ..sequencia(inicio + 1, fim)]
  }
}

pub fn sequencia_examples() {
  check.eq(sequencia(3, 2), [])
  check.eq(sequencia(3, 3), [3])
  check.eq(sequencia(0, 4), [0, 1, 2, 3, 4])
}

/// Devolve True se a casa é escura (só as casas escuras são usadas no jogo).
pub fn casa_escura(pos: Posicao) -> Bool {
  { pos.lin + pos.col } % 2 == 1
}

pub fn casa_escura_examples() {
  check.eq(casa_escura(Posicao(0, 0)), False)
  check.eq(casa_escura(Posicao(0, 1)), True)
  check.eq(casa_escura(Posicao(1, 0)), True)
  check.eq(casa_escura(Posicao(7, 7)), False)
}

/// Produz as peças de um jogador, uma para cada casa escura das linhas dadas.
pub fn pecas_nas_linhas(jogador: Cor, linhas: List(Int)) -> List(Peca) {
  linhas
  |> list.flat_map(fn(lin) {
    sequencia(0, tamanho - 1)
    |> list.map(fn(col) { Posicao(lin, col) })
    |> list.filter(casa_escura)
    |> list.map(fn(pos) { Peca(jogador, pos) })
  })
}

pub fn pecas_nas_linhas_examples() {
  check.eq(pecas_nas_linhas(Pretas, [0]), [
    Peca(Pretas, Posicao(0, 1)),
    Peca(Pretas, Posicao(0, 3)),
    Peca(Pretas, Posicao(0, 5)),
    Peca(Pretas, Posicao(0, 7)),
  ])
  check.eq(pecas_nas_linhas(Brancas, []), [])
}

/// Posição inicial: Pretas nas linhas 0 a 2 (topo), Brancas nas linhas 5 a 7.
pub fn pecas_iniciais() -> List(Peca) {
  list.append(
    pecas_nas_linhas(Pretas, [0, 1, 2]),
    pecas_nas_linhas(Brancas, [5, 6, 7]),
  )
}

pub fn pecas_iniciais_examples() {
  let pecas = pecas_iniciais()
  check.eq(list.length(pecas), 24)
  check.eq(list.length(list.filter(pecas, fn(p) { p.dono == Brancas })), 12)
  check.eq(list.length(list.filter(pecas, fn(p) { p.dono == Pretas })), 12)
}

/// Devolve a peça que está na posição dada, se houver.
pub fn peca_em(pecas: List(Peca), pos: Posicao) -> Option(Peca) {
  case list.find(pecas, fn(p) { p.posicao == pos }) {
    Ok(peca) -> Some(peca)
    Error(_) -> None
  }
}

pub fn peca_em_examples() {
  let pecas = pecas_iniciais()
  check.eq(peca_em(pecas, Posicao(0, 1)), Some(Peca(Pretas, Posicao(0, 1))))
  check.eq(peca_em(pecas, Posicao(7, 0)), Some(Peca(Brancas, Posicao(7, 0))))
  check.eq(peca_em(pecas, Posicao(3, 2)), None)
  check.eq(peca_em([], Posicao(0, 1)), None)
}

/// Devolve True se a posição está dentro do tabuleiro.
pub fn dentro_do_tabuleiro(pos: Posicao) -> Bool {
  pos.lin >= 0 && pos.lin < tamanho && pos.col >= 0 && pos.col < tamanho
}

pub fn dentro_do_tabuleiro_examples() {
  check.eq(dentro_do_tabuleiro(Posicao(0, 0)), True)
  check.eq(dentro_do_tabuleiro(Posicao(7, 7)), True)
  check.eq(dentro_do_tabuleiro(Posicao(-1, 3)), False)
  check.eq(dentro_do_tabuleiro(Posicao(3, 8)), False)
}

/// Devolve True se a posição está dentro do tabuleiro e sem peça.
pub fn casa_livre(pecas: List(Peca), pos: Posicao) -> Bool {
  dentro_do_tabuleiro(pos) && peca_em(pecas, pos) == None
}

pub fn casa_livre_examples() {
  let pecas = pecas_iniciais()
  check.eq(casa_livre(pecas, Posicao(3, 2)), True)
  check.eq(casa_livre(pecas, Posicao(0, 1)), False)
  check.eq(casa_livre(pecas, Posicao(8, 1)), False)
}

/// Cria uma partida para *num_jogadores* jogadores, cada um no seu canto,
/// sem pontos e sem frutas na grade.
pub fn nova_partida() -> Partida {
  Partida(
    jogadores: [Jogador(0, Brancas, Posicao(0, 0), None),
      Jogador(0, Pretas, Posicao(0,0), None)],
    pecas: [Peca(Pretas, Posicao(0, 1)), Peca(Pretas, Posicao(0, 3)),
    Peca(Pretas, Posicao(0, 5)), Peca(Pretas, Posicao(0, 7)), Peca(Pretas, Posicao(1, 0)),
    Peca(Pretas, Posicao(1, 2)), Peca(Pretas, Posicao(1, 4)), Peca(Pretas, Posicao(1, 6)),
    Peca(Pretas, Posicao(2, 1)), Peca(Pretas, Posicao(2, 3)), Peca(Pretas, Posicao(2, 5)),
    Peca(Pretas, Posicao(2, 7)),
    Peca(Pretas, Posicao(15, 0)), Peca(Pretas, Posicao(15, 2)),
    Peca(Pretas, Posicao(15, 4)), Peca(Pretas, Posicao(15, 6)), Peca(Pretas, Posicao(14, 1)),
    Peca(Pretas, Posicao(14, 3)), Peca(Pretas, Posicao(14, 5)), Peca(Pretas, Posicao(14, 7)),
    Peca(Pretas, Posicao(13, 0)), Peca(Pretas, Posicao(13, 2)), Peca(Pretas, Posicao(13, 4)),
    Peca(Pretas, Posicao(13, 6))],
    vez: Brancas
    tiques_restantes: duracao
  )
}

pub fn nova_partida_examples() {
  check.eq(
    nova_partida(),
    Partida(
      [Jogador(0, Brancas, Posicao(0, 0), None),
      Jogador(0, Pretas, Posicao(0,0), None)],
      [Peca(Pretas, Posicao(0, 1)), Peca(Pretas, Posicao(0, 3)),
      Peca(Pretas, Posicao(0, 5)), Peca(Pretas, Posicao(0, 7)), Peca(Pretas, Posicao(1, 0)),
      Peca(Pretas, Posicao(1, 2)), Peca(Pretas, Posicao(1, 4)), Peca(Pretas, Posicao(1, 6)),
      Peca(Pretas, Posicao(2, 1)), Peca(Pretas, Posicao(2, 3)), Peca(Pretas, Posicao(2, 5)),
      Peca(Pretas, Posicao(2, 7)),
      Peca(Pretas, Posicao(15, 0)), Peca(Pretas, Posicao(15, 2)),
      Peca(Pretas, Posicao(15, 4)), Peca(Pretas, Posicao(15, 6)), Peca(Pretas, Posicao(14, 1)),
      Peca(Pretas, Posicao(14, 3)), Peca(Pretas, Posicao(14, 5)), Peca(Pretas, Posicao(14, 7)),
      Peca(Pretas, Posicao(13, 0)), Peca(Pretas, Posicao(13, 2)), Peca(Pretas, Posicao(13, 4)),
      Peca(Pretas, Posicao(13, 6))],
      Brancas, 1800)
  )
  check.eq(nova_partida().jogadores, [
    Jogador(0, Brancas, Posicao(0, 0), None),
    Jogador(0, Pretas, Posicao(0, 0), None)
  ])
}

// ----------------------------------------------------------------------
// Teclas
// ----------------------------------------------------------------------

/// Trata a *tecla* pressionada pelo jogador *jogador*. Na abertura e no
/// resultado, Enter começa uma nova partida. Na partida, as setas movem o
/// personagem do jogador. As outras teclas não mudam o estado.
pub fn trata_tecla(estado: Estado, jogador: Cor, tecla: key.Event) -> Estado {
  case estado, tecla {
    Abertura, key.Enter -> EmPartida(nova_partida())
    Resultado(jogadores, resultado), key.Enter -> EmPartida(nova_partida())
    EmPartida(partida), key.Enter ->
      case list.find(partida.jogadores, fn(j) { j.cor == jogador }) {
        Error(_) -> estado

        Ok(j) ->
          case j.selecionada {
            None -> EmPartida(seleciona_peca(partida))
            Some(_) -> EmPartida(confirma_movimento(partida, jogador))
          }
      }
    EmPartida(partida), _ ->
      case list.find(partida.jogadores, fn(j) { j.cor == jogador }) {
        Error(_) -> estado
        Ok(j) ->
          case j.selecionada {
            None ->
              case direcao(tecla) {
                Some(d) -> EmPartida(move_ponteiro(partida, jogador, d))
                None -> estado
              }
            Some(_) ->
              case direcao_peca_selecionada(tecla) {
                Some(d) -> EmPartida(move_ponteiro(partida, jogador, d))
                None -> estado
              }
          }
      }
    _, _ -> estado
  }
}

pub fn trata_tecla_examples() {
  let p = nova_partida()
  let fim = [
    Jogador(1, Brancas, Posicao(3, 4), None),
    Jogador(2, Pretas, Posicao(7, 1), None),
  ]
  let jogadores = [
    Jogador(0, Brancas, Posicao(0, 0), None),
    Jogador(0, Pretas, Posicao(0, 0), None),
  ]
  let partida =
    Partida(
      [
        Jogador(0, Brancas, Posicao(0, 0), None),
        Jogador(0, Pretas, Posicao(0, 0), None),
      ],
      [
        Peca(Brancas, Posicao(0, 1)),
        Peca(Brancas, Posicao(0, 3)),
        Peca(Brancas, Posicao(0, 5)),
        Peca(Brancas, Posicao(0, 7)),
        Peca(Brancas, Posicao(1, 0)),
        Peca(Brancas, Posicao(1, 2)),
        Peca(Brancas, Posicao(1, 4)),
        Peca(Brancas, Posicao(1, 6)),
        Peca(Brancas, Posicao(2, 1)),
        Peca(Brancas, Posicao(2, 3)),
        Peca(Brancas, Posicao(2, 5)),
        Peca(Brancas, Posicao(2, 7)),
        Peca(Pretas, Posicao(7, 0)),
        Peca(Pretas, Posicao(7, 2)),
        Peca(Pretas, Posicao(7, 4)),
        Peca(Pretas, Posicao(7, 6)),
        Peca(Pretas, Posicao(6, 1)),
        Peca(Pretas, Posicao(6, 3)),
        Peca(Pretas, Posicao(6, 5)),
        Peca(Pretas, Posicao(6, 7)),
        Peca(Pretas, Posicao(5, 0)),
        Peca(Pretas, Posicao(5, 2)),
        Peca(Pretas, Posicao(5, 4)),
        Peca(Pretas, Posicao(5, 6)),
      ],
      Brancas,
      1800,
    )
  // abertura
  check.eq(trata_tecla(Abertura, Brancas, key.Enter), EmPartida(partida))
  check.eq(trata_tecla(Abertura, Pretas, key.ArrowUp), Abertura)
  // partida
  check.eq(
    trata_tecla(EmPartida(partida), Brancas, key.ArrowRight),
    EmPartida(move_ponteiro(partida, Brancas, Direita)),
  )
  check.eq(
    trata_tecla(EmPartida(partida), Pretas, key.Enter),
    EmPartida(partida),
  )
  // resultado
  check.eq(
    trata_tecla(Resultado(fim, Derrota), Brancas, key.Enter),
    EmPartida(partida),
  )
  check.eq(
    trata_tecla(Resultado(fim, Vitoria), Pretas, key.Char("a")),
    Resultado(fim, Vitoria),
  )
}

/// Devolve a direção correspondente a *tecla*, ou None se *tecla* não é uma
/// seta.
pub fn direcao(tecla: key.Event) -> Option(Direcao) {
  case tecla {
    key.ArrowUp -> Some(Cima)
    key.ArrowDown -> Some(Baixo)
    key.ArrowLeft -> Some(Esquerda)
    key.ArrowRight -> Some(Direita)
    _ -> None
  }
}

pub fn direcao_examples() {
  check.eq(direcao(key.ArrowUp), Some(Cima))
  check.eq(direcao(key.ArrowDown), Some(Baixo))
  check.eq(direcao(key.ArrowLeft), Some(Esquerda))
  check.eq(direcao(key.ArrowRight), Some(Direita))
  check.eq(direcao(key.Enter), None)
}

/// Devolve a direção correspondente a *tecla*, ou None se *tecla* for uma
/// tecla pra cima, baixo ou não for uma seta
pub fn direcao_peca_selecionada(tecla: key.Event) -> Option(Direcao) {
  case tecla {
    key.ArrowLeft -> Some(Esquerda)
    key.ArrowRight -> Some(Direita)
    _ -> None
  }
}

pub fn direcao_peca_selecionada_examples() {
  check.eq(direcao_peca_selecionada(key.ArrowLeft), Some(Esquerda))
  check.eq(direcao_peca_selecionada(key.ArrowRight), Some(Direita))
  check.eq(direcao_peca_selecionada(key.ArrowUp), None)
  check.eq(direcao_peca_selecionada(key.ArrowDown), None)
  check.eq(direcao_peca_selecionada(key.Enter), None)
}

/// Devolve a casa vizinha de *p* na direção *d*. Se a vizinha está fora da
/// grade, devolve *p*.
pub fn desloca_ponteiro(p: Posicao, d: Direcao) -> Posicao {
  let destino = case d {
    Cima -> Posicao(p.linha + 1, p.coluna)
    Baixo -> Posicao(p.linha - 1, p.coluna)
    Esquerda -> Posicao(p.linha, p.coluna - 1)
    Direita -> Posicao(p.linha, p.coluna + 1)
  }
  Posicao(
    int.clamp(destino.linha, 0, tamanho - 1),
    int.clamp(destino.coluna, 0, tamanho - 1),
  )
}

pub fn desloca_ponteiro_examples() {
  check.eq(desloca_ponteiro(Posicao(4, 5), Cima), Posicao(5, 5))
  check.eq(desloca_ponteiro(Posicao(4, 5), Baixo), Posicao(3, 5))
  check.eq(desloca_ponteiro(Posicao(4, 5), Esquerda), Posicao(4, 4))
  check.eq(desloca_ponteiro(Posicao(4, 5), Direita), Posicao(4, 6))
  // bordas
  check.eq(desloca_ponteiro(Posicao(7, 5), Cima), Posicao(7, 5))
  check.eq(desloca_ponteiro(Posicao(0, 5), Baixo), Posicao(0, 5))
  check.eq(desloca_ponteiro(Posicao(4, 0), Esquerda), Posicao(4, 0))
  check.eq(desloca_ponteiro(Posicao(4, 7), Direita), Posicao(4, 7))
}

/// Move o cursor normalmente ou pelas diagonais quando há uma peça selecionada.
pub fn move_ponteiro(partida: Partida, cor: Cor, d: Direcao) -> Partida {
  let jogadores_atualizados =
    list.map(partida.jogadores, fn(j) {
      case j.cor == cor {
        True -> {
          let destino = case j.selecionada {
            None -> desloca_ponteiro(j.cursor, d)
            Some(_) -> desloca_ponteiro_diagonal(j.cursor, cor, d)
          }

          Jogador(..j, cursor: destino)
        }
        False -> j
      }
    })

  Partida(..partida, jogadores: jogadores_atualizados)
}

pub fn move_ponteiro_examples() {
  let p =
    Partida(
      [
        Jogador(0, Brancas, Posicao(0, 0), None),
        Jogador(0, Pretas, Posicao(0, 0), None),
      ],
      [
        Peca(Pretas, Posicao(0, 1)),
        Peca(Pretas, Posicao(0, 3)),
        Peca(Pretas, Posicao(0, 5)),
        Peca(Pretas, Posicao(0, 7)),
        Peca(Pretas, Posicao(1, 0)),
        Peca(Pretas, Posicao(1, 2)),
        Peca(Pretas, Posicao(1, 4)),
        Peca(Pretas, Posicao(1, 6)),
        Peca(Pretas, Posicao(2, 1)),
        Peca(Pretas, Posicao(2, 3)),
        Peca(Pretas, Posicao(2, 5)),
        Peca(Pretas, Posicao(2, 7)),
        Peca(Pretas, Posicao(7, 0)),
        Peca(Pretas, Posicao(7, 2)),
        Peca(Pretas, Posicao(7, 4)),
        Peca(Pretas, Posicao(7, 6)),
        Peca(Pretas, Posicao(6, 1)),
        Peca(Pretas, Posicao(6, 3)),
        Peca(Pretas, Posicao(6, 5)),
        Peca(Pretas, Posicao(6, 7)),
        Peca(Pretas, Posicao(5, 0)),
        Peca(Pretas, Posicao(5, 2)),
        Peca(Pretas, Posicao(5, 4)),
        Peca(Pretas, Posicao(5, 6)),
      ],
      Brancas,
      1800,
    )
  check.eq(move_ponteiro(p, Pretas, Baixo), p)
  check.eq(
    move_ponteiro(p, Pretas, Cima),
    Partida(
      jogadores: [
        Jogador(0, Brancas, Posicao(0, 0), None),
        Jogador(0, Pretas, Posicao(1, 0), None),
      ],
      pecas: [
        Peca(Pretas, Posicao(0, 1)),
        Peca(Pretas, Posicao(0, 3)),
        Peca(Pretas, Posicao(0, 5)),
        Peca(Pretas, Posicao(0, 7)),
        Peca(Pretas, Posicao(1, 0)),
        Peca(Pretas, Posicao(1, 2)),
        Peca(Pretas, Posicao(1, 4)),
        Peca(Pretas, Posicao(1, 6)),
        Peca(Pretas, Posicao(2, 1)),
        Peca(Pretas, Posicao(2, 3)),
        Peca(Pretas, Posicao(2, 5)),
        Peca(Pretas, Posicao(2, 7)),
        Peca(Pretas, Posicao(7, 0)),
        Peca(Pretas, Posicao(7, 2)),
        Peca(Pretas, Posicao(7, 4)),
        Peca(Pretas, Posicao(7, 6)),
        Peca(Pretas, Posicao(6, 1)),
        Peca(Pretas, Posicao(6, 3)),
        Peca(Pretas, Posicao(6, 5)),
        Peca(Pretas, Posicao(6, 7)),
        Peca(Pretas, Posicao(5, 0)),
        Peca(Pretas, Posicao(5, 2)),
        Peca(Pretas, Posicao(5, 4)),
        Peca(Pretas, Posicao(5, 6)),
      ],
      vez: Brancas,
      tiques_restantes: 1800,
    ),
  )
}

/// Desloca o cursor diagonalmente, respeitando a direção da peça.
pub fn desloca_ponteiro_diagonal(p: Posicao, cor: Cor, d: Direcao) -> Posicao {
  let linha = case cor {
    Brancas -> p.linha + 1
    Pretas -> p.linha - 1
  }

  let coluna = case d {
    Esquerda -> p.coluna - 1
    Direita -> p.coluna + 1
    _ -> p.coluna
  }

  Posicao(int.clamp(linha, 0, tamanho - 1), int.clamp(coluna, 0, tamanho - 1))
}

pub fn desloca_ponteiro_diagonal_examples() {
  check.eq(
    desloca_ponteiro_diagonal(Posicao(2, 3), Brancas, Direita),
    Posicao(3, 4),
  )
  check.eq(
    desloca_ponteiro_diagonal(Posicao(2, 3), Brancas, Esquerda),
    Posicao(3, 2),
  )
  check.eq(
    desloca_ponteiro_diagonal(Posicao(5, 3), Pretas, Direita),
    Posicao(4, 4),
  )
  check.eq(
    desloca_ponteiro_diagonal(Posicao(5, 3), Pretas, Esquerda),
    Posicao(4, 2),
  )
  check.eq(
    desloca_ponteiro_diagonal(Posicao(7, 7), Brancas, Direita),
    Posicao(7, 7),
  )
}

/// Confirma o movimento da peça selecionada.
pub fn confirma_movimento(partida: Partida, cor: Cor) -> Partida {
  case list.find(partida.jogadores, fn(j) { j.cor == cor }) {
    Error(_) -> partida

    Ok(jogador) -> {
      case jogador.selecionada {
        None -> seleciona_peca(partida)

        Some(origem) -> {
          let destino = jogador.cursor

          case
            list.find(partida.pecas, fn(p) {
              p.posicao == origem && p.jogador == cor
            })
          {
            Error(_) -> partida

            Ok(peca) -> {
              let delta_linha = case cor {
                Brancas -> 1
                Pretas -> -1
              }

              let diferenca_linha = destino.linha - origem.linha
              let diferenca_coluna =
                int.absolute_value(destino.coluna - origem.coluna)

              let movimento_simples =
                diferenca_linha == delta_linha
                && diferenca_coluna == 1
                && !ocupada(partida.pecas, destino)

              let movimento_captura =
                diferenca_linha == delta_linha * 2 && diferenca_coluna == 2

              case movimento_simples {
                True -> finaliza_movimento(partida, peca, destino, None)

                False -> {
                  case movimento_captura {
                    False -> partida

                    True -> {
                      let meio =
                        Posicao(
                          origem.linha + delta_linha,
                          { origem.coluna + destino.coluna } / 2,
                        )

                      case
                        list.find(partida.pecas, fn(p) {
                          p.posicao == meio && p.jogador != cor
                        })
                      {
                        Error(_) -> partida
                        Ok(vitima) ->
                          case ocupada(partida.pecas, destino) {
                            True -> partida
                            False ->
                              finaliza_movimento(
                                partida,
                                peca,
                                destino,
                                Some(vitima),
                              )
                          }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

pub fn confirma_movimento_examples() {
  let peca = Peca(Brancas, Posicao(2, 1))
  let partida =
    Partida(
      jogadores: [
        Jogador(0, Brancas, Posicao(3, 2), Some(Posicao(2, 1))),
        Jogador(0, Pretas, Posicao(5, 0), None),
      ],
      pecas: [peca],
      vez: Brancas,
      tiques_restantes: 1800,
    )
  let esperado =
    Partida(
      ..partida,
      jogadores: [
        Jogador(0, Brancas, Posicao(3, 2), None),
        Jogador(0, Pretas, Posicao(5, 0), None),
      ],
      pecas: [Peca(Brancas, Posicao(3, 2))],
      vez: Pretas,
    )
  let bloqueada =
    Partida(..partida, pecas: [
      peca,
      Peca(Pretas, Posicao(3, 2)),
    ])

  check.eq(confirma_movimento(partida, Brancas), esperado)
  check.eq(confirma_movimento(bloqueada, Brancas), bloqueada)
}

/// Atualiza a posição da peça e remove a vítima, se houver.
pub fn finaliza_movimento(
  partida: Partida,
  peca: Peca,
  destino: Posicao,
  vitima: Option(Peca),
) -> Partida {
  let pecas_atualizadas =
    partida.pecas
    |> list.filter(fn(p) {
      case vitima {
        None -> True
        Some(v) -> p != v
      }
    })
    |> list.map(fn(p) {
      case p == peca {
        True -> Peca(..p, posicao: destino)
        False -> p
      }
    })

  let jogadores_atualizados =
    list.map(partida.jogadores, fn(j) {
      case j.cor == partida.vez {
        True -> Jogador(..j, cursor: destino, selecionada: None)
        False -> j
      }
    })

  Partida(
    ..partida,
    pecas: pecas_atualizadas,
    jogadores: jogadores_atualizados,
    vez: case partida.vez {
      Brancas -> Pretas
      Pretas -> Brancas
    },
  )
}

pub fn finaliza_movimento_examples() {
  let atacante = Peca(Brancas, Posicao(2, 1))
  let vitima = Peca(Pretas, Posicao(3, 2))
  let partida =
    Partida(
      jogadores: [
        Jogador(0, Brancas, Posicao(2, 1), Some(Posicao(2, 1))),
        Jogador(0, Pretas, Posicao(5, 0), None),
      ],
      pecas: [atacante, vitima],
      vez: Brancas,
      tiques_restantes: 1800,
    )
  let simples = finaliza_movimento(partida, atacante, Posicao(3, 0), None)
  let com_captura =
    finaliza_movimento(partida, atacante, Posicao(4, 3), Some(vitima))

  check.eq(simples.pecas, [Peca(Brancas, Posicao(3, 0)), vitima])
  check.eq(simples.vez, Pretas)
  check.eq(com_captura.pecas, [Peca(Brancas, Posicao(4, 3))])
  check.eq(com_captura.vez, Pretas)
}

/// Devolve True se algum dos *jogadores* está na casa *p*, False caso
/// contrário.
pub fn ocupada(tabuleiro: List(Peca), p: Posicao) -> Bool {
  list.any(tabuleiro, fn(t) { t.posicao == p })
}

pub fn ocupada_examples() {
  let js = [Peca(Pretas, Posicao(0, 0)), Peca(Brancas, Posicao(3, 4))]
  check.eq(ocupada([], Posicao(0, 0)), False)
  check.eq(ocupada(js, Posicao(3, 4)), True)
  check.eq(ocupada(js, Posicao(4, 3)), False)
}

/// Seleciona a peça que está na posição do cursor do jogador da vez em *partida*.
pub fn seleciona_peca(partida: Partida) -> Partida {
  case list.find(partida.jogadores, fn(j) { j.cor == partida.vez }) {
    Error(_) -> partida

    Ok(jogador) -> {
      case jogador.selecionada {
        Some(_) -> partida

        None -> {
          case
            list.find(partida.pecas, fn(p) {
              p.posicao == jogador.cursor && p.jogador == partida.vez
            })
          {
            Error(_) -> partida

            Ok(_) -> {
              let jogadores_atualizados =
                list.map(partida.jogadores, fn(j) {
                  case j.cor == partida.vez {
                    True -> Jogador(..j, selecionada: Some(jogador.cursor))
                    False -> j
                  }
                })

              Partida(..partida, jogadores: jogadores_atualizados)
            }
          }
        }
      }
    }
  }
}

pub fn seleciona_peca_examples() {
  let partida = nova_partida()
  let jogadores = [
    Jogador(0, Brancas, Posicao(2, 1), None),
    Jogador(0, Pretas, Posicao(5, 0), None),
  ]
  let p = Partida(..partida, jogadores: jogadores)
  let esperado =
    Partida(..p, jogadores: [
      Jogador(0, Brancas, Posicao(2, 1), Some(Posicao(2, 1))),
      Jogador(0, Pretas, Posicao(5, 0), None),
    ])
  let vazio =
    Partida(..partida, jogadores: [
      Jogador(0, Brancas, Posicao(3, 3), None),
      Jogador(0, Pretas, Posicao(5, 0), None),
    ])

  check.eq(seleciona_peca(p), esperado)
  check.eq(seleciona_peca(vazio), vazio)
}

/// Devolve a casa na diagonal de *p* na direcao *d*. Se a diagonal está fora da
/// grade, devolve *p*.
pub fn desloca_peca_selecionada(p: Peca, d: Direcao) -> Posicao {
  let destino = case p.jogador {
    Brancas -> {
      case d {
        Esquerda -> Posicao(p.posicao.linha + 1, p.posicao.coluna - 1)
        Direita -> Posicao(p.posicao.linha + 1, p.posicao.coluna + 1)
        _ -> p.posicao
      }
    }
    Pretas -> {
      case d {
        Esquerda -> Posicao(p.posicao.linha - 1, p.posicao.coluna - 1)
        Direita -> Posicao(p.posicao.linha - 1, p.posicao.coluna + 1)
        _ -> p.posicao
      }
    }
  }
  case
    destino.linha < 0
    || destino.linha >= tamanho
    || destino.coluna < 0
    || destino.coluna >= tamanho
  {
    True -> p.posicao
    False -> destino
  }
}

pub fn desloca_peca_selecionada_examples() {
  let pecab = Peca(Brancas, Posicao(1, 1))
  let pecap = Peca(Pretas, Posicao(5, 1))
  let pecax = Peca(Brancas, Posicao(7, 1))
  let pecay = Peca(Pretas, Posicao(0, 0))
  check.eq(desloca_peca_selecionada(pecab, Direita), Posicao(2, 2))
  check.eq(desloca_peca_selecionada(pecap, Esquerda), Posicao(4, 0))
  check.eq(desloca_peca_selecionada(pecax, Esquerda), Posicao(7, 1))
  check.eq(desloca_peca_selecionada(pecay, Direita), Posicao(0, 0))
}

/// Move a peça *peca* da *partida* na direção *d*.
/// Caso o destino esteja ocupado por uma peça de mesma cor o movimento
/// não acontece, se ele estiver ocupado por uma peça de outra cor, o
/// movimento ocorre se a casa na diagonal seguinte na mesma direção
/// estiver deocupada, assim ocorrendo o consumo da peca
pub fn move_peca_selecionada(
  peca: Peca,
  partida: Partida,
  d: Direcao,
) -> Partida {
  let destino = desloca_peca_selecionada(peca, d)
  case ocupada(partida.pecas, destino) {
    False -> {
      let pecas_atualizadas =
        list.map(partida.pecas, fn(p) {
          case p == peca {
            True -> Peca(..p, posicao: destino)
            False -> p
          }
        })
      let jogadores_atualizados =
        list.map(partida.jogadores, fn(j) {
          case j.cor == peca.jogador {
            True -> Jogador(..j, cursor: destino, selecionada: None)
            False -> j
          }
        })
      Partida(
        ..partida,
        pecas: pecas_atualizadas,
        jogadores: jogadores_atualizados,
      )
    }

    True -> {
      case list.find(partida.pecas, fn(p) { p.posicao == destino }) {
        Error(_) -> partida

        Ok(vitima) ->
          case vitima.jogador == peca.jogador {
            True -> partida
            False -> captura(peca, vitima, partida)
          }
      }
    }
  }
}

pub fn move_peca_selecionada_examples() {
  let peca = Peca(Brancas, Posicao(2, 1))
  let partida =
    Partida(
      jogadores: [
        Jogador(0, Brancas, Posicao(2, 1), Some(Posicao(2, 1))),
        Jogador(0, Pretas, Posicao(5, 0), None),
      ],
      pecas: [peca],
      vez: Brancas,
      tiques_restantes: 1800,
    )
  let movimento = move_peca_selecionada(peca, partida, Direita)
  let bloqueada =
    Partida(..partida, pecas: [
      peca,
      Peca(Brancas, Posicao(3, 2)),
    ])

  check.eq(movimento.pecas, [Peca(Brancas, Posicao(3, 2))])
  check.eq(move_peca_selecionada(peca, bloqueada, Direita), bloqueada)
}

/// A peça *atacante* captura a peça *vitima* da *partida*.
/// *atacante* se move para a diagonal da peça atacada, na mesma direção da
/// captura, e *vitima* some da partida
pub fn captura(atacante: Peca, vitima: Peca, partida: Partida) -> Partida {
  let deslocamento_linha = case atacante.jogador {
    Brancas -> 1
    Pretas -> -1
  }
  let deslocamento_coluna = case
    vitima.posicao.coluna > atacante.posicao.coluna
  {
    True -> 1
    False -> -1
  }
  let destino =
    Posicao(
      vitima.posicao.linha + deslocamento_linha,
      vitima.posicao.coluna + deslocamento_coluna,
    )
  let captura_valida =
    vitima.jogador != atacante.jogador
    && vitima.posicao.linha == atacante.posicao.linha + deslocamento_linha
    && {
      vitima.posicao.coluna == atacante.posicao.coluna + 1
      || vitima.posicao.coluna == atacante.posicao.coluna - 1
    }
    && destino.linha >= 0
    && destino.linha < tamanho
    && destino.coluna >= 0
    && destino.coluna < tamanho
    && !ocupada(partida.pecas, destino)

  case captura_valida {
    True -> {
      let pecas_atualizadas =
        partida.pecas
        |> list.filter(fn(p) { p != vitima })
        |> list.map(fn(p) {
          case p == atacante {
            True -> Peca(atacante.jogador, destino)
            False -> p
          }
        })

      Partida(..partida, pecas: pecas_atualizadas)
    }
    False -> partida
  }
}

pub fn captura_examples() {
  let atacante = Peca(Brancas, Posicao(2, 1))
  let vitima = Peca(Pretas, Posicao(3, 2))
  let partida =
    Partida(
      jogadores: [
        Jogador(0, Brancas, Posicao(2, 1), None),
        Jogador(0, Pretas, Posicao(5, 0), None),
      ],
      pecas: [atacante, vitima],
      vez: Brancas,
      tiques_restantes: 1800,
    )
  let resultado = captura(atacante, vitima, partida)
  let bloqueada =
    Partida(..partida, pecas: [
      atacante,
      vitima,
      Peca(Brancas, Posicao(4, 3)),
    ])

  check.eq(resultado.pecas, [Peca(Brancas, Posicao(4, 3))])
  check.eq(captura(atacante, vitima, bloqueada), bloqueada)
}

// ----------------------------------------------------------------------
// Tiques
// ----------------------------------------------------------------------

/// Avança *estado* em um tique.
pub fn tique(estado: Estado) -> Estado {
  case estado {
    EmPartida(partida) -> avanca(partida)
    _ -> estado
  }
}

pub fn tique_examples() {
  let partida = nova_partida()
  // Na abertura, o estado não muda.
  check.eq(tique(Abertura), Abertura)
  // Durante a partida, o tempo diminui.
  check.eq(tique(EmPartida(partida)), avanca(partida))
}

/// Avança a *partida* em um tique.
/// Quando o tempo acaba, verifica quem possui mais peças.
pub fn avanca(partida: Partida) -> Estado {
  let tiques_restantes = partida.tiques_restantes - 1

  case tiques_restantes <= 0 {
    True -> {
      let brancas =
        partida.pecas
        |> list.filter(fn(p) { p.jogador == Brancas })
        |> list.length
      let pretas =
        partida.pecas
        |> list.filter(fn(p) { p.jogador == Pretas })
        |> list.length
      case brancas > pretas {
        True -> Resultado(partida.jogadores, Vitoria)
        False -> {
          case pretas > brancas {
            True -> Resultado(partida.jogadores, Derrota)
            False -> Resultado(partida.jogadores, Empate)
          }
        }
      }
    }
    False -> EmPartida(Partida(..partida, tiques_restantes: tiques_restantes))
  }
}

pub fn avanca_examples() {
  let partida = nova_partida()
  let empate = Partida(partida.jogadores, [], Brancas, 1)
  let vitoria_brancas =
    Partida(
      partida.jogadores,
      [
        Peca(Brancas, Posicao(2, 1)),
        Peca(Brancas, Posicao(2, 3)),
        Peca(Pretas, Posicao(5, 0)),
      ],
      Brancas,
      1,
    )
  let vitoria_pretas =
    Partida(
      partida.jogadores,
      [
        Peca(Brancas, Posicao(2, 1)),
        Peca(Pretas, Posicao(5, 0)),
        Peca(Pretas, Posicao(5, 2)),
      ],
      Brancas,
      1,
    )

  check.eq(
    avanca(partida),
    EmPartida(Partida(..partida, tiques_restantes: 1799)),
  )
  check.eq(avanca(empate), Resultado(partida.jogadores, Empate))
  check.eq(avanca(vitoria_brancas), Resultado(partida.jogadores, Vitoria))
  check.eq(avanca(vitoria_pretas), Resultado(partida.jogadores, Derrota))
}

// ----------------------------------------------------------------------
// Imagens
// ----------------------------------------------------------------------

/// Fundo de uma casa clara.
fn fundo_claro() -> Image {
  image.rectangle(lado_casa, lado_casa, fill.beige)
}

/// Fundo de uma casa escura.
fn fundo_escuro() -> Image {
  image.rectangle(lado_casa, lado_casa, fill.brown)
}

/// Peça Branca: círculo branco com contorno preto.
fn imagem_peca_branca() -> Image {
  let raio = lado_casa * 2 / 5
  sobrepor(image.circle(raio, stroke.black), image.circle(raio, fill.white))
}

/// Peça Preta: círculo preto com contorno branco.
fn imagem_peca_preta() -> Image {
  let raio = lado_casa * 2 / 5
  sobrepor(image.circle(raio, stroke.white), image.circle(raio, fill.black))
}

/// Coloca `topo` sobre `fundo`, alinhados pelo centro.
fn sobrepor(topo: Image, fundo: Image) -> Image {
  image.overlay(topo, fundo)
}

fn ao_lado(esquerda: Image, direita: Image) -> Image {
  image.beside(esquerda, direita)
}

fn empilhar(cima: Image, baixo: Image) -> Image {
  image.above(cima, baixo)
}

fn imagem_vazia() -> Image {
  image.empty
}

// ----------------------------------------------------------------------
// Desenhos
// ----------------------------------------------------------------------

/// Desenha uma peça (sem a casa).
pub fn desenhar_peca(dono: Cor) -> Image {
  case dono {
    Brancas -> imagem_peca_brancas()
    Pretas -> imagem_peca_pretas()
  }
}

/// Desenha uma casa com a peça que está nela (se houver).
pub fn desenhar_casa(
  pos: Posicao,
  pecas: List(Peca),
) -> Image {
  let fundo = case casa_escura(pos) {
    True -> fundo_escuro()
    False -> fundo_claro()
  }
  let com_peca = case peca_em(pecas, pos) {
    Some(peca) -> sobrepor(desenhar_peca(peca.dono), fundo)
    None -> fundo
  }
}

/// Índices de linha ou coluna na ordem em que o jogador os vê: as Brancas
/// veem o tabuleiro como ele é; as Pretas o veem girado em 180 graus, então
/// a ordem é invertida (tanto nas linhas quanto nas colunas).
pub fn ordem_de_visao(jogador: Cor) -> List(Int) {
  let normal = sequencia(0, tamanho - 1)
  case jogador {
    Brancas -> normal
    Pretas -> list.reverse(normal)
  }
}

pub fn ordem_de_visao_examples() {
  check.eq(ordem_de_visao(Brancas), [0, 1, 2, 3, 4, 5, 6, 7])
  check.eq(ordem_de_visao(Pretas), [7, 6, 5, 4, 3, 2, 1, 0])
}

/// Desenha uma linha do tabuleiro, percorrendo as colunas na ordem dada.
pub fn desenhar_linha(
  lin: Int,
  colunas: List(Int),
  pecas: List(Peca)
) -> Image {
  colunas
  |> list.map(fn(col) {
    desenhar_casa(
      Posicao(lin, col),
      pecas
    )
  })
  |> list.fold(imagem_vazia(), ao_lado)
}

/// Desenha o tabuleiro inteiro do ponto de vista de `jogador`.
pub fn desenhar_tabuleiro(
  pecas: List(Peca),
  jogador: Cor,
) -> Image {
  let ordem = ordem_de_visao(jogador)
  ordem
  |> list.map(fn(lin) {
    desenhar_linha(
      lin,
      ordem,
      pecas
    )
  })
  |> list.fold(imagem_vazia(), empilhar)
}
