/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicos;
import clinica.DadosAdicionaisPaciente;
import clinica.Prontuario;
import repositorio.RepositorioDadosAdicionais;
import repositorio.RepositorioProntuario;
import clinica.Paciente;
import clinica.Consulta;
import java.time.LocalDate;
import java.util.List;
/**
 * Classe que contém todas as operações que um médico pode fazer no sistema, tais como:
 * -Gerenciar os dados adicionais do paciente (cadastrar, atualizar e remover)
 * -Gerenciar prontuário do paciente (cadastrar, atualizar e remover)
 * -Gerar relatórios médicos - receita(0),atestado(1),declaração de acompanhamento(2), clientes atendidos no mês(3)
 *Para os relatórios médicos, será feito um polimorfismo de sobrecarga para cada padrão de parâmetros diferente que o tipo de relatório receber
 * 
 */

public class ServicoMedico {
    private RepositorioDadosAdicionais repositorioDadosAdicionais;
    private RepositorioProntuario repositorioProntuarios;
    
    public ServicoMedico() {}
    public ServicoMedico(RepositorioDadosAdicionais dadosAdicionais, RepositorioProntuario prontuarios) {
        this.repositorioDadosAdicionais = dadosAdicionais;
        this.repositorioProntuarios = prontuarios;
    }
    
    //Gerencia dados adicionais
    public void cadastraDadosAdicionais(DadosAdicionaisPaciente novoDadosAdd) {
        repositorioDadosAdicionais.cadastrarDados(novoDadosAdd);
        System.out.println("Dados adicionais do paciente cadastrados com sucesso!");
    }
    //atualiza os dados adicionais
    public void atualizaDadosFuma(Paciente paciente, boolean novoFuma) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setFuma(novoFuma);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    public void atualizaDadosBebe(Paciente paciente, boolean novoBebe) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setBebe(novoBebe);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    public void atualizaDadosDiabetes(Paciente paciente, boolean novoDiabetes) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setDiabetes(novoDiabetes);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    public void atualizaDadosColesterol(Paciente paciente, boolean novoColesterol) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setColesterol(novoColesterol);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    public void atualizaDadosDoencaCardiaca(Paciente paciente, boolean novoDoencaCardiaca) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setDoencaCardiaca(novoDoencaCardiaca);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    public void atualizaDadosCirurgias(Paciente paciente, String novoCirurgias) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setCirurgias(novoCirurgias);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    public void atualizaDadosAlergias(Paciente paciente, String novoAlergias) {
        DadosAdicionaisPaciente dadosExistentes = repositorioDadosAdicionais.buscaDadosAdicionais(paciente);
        dadosExistentes.setAlergias(novoAlergias);
        System.out.println("Dado adicional atualizado com sucesso!");
    }
    
    //remove os dados adicionais
    public void removeDadosAdicionais(DadosAdicionaisPaciente dadoAddRem) {
        repositorioDadosAdicionais.removeDadosAdicionais(dadoAddRem);
        System.out.println("Dados adicionais removidos com sucesso!");
    }
    
    //gerencia Prontuario
    public void cadastraProntuario(Prontuario novoProntuario) {
        repositorioProntuarios.cadastrarProntuario(novoProntuario);
        System.out.println("Prontuário cadastrado com sucesso!");
    }
    
    //atualiza dados do prontuário
    public void atualizaProntSintomas(int idPaciente, String novoSintoma) {
        Prontuario prontuarioExistente = repositorioProntuarios.buscaProntuario(idPaciente);
        prontuarioExistente.setSintomas(novoSintoma);
        System.out.println("Sintomas do paciente atualizados!");
    }
    public void atualizaDiagnostico(int idPaciente, String novoDiagnostico) {
        Prontuario prontuarioExistente = repositorioProntuarios.buscaProntuario(idPaciente);
        prontuarioExistente.setDiagnostico(novoDiagnostico);
        System.out.println("Diagnóstico do paciente atualizado!");
    }
    public void atualizaProntPrescricao(int idPaciente, String novoPrescricao) {
        Prontuario prontuarioExistente = repositorioProntuarios.buscaProntuario(idPaciente);
        prontuarioExistente.setPrescricao(novoPrescricao);
        System.out.println("Prescrição médica do paciente atualizado!");
    }
    
    //remove prontuário
    public void removeProntuario(Prontuario prontRem) {
        repositorioProntuarios.removeProntuario(prontRem);
        System.out.println("Prontuário removido com sucesso!");
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
    
    //geraRelatorio() para declaração de acompanhamento -> infoAdicional inclue informações do indivíduo a acompanhar o paciente(ex: Maria Gil, irmã).
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
    
    //geraRelatorio() para acessar todos os clientes do mês
    public void geraRelatorio(int tipoRelatorio, String medico, List<Consulta> todasConsultas, LocalDate data) {
        if (tipoRelatorio == 3) {
            System.out.println("------------CLIENTES-NO-MÊS------------");
            System.out.println("MÉDICO: " + medico);
            System.out.println("MÊS E ANO SELECIONADOS: " + data.getMonthValue() + "/" + data.getYear());
            
            int i = 0; //tipo de contador para a numeração de clientes
            if (todasConsultas == null) {
                System.out.println("Nenhum Cliente atendido nesse mês");
            } else {
                for (Consulta c: todasConsultas) {
                    if (c.getMedico().equals(medico)) {
                        if (c.getData().getMonthValue() == data.getMonthValue() && c.getData().getYear() == data.getYear()) {
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
