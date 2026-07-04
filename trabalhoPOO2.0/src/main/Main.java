/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import frames.LoginFrame;
import repositorio.RepositorioConsultas;
import repositorio.RepositorioDadosAdicionais;
import repositorio.RepositorioPaciente;
import repositorio.RepositorioProntuario;
import servicos.ServicoMedico;
import servicos.ServicoSecretaria;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 *Classe Main
 * Ela vai exibir exemplos para mostrar como é estruturado e realizado as operações.
 */
public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("TrabalhoPU");
        EntityManager em = emf.createEntityManager();
        
        RepositorioConsultas repoConsultas = new RepositorioConsultas(em);
        RepositorioPaciente repoPaciente = new RepositorioPaciente(em);
        RepositorioProntuario repoProntuario = new RepositorioProntuario(em);
        RepositorioDadosAdicionais repoDados = new RepositorioDadosAdicionais(em);
        
        ServicoSecretaria servicoSecretaria = new ServicoSecretaria(repoPaciente, repoConsultas);
        ServicoMedico servicoMedico = new ServicoMedico(repoDados, repoProntuario, repoPaciente, repoConsultas);
        
        LoginFrame telaLogin = new LoginFrame();
        telaLogin.setLocationRelativeTo(null);
        telaLogin.setVisible(true);
            
        
    }
    
}