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
import java.time.LocalDate;
import java.util.List;


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
        GerenciadorMensagens gerenciadorMensagens = new GerenciadorMensagens();
        
        
        //repositórios para uso do médico e instanciação do serviço
        RepositorioDadosAdicionais repDadosAdicionais = new RepositorioDadosAdicionais();
        RepositorioProntuario repProntuario = new RepositorioProntuario();
        ServicoMedico servicoMedico = new ServicoMedico(repDadosAdicionais, repProntuario);
        
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
        
        //o mesmo com consultas
        Consulta c1 = new Consulta(LocalDate.of(2026, 6, 3), 12, 30, "Barnabe", p1 , "normal");
        Consulta c2 = new Consulta(LocalDate.of(2026, 6, 8), 16, 00, "Barnabe", p1 , "retorno");
        Consulta c3 = new Consulta(LocalDate.of(2026, 6, 8), 16, 00, "Denise", p2 , "normal");
        Consulta c4 = new Consulta(LocalDate.of(2026, 6, 8), 16, 30, "Denise", p3 , "normal");
        Consulta c5 = new Consulta(LocalDate.of(2026, 6, 9), 16, 30, "Wilda", p4 , "normal");
        Consulta c6 = new Consulta(LocalDate.of(2026, 6, 8), 16, 30, "Denise", p5 , "normal");
        Consulta c7 = new Consulta(LocalDate.of(2026, 6, 8), 18, 30, "Barnabe", p3 , "retorno");
        Consulta c8 = new Consulta(LocalDate.of(2026, 6, 8), 18, 30, "Wilda", p4 , "retorno");
        
        //registrando consultas no sistema
        servicoSecretaria.cadastrarConsulta(c1);
        servicoSecretaria.cadastrarConsulta(c2);
        servicoSecretaria.cadastrarConsulta(c3);
        servicoSecretaria.cadastrarConsulta(c4);
        servicoSecretaria.cadastrarConsulta(c5);
        servicoSecretaria.cadastrarConsulta(c6);
        servicoSecretaria.cadastrarConsulta(c7);
        servicoSecretaria.cadastrarConsulta(c8);
        
        //envio das mensagens por SMS ou email
        List<Consulta> consultasAmanha = servicoSecretaria.gerarRelatorioComContato();
        gerenciadorMensagens.enviarMensagens(consultasAmanha);
    }
    
}
