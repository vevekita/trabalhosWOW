/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinica;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

/**
 * Pojo que representa a ficha de um paciente, armazenando os seguintes dados de um paciente:
 * - Dado de Identificação (ID)
 * - Data de nascimento
 * - Endereço
 * - Telefone
 * - Email
 * - Tipo de convênio (Particular ou plano de saúde)
 * Este pojo será mapeado em uma tabela chamada PACIENTES no banco de dados.
 */

@Entity
@Table(name="PACIENTES") 
public class Paciente {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int dadoIdentificacao;
    private String nome;
    private String dataNascimento;
    private String endereco;
    private String telefone;
    private String email;
    private String tipoConvenio;
    
    // Construtores
    public Paciente() {}
    public Paciente(int dadoIdentificacao, String nome, String dataNascimento, String endereco, String telefone, String email, String tipoConvenio) {
        this.dadoIdentificacao = dadoIdentificacao;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.tipoConvenio = tipoConvenio;
    }
    
    public int getDadoIdentificacao() {
        return dadoIdentificacao;
    }

    public void setDadoIdentificacao(int dadoIdentificacao) {
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
