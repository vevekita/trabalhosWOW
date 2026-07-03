/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinica;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pojo para representar um objeto Prontuario que armazena as informações clínicas de um paciente, como:
 * - IdPaciente (para associação à um objeto Paciente)
 * - Sintomas
 * - Diagnóstico
 * - Prescrição
 * Este pojo será mapeado em uma tabela chamada PRONTUÁRIOS no banco de dados.
 */

@Entity
@Table(name="PRONTUÁRIOS")
public class Prontuario {
    @Id
    private int idPaciente; // Existe apenas um prontuário por paciente, então é possível identificar um prontuário pelo id do paciente 
    private String sintomas;
    private String diagnostico;
    private String prescricao;
    
    // Construtores
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
