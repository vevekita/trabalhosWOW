/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinica;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

/**
 * Classe que armazena as informações clínicas de um paciente:
 * -IdPaciente (para associação à uma classe Paciente)
 * -Sintomas
 * -Diagnostico
 * -Prescrição
 * 
 */
@Entity
@Table(name="PRONTUÁRIOS")
public class Prontuario {
    // Pojo para representar um objeto do tipo Prontuario com id do paciente, sintomas, diagnostico e prescrição.
    // Este Pojo será mapeado em uma tabela chamada PRONTUÁRIOS no banco de dados.
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int idPaciente;
    @Column(length=100, name = "SINTOMAS", nullable = false)
    private String sintomas;
    @Column(length=50, name = "DIAGNÓSTICO", nullable = false)
    private String diagnostico;
    @Column(length=50, name = "PRESCRIÇÃO", nullable = false)
    private String prescricao;
    
    public Prontuario(){}
    public Prontuario(int idPaciente, String sintomas, String diagnostico, String prescricao){
        this.idPaciente = idPaciente;
        this.sintomas = sintomas;
        this.diagnostico = diagnostico;
        this.prescricao = prescricao;
    }
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
