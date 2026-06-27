/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;
import java.util.ArrayList;
import clinica.Paciente;
import java.util.List;

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
            
    public boolean adicionarPaciente(Paciente paciente){
        if(verificaPacienteNaLista(paciente) == false){
            pacientes.add(paciente);
            return true;
        }
        else{
            return false;
        }
    }
    
    public Paciente buscaPaciente(int idPaciente){
        for (Paciente p : pacientes) {
            if (p.getDadoIdentificacao() == idPaciente) {
                return p; 
            }
        }
        return null;
    }
    
    public void removePaciente(Paciente paciente){
        pacientes.remove(paciente);
    }
    
    private boolean verificaPacienteNaLista(Paciente paciente){
        boolean estaPresente = false;
        
        for(Paciente p: pacientes){
            if(p.getDadoIdentificacao() == paciente.getDadoIdentificacao()){
            estaPresente = true;
            break;
            }
        }
        
        return estaPresente;
    }
    
    public List<Paciente> listarPacientes(){
        return new ArrayList<>(pacientes); // Retorna uma cópia da lista de pacientes
    }
}
