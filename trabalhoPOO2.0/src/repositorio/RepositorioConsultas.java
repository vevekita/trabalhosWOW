/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package repositorio;
import javax.persistence.EntityManager;
import java.util.List;
import clinica.Consulta;
import java.time.LocalDate;
import javax.persistence.Query;

/**
 *
 * Classe que guarda uma lista de consultas na Clinica
 * 
 * Tem como publicos os métodos:
 * - adicionar consulta(Que adiciona uma consulta SE não houver colisão de horário com outra consulta ja existente)
 * - buscar consulta(Busca uma consulta, a partir do paciente)
 * - remove consulta(Auto explicativo)
 * - Atualiza x(Que atualiza a informação x de uma determinada consulta, podendo ser: Data, Horario, Medico, Paciente ou Tipo de Consulta)
 * 
 * Além disso, essa classe contém um método privado:
 * - verifica colisão de horários(Serve para verificar se ocorrerá uma colisão de horários)
 */

public class RepositorioConsultas {
    private final EntityManager em;
    
    public RepositorioConsultas(EntityManager em) {
        this.em = em;
    }
    
    public boolean adicionarConsulta(Consulta consulta){
        if (verificaColisaoHorarios(consulta.getHoras(), consulta.getMinutos(), consulta.getData(), consulta.getMedico(), consulta.getPacienteId(), consulta.getTipoConsulta()) == false) {
            em.getTransaction().begin();
            em.persist(consulta);
            em.getTransaction().commit();
            return true;
        }
        return false;
    }
    
    public boolean atualizarConsulta(int idConsulta, int novasHoras, int novosMinutos, LocalDate novaData, String Medico, int idPaciente, String novoTipo) {
        Consulta consultaAtual = buscarConsulta(idConsulta);
        if (consultaAtual != null) {
            if (!verificaColisaoHorarios(novasHoras, novosMinutos, novaData, Medico, novoTipo, consultaAtual)) {
                em.getTransaction().begin();
                consultaAtual.setHoras(novasHoras);
                consultaAtual.setMinutos(novosMinutos);
                consultaAtual.setData(novaData);
                consultaAtual.setMedico(Medico);
                consultaAtual.setPacienteId(idPaciente);
                consultaAtual.setTipoConsulta(novoTipo);
                
                em.getTransaction().commit(); 
                return true;
            }
        }
        return false;
    }

    public Consulta buscarConsulta(int consultaId) {
        return em.find(Consulta.class, consultaId);
        
    }
    
    public void removeConsulta(int consultaId){
        em.getTransaction().begin();
        Consulta consulta = em.find(Consulta.class, consultaId);
        if (consulta != null) {
            em.remove(consulta);
        }
        em.getTransaction().commit();
    }
    
    public List<Consulta> listarConsultas() { //lista todas as consultas
        Query query = em.createQuery("SELECT c FROM Consulta c");
        return query.getResultList();
    }
    
    //sobrecarga paa cadastro:
    public boolean verificaColisaoHorarios(int horas, int minutos, LocalDate data, String medico, int pacienteId, String tipoConsulta){
        boolean ocorreColisao = false;
        List<Consulta> todasConsultas = listarConsultas();
        
        if (todasConsultas.isEmpty()){
            ocorreColisao = false;
        }
        else{
            int horarioCons = (horas * 60) + minutos;
            int duracaoCons = 0;
            //se o tipo da Consulta for o normal(duração de 1h):
            if ("normal".equals(tipoConsulta)) {
                duracaoCons = 60;
            } else {
                //se o tipo da Consulta for de retorno (duração de 30min):
                if ("retorno".equals(tipoConsulta)) {
                    duracaoCons = 30;
                }
            }
            int fimCons = horarioCons + duracaoCons;
            
            for (Consulta c: todasConsultas){
                
                int horarioC = (c.getHoras() * 60) + c.getMinutos();
                int duracaoC = 0;
                if ("normal".equals(c.getTipoConsulta())) {
                    duracaoC = 60;
                } else {
                    if ("retorno".equals(c.getTipoConsulta())) {
                        duracaoC = 30;
                    }
                }
                int fimC = horarioC + duracaoC;
                if (c.getData().equals(data)){
                    if (c.getMedico().equals(medico) || c.getPacienteId() == pacienteId) {
                        if (horarioC < fimCons && fimC > horarioCons) { //verifica sobreposição de horários
                            ocorreColisao = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return ocorreColisao;
    }
    //sobrecarga para atualizações:
    public boolean verificaColisaoHorarios(int horas, int minutos, LocalDate data, String medico, String tipoConsulta, Consulta consulta) {
        List<Consulta> todasConsultas = listarConsultas();
        boolean ocorreColisao = false;
        
        if (consulta == null) {
            return false;
        }
        if (todasConsultas.isEmpty()){
            ocorreColisao = false;
        }
        else{
            int horarioCons = (horas * 60) + minutos;
            int duracaoCons = 0;
            //se o tipo da Consulta for o normal(duração de 1h):
            if ("normal".equals(tipoConsulta)) {
                duracaoCons = 60;
            } else {
                //se o tipo da Consulta for de retorno (duração de 30min):
                if ("retorno".equals(tipoConsulta)) {
                    duracaoCons = 30;
                }
            }
            int fimCons = horarioCons + duracaoCons;
            
            for (Consulta c: todasConsultas){
                if (c.getIdConsulta() == consulta.getIdConsulta()) { //se a consulta selecionada foi encontrada na lista:
                    continue; //desconsidera as operações dessa consulta específica dentro do loop
                }
                int horarioC = (c.getHoras() * 60) + c.getMinutos();
                int duracaoC = 0;
                if ("normal".equals(c.getTipoConsulta())) {
                    duracaoC = 60;
                } else {
                    if ("retorno".equals(c.getTipoConsulta())) {
                        duracaoC = 30;
                    }
                }
                int fimC = horarioC + duracaoC;
                if (c.getData().equals(data)){
                    if (c.getMedico().equals(medico) || c.getPacienteId() == consulta.getPacienteId()) {
                        if (horarioC < fimCons && fimC > horarioCons) { //verifica sobreposição de horários
                            ocorreColisao = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return ocorreColisao;
    }
}
