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
 *Classe dos dados adicionais do Paciente. Ela é acessada somente pelo médico!
 * Essa classe, por complementar a classe Paciente, mas por precisar ter acesso restrito do médico, possui um atributo Paciente a fim de criar uma associação
 * - Fuma (sim ou não)
 * - Bebe (sim ou não)
 * - Colesterol (sim ou não)
 * - Diabetes (sim ou não)
 * - Doença cardíaca (sim ou não)
 * - Se já fez cirurgia (se sim, especificar qual tipo)
 * - Se tem alguma alergia (se sim, especificar do quê)
 */

@Entity
@Table(name="DADOS ADICIONAIS DO PACIENTE")
public class DadosAdicionaisPaciente {
    // Pojo para representar um objeto do tipo DadosAdicionaisPaciente com nome do paciente,
    // se fuma, se bebe, tipo de colesterol, se tem diabetes, se tem doença cardiaca, se tem cirurgias e se tem alergias.
    // Este Pojo será mapeado em uma tabela chamada Consultas no banco de dados.
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Paciente paciente;
    private boolean fuma;
    private boolean bebe;
    private boolean colesterol;
    private boolean diabetes;
    private boolean doencaCardiaca;
    private String cirurgias;
    private String alergias;

    public DadosAdicionaisPaciente() {}
    public DadosAdicionaisPaciente(Paciente paciente, boolean fuma, boolean bebe, boolean colesterol, boolean diabetes,boolean doencaCardiaca, String cirurgias, String alergias){
        this.paciente = paciente;
        this.fuma = fuma;
        this.bebe = bebe;
        this.colesterol = colesterol;
        this.diabetes = diabetes;
        this.doencaCardiaca = doencaCardiaca;
        this.cirurgias = cirurgias;
        this.alergias = alergias;
    }
    
    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
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
