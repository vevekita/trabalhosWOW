package clinica;


public class DadosAdicionaisPaciente {
    private int idPaciente;
    private boolean fuma;
    private boolean bebe;
    private boolean colesterol;
    private boolean diabetes;
    private boolean doencaCardiaca;
    private String cirurgias;
    private String alergias;
    
    public DadosAdicionaisPaciente() {}
    public DadosAdicionaisPaciente(int idPaciente, boolean fuma, boolean bebe, boolean colesterol, boolean diabetes,boolean doencaCardiaca, String cirurgias, String alergias){
        this.idPaciente = idPaciente;
        this.fuma = fuma;
        this.bebe = bebe;
        this.colesterol = colesterol;
        this.diabetes = diabetes;
        this.doencaCardiaca = doencaCardiaca;
        this.cirurgias = cirurgias;
        this.alergias = alergias;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
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
