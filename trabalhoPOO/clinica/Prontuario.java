package clinica;

/**
 * Classe que armazena as informações clínicas de um paciente:
 * -IdPaciente (para associação à uma classe Paciente)
 * -Sintomas
 * -Diagnostico
 * -Prescrição
 * 
 */
public class Prontuario {
    private int idPaciente;
    private String sintomas;
    private String diagnostico;
    private String prescricao;

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getPrescricao() {
        return prescricao;
    }

    public void setPrescricao(String prescricao) {
        this.prescricao = prescricao;
    }
    
    
}
