package clinica;


public class Consulta {
    private String data;
    private String horario;
    private String medico;
    private Paciente paciente;
    private String tipoConsulta; //consulta normal(1h) ou retorno(30min)
    
    public Consulta() {
        
    }
    public Consulta(String data, String horario, String medico, Paciente paciente, String tipoConsulta) {
        this.data = data;
        this.horario = horario;
        this.medico = medico;
        this.paciente = paciente;
        this.tipoConsulta = tipoConsulta;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setMedico(String medico) {
        this.medico = medico;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getData() {
        return data;
    }

    public String getHorario() {
        return horario;
    }

    public String getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }
    
    public String getTipoConsulta() {
        return tipoConsulta;
    }
}
