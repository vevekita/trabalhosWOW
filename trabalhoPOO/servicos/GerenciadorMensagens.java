package servico;

import clinica.Consulta;
import java.util.List;

public class GerenciadorMensagens {
    /*Recebe a lista das consultas de amanhã que a secretaria gerou que possuem contato*/
    
    public void enviarMensagens(List<Consulta> consultasParaAvisar){
        if(consultasParaAvisar == null || consultasParaAvisar.isEmpty()){
            System.out.println("Nenhuma mensagem a ser enviada!");
        }
        else{
            for(Consulta c : consultasParaAvisar){
                String nomePaciente = c.getPaciente().getNome();
                String medico = c.getMedico();
                String horario = c.getHoras() + ":" + c.getMinutos();
                String telefone = c.getPaciente().getTelefone();
                String email = c.getPaciente().getEmail();
                
                if(telefone != null){ //se tiver telefone envia SMS
                    System.out.println("(SMS enviado para o número " + telefone + ") " + nomePaciente + " sua consulta é amanhã às " + horario + " com o médico " + medico);
                }
                if(email != null){ //se tiver email envia email
                    System.out.println("(Email enviado para o email " + email + ") " + nomePaciente + " sua consulta é amanhã às " + horario + " com o médico " + medico);
                }
            }
        }
    }
}
