package repositorio;

import java.util.List;
import java.util.ArrayList;
import clinica.Paciente;

/**
 *
 * Classe que guarda e opera a lista de Pacientes na Clinica
 * 
 * Tem como publicos os métodos:
 * - adicionar pacientes(Que adiciona pacientes à lista)
 * - busca paciente(Que busca o objeto paciente, a partir do seu número de indentificação)
 * - remove paciente(Que remove um paciente da lista)
 * - atualiza x(Atualiza a informação x de determinado paciente na lista, onde x pode ser: Endereço, info de contato ou tipo de convenio)
 */

public class RepositorioPaciente {
    private final List<Paciente> pacientes = new ArrayList<>();
            
    public void adicionarPaciente(Paciente paciente){
        pacientes.add(paciente);
    }

    public void removePaciente(Paciente paciente){
        pacientes.remove(paciente);
    }

    public Paciente buscaPaciente(int idPaciente){
        for (Paciente p : pacientes) {
            if (p.getDadoIdentificacao() == idPaciente) {
                return p;
            }
        }
        return null;
    }
    
    /*public Paciente buscaPaciente(int idPaciente){
        int index = -1;
        Paciente p1 = new Paciente();
        
        for (Paciente p: pacientes){
            if (p.getDadoIdentificacao() == idPaciente){
                index = pacientes.indexOf(p);
                break;
            }
        }
        
        if(index != -1){
            p1 = pacientes.get(index);
        }
        else{
            p1.setDadosIdentificacao(-1);      /*Marcação lógica de que o Paciente não existe, ou seja, o Paciente não está na lista*/
        }
        
        /*return p1;
    }*/

    //passei a parte de atualizar pro ServicoSecretaria
    /*public void atualizaDataNascimento(Paciente paciente, String novaData){
        int index = pacientes.indexOf(paciente);            /*Indice na lista que se encontra o paciente que será atualizado*/
        /*pacientes.get(index).setDataNascimento(novaData);
    }
    
    public void atualizaEndereco(Paciente paciente, String novoEndereco){
        int index = pacientes.indexOf(paciente);
        pacientes.get(index).setEndereco(novoEndereco);
    }
    
    public void atualizaEmail(Paciente paciente, String novoEmail){
        int index = pacientes.indexOf(paciente);
        pacientes.get(index).setEmail(novoEmail);
    }
    
    public void atualizaTelefone(Paciente paciente, String telefone){
        int index = pacientes.indexOf(paciente);
        pacientes.get(index).setTelefone(telefone);
    }
    
    public void atualizaTipoConvenio(Paciente paciente, String novoConvenio){
        int index = pacientes.indexOf(paciente);
        pacientes.get(index).setTipoConvenio(novoConvenio);
    }*/
}

