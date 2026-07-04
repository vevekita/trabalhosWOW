/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicos;

import clinica.Paciente;
import clinica.Consulta;
import repositorio.RepositorioPaciente;
import java.util.List;
import javax.swing.JOptionPane;

public class GerenciadorMensagens {
    /*Recebe a lista das consultas de amanhã que a secretaria gerou que possuem contato*/
    private final RepositorioPaciente repositorioPaciente;
    
    public GerenciadorMensagens(RepositorioPaciente repositorioPaciente) {
        this.repositorioPaciente = repositorioPaciente;
    }
    
    public void enviarMensagens(List<Consulta> consultasParaAvisar){
        if(consultasParaAvisar == null || consultasParaAvisar.isEmpty()){
            JOptionPane.showMessageDialog(null, "Nenhuma mensagem a ser enviada!"); // Aparece um pop-up na tela com a mensagem
        }
        else{
            for(Consulta c : consultasParaAvisar){
                Paciente pacienteAtual = repositorioPaciente.buscaPaciente(c.getPacienteId());
                if (pacienteAtual != null) {
                    String nomePaciente = pacienteAtual.getNome();
                    String medico = c.getMedico();
                    String horario = c.getHoras() + ":" + (c.getMinutos() < 10 ? "0" + c.getMinutos() : c.getMinutos());
                    String telefone = pacienteAtual.getTelefone();
                    String email = pacienteAtual.getEmail();
                
                    if(telefone != null){ // Se tiver telefone envia SMS (aparece um pop-up na tela)
                        JOptionPane.showMessageDialog(null, "(SMS enviado para o numero " + telefone + ") " + nomePaciente + " sua consulta esta marcada para amanha as " + horario + " com o(a) doutor(a) " + medico);
                    }
                    if(email != null){ // Se tiver email envia email (aparece um pop-up na tela)
                        JOptionPane.showMessageDialog(null, "(Email enviado para o email " + email + ") " + nomePaciente + " sua consulta esta marcada para amanha as " + horario + " com o(a) doutor(a) " + medico);
                    }
                }
            }
        }
    }
}
