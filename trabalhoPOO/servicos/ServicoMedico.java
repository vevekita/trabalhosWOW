package servicos;
import clinica.DadosAdicionaisPaciente;
import clinica.Prontuario;
import repositorio.RepositorioDadosAdicionais;
import repositorio.RepositorioProntuario;
import clinica.Paciente;
import clinica.Consulta;
import java.util.ArrayList;
/**
 * Classe que contém todas as operações que um médico pode fazer no sistema, tais como:
 * -Gerenciar os dados adicionais do paciente (cadastrar, atualizar e remover)
 * -Gerenciar prontuário do paciente (cadastrar, atualizar e remover)
 * -Gerar relatórios médicos - receita(0),atestado(1),declaração de acompanhamento(2), clientes atendidos no mês(3)
 *Para os relatórios médicos, será feito um polimorfismo de sobrecarga para cada padrão de parâmetros diferente que o tipo de relatório receber
 * 
 */

public class ServicoMedico {
    private RepositorioDadosAdicionais dadosAdicionais;
    private RepositorioProntuario prontuarios;
    
    public ServicoMedico() {
        
    }
    public ServicoMedico(RepositorioDadosAdicionais dadosAdicionais, RepositorioProntuario prontuarios) {
        this.dadosAdicionais = dadosAdicionais;
        this.prontuarios = prontuarios;
    }
    
    //Gerencia dados adicionais
    public void cadastraDadosAdicionais(DadosAdicionaisPaciente novoDadosAdd) {
        dadosAdicionais.adicionarDadosAdicionais(novoDadosAdd);
    }
    //atualiza os dados adicionais
    public void atualizaDadosId(DadosAdicionaisPaciente dadosAdPaciente, int novoIdPaciente) {
        dadosAdPaciente.setIdPaciente(novoIdPaciente);
    }
    public void atualizaDadosFuma(DadosAdicionaisPaciente dadosAdPaciente, boolean novoFuma) {
        dadosAdPaciente.setFuma(novoFuma);
    }
    public void atualizaDadosBebe(DadosAdicionaisPaciente dadosAdPaciente, boolean novoBebe) {
        dadosAdPaciente.setBebe(novoBebe);
    }
    public void atualizaDadosDiabetes(DadosAdicionaisPaciente dadosAdPaciente, boolean novoDiabetes) {
        dadosAdPaciente.setDiabetes(novoDiabetes);
    }
    public void atualizaDadosColesterol(DadosAdicionaisPaciente dadosAdPaciente, boolean novoColesterol) {
        dadosAdPaciente.setColesterol(novoColesterol);
    }
    public void atualizaDadosDoencaCardiaca(DadosAdicionaisPaciente dadosAdPaciente, boolean novoDoencaCardiaca) {
        dadosAdPaciente.setDoencaCardiaca(novoDoencaCardiaca);
    }
    public void atualizaDadosCirurgias(DadosAdicionaisPaciente dadosAdPaciente, String novoCirurgias) {
        dadosAdPaciente.setCirurgias(novoCirurgias);
    }
    public void atualizaDadosAlergias(DadosAdicionaisPaciente dadosAdPaciente, String novoAlergias) {
        dadosAdPaciente.setAlergias(novoAlergias);
    }
    
    //remove os dados adicionais
    public void removeDadosAdicionais(DadosAdicionaisPaciente dadoAddRem) {
        dadosAdicionais.removeDadosAdicionais(dadoAddRem);
    }
    
    //gerencia Prontuario
    public void cadastraProntuario(Prontuario novoProntuario) {
        prontuarios.cadastraProntuario(novoProntuario);
    }
    
    //atualiza dados do prontuário
    public void atualizaProntId(Prontuario prontuario, int novoIdPaciente) {
        prontuario.setIdPaciente(novoIdPaciente);
    }
    public void atualizaProntSintomas(Prontuario prontuario, String novoSintoma) {
        prontuario.setSintomas(novoSintoma);
    }
    public void atualizaDiagnostico(Prontuario prontuario, String novoDiagnostico) {
        prontuario.setDiagnostico(novoDiagnostico);
    }
    public void atualizaProntPrescricao(Prontuario prontuario, String novoPrescricao) {
        prontuario.setPrescricao(novoPrescricao);
    }
    
    //remove prontuário
    public void removeProntuario(Prontuario prontRem) {
        prontuarios.removeProntuario(prontRem);
    }
    
    //Gera relatórios médicos:
    //geraRelatorio() para receita -> infoAdicional inclue informações básicas do medicamento(ex: "sertralina 50g").
    public void geraRelatorio(int tipoRelatorio, String medico, Paciente paciente, String infoAdicional) {
        if (tipoRelatorio == 0) {
            System.out.println("------------RECEITA-MÉDICA------------");
            System.out.println("PACIENTE: " + paciente.getNome());
            System.out.println("MÉDICO: " + medico);
            System.out.println("INFORMAÇÕES DO MEDICAMENTO: " + infoAdicional);
        } else {
            System.out.println("Relatório inválido! Por favor, verifique o tipo de relatório desejado e suas informações necessárias.");
        }
    }
    
    //geraRelatorio() para declaração de acompanhamento -> infoAdicional inclue informações do indivíduo a acompanhar o paciente(ex: Maria Giu, irmã).
    public void geraRelatorio(int tipoRelatorio, String medico, Paciente paciente, String infoAdicional, int diasAfastado) {
        if (tipoRelatorio == 1) {
            System.out.println("-------DECLARAÇÃO-DE-ACOMPANHAMENTO-------");
            System.out.println("PACIENTE: " + paciente.getNome());
            System.out.println("MÉDICO: " + medico);
            System.out.println("INFORMAÇÕES DO ACOMPANHANTE: " + infoAdicional);
            System.out.println("TEMPO DE AFASTAMENTO: " + diasAfastado + " dias");
        } else{
            System.out.println("Relatório inválido! Por favor, verifique o tipo de relatório desejado e suas informações necessárias.");
        }
    }
    
    //geraRelatorio() para o atestado do paciente
    public void geraRelatorio(int tipoRelatorio, String medico, Paciente paciente, int diasAfastado) {
        if (tipoRelatorio == 2) {
            System.out.println("------------ATESTADO-MÉDICO------------");
            System.out.println("PACIENTE: " + paciente.getNome());
            System.out.println("MÉDICO: " + medico);
            System.out.println("TEMPO DE AFASTAMENTO: " + diasAfastado + "dias");
        } else {
            System.out.println("Relatório inválido! Por favor, verifique o tipo de relatório desejado e suas informações necessárias.");
        }
    }
    
    //geraRelatorio() para acessar todos os clientes do mês -> mesAno se refere ao Mês e o Ano selecionado (ex: 06/2026)
    public void geraRelatorio(int tipoRelatorio, String medico, ArrayList<Consulta> todasConsultas, String mesAno) {
        if (tipoRelatorio == 3) {
            System.out.println("------------CLIENTES-NO-MÊS------------");
            System.out.println("MÉDICO: " + medico);
            System.out.println("MÊS E ANO SELECIONADOS: " + mesAno);
            
            int i = 0; //tipo de contador para a numeração de clientes
            if (todasConsultas == null) {
                System.out.println("Nenhum Cliente atendido nesse mês");
            } else {
                for (Consulta c: todasConsultas) {
                    if (c.getMedico().equals(medico)) {
                        if (c.getData().contains("/"+mesAno)) {
                            i += 1;
                            System.out.println("PACIENTE #" + i + " - " + c.getPaciente().getNome());
                        }
                    }
                }
            }
            
            
        } else {
            System.out.println("Relatório inválido! Por favor, verifique o tipo de relatório desejado e suas informações necessárias.");
        }
    }
}
