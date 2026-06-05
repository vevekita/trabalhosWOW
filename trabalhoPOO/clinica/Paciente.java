package clinica;
/**
 * Classe representa uma ficha de um paciente, armazenando as informações de um paciente:
 * -Dado de Identificação (ID)
 * -Data de nascimento
 * -Endereço
 * -Informações de contato (como e-mail e celular)
 * -Tipo de convênio (Particular ou planoo de saúde)
 * 
 */
public class Paciente{
    private int dadoIdentificacao;
    private String dataNascimento;
    private String endereco;
    private String infoContato; //email e/ou telefone
    private String tipoConvenio; //particular ou plano de saúde
    
    public Paciente() {
        
    }
    public Paciente(int dadoIdentificacao, String dataNascimento, String endereco, String infoContato, String tipoConvenio, DadosAdicionaisPaciente dadosAdicionais) {
        this.dadoIdentificacao = dadoIdentificacao;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.infoContato = infoContato;
        this.tipoConvenio = tipoConvenio;
    }
    
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
