package repositorio;
import java.util.ArrayList;
import java.util.List;
import clinica.Consulta;
import clinica.Paciente;
import java.time.LocalDate;

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
 * Além disso, essa classe contém um método no qual é utilizado polimorfismo de sobreposição:
 * - verifica colisão de horários(Serve para verificar se ocorrerá uma colisão de horários), para cadastro e para atualizações(de data, de horario e de tipo de consulta)
 */

public class RepositorioConsultas {
    private final List<Consulta> consultas = new ArrayList<>();
    
    public List<Consulta> getConsultas(){
        return consultas;
    }
    
    public boolean adicionarConsulta(Consulta consulta){
        if (consultas.isEmpty()){
            consultas.add(consulta);
            return true;
        }
        else{
            if(verificaColisaoHorarios(consulta.getHoras(), consulta.getMinutos(), consulta.getData(), consulta.getMedico(), consulta.getPaciente(), consulta.getTipoConsulta()) == false){
                consultas.add(consulta);
                return true;
            }
            else{
                return false;
            }
        }
        
    }

    public Consulta buscarConsulta(LocalDate data, int horas, int minutos, String medico) { // Não tem como um mesmo médico estar em duas consultas diferentes no mesmo horário no mesmo dia
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
    
    //sobrecarga paa cadastro:
    public boolean verificaColisaoHorarios(int horas, int minutos, LocalDate data, String medico, Paciente paciente, String tipoConsulta){
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
    //sobrecarga para atualizações:
    public boolean verificaColisaoHorarios(int horas, int minutos, LocalDate data, String medico, String tipoConsulta, Consulta consulta) {
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
                if (consulta != null && c == consulta) { //se a consulta selecionada foi encontrada na lista:
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
                    if (c.getMedico().equals(medico) || c.getPaciente().equals(consulta.getPaciente())) {
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
