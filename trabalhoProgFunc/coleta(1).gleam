// Disputa de coleta
//
// Exemplo de jogo multijogador do Trabalho 01.
//
// Autor: Marco A. L. Barbosa (professor)
//
// Regras
//
// - De 2 a 4 jogadores. Cada jogador começa em um canto de uma grade de 10x10
//   casas.
// - Frutas aparecem em casas livres aleatórias. No início da partida aparece
//   uma fruta a cada 2 segundos, e o intervalo diminui até 0,5 segundo no fim.
// - O jogador que entra em uma casa com fruta pega a fruta e ganha 1 ponto.
// - Dois jogadores não podem ocupar a mesma casa.
// - A partida dura 60 segundos. Vence quem pegou mais frutas, e pode haver
//   empate.
//
// Controles
//
// - Enter: começa a partida (na abertura) ou joga novamente (no resultado).
// - Setas: movem o personagem uma casa.

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
const tamanho = 10

/// Lado de uma casa, em pixels.
const lado_casa = 40

/// Quantidade de tiques por segundo.
const tiques_por_segundo = 10

/// Duração da partida, em tiques (60 segundos).
const duracao = 600

/// Tiques entre o aparecimento de duas frutas no início da partida (2 s).
const intervalo_inicial = 20

/// Tiques entre o aparecimento de duas frutas no fim da partida (0,5 s).
const intervalo_final = 5

/// Largura da tela, em pixels.
const largura = 400

/// Altura da tela, em pixels.
const altura = 480

// ----------------------------------------------------------------------
// Tipos de dados
// ----------------------------------------------------------------------

/// Uma casa da grade. *lin* e *col* estão entre 0 e `tamanho - 1`; a casa
/// Posicao(0, 0) fica no canto superior esquerdo.
pub type Posicao {
  Posicao(lin: Int, col: Int)
}

/// Um jogador da partida. *numero* começa em 1 e define a cor e o canto
/// inicial do jogador. *pontos* é a quantidade de frutas que ele pegou.
pub type Jogador {
  Jogador(numero: Int, posicao: Posicao, pontos: Int)
}

/// Uma partida em andamento. *tiques* é a quantidade de tiques desde o início
/// da partida e *proxima_fruta* é a quantidade de tiques que faltam para a
/// próxima fruta aparecer.
pub type Partida {
  Partida(
    jogadores: List(Jogador),
    frutas: List(Posicao),
    tiques: Int,
    proxima_fruta: Int,
  )
}

/// As imagens das frutas, carregadas uma vez no início do jogo.
pub type ImagensFrutas {
  ImagensFrutas(
    maca: Image,
    banana: Image,
    uva: Image,
    morango: Image,
    laranja: Image,
  )
}

/// O estado do jogo. Todos os estados guardam o gerador de números
/// aleatórios usado para sortear a posição das frutas e as imagens das
/// frutas.
pub type Estado {
  /// Antes da primeira partida, com a quantidade de jogadores.
  Abertura(gerador: Generator, imagens: ImagensFrutas, num_jogadores: Int)
  /// Durante uma partida.
  EmPartida(gerador: Generator, imagens: ImagensFrutas, partida: Partida)
  /// Depois de uma partida, com os jogadores e os seus pontos.
  Resultado(
    gerador: Generator,
    imagens: ImagensFrutas,
    jogadores: List(Jogador),
  )
}

/// A direção de um movimento.
pub type Direcao {
  Cima
  Baixo
  Esquerda
  Direita
}

// ----------------------------------------------------------------------
// Mundo
// ----------------------------------------------------------------------

pub fn smain() {
  mworld.create_from(inicio, min: 2, max: 4, to_image: desenha)
  |> mworld.tick_rate(tiques_por_segundo)
  |> mworld.on_tick(tique)
  |> mworld.on_key_down(tecla)
}

/// Cria o estado inicial com o gerador de *env*, as imagens das frutas e a
/// quantidade de *jogadores*.
fn inicio(env: Env, jogadores: List(mworld.Player)) -> Estado {
  Abertura(env.random(env), carrega_imagens(), list.length(jogadores))
}

/// Carrega as imagens das frutas, feitas a partir dos emojis da fonte Noto
/// Color Emoji (SIL Open Font License).
fn carrega_imagens() -> ImagensFrutas {
  ImagensFrutas(
    maca: image.bitmap_data_uri("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAGSUlEQVR42qWWa4hdVxXHf2vvc859ztyZzsuEmcR20trmZc0oRPyQJhjazNQSA5MKIQURWhEjVBCJrZ3EggQpKKJUaz9IsaidUlBp8EOETG1aLSaVjObZaTLJmM4j87hz3/ees5cf7kwmNdaxnQX7sDkf1o+1/mutvYQPYZ/vWb+mdr0wOTQ6WgYErX/XPbZ+fbzB22d92RFG3F6LTKyrvaKt4ewPfvvEe0e2DeANHSYEMMtBBsD092P7tmw60hpPDseak18GePTRHg+BDY9v+l66PfbXION/J/KDrcmUdGxek8tsWZNtTseikwDtG9BFf7Ic7DC4bXffsbElkxmO+x5zxfLxo6eGdwC6/pubnk91xL9SzkWo06irqSh3NM+HLa0Ek2Pudz87MLF7QDGHBbfo0/4v4HFgQz92VTU1f7nsvVxDezLJxOfWauaYfqN1Y7ox+Uwh56ppv2Y+uWrO3J7Ja6rBetkpd6V8Odxz6kQpf/wQMjS0TIQ6gOEMIoNEN/9PdH1qdV+nuVAtVv58YZdtTCSCravjWb2zJW9NLHKVuJFq2f3lF9smHmQNMz/v6fEfO3mydrOPW4Daj10EvbCzI3VnVOm+tyloz4nzrxfnR54Y6z4Qtie/fm1HJby7Ped1NFSoieDPo58eisnG3/snpGyOng7Kr+zLj55TkAWI3gLUAYwcxp3vXXtvB/MHEp63E2s6A2OEigUT8k7Vn3k8uq158/0z0mgjStOOjrOGz77eQPOExY8LiaQyr1FxKs8zn6mdP6RL0am3eNd+jBwmmuxr/25GZ58K2lo9ilWIypy87Nzp0ap2pzxrUnpbKg1bfiyEUYWRcpHORJzuthjFVoglIpdqr7omI8n2U01PvTV913p56MKXXhoEwNmb0zjV2/Gjtnh40N63W3j4a1F0T4+Yc3+nWsmZM9WqOVsu6cVSmVk/KdXyFBXKfKIjxo4NAQ2ra6TbasSbQ7EGI4FTmqu1zHh600PnM/aB2syfFKwswi73rdq31iv+ivv21PjCfo9qWYgi+OFBmHkPJwH/mg+ZrUTUnJL2DR0pS1PCgCrq6vooddHUgcRUZ4dTbupSLLoYK97zYPbKux6DuBd2bk61h6NHWLtO2bnHUsrXtX3xJzB+FY0nMeroarZ0iV0qAVXU1RVarIzFopA6XWIfq2rLlVQw7/xHgENGQLcnpnclAu2kZ5sjiBuMgZefh1OvQyKJaL1vNVRcbeGES1H9194SIBJiyUhcEGkssttvjLaUq/SRSCpd3YofwBvH4M1j0NgEzi35EDA3HVluLiqIr0YDJ4J8vL+tLe0BJIQNeDGhISPMTcNrr0I88T7YRzUDOFFQUr0F0t7Wzs6EmFoLhRwUckJuDqYnIJ6qK79CcwrqZOFpSeJ1x4t+6Dw/ZgX362fFxOLg+YuDYcXhRRVBqwZMVHozmC+ZF9+ZKfhCHj/ATIwpVy7WgboyoCpglHLWU6oGY5h6bnZ23gBRxekYouAHShBbMexGeyjkrwXOR6igZwE1ACHyNiJab6yVw1QBXylP++QnAxFPKSmv3WiL6Wr0RyIVdPkNgP+nFQxoKEyeTapVMbMalidCd5R6lpG3x6ZPFCtuBE/EKW4lMASwyuRwinLWRhnfSEHdH75YHrmq9FvDNuzeM1SzNfdTrIiRjwjUhXxZZep0muxYTAMfybrIjTt3RIBBBjEMESnIsWv2uVIpvIQV+6Gi1IXjK1EojJ9sYO5KDBO4MIO11zV8dldx5JSj3+6FyAgo/ZhHTk8UxkvRVwExBrdcI+oiyFPwlcJ4wNgbjeTGA8R3YQbPv+Zq/zifz31bGTAw6N734i8+U9d6W59c1eA/TdWFTjFGMOiSPMiC8qYOLM96zF1KkBsPQBSxWmvE8+dcePWfteL2PZWrI1ofve7WFWMBOtnb+v22lHewPptchEEQrY/rEK0WLaVpX/ITvitO+6JOMJ5TK2KbsUxqeO6tSmX3/url8y+B3cvSMvaBS9To/a17WwL7dKri30XJUCoZKjlLlPMo5w2VmhAXwXgKAqJQUFcsOP3lq974kwez2dn/hH3wmrgA/RYtDQ+nW/bHatIrmHVOXVwMF0om+o2xbsITs0Wd2aqo85C/TWj4ys7Cu8MLtXQjjTfbvwFg1gQY+7giiAAAAABJRU5ErkJggg=="),
    banana: image.bitmap_data_uri("data:image/png;base64,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"),
    uva: image.bitmap_data_uri("data:image/png;base64,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"),
    morango: image.bitmap_data_uri("data:image/png;base64,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"),
    laranja: image.bitmap_data_uri("data:image/png;base64,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"),
  )
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

/// Devolve o canto da grade em que o jogador *numero* começa a partida.
pub fn canto(numero: Int) -> Posicao {
  case numero {
    1 -> Posicao(0, 0)
    2 -> Posicao(tamanho - 1, tamanho - 1)
    3 -> Posicao(0, tamanho - 1)
    _ -> Posicao(tamanho - 1, 0)
  }
}

pub fn canto_examples() {
  check.eq(canto(1), Posicao(0, 0))
  check.eq(canto(2), Posicao(9, 9))
  check.eq(canto(3), Posicao(0, 9))
  check.eq(canto(4), Posicao(9, 0))
}

/// Cria uma partida para *num_jogadores* jogadores, cada um no seu canto,
/// sem pontos e sem frutas na grade.
pub fn nova_partida(num_jogadores: Int) -> Partida {
  Partida(
    jogadores: list.map(sequencia(1, num_jogadores), fn(numero) {
      Jogador(numero, canto(numero), 0)
    }),
    frutas: [],
    tiques: 0,
    proxima_fruta: intervalo_inicial,
  )
}

pub fn nova_partida_examples() {
  check.eq(
    nova_partida(2),
    Partida(
      [Jogador(1, Posicao(0, 0), 0), Jogador(2, Posicao(9, 9), 0)],
      [],
      0,
      20,
    ),
  )
  check.eq(nova_partida(3).jogadores, [
    Jogador(1, Posicao(0, 0), 0),
    Jogador(2, Posicao(9, 9), 0),
    Jogador(3, Posicao(0, 9), 0),
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

// ----------------------------------------------------------------------
// Tiques
// ----------------------------------------------------------------------

/// Avança o *estado* em um tique. Só a partida muda com o tempo.
pub fn tique(estado: Estado) -> Estado {
  case estado {
    EmPartida(gerador, imagens, partida) -> avanca(gerador, imagens, partida)
    _ -> estado
  }
}

pub fn tique_examples() {
  let g = random.new(0)
  let im = imagens_exemplo()
  let fim = [Jogador(1, Posicao(3, 4), 5), Jogador(2, Posicao(8, 1), 2)]
  check.eq(tique(Abertura(g, im, 3)), Abertura(g, im, 3))
  check.eq(tique(Resultado(g, im, fim)), Resultado(g, im, fim))
  let p = Partida(fim, [], 10, 5)
  check.eq(tique(EmPartida(g, im, p)), avanca(g, im, p))
}

/// Avança a *partida* em um tique, usando *gerador* para sortear a posição
/// de uma nova fruta. As *imagens* passam para o próximo estado. Quando o
/// tempo acaba, a partida termina. Quando chega a hora, uma fruta aparece e
/// começa a contagem para a próxima.
pub fn avanca(
  gerador: Generator,
  imagens: ImagensFrutas,
  partida: Partida,
) -> Estado {
  let tiques = partida.tiques + 1
  case tiques >= duracao, partida.proxima_fruta <= 1 {
    True, _ -> Resultado(gerador, imagens, partida.jogadores)
    False, True -> {
      let #(frutas, gerador) = sorteia_fruta(gerador, partida)
      EmPartida(
        gerador,
        imagens,
        Partida(
          ..partida,
          frutas:,
          tiques:,
          proxima_fruta: intervalo_fruta(tiques),
        ),
      )
    }
    False, False ->
      EmPartida(
        gerador,
        imagens,
        Partida(..partida, tiques:, proxima_fruta: partida.proxima_fruta - 1),
      )
  }
}

pub fn avanca_examples() {
  let g = random.new(0)
  let im = imagens_exemplo()
  let js = [Jogador(1, Posicao(0, 0), 3), Jogador(2, Posicao(9, 9), 4)]
  let p = Partida(js, [Posicao(5, 5)], 100, 7)
  // nada acontece além da contagem
  check.eq(
    avanca(g, im, p),
    EmPartida(g, im, Partida(js, [Posicao(5, 5)], 101, 6)),
  )
  // uma fruta aparece
  let p = Partida(js, [Posicao(5, 5)], 300, 1)
  let mock =
    random.mock([random.pick_returns(casas_livres(p), Ok(Posicao(2, 3)))])
  check.eq(
    avanca(mock, im, p),
    EmPartida(
      random.mock([]),
      im,
      Partida(js, [Posicao(2, 3), Posicao(5, 5)], 301, intervalo_fruta(301)),
    ),
  )
  // o tempo acaba
  check.eq(avanca(g, im, Partida(js, [], 599, 3)), Resultado(g, im, js))
}

/// Devolve a quantidade de tiques entre o aparecimento de uma fruta e o da
/// próxima, quando já se passaram *tiques* tiques desde o início da partida.
/// O intervalo diminui linearmente de `intervalo_inicial`, no início da
/// partida, até `intervalo_final`, no fim.
pub fn intervalo_fruta(tiques: Int) -> Int {
  intervalo_inicial - { intervalo_inicial - intervalo_final } * tiques / duracao
}

pub fn intervalo_fruta_examples() {
  check.eq(intervalo_fruta(0), 20)
  check.eq(intervalo_fruta(200), 15)
  check.eq(intervalo_fruta(400), 10)
  check.eq(intervalo_fruta(600), 5)
}

/// Produz a lista de todas as casas da grade, linha por linha.
pub fn todas_as_casas() -> List(Posicao) {
  list.flat_map(sequencia(0, tamanho - 1), fn(lin) {
    list.map(sequencia(0, tamanho - 1), fn(col) { Posicao(lin, col) })
  })
}

pub fn todas_as_casas_examples() {
  check.eq(list.length(todas_as_casas()), 100)
  check.eq(list.first(todas_as_casas()), Ok(Posicao(0, 0)))
  check.eq(list.last(todas_as_casas()), Ok(Posicao(9, 9)))
}

/// Produz a lista das casas da *partida* que não têm fruta nem jogador.
pub fn casas_livres(partida: Partida) -> List(Posicao) {
  list.filter(todas_as_casas(), fn(c) {
    !list.contains(partida.frutas, c) && !ocupada(partida.jogadores, c)
  })
}

pub fn casas_livres_examples() {
  let js = [Jogador(1, Posicao(0, 0), 0), Jogador(2, Posicao(9, 9), 0)]
  let livres = casas_livres(Partida(js, [Posicao(0, 1), Posicao(5, 5)], 0, 1))
  check.eq(list.length(livres), 96)
  check.eq(list.first(livres), Ok(Posicao(0, 2)))
  check.eq(list.contains(livres, Posicao(5, 5)), False)
  check.eq(list.contains(livres, Posicao(5, 6)), True)
}

/// Adiciona às frutas da *partida* uma fruta em uma casa livre sorteada com
/// *gerador*. Se não há casa livre, as frutas não mudam. Devolve as frutas e
/// o novo gerador.
pub fn sorteia_fruta(
  gerador: Generator,
  partida: Partida,
) -> #(List(Posicao), Generator) {
  case random.pick(gerador, casas_livres(partida)) {
    #(Ok(casa), gerador) -> #([casa, ..partida.frutas], gerador)
    #(Error(_), gerador) -> #(partida.frutas, gerador)
  }
}

pub fn sorteia_fruta_examples() {
  let js = [Jogador(1, Posicao(0, 0), 0), Jogador(2, Posicao(9, 9), 0)]
  let p = Partida(js, [Posicao(5, 5)], 40, 1)
  let mock =
    random.mock([random.pick_returns(casas_livres(p), Ok(Posicao(7, 2)))])
  check.eq(sorteia_fruta(mock, p), #(
    [Posicao(7, 2), Posicao(5, 5)],
    random.mock([]),
  ))
  // grade cheia
  let cheia = Partida(js, casas_livres(Partida(js, [], 0, 1)), 40, 1)
  let mock = random.mock([random.pick_returns([], Error(Nil))])
  check.eq(sorteia_fruta(mock, cheia), #(cheia.frutas, random.mock([])))
}

// ----------------------------------------------------------------------
// Resultado
// ----------------------------------------------------------------------

/// Produz a lista dos números dos jogadores que têm a maior pontuação entre
/// os *jogadores*.
pub fn vencedores(jogadores: List(Jogador)) -> List(Int) {
  let maximo = list.fold(jogadores, 0, fn(m, j) { int.max(m, j.pontos) })
  jogadores
  |> list.filter(fn(j) { j.pontos == maximo })
  |> list.map(fn(j) { j.numero })
}

pub fn vencedores_examples() {
  let p = Posicao(0, 0)
  check.eq(vencedores([Jogador(1, p, 3), Jogador(2, p, 5)]), [2])
  check.eq(vencedores([Jogador(1, p, 4), Jogador(2, p, 1), Jogador(3, p, 4)]), [
    1,
    3,
  ])
  check.eq(vencedores([Jogador(1, p, 0), Jogador(2, p, 0)]), [1, 2])
}

/// Produz a mensagem de resultado para o jogador *numero*: "Você venceu!"
/// se ele é o único com a maior pontuação, "Empate!" se ele divide a maior
/// pontuação com outros, e "Você perdeu!" caso contrário.
pub fn mensagem_resultado(jogadores: List(Jogador), numero: Int) -> String {
  let vs = vencedores(jogadores)
  case list.contains(vs, numero), vs {
    True, [_] -> "Você venceu!"
    True, _ -> "Empate!"
    False, _ -> "Você perdeu!"
  }
}

pub fn mensagem_resultado_examples() {
  let p = Posicao(0, 0)
  let js = [Jogador(1, p, 4), Jogador(2, p, 1), Jogador(3, p, 4)]
  check.eq(
    mensagem_resultado([Jogador(1, p, 3), Jogador(2, p, 5)], 2),
    "Você venceu!",
  )
  check.eq(
    mensagem_resultado([Jogador(1, p, 3), Jogador(2, p, 5)], 1),
    "Você perdeu!",
  )
  check.eq(mensagem_resultado(js, 1), "Empate!")
  check.eq(mensagem_resultado(js, 2), "Você perdeu!")
}

// ----------------------------------------------------------------------
// Desenho
// ----------------------------------------------------------------------

/// Devolve a quantidade de segundos que faltam para a partida terminar
/// quando já se passaram *tiques* tiques, arredondada para cima.
pub fn segundos_restantes(tiques: Int) -> Int {
  { duracao - tiques + tiques_por_segundo - 1 } / tiques_por_segundo
}

pub fn segundos_restantes_examples() {
  check.eq(segundos_restantes(0), 60)
  check.eq(segundos_restantes(1), 60)
  check.eq(segundos_restantes(10), 59)
  check.eq(segundos_restantes(599), 1)
}

/// Devolve a cor do jogador *numero*.
pub fn cor(numero: Int) -> Color {
  case numero {
    1 -> color.red
    2 -> color.blue
    3 -> color.green
    _ -> color.purple
  }
}

pub fn cor_examples() {
  check.eq(cor(1), color.red)
  check.eq(cor(2), color.blue)
  check.eq(cor(3), color.green)
  check.eq(cor(4), color.purple)
}

/// Desenha a tela do *estado* para o jogador *numero*.
pub fn tela(estado: Estado, numero: Int) -> Image {
  let conteudo = case estado {
    Abertura(_, _, _) -> tela_abertura(numero)
    EmPartida(_, imagens, partida) -> tela_partida(imagens, partida, numero)
    Resultado(_, _, jogadores) -> tela_resultado(jogadores, numero)
  }
  image.overlay(conteudo, image.rectangle(largura, altura, fill.white))
}

/// Empilha as *imagens*, a primeira no topo.
pub fn empilha(imagens: List(Image)) -> Image {
  list.fold(imagens, image.empty, image.above)
}

/// Desenha a tela de abertura para o jogador *numero*.
pub fn tela_abertura(numero: Int) -> Image {
  empilha([
    texto("Disputa de coleta", 32),
    texto(" ", 16),
    texto("Pegue mais frutas que os outros em 60 segundos.", 16),
    texto("Use as setas para mover o seu personagem.", 16),
    texto(" ", 16),
    image.beside(
      texto("Você é o jogador " <> int.to_string(numero) <> "  ", 20),
      image.square(lado_casa / 2, style.fill(cor(numero))),
    ),
    texto(" ", 16),
    texto("Pressione Enter para começar", 20),
  ])
}

/// Desenha a tela da *partida* para o jogador *numero*: o cabeçalho com os
/// pontos e o tempo, e a grade com as frutas, desenhadas com *imagens*, e os
/// jogadores.
pub fn tela_partida(
  imagens: ImagensFrutas,
  partida: Partida,
  numero: Int,
) -> Image {
  image.above(cabecalho(partida, numero), grade(imagens, partida, numero))
}

/// Desenha o cabeçalho da *partida* para o jogador *numero*: os pontos do
/// jogador em destaque, os pontos dos outros e o tempo restante.
pub fn cabecalho(partida: Partida, numero: Int) -> Image {
  let #(meus, outros) =
    list.partition(partida.jogadores, fn(j) { j.numero == numero })
  let meus_pontos = list.fold(meus, 0, fn(soma, j) { soma + j.pontos })
  let placar_outros =
    list.map(outros, fn(j) {
      image.text(
        "  J" <> int.to_string(j.numero) <> ": " <> int.to_string(j.pontos),
        16,
        style.fill(cor(j.numero)),
      )
    })
  empilha([
    image.text(
      "Você: " <> int.to_string(meus_pontos),
      28,
      style.fill(cor(numero)),
    ),
    list.fold(placar_outros, image.empty, image.beside),
    texto(
      "Tempo: " <> int.to_string(segundos_restantes(partida.tiques)) <> " s",
      16,
    ),
  ])
}

/// Desenha a grade da *partida*, com as frutas, desenhadas com *imagens*, e
/// os jogadores. O personagem do jogador *numero* tem uma borda grossa.
pub fn grade(imagens: ImagensFrutas, partida: Partida, numero: Int) -> Image {
  let fundo =
    image.rectangle(
      tamanho * lado_casa,
      tamanho * lado_casa,
      style.join([style.fill(color.white), style.stroke(color.gray)]),
    )
  let com_frutas =
    list.fold(partida.frutas, fundo, fn(cena, f) {
      coloca(cena, f, fruta(imagens, f))
    })
  list.fold(partida.jogadores, com_frutas, fn(cena, j) {
    coloca(cena, j.posicao, personagem(j.numero, j.numero == numero))
  })
}

/// Desenha o personagem do jogador *numero*, com uma borda grossa se
/// *destaque* é True.
pub fn personagem(numero: Int, destaque: Bool) -> Image {
  let borda = case destaque {
    True -> [style.stroke(color.black), style.stroke_width(4)]
    False -> []
  }
  image.square(lado_casa - 8, style.join([style.fill(cor(numero)), ..borda]))
}

pub fn personagem_examples() {
  check.same(personagem(2, False), image.square(32, style.fill(color.blue)))
  check.same(
    personagem(1, True),
    image.square(
      32,
      style.join([
        style.fill(color.red),
        style.stroke(color.black),
        style.stroke_width(4),
      ]),
    ),
  )
}

/// Devolve a imagem, entre as *imagens*, da fruta que está na casa *p*. A
/// fruta depende só da casa, para que não mude enquanto está na grade.
pub fn fruta(imagens: ImagensFrutas, p: Posicao) -> Image {
  case { p.lin + p.col } % 5 {
    0 -> imagens.maca
    1 -> imagens.banana
    2 -> imagens.uva
    3 -> imagens.morango
    _ -> imagens.laranja
  }
}

pub fn fruta_examples() {
  let im = imagens_exemplo()
  check.same(fruta(im, Posicao(0, 0)), im.maca)
  check.same(fruta(im, Posicao(0, 1)), im.banana)
  check.same(fruta(im, Posicao(1, 1)), im.uva)
  check.same(fruta(im, Posicao(9, 9)), im.morango)
  check.same(fruta(im, Posicao(1, 3)), im.laranja)
}

/// Imagens das frutas para os exemplos, uma diferente da outra.
pub fn imagens_exemplo() -> ImagensFrutas {
  ImagensFrutas(
    maca: image.circle(5, fill.red),
    banana: image.circle(5, fill.yellow),
    uva: image.circle(5, fill.purple),
    morango: image.circle(5, fill.pink),
    laranja: image.circle(5, fill.orange),
  )
}

/// Coloca a *imagem* centralizada na casa *p* da *cena*.
pub fn coloca(cena: Image, p: Posicao, imagem: Image) -> Image {
  image.place_image(
    cena,
    p.col * lado_casa + lado_casa / 2,
    p.lin * lado_casa + lado_casa / 2,
    imagem,
  )
}

pub fn coloca_examples() {
  let cena = image.empty_scene(400, 400)
  let bola = image.circle(10, fill.gold)
  check.same(
    coloca(cena, Posicao(0, 0), bola),
    image.place_image(cena, 20, 20, bola),
  )
  check.same(
    coloca(cena, Posicao(2, 5), bola),
    image.place_image(cena, 220, 100, bola),
  )
  check.same(
    coloca(cena, Posicao(9, 9), bola),
    image.place_image(cena, 380, 380, bola),
  )
}

/// Desenha a tela de resultado dos *jogadores* para o jogador *numero*.
pub fn tela_resultado(jogadores: List(Jogador), numero: Int) -> Image {
  let placar =
    list.map(jogadores, fn(j) {
      image.text(
        "Jogador "
          <> int.to_string(j.numero)
          <> ": "
          <> int.to_string(j.pontos)
          <> " frutas",
        20,
        style.fill(cor(j.numero)),
      )
    })
  empilha(
    list.flatten([
      [texto(mensagem_resultado(jogadores, numero), 32), texto(" ", 16)],
      placar,
      [texto(" ", 16), texto("Pressione Enter para jogar novamente", 20)],
    ]),
  )
}

/// Produz uma imagem com o texto *s* em preto, com tamanho *tamanho_fonte*.
pub fn texto(s: String, tamanho_fonte: Int) -> Image {
  image.text(s, tamanho_fonte, fill.black)
}
