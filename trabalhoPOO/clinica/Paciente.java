package clinica;
/**
 * Classe representa uma ficha de um paciente, armazenando as informações de um paciente:
 * -Dado de Identificação (ID)
 * -Data de nascimento
 * -Endereço
 * -telefone
 * -email
 * -Tipo de convênio (Particular ou planoo de saúde)
 * 
 */
public class Paciente{
    private int dadoIdentificacao;
    private String nome;
    private String dataNascimento;
    private String endereco;
    private String telefone;
    private String email;
    private String tipoConvenio; //particular ou plano de saúde
    
    public Paciente() {
        
    }
    public Paciente(int dadoIdentificacao, String nome, String dataNascimento, String endereco, String telefone, String email, String tipoConvenio) {
        this.dadoIdentificacao = dadoIdentificacao;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.tipoConvenio = tipoConvenio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getTipoConvenio() {
        return tipoConvenio;
    }

    public void setTipoConvenio(String tipoConvenio) {
        this.tipoConvenio = tipoConvenio;
    }
}
