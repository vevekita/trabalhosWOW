package clinica;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

/**
 * Classe que armazena os dados de determinada consulta, como:
 * -Data
 * -Horas
 * -Minutos
 * -Nome do médico
 * -Paciente
 * -Tipo de consulta (normal -> 1 hora ou retorno .> 30 minutos)
 * Essa classe é acessada pela Secretária
 */
@Entity
@Table(name="CONSULTAS")
public class Consulta {
    // Pojo para representar um objeto do tipo Consulta com data, horas, minutos, 
    // nome do médico, nome do paciente, tipo da consulta.
    // Este Pojo será mapeado em uma tabela chamada Consultas no banco de dados.
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private LocalDate data; // Existe uma notação específica, mas tem que ver se dá pra usar sem
    private int horas;
    private int minutos;
    private String medico;
    private Paciente paciente;
    private String tipoConsulta; //consulta normal(1h) ou retorno(30min)
    
    public Consulta() {}
    public Consulta(LocalDate data, int horas, int minutos, String medico, Paciente paciente, String tipoConsulta) {
        this.data = data;
        this.horas = horas;
        this.minutos = minutos;
        this.medico = medico;
        this.paciente = paciente;
        this.tipoConsulta = tipoConsulta;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
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

    public LocalDate getData() {
        return data;
    }

    public int getHoras() {
        return horas;
    }

    public int getMinutos() {
        return minutos;
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
