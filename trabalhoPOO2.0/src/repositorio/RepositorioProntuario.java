/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;
import java.util.ArrayList;
import java.util.List;
import clinica.Prontuario;
import javax.persistence.EntityManager;
import javax.persistence.Query;

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
    private final EntityManager em;

    public RepositorioProntuario(EntityManager em) {
        this.em = em;
    }
    
    public void adicionarProntuario(Prontuario prontuario){
        em.getTransaction().begin();
        em.persist(prontuario);
        em.getTransaction().commit();
    }
    
    public void atualizarProntuario(Prontuario prontuario) {
        em.getTransaction().begin();
        Prontuario prontuarioAtual = em.find(Prontuario.class, prontuario.getIdPaciente());
        if (prontuarioAtual != null) {
            em.persist(prontuario);
            String sintomas = prontuario.getSintomas();
            String diagnostico = prontuario.getDiagnostico();
            String prescricao = prontuario.getPrescricao();
            prontuarioAtual.setSintomas(sintomas);
            prontuarioAtual.setDiagnostico(diagnostico);
            prontuarioAtual.setPrescricao(prescricao);
        }
        em.getTransaction().commit();
    }
    
    public void removeProntuario(int idPaciente){
        em.getTransaction().begin();
        Prontuario prontuarioAtual = em.find(Prontuario.class, idPaciente);
        if (prontuarioAtual != null) {
            em.remove(prontuarioAtual);
        }
        em.getTransaction().commit();
    }
    
    public Prontuario buscaProntuario(int idPaciente){
        return em.find(Prontuario.class, idPaciente);
    }
}
