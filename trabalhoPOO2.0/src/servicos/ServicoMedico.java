/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicos;
import clinica.DadosAdicionaisPaciente;
import clinica.Prontuario;
import repositorio.RepositorioDadosAdicionais;
import repositorio.RepositorioProntuario;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioConsultas;
import clinica.Paciente;
import clinica.Consulta;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private RepositorioPaciente repositorioPaciente;
    private RepositorioConsultas repositorioConsultas;
    
    public ServicoMedico() {}
    public ServicoMedico(RepositorioDadosAdicionais dadosAdicionais, RepositorioProntuario prontuarios, RepositorioPaciente pacientes, RepositorioConsultas consultas) {
        this.repositorioConsultas = consultas;
        this.repositorioDadosAdicionais = dadosAdicionais;
        this.repositorioProntuarios = prontuarios;
        this.repositorioPaciente = pacientes;
    }
    
    //Gerencia dados adicionais
    public void cadastraDadosAdicionais(DadosAdicionaisPaciente novoDadosAdd) {
        repositorioDadosAdicionais.adicionarDados(novoDadosAdd);
        System.out.println("Dados adicionais do paciente cadastrados com sucesso!");
    }
    //atualiza os dados adicionais
   
    public void atualizarDadosAdicionais(DadosAdicionaisPaciente dados) {
        repositorioDadosAdicionais.atualizarDados(dados);
    }
    
    //remove os dados adicionais
    public void removeDadosAdicionais(DadosAdicionaisPaciente dadoAddRem) {
        repositorioDadosAdicionais.removeDadosAdicionais(dadoAddRem.getPacienteId());
        System.out.println("Dados adicionais removidos com sucesso!");
    }
    
    //gerencia Prontuario
    public void cadastraProntuario(Prontuario novoProntuario) {
        repositorioProntuarios.adicionarProntuario(novoProntuario);
        System.out.println("Prontuário cadastrado com sucesso!");
    }
    
    //atualiza dados do prontuário
    public void atualizaProntuario(Prontuario novoPront) {
        repositorioProntuarios.atualizarProntuario(novoPront);
        System.out.println("Prontuário atualizado com sucesso!");
    }
    
    //remove prontuário
    public void removeProntuario(Prontuario prontRem) {
        repositorioProntuarios.removeProntuario(prontRem.getIdPaciente());
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
    public List<String> geraRelatorio(int tipoRelatorio, LocalDate data) {
        List<String> pacientes = new ArrayList<>();
        if (tipoRelatorio == 3) {
            System.out.println("------------CLIENTES-NO-MÊS------------");
            System.out.println("MÊS E ANO SELECIONADOS: " + data.getMonthValue() + "/" + data.getYear());
            List<Consulta> todasConsultas = repositorioConsultas.listarConsultas();
            if (todasConsultas == null) {
                System.out.println("Nenhum Cliente atendido nesse mês");
            } else {
                System.out.println("PACIENTES");
                for (Consulta c: todasConsultas) {
                    if (c.getData().getMonthValue() == data.getMonthValue() && c.getData().getYear() == data.getYear()) {
                        Paciente pacienteAtual = repositorioPaciente.buscaPaciente(c.getPacienteId());
                        if (pacienteAtual != null) {
                            pacientes.add(pacienteAtual.getNome());
                            System.out.println( "ID: "+ c.getPacienteId() + " | NOME: " + pacienteAtual.getNome());
                        }
                    }  
                }
            }
        } else {
            System.out.println("Relatório inválido! Por favor, verifique o tipo de relatório desejado e suas informações necessárias.");
        }
        return pacientes;
    }
}
