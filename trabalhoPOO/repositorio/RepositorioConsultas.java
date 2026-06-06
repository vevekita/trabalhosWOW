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
            if(verificaColisaoHorarios(consulta.getHorario(), consulta.getData()) == false){
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

    public Consulta buscarConsulta(String data, String horario, String medico) { // Não tem como um mesmo médico estar em duas consultas diferentes no mesmo horário no mesmo dia
        for (Consulta c : consultas) {
            if (c.getData().equals(data) && c.getHorario().equals(horario) && c.getMedico().equals(medico)) {
                return c; // Retorna a consulta encontrada
            }
        }
        return null; // Não encontrou
    }
    
    public void removeConsulta(Consulta consulta){
        consultas.remove(consulta);
    }
    
    private boolean verificaColisaoHorarios(String horario, String data, String medico){
        boolean ocorreColisao = false;
        
        if (consultas.isEmpty()){
            ocorreColisao = false;
        }
        else{
            for (Consulta c: consultas){
                if (c.getHorario().equals(horario) && c.getData().equals(data) && c.getMedico().equals(medico)){
                    ocorreColisao = true;
                    break;
                }
            }
        }
        
        return ocorreColisao;
    }

    //a parte de atualizar foi passada pro ServicoSecretaria
    /*public void atualizaData(Consulta consulta, String novaData){
        if(verificaColisaoHorarios(consulta.getHorario(), novaData) == false){
            int index = consultas.indexOf(consulta);
            consultas.get(index).setData(novaData);
        }
    }
    
    public void atualizaHorario(Consulta consulta, String novoHorario){
        if(verificaColisaoHorarios(novoHorario, consulta.getData()) == false){
            int index = consultas.indexOf(consulta);
            consultas.get(index).setHorario(novoHorario);
        }
    }
    
    public void atualizaMedico(Consulta consulta, String medico){
        int index = consultas.indexOf(consulta);
        consultas.get(index).setMedico(medico);
    }
    
    public void atualizaPaciente(Consulta consulta, Paciente novoPaciente){
        int index = consultas.indexOf(consulta);
        consultas.get(index).setPaciente(novoPaciente);
    }
    
    public void atualizaTipoConsulta(Consulta consulta, String novoTipo){
        int index = consultas.indexOf(consulta);
        consultas.get(index).setTipoConsulta(novoTipo);
    }*/
}
