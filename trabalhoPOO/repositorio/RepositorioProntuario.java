package repositorio;
import java.util.ArrayList;
import clinica.Prontuario;

/**
 *
 * Classe que armazena uma lista de prontuarios da clinica
 * 
 * Temos como métodos públicos:
 * - cadastrar Prontuario(Cadastra um novo prontuario)
 * - remove Prontuario
 * - busca Prontuario(Busca o prontuario de um determinado paciente)
 * - atualiza x(Atualiza a informação x de um determinado prontuario)
 */
public class RepositorioProntuario {
    private final ArrayList<Prontuario> prontuarios;
    
    public RepositorioProntuario(){
        prontuarios = new ArrayList();
    }
    
    public void cadastrarProntuario(Prontuario prontuario){
        prontuarios.add(prontuario);
    }
    
    public void removeProntuario(Prontuario prontuario){
        prontuarios.remove(prontuario);
    }
    
    public Prontuario buscaProntuario(int idPaciente){
        for(Prontuario p: prontuarios){
            if(p.getIdPaciente() == idPaciente){               
                return p;
            }  
        }
        
        return null;
    }
    
    public void atualizaSintomas(Prontuario prontuario, String novosSintomas){
        int index = prontuarios.indexOf(prontuario);
        
        prontuarios.get(index).setSintomas(novosSintomas);
    }
    
    public void atualizaDiagnostico(Prontuario prontuario, String novoDiagnostico){
        int index = prontuarios.indexOf(prontuario);
        
        prontuarios.get(index).setDiagnostico(novoDiagnostico);
    }
    
    public void atualizaPrescricao(Prontuario prontuario, String novaPrescricao){
        int index = prontuarios.indexOf(prontuario);
        
        prontuarios.get(index).setPrescricao(novaPrescricao);
    }
}

