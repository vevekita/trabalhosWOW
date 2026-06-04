package clinica;

public class Paciente {
    private int dadoIdentificacao;
    private String dataNascimento;
    private String endereco;
    private String infoContato;
    private String tipoConvenio;

    public int getDadoIdentificacao() {
        return dadoIdentificacao;
    }

    public void setDadosIdentificacao(int dadoIdentificacao) {
        this.dadoIdentificacao = dadoIdentificacao;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getInfoContato() {
        return infoContato;
    }

    public void setInfoContato(String infoContato) {
        this.infoContato = infoContato;
    }

    public String getTipoConvenio() {
        return tipoConvenio;
    }

    public void setTipoConvenio(String tipoConvenio) {
        this.tipoConvenio = tipoConvenio;
    }
    
    
}
