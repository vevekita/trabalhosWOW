/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicos;

import repositorio.RepositorioPaciente;
import repositorio.RepositorioConsultas;
import clinica.Paciente;
import clinica.Consulta;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ServicoSecretaria {
    private final RepositorioPaciente repositorioPaciente; //acessa o repositório dos pacientes
    private final RepositorioConsultas repositorioConsultas; //acessa o repositório das consultas
    
    public ServicoSecretaria(RepositorioPaciente repositorioPaciente, RepositorioConsultas repositorioConsultas){
        this.repositorioPaciente = repositorioPaciente;
        this.repositorioConsultas = repositorioConsultas;
    }
    
    public void cadastrarPaciente(Paciente paciente){
        repositorioPaciente.adicionarPaciente(paciente);
    }
    
    public void removerPaciente(int id){
        repositorioPaciente.removePaciente(id);
    }
    
    public void atualizarDataNascimento(int id, String novaDataNascimento){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        if(pacienteExistente != null){
            pacienteExistente.setDataNascimento(novaDataNascimento);
            repositorioPaciente.adicionarPaciente(pacienteExistente);
            System.out.println("Data de nascimento do paciente atualizado com sucesso!");
        }
        else{
            System.out.println("Não foi possível encontrar o paciente para atualização do dado!");
        }
    }
    
    public void atualizarNovoEndereco(int id, String novoEndereco){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        if(pacienteExistente != null){
            pacienteExistente.setEndereco(novoEndereco);
            repositorioPaciente.adicionarPaciente(pacienteExistente);
            System.out.println("Endereço do paciente atualizado com sucesso!");
        }
        else{
            System.out.println("Não foi possível encontrar o paciente para atualização do dado!");
        }
    }
    
    public void atualizarTelefone(int id, String novoTelefone){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        if(pacienteExistente != null){
            pacienteExistente.setTelefone(novoTelefone);
            repositorioPaciente.adicionarPaciente(pacienteExistente);
            System.out.println("Telefone do paciente atualizado com sucesso!");
        }
        else{
            System.out.println("Não foi possível encontrar o paciente para atualização do dado!");
        }
    }
    
    public void atualizarEmail(int id, String novoEmail){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        if(pacienteExistente != null){
            pacienteExistente.setEmail(novoEmail);
            repositorioPaciente.adicionarPaciente(pacienteExistente);
            System.out.println("Email do paciente atualizado com sucesso!");
        }
        else{
            System.out.println("Não foi possível encontrar o paciente para atualização do dado!");
        }
    }
    
    public void atualizarTipoConvenio(int id, String novoTipoConvenio){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        if(pacienteExistente != null){
            pacienteExistente.setTipoConvenio(novoTipoConvenio);
            repositorioPaciente.adicionarPaciente(pacienteExistente);
            System.out.println("Tipo de convênio do paciente atualizado com sucesso!");
        }
        else{
            System.out.println("Não foi possível encontrar o paciente para atualização do dado!");
        }
    }
    
    public void cadastrarConsulta(Consulta consulta){
        boolean verificacao = repositorioConsultas.adicionarConsulta(consulta);
        if(verificacao == true){
            System.out.println("Consulta cadastrada com sucesso!");
        } else {
            System.out.println("Não foi possível realizar o cadastro da consulta pois já existe uma consulta marcada.");
        }
    }
    
    public void removerConsulta(int idConsulta){
        repositorioConsultas.removeConsulta(idConsulta);
    }
    
    public void atualizarDataConsulta(int idConsulta, LocalDate novaData){
        Consulta consultaExistente = repositorioConsultas.buscarConsulta(idConsulta);
        if(consultaExistente != null){
            int horas = consultaExistente.getHoras();
            int minutos = consultaExistente.getMinutos();
            String medico = consultaExistente.getMedico();
            int idPaciente =  consultaExistente.getPacienteId();
            String tipoConsulta =  consultaExistente.getTipoConsulta();
            boolean atualizou = repositorioConsultas.atualizarConsulta(idConsulta, horas, minutos, novaData, medico, idPaciente, tipoConsulta);
            if (atualizou == true) {
                System.out.println("Data da consulta atualizada com sucesso!");
            } else {
                System.out.println("Não foi possível atualizar a data: horario já agendado!");
            }
        } else {
            System.out.println("Não foi possível encontrar a consulta para atualização do dado!");
        }
    }
    
    public void atualizarHorarioConsulta(int idConsulta, int horasNovas, int minutosNovos){
        Consulta consultaExistente = repositorioConsultas.buscarConsulta(idConsulta);
        if (consultaExistente != null) {
            LocalDate data = consultaExistente.getData();
            String medico = consultaExistente.getMedico();
            int idPaciente = consultaExistente.getPacienteId();
            String tipoConsulta = consultaExistente.getTipoConsulta();
            boolean atualizado = repositorioConsultas.atualizarConsulta(idConsulta, horasNovas, minutosNovos, data, medico, idPaciente, tipoConsulta);
            if (atualizado == true) {
                System.out.println("Horário da consulta atualizada com sucesso!");
            } else {
                System.out.println("Horário da consulta inválido: Já existe uma consulta agendado no horário");
            }
        } else {
            System.out.println("Consulta não encontrada");
        }
    }
    public void atualizarTipoConsulta(int idConsulta, String novoTipoConsulta){
        Consulta consultaExistente = repositorioConsultas.buscarConsulta(idConsulta);
        if(consultaExistente != null){
            int horas = consultaExistente.getHoras();
            int minutos = consultaExistente.getMinutos();
            LocalDate data = consultaExistente.getData();
            String medico = consultaExistente.getMedico();
            int idPaciente = consultaExistente.getPacienteId();
            boolean atualizado = repositorioConsultas.atualizarConsulta(idConsulta, horas, minutos, data, medico, idPaciente, novoTipoConsulta);
            if (atualizado == true) {
                System.out.println("Tipo da consulta atualizada com sucesso!");
            } else {
                System.out.println("Não foi possível atualizar o tipo de consulta: ocupação de horario reservado!");
            }  
        }
        else{
            System.out.println("Não foi possível encontrar a consulta para atualização do dado!");
        }
    }
    
    //gera o relatório das consultas de amanhã cujos pacientes possuem pelo menos uma forma de contato (email ou telefone)
    public List<Consulta> gerarRelatorioComContato(){
        LocalDate amanha = LocalDate.now().plusDays(1); //para conseguir a data de amanhã
        List<Consulta> todasConsultas = repositorioConsultas.listarConsultas();
        List<Consulta> consultasAmanhaContato = new ArrayList<>();
        
        for(Consulta c : todasConsultas){
            if(c.getData().equals(amanha)){
                Paciente pacienteAtual = repositorioPaciente.buscaPaciente(c.getPacienteId());
                String telefone = pacienteAtual.getTelefone();
                String email = pacienteAtual.getEmail();
                
                if(telefone != null || email != null){
                    consultasAmanhaContato.add(c);
                }
            }
        }
        
        return consultasAmanhaContato;
    }
    
    //gera o relatório das consultas de amanhã cujos pacientes não possuem forma de contato
    public List<Consulta> gerarRelatorioSemContato(){
        LocalDate amanha = LocalDate.now().plusDays(1);
        List<Consulta> todasConsultas = repositorioConsultas.listarConsultas();
        List<Consulta> consultasAmanhaSemContato = new ArrayList<>();
        
        for(Consulta c : todasConsultas){
            if(c.getData().equals(amanha)){
                Paciente pacienteAtual = repositorioPaciente.buscaPaciente(c.getPacienteId());
                String telefone = pacienteAtual.getTelefone();
                String email = pacienteAtual.getEmail();
                
                if(telefone == null && email == null){
                    consultasAmanhaSemContato.add(c);
                }
            }
        }
        return consultasAmanhaSemContato;
    }
    
    public List<Paciente> listarPacientes() {
        return repositorioPaciente.listarPacientes();
    }
    
    public Paciente buscaPaciente(int idPaciente) {
        return repositorioPaciente.buscaPaciente(idPaciente);
    }
    
    public List<Consulta> listarConsultas() {
        return repositorioConsultas.listarConsultas();
    }
    
    public List<Consulta> listarConsultaPessoa(int idPaciente) {
        return repositorioConsultas.listarConsultasPaciente(idPaciente);
    }
    
    public Consulta buscaConsulta(int idConsulta) {
        return repositorioConsultas.buscarConsulta(idConsulta);
    }
}
