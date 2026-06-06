package servicos;

import repositorio.RepositorioPaciente;
import repositorio.RepositorioConsultas;
import clinica.Paciente;
import clinica.Consulta;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta classe condensa tudo que a secretária tem acesso(Classes Paciente, Consulta, e usa das classes importadas para realizar as operações:
 * -Gerencia Pacientes (criar ficha de paciente novo, alterar um que já exista, ou eliminar)
 * -Gerencia Consultas (criar registro de consulta nova, alterar uma que já exista no sistema, ou eliminar)
 * -Gera relatórios de consulta (consultas relativas ao dia seguinte, filtrado pelo tipo de contato do paciente)
 */

public class ServicoSecretaria {
    private final RepositorioPaciente repositorioPaciente; //acessa o repositório dos pacientes
    private final RepositorioConsultas repositorioConsultas; //acessa o repositório das consultas
    
    public ServicoSecretaria(RepositorioPaciente repositorioPaciente, RepositorioConsultas repositorioConsultas){
        this.repositorioPaciente = repositorioPaciente;
        this.repositorioConsultas = repositorioConsultas;
    }
    
    public void cadastrarPaciente(Paciente paciente){
        repositorioPaciente.adicionarPaciente(paciente);
        System.out.println("Paciente cadastrado com sucesso!");
    }
    
    public void removerPaciente(Paciente paciente){
        repositorioPaciente.removePaciente(paciente);
        System.out.println("Paciente removido com sucesso!");
    }

    public void atualizarDataNascimento(int id, String novaDataNascimento){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        pacienteExistente.setDataNascimento(novaDataNascimento);
        System.out.println("Data de nascimento do paciente atualizado com sucesso!");
    }
    
    public void atualizarNovoEndereco(int id, String novoEndereco){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        pacienteExistente.setEndereco(novoEndereco);
        System.out.println("Endereço do paciente atualizado com sucesso!");
    }
    
    public void atualizarTelefone(int id, String novoTelefone){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        pacienteExistente.setTelefone(novoTelefone);
        System.out.println("Telefone do paciente atualizado com sucesso!");
    }
    
    public void atualizarEmail(int id, String novoEmail){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        pacienteExistente.setEmail(novoEmail);
        System.out.println("Email do paciente atualizado com sucesso!");
    }
    
    public void atualizarTipoConvenio(int id, String novoTipoConvenio){
        Paciente pacienteExistente = repositorioPaciente.buscaPaciente(id);
        pacienteExistente.setTipoConvenio(novoTipoConvenio);
        System.out.println("Tipo de convênio do paciente atualizado com sucesso!");
    }

    public void cadastrarConsulta(Consulta consulta){
        repositorioConsultas.adicionarConsulta(consulta);
        System.out.println("Consulta cadastrada com sucesso!");
    }
    
    public void removerConsulta(Consulta consulta){
        repositorioConsultas.removeConsulta(consulta);
        System.out.println("Consulta removida com sucesso!");
    }

    public void atualizarDataConsulta(String dataAntiga, String horarioAntigo, String medicoAntigo, String novaData){
        Consulta consultaExistente = repositorioConsultas.buscarConsulta(dataAntiga, horarioAntigo, medicoAntigo);
        consultaExistente.setData(novaData);
        System.out.println("Data da consulta atualizada com sucesso!");
    }
    
    public void atualizarHorarioConsulta(String dataAntiga, String horarioAntigo, String medicoAntigo, String novoHorario){
        Consulta consultaExistente = repositorioConsultas.buscarConsulta(dataAntiga, horarioAntigo, medicoAntigo);
        consultaExistente.setHorario(novoHorario);
        System.out.println("Horário da consulta atualizada com sucesso!");
    }
    
    public void atualizarTipoConsulta(String dataAntiga, String horarioAntigo, String medicoAntigo, String novoTipoConsulta){
        Consulta consultaExistente = repositorioConsultas.buscarConsulta(dataAntiga, horarioAntigo, medicoAntigo);
        consultaExistente.setTipoConsulta(novoTipoConsulta);
        System.out.println("Tipo da consulta atualizada com sucesso!");
    }
}
