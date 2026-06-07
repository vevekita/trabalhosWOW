package repositorio;
import java.util.ArrayList;
import java.util.List;
import clinica.Consulta;
import clinica.Paciente;

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
    private final List<Consulta> consultas = new ArrayList<>();
    
    public void adicionarConsulta(Consulta consulta){
        if (consultas.isEmpty()){
            consultas.add(consulta);
        }
        else{
            if(verificaColisaoHorarios(consulta.getHoras(), consulta.getMinutos(), consulta.getData(), consulta.getMedico(), consulta.getPaciente(), consulta.getTipoConsulta()) == false){
                consultas.add(consulta);
            }
        }
        
    }
    
    public Consulta buscarConsultaPaciente(Paciente paciente){
        int index = -1;
        
        for(Consulta c: consultas){
            if (c.getPaciente().equals(paciente)){
                index = consultas.indexOf(c);
                break;
            }
        }
        return consultas.get(index);
    }

    public Consulta buscarConsulta(String data, int horas, int minutos, String medico) { // Não tem como um mesmo médico estar em duas consultas diferentes no mesmo horário no mesmo dia
        for (Consulta c : consultas) {
            if (c.getData().equals(data) && c.getHoras() == horas && c.getMinutos() == minutos && c.getMedico().equals(medico)) {
                return c; // Retorna a consulta encontrada
            }
        }
        return null; // Não encontrou
    }
    
    public void removeConsulta(Consulta consulta){
        consultas.remove(consulta);
    }
    
    public boolean verificaColisaoHorarios(int horas, int minutos, String data, String medico, Paciente paciente, String tipoConsulta){
        boolean ocorreColisao = false;
        
        if (consultas.isEmpty()){
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
            
            for (Consulta c: consultas){
                
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
                if (c.getHoras() == horas && c.getMinutos() == minutos && c.getData().equals(data) && c.getMedico().equals(medico) && c.getPaciente().equals(paciente) && c.getTipoConsulta().equals(tipoConsulta)) {  //se a consulta de verificação é igual à consulta na lista:
                    ocorreColisao = false;
                    break;
                }
                if (c.getData().equals(data)){
                    if (c.getMedico().equals(medico) || c.getPaciente().equals(paciente)) {
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
    public List<Consulta> listarConsultas(){
        return new ArrayList<>(consultas); // Retorna uma cópia da lista de consultas
    }
}
