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
 * Classe representa uma ficha de um paciente, armazenando as informações de um paciente:
 * -Dado de Identificação (ID)
 * -Data de nascimento
 * -Endereço
 * -telefone
 * -email
 * -Tipo de convênio (Particular ou planoo de saúde)
 * 
 */
@Entity
@Table(name="PACIENTES") 
public class Paciente {
    // Pojo para representar um objeto do tipo Paciente com dadoIdentificação, nome, dataNascimento, endereco,
    // telefone, email e tipoConvenio.
    // Este Pojo será mapeado em uma tabela chamada Pacientes no banco de dados.
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int dadoIdentificacao;
    @Column(length=100, name = "NOME", nullable = false)
    private String nome;
    @Column(length=10, name = "DATA DE NASCIMENTO", nullable = false)
    private String dataNascimento;
    @Column(length=20, name = "ENDEREÇO", nullable = false)
    private String endereco;
    @Column(length=13, name = "TELEFONE", nullable = true)
    private String telefone;
    @Column(length=20, name = "EMAIL", nullable = true)
    private String email;
    @Column(length=15, name = "TIPO DE CONVÊNIO", nullable = false)
    private String tipoConvenio; //particular ou plano de saúde

    public int getDadoIdentificacao() {
        return dadoIdentificacao;
    }

    public void setDadosIdentificacao(int dadoIdentificacao) {
        this.dadoIdentificacao = dadoIdentificacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getTipoConvenio() {
        return tipoConvenio;
    }

    public void setTipoConvenio(String tipoConvenio) {
        this.tipoConvenio = tipoConvenio;
    }
    
    
}
