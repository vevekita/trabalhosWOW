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

/// Trata a *tecla* pressionada pelo jogador *numero*. Na abertura e no
/// resultado, Enter começa uma nova partida. Na partida, as setas movem o
/// personagem do jogador. As outras teclas não mudam o estado.
pub fn trata_tecla(estado: Estado, numero: Int, tecla: key.Event) -> Estado {
  case estado, tecla {
    Abertura(gerador, imagens, num_jogadores), key.Enter ->
      EmPartida(gerador, imagens, nova_partida(num_jogadores))
    Resultado(gerador, imagens, jogadores), key.Enter ->
      EmPartida(gerador, imagens, nova_partida(list.length(jogadores)))
    EmPartida(gerador, imagens, partida), _ ->
      case direcao(tecla) {
        Some(d) -> EmPartida(gerador, imagens, move(partida, numero, d))
        None -> estado
      }
    _, _ -> estado
  }
}

pub fn trata_tecla_examples() {
  let g = random.new(0)
  let im = imagens_exemplo()
  let p = nova_partida(2)
  let fim = [Jogador(1, Posicao(3, 4), 5), Jogador(2, Posicao(8, 1), 2)]
  // abertura
  check.eq(trata_tecla(Abertura(g, im, 2), 1, key.Enter), EmPartida(g, im, p))
  check.eq(trata_tecla(Abertura(g, im, 2), 2, key.ArrowUp), Abertura(g, im, 2))
  // partida
  check.eq(
    trata_tecla(EmPartida(g, im, p), 1, key.ArrowRight),
    EmPartida(g, im, move(p, 1, Direita)),
  )
  check.eq(trata_tecla(EmPartida(g, im, p), 2, key.Enter), EmPartida(g, im, p))
  // resultado
  check.eq(
    trata_tecla(Resultado(g, im, fim), 2, key.Enter),
    EmPartida(g, im, p),
  )
  check.eq(
    trata_tecla(Resultado(g, im, fim), 1, key.Char("a")),
    Resultado(g, im, fim),
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

/// Devolve a casa vizinha de *p* na direção *d*. Se a vizinha está fora da
/// grade, devolve *p*.
pub fn desloca(p: Posicao, d: Direcao) -> Posicao {
  let destino = case d {
    Cima -> Posicao(p.lin - 1, p.col)
    Baixo -> Posicao(p.lin + 1, p.col)
    Esquerda -> Posicao(p.lin, p.col - 1)
    Direita -> Posicao(p.lin, p.col + 1)
  }
  Posicao(
    int.clamp(destino.lin, 0, tamanho - 1),
    int.clamp(destino.col, 0, tamanho - 1),
  )
}

pub fn desloca_examples() {
  check.eq(desloca(Posicao(4, 5), Cima), Posicao(3, 5))
  check.eq(desloca(Posicao(4, 5), Baixo), Posicao(5, 5))
  check.eq(desloca(Posicao(4, 5), Esquerda), Posicao(4, 4))
  check.eq(desloca(Posicao(4, 5), Direita), Posicao(4, 6))
  // bordas
  check.eq(desloca(Posicao(0, 5), Cima), Posicao(0, 5))
  check.eq(desloca(Posicao(9, 5), Baixo), Posicao(9, 5))
  check.eq(desloca(Posicao(4, 0), Esquerda), Posicao(4, 0))
  check.eq(desloca(Posicao(4, 9), Direita), Posicao(4, 9))
}

/// Devolve True se algum dos *jogadores* está na casa *p*, False caso
/// contrário.
pub fn ocupada(jogadores: List(Jogador), p: Posicao) -> Bool {
  list.any(jogadores, fn(j) { j.posicao == p })
}

pub fn ocupada_examples() {
  let js = [Jogador(1, Posicao(0, 0), 0), Jogador(2, Posicao(3, 4), 0)]
  check.eq(ocupada([], Posicao(0, 0)), False)
  check.eq(ocupada(js, Posicao(3, 4)), True)
  check.eq(ocupada(js, Posicao(4, 3)), False)
}

/// Move o jogador *numero* da *partida* uma casa na direção *d*. O
/// movimento não acontece se a casa de destino está ocupada por outro
/// jogador ou fora da grade. Se há uma fruta no destino, o jogador pega a
/// fruta e ganha um ponto.
pub fn move(partida: Partida, numero: Int, d: Direcao) -> Partida {
  case list.find(partida.jogadores, fn(j) { j.numero == numero }) {
    Error(_) -> partida
    Ok(jogador) -> {
      let destino = desloca(jogador.posicao, d)
      case ocupada(partida.jogadores, destino) {
        True -> partida
        False -> {
          let ganho = case list.contains(partida.frutas, destino) {
            True -> 1
            False -> 0
          }
          Partida(
            ..partida,
            jogadores: list.map(partida.jogadores, fn(j) {
              case j.numero == numero {
                True -> Jogador(..j, posicao: destino, pontos: j.pontos + ganho)
                False -> j
              }
            }),
            frutas: list.filter(partida.frutas, fn(f) { f != destino }),
          )
        }
      }
    }
  }
}

pub fn move_examples() {
  let p =
    Partida(
      jogadores: [Jogador(1, Posicao(0, 0), 2), Jogador(2, Posicao(0, 2), 1)],
      frutas: [Posicao(1, 0), Posicao(5, 5)],
      tiques: 100,
      proxima_fruta: 7,
    )
  // casa livre e sem fruta
  check.eq(
    move(p, 2, Baixo),
    Partida(..p, jogadores: [
      Jogador(1, Posicao(0, 0), 2),
      Jogador(2, Posicao(1, 2), 1),
    ]),
  )
  // casa com fruta
  check.eq(
    move(p, 1, Baixo),
    Partida(
      ..p,
      jogadores: [Jogador(1, Posicao(1, 0), 3), Jogador(2, Posicao(0, 2), 1)],
      frutas: [Posicao(5, 5)],
    ),
  )
  // fora da grade
  check.eq(move(p, 1, Cima), p)
  // casa ocupada por outro jogador
  check.eq(move(move(p, 1, Direita), 2, Esquerda), move(p, 1, Direita))
  // jogador que não está na partida
  check.eq(move(p, 3, Baixo), p)
}
