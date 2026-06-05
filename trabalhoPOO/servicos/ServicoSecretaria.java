package servicos;
import clinica.Paciente;
import clinica.Consulta;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioConsultas;
/**
 * Esta classe condensa tudo que a secretária tem acesso(Classes Paciente, Consulta, e usa das classes importadas para realizar as operações:
 * -Gerencia Pacientes (criar ficha de paciente novo, alterar um que já exista, ou eliminar)
 * -Gerencia Consultas (criar registro de consulta nova, alterar uma que já exista no sistema, ou eliminar)
 * -Gera relatórios de consulta (consultas relativas ao dia seguinte, filtrado pelo tipo de contato do paciente)
 */

public class ServicoSecretaria {
    private RepositorioPaciente pacientes; //acesso à lista de pacientes registrados
    private RepositorioConsultas consultas; //acesso à lista de consultas registradas
    
    public ServicoSecretaria() {
        
    }
    public ServicoSecretaria(RepositorioPaciente pacientes, RepositorioConsultas consultas) {
        this.pacientes = pacientes;
        this.consultas = consultas;
    }
    
    public void cadastrarPaciente() {
        
    }
    public void cadastrarConsulta() {
        
    }
}
