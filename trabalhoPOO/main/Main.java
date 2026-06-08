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
        
        //atualizando cadastros de Pacientes
        servicoSecretaria.atualizarDataNascimento(1, "23/04/1988");
        servicoSecretaria.atualizarEmail(1, "abigail123@gmail.com");
        servicoSecretaria.atualizarNovoEndereco(1, "Rua Pamonha");
        servicoSecretaria.atualizarTelefone(1, "(22)1234-8765");
        servicoSecretaria.atualizarTipoConvenio(1, "Público");
        System.out.println(repPaciente.buscaPaciente(1).getDataNascimento());
        System.out.println(repPaciente.buscaPaciente(1).getEmail());
        System.out.println(repPaciente.buscaPaciente(1).getEndereco());
        System.out.println(repPaciente.buscaPaciente(1).getTelefone());
        System.out.println(repPaciente.buscaPaciente(1).getTipoConvenio());
        
        System.out.println("\nRepositorio dos pacientes----------");
        if (repPaciente.listarPacientes().isEmpty()) {
            System.out.println("Nenhum paciente cadastrado no repositorio.");
        } else {
            for (Paciente p : repPaciente.listarPacientes()) {
                System.out.println(
                        "ID: " + p.getDadoIdentificacao() + 
                        " | Nome: " + p.getNome() +  
                        " | Data de Nascimento: " + p.getDataNascimento() +
                        " | Endereco: " + p.getEndereco() +
                        " | Telefone: " + p.getTelefone() +
                        " | Email: " + p.getEmail() +
                        " | Tipo de Convenio: " + p.getTipoConvenio()
                );
            }
        }
        
        //atualizando cadastros de consutas
        servicoSecretaria.atualizarTipoConsulta(LocalDate.of(2026, 6, 3), 12, 30, "Barnabe", "retorno");
        servicoSecretaria.atualizarDataConsulta(LocalDate.of(2026, 6, 3), 12, 30, "Barnabe", LocalDate.of(2026, 6, 4));
        servicoSecretaria.atualizarHorarioConsulta(LocalDate.of(2026, 6, 4), 12, 30, "Barnabe", 13, 0);
        System.out.println(repConsultas.buscarConsulta(LocalDate.of(2026, 6, 4), 13, 0, "Barnabe").getData());
        System.out.println(repConsultas.buscarConsulta(LocalDate.of(2026, 6, 4), 13, 0, "Barnabe").getHoras());
        System.out.println(repConsultas.buscarConsulta(LocalDate.of(2026, 6, 4), 13, 0, "Barnabe").getMinutos());
        System.out.println(repConsultas.buscarConsulta(LocalDate.of(2026, 6, 4), 13, 0, "Barnabe").getMedico());
        System.out.println(repConsultas.buscarConsulta(LocalDate.of(2026, 6, 4), 13, 0, "Barnabe").getTipoConsulta());
        
        
        //removendo cadastros de consultas
        servicoSecretaria.removerConsulta(c1);
        
        //removendo cadastro de pacientes
        servicoSecretaria.removerPaciente(p1);
        
        //Instanciando Dados adicionais
        DadosAdicionaisPaciente d1 = new DadosAdicionaisPaciente(p2, false, true, false, false, false, "Nenhuma", "Paracetamol");
        DadosAdicionaisPaciente d2 = new DadosAdicionaisPaciente(p3, true, true, true, true, false, "Nenhuma", "Nenhuma");
        DadosAdicionaisPaciente d3 = new DadosAdicionaisPaciente(p4, false, false, false, false, true, "Cirurgia de sopro", "Dipirona e Paracetamol");
        DadosAdicionaisPaciente d4 = new DadosAdicionaisPaciente(p5, false, true, true, false, false, "Nenhuma", "Nenhuma");
        
        //registrando os dados adicionais no sistema
        servicoMedico.cadastraDadosAdicionais(d1);
        servicoMedico.cadastraDadosAdicionais(d2);
        servicoMedico.cadastraDadosAdicionais(d3);
        servicoMedico.cadastraDadosAdicionais(d4);
        
        //atualizando os dados adicionais
        servicoMedico.atualizaDadosAlergias(p3, "Leite");
        servicoMedico.atualizaDadosBebe(p3, false);
        servicoMedico.atualizaDadosCirurgias(p3, "Cirurgia no braco");
        servicoMedico.atualizaDadosColesterol(p3, false);
        servicoMedico.atualizaDadosDiabetes(p3, false);
        servicoMedico.atualizaDadosDoencaCardiaca(p3, true);
        servicoMedico.atualizaDadosFuma(p3, false);
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).getAlergias());
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).getCirurgias());
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).isBebe());
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).isColesterol());
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).isDiabetes());
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).isDoencaCardiaca());
        System.out.println(repDadosAdicionais.buscaDadosAdicionais(p3).isFuma());
        
        //removendo dados adicionais
        servicoMedico.removeDadosAdicionais(d2);

        //instanciando prontuarios
        Prontuario pr3 = new Prontuario(3, "Dor no peito", "Pneumonia", "Inalacao 2x por dia, por 7 dias");
        Prontuario pr4 = new Prontuario(4, "Dor de cabeca", "Virose", "Benegripe 1x ao dia, por 5 dias");
        Prontuario pr5 = new Prontuario(5, "Enjoo e dor de cabeca", "Tétano", "Paracetamol 3x ao dia por 7 dias");
        
        //Cadastrando os prontuarios
        servicoMedico.cadastraProntuario(pr3);
        servicoMedico.cadastraProntuario(pr4);
        servicoMedico.cadastraProntuario(pr5);
        
        //atualizando prontuarios
        servicoMedico.atualizaProntSintomas(3, "Dor no peito e Coriza");
        servicoMedico.atualizaDiagnostico(3, "Bronquite");
        servicoMedico.atualizaProntPrescricao(3, "Inalacao 2x por dia, por 7 dias, e dipirona 2x ao dia, se tiver dor");
        System.out.println(repProntuario.buscaProntuario(3).getSintomas());
        System.out.println(repProntuario.buscaProntuario(3).getDiagnostico());
        System.out.println(repProntuario.buscaProntuario(3).getPrescricao());
        
        //removendo prontuarios
        servicoMedico.removeProntuario(pr3);
        
        System.out.println("\nRepositorio dos pacientes----------");
        if (repPaciente.listarPacientes().isEmpty()) {
            System.out.println("Nenhum paciente cadastrado no repositorio.");
        } else {
            for (Paciente p : repPaciente.listarPacientes()) {
                System.out.println(
                        "ID: " + p.getDadoIdentificacao() + 
                        " | Nome: " + p.getNome() +  
                        " | Data de Nascimento: " + p.getDataNascimento() +
                        " | Endereco: " + p.getEndereco() +
                        " | Telefone: " + p.getTelefone() +
                        " | Email: " + p.getEmail() +
                        " | Tipo de Convenio: " + p.getTipoConvenio()
                );
            }
        }

        System.out.println("\nRepositorio das consultas-----------");
        if (repConsultas.listarConsultas().isEmpty()) {
            System.out.println("Nenhuma consulta cadastrada no repositorio.");
        } else {
            for (Consulta c : repConsultas.listarConsultas()) {
                System.out.println(
                        "Paciente: " + c.getPaciente().getNome() +                      
                        " | Data: " + c.getData() +
                        " | Medico: " + c.getMedico() +
                        " | Paciente: " + c.getPaciente().getNome() +
                        " | Horario: " + c.getHoras() + ":" + (c.getMinutos() < 10 ? "0" + c.getMinutos() : c.getMinutos()) +
                        " | Tipo de Consulta: " + c.getTipoConsulta()
                );
            }
        }
        
        
        System.out.println("\nRepositorio dos dados adicionais dos pacientes-----------");
        if (repDadosAdicionais.listarDadosAdicionais().isEmpty()) {
            System.out.println("Nenhum dado adicional cadastrado no repositorio.");
        } else {
            for (DadosAdicionaisPaciente d : repDadosAdicionais.listarDadosAdicionais()) {
                System.out.println(
                        "Paciente: " + d.getPaciente().getNome() +                      
                        " | Fuma: " + d.isFuma() +
                        " | Bebe: " + d.isBebe() +
                        " | Colesterol: " + d.isColesterol() +
                        " | Diabetes: " + d.isDiabetes() +
                        " | Doenca Cardiaca: " + d.isDoencaCardiaca() +
                        " | Cirugias: " + d.getCirurgias() +
                        " | Alergias: " + d.getAlergias()
                );
            }
        }
        
        System.out.println("\nRepositorio dos Prontuarios-----------");
        if (repProntuario.listarProntuarios().isEmpty()) {
            System.out.println("Nenhum prontuario cadastrado no repositorio.");
        } else {
            for (Prontuario pr : repProntuario.listarProntuarios()) {
                System.out.println(
                        "Paciente: " + pr.getIdPaciente() +                      
                        " | Sintomas: " + pr.getSintomas() +
                        " | Diagnostico: " + pr.getDiagnostico() +
                        " | Prescricao: " + pr.getPrescricao()
                );
            }
        }


    }
    
}
