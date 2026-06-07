package main;
import clinica.Consulta;
import clinica.DadosAdicionaisPaciente;
import clinica.Paciente;
import clinica.Prontuario;
import repositorio.RepositorioConsultas;
import repositorio.RepositorioDadosAdicionais;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioProntuario;
import servicos.GerenciadorMensagens;
import servicos.ServicoMedico;
import servicos.ServicoSecretaria;


/**
 *Classe Main
 * Ela vai exibir exemplos para mostrar como é estruturado e realizado as operações.
 */
public class Main {
    public static void main(String[] args) {
        //repositórios para uso da secretária e instanciação do serviço
        RepositorioPaciente repPaciente = new RepositorioPaciente();
        RepositorioConsultas repConsultas = new RepositorioConsultas();
        ServicoSecretaria servicoSecretaria = new ServicoSecretaria(repPaciente, repConsultas);
        
        //repositórios para uso do médico e instanciação do serviço
        RepositorioDadosAdicionais repDadosAdicionais = new RepositorioDadosAdicionais();
        RepositorioProntuario repProntuario = new RepositorioProntuario();
        ServicoMedico servicoMedico = new ServicoMedico(repDadosAdicionais, repProntuario);

        //criação dos objetos Paciente
        Paciente p1 = new Paciente(1, "Abgail", "22/03/1988", "Rua das Abobrinhas, N762", "(22) 78943567", null, "Particular");
        Paciente p2 = new Paciente(2, "Alexandre", "14/08/1967", "Rua Ofélia, N128", null, "alexandreogrande@hotmail.com", "Público");
        Paciente p3 = new Paciente(3, "Katylin", "12/12/2012", "Rua genérica, N001", null, null, "Particular");
        Paciente p4 = new Paciente(4, "Luis Carlos", "30/03/2000", "Rua genérica, N023", "(22) 444355467", "luizcarlos2000@gmail.com", "Público");
        Paciente p5 = new Paciente(5, "Amon", "11/11/2006", "Praça incrível, N232", null, "algumacoisa@gmail.com", "Público");
        
        //registrando pacientes no sistema
        servicoSecretaria.cadastrarPaciente(p1);
        servicoSecretaria.cadastrarPaciente(p2);
        servicoSecretaria.cadastrarPaciente(p3);
        servicoSecretaria.cadastrarPaciente(p4);
        servicoSecretaria.cadastrarPaciente(p5);
        
        //o mesmo com consultas...
    }
    
}
