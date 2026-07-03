/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;
import clinica.DadosAdicionaisPaciente;
import javax.persistence.EntityManager;

/**
 *
 * Classe que armazena uma lista de dados adicionais aos registros dos pacientes da clinica
 * 
 * Tem como publico os métodos:
 * - cadastrar dados(Faz um novo cadastro de dados adicionais, ou substitui um ja existente)
 * - busca dados adicionais(Busca os dados adicionais de um determinado paciente)
 * - remove dados adicionais
 * - atualiza x (Atualiza as informações do atributo x dos dados adicionais de um determinado paciente)
 * 
 */
public class RepositorioDadosAdicionais {
    private final EntityManager em;

    public RepositorioDadosAdicionais(EntityManager em) {
        this.em = em;
    }
    
    public void adicionarDados(DadosAdicionaisPaciente dados){
        em.getTransaction().begin();
        em.persist(dados);
        em.getTransaction().commit();
    }
    
    public void atualizarDados(DadosAdicionaisPaciente dados) {
        em.getTransaction().begin();
        DadosAdicionaisPaciente dadosAtuais = em.find(DadosAdicionaisPaciente.class, dados.getPacienteId());
        if (dadosAtuais != null) {
        dadosAtuais.setFuma(dados.isFuma());
        dadosAtuais.setBebe(dados.isBebe());
        dadosAtuais.setColesterol(dados.isColesterol());
        dadosAtuais.setDiabetes(dados.isDiabetes());
        dadosAtuais.setDoencaCardiaca(dados.isDoencaCardiaca());
        dadosAtuais.setCirurgias(dados.getCirurgias());
        dadosAtuais.setAlergias(dados.getAlergias());
        }
        em.getTransaction().commit();  
    }
    
    public DadosAdicionaisPaciente buscaDadosAdicionais(int pacienteId){
        return em.find(DadosAdicionaisPaciente.class, pacienteId);
    }
    
    public void removeDadosAdicionais(int pacienteId){
        em.getTransaction().begin();
        DadosAdicionaisPaciente dadosAdicionais = em.find(DadosAdicionaisPaciente.class, pacienteId);
        if (dadosAdicionais != null) {
            em.remove(dadosAdicionais);
        }
        em.getTransaction().commit();
    }
}
    
