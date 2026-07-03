/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinica;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Pojo para representar um objeto de Dados Adicionais do Paciente com os dados:
 * - Id do paciente (para identificação)
 * - Fuma (sim ou não)
 * - Bebe (sim ou não)
 * - Colesterol (sim ou não)
 * - Diabetes (sim ou não)
 * - Doença cardíaca (sim ou não)
 * - Se já fez cirurgia (se sim, especificar qual tipo)
 * - Se tem alguma alergia (se sim, especificar do quê)
 * Essa classe complementa a classe do Paciente, 
 * mas por precisar ter acesso restrito do médico, possui um atributo pacienteId criando uma associação;
 * Este pojo será mapeado em uma tabela chamada DADOS ADICIONAIS DO PACIENTE no banco de dados.
 */

@Entity
@Table(name="DADOS ADICIONAIS DO PACIENTE")
public class DadosAdicionaisPaciente {
    @Id
    private int pacienteId;
    private boolean fuma;
    private boolean bebe;
    private boolean colesterol;
    private boolean diabetes;
    private boolean doencaCardiaca;
    private String cirurgias;
    private String alergias;
    
    // Construtores
    public DadosAdicionaisPaciente() {}
    public DadosAdicionaisPaciente(int pacienteId, boolean fuma, boolean bebe, boolean colesterol, boolean diabetes,boolean doencaCardiaca, String cirurgias, String alergias){
        this.pacienteId = pacienteId;
        this.fuma = fuma;
        this.bebe = bebe;
        this.colesterol = colesterol;
        this.diabetes = diabetes;
        this.doencaCardiaca = doencaCardiaca;
        this.cirurgias = cirurgias;
        this.alergias = alergias;
    }

    public int getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(int pacienteId) {
        this.pacienteId = pacienteId;
    }

    public boolean isFuma() {
        return fuma;
    }

    public void setFuma(boolean fuma) {
        this.fuma = fuma;
    }

    public boolean isBebe() {
        return bebe;
    }

    public void setBebe(boolean bebe) {
        this.bebe = bebe;
    }

    public boolean isColesterol() {
        return colesterol;
    }

    public void setColesterol(boolean colesterol) {
        this.colesterol = colesterol;
    }

    public boolean isDiabetes() {
        return diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public boolean isDoencaCardiaca() {
        return doencaCardiaca;
    }

    public void setDoencaCardiaca(boolean doencaCardiaca) {
        this.doencaCardiaca = doencaCardiaca;
    }

    public String getCirurgias() {
        return cirurgias;
    }

    public void setCirurgias(String cirurgias) {
        this.cirurgias = cirurgias;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }
    
    
}
