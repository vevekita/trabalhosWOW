/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;
import javax.persistence.EntityManager;
import clinica.Paciente;
import java.util.List;
import javax.persistence.Query;

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
    private final EntityManager em;
    
    public RepositorioPaciente(EntityManager em) {
        this.em = em;
    }
            
    public void adicionarPaciente(Paciente pacienteAtual) {
        em.getTransaction().begin();
        em.persist(pacienteAtual);
        em.getTransaction().commit();
    }
    
    public void atualizarPaciente(Paciente paciente) {
        em.getTransaction().begin();
        Paciente pacienteAtual = em.find(Paciente.class, paciente.getDadoIdentificacao());
        if (pacienteAtual == null) {
            em.persist(pacienteAtual);
        } else {
            pacienteAtual.setNome(pacienteAtual.getNome());
            pacienteAtual.setDataNascimento(pacienteAtual.getDataNascimento());
            pacienteAtual.setEndereco(pacienteAtual.getEndereco());
            pacienteAtual.setTelefone(pacienteAtual.getTelefone());
            pacienteAtual.setEmail(pacienteAtual.getEmail());
            pacienteAtual.setTipoConvenio(pacienteAtual.getTipoConvenio());
        }
        em.getTransaction().commit();
    }
            
    public Paciente buscaPaciente(int idPaciente){
        return em.find(Paciente.class, idPaciente);
    }
    
    public void removePaciente(int idPaciente){
        em.getTransaction().begin();   
        Paciente paciente = em.find(Paciente.class, idPaciente);
        if (paciente != null) {
            em.remove(paciente);
        }
        em.getTransaction().commit();
    }
}
