package repositorio;
import java.util.ArrayList;
import clinica.DadosAdicionaisPaciente;
import clinica.Paciente;

/**
 *
 * Classe que armazena uma lista de dados adicionais aos registros dos pacientes da clinica
 * 
 * Tem como publico os métodos:
 * - cadastrar dados(Faz um novo cadastro de dados adicionais, ou substitui um ja existente)
 * - busca dados adicionais(Busca os dados adicionais de um determinado paciente)
 * - remove dados adicionais
 * - atualiza x (Atualiza as informações do atributo x dos dados adicionais de um determinado paciente)
 * 
 * Além disso, também foi feito um método privado para o auxilio de outros métodos:
 * - busca indice dados(Busca o indice em que os dados adicionais de um determinado paciente se encontra na lista)
 */
public class RepositorioDadosAdicionais {
    private final ArrayList<DadosAdicionaisPaciente> dadosAdicionais;
    
    public RepositorioDadosAdicionais(){
        dadosAdicionais = new ArrayList();
    }
    
    public void cadastrarDados(DadosAdicionaisPaciente dados){
        int index = buscaIndiceDados(dados.getPaciente());
        
        if(index >= dadosAdicionais.size()){
            dadosAdicionais.add(dados);
        }
        else{
            dadosAdicionais.set(index, dados);
        }
    }
    
    private int buscaIndiceDados(Paciente paciente){
        int index = 0;
        for(DadosAdicionaisPaciente d: dadosAdicionais){
            if(d.getPaciente().equals(paciente)){
                break;
            }
            else{
                index++;
            }
        }
        
        return index;
    }
    
    public DadosAdicionaisPaciente buscaDadosAdicionais(Paciente paciente){
        int index = buscaIndiceDados(paciente);
        DadosAdicionaisPaciente dados = new DadosAdicionaisPaciente();
        
        if(index < dadosAdicionais.size()){
            dados = dadosAdicionais.get(index);
        }
        
        return dados;
    }
    
    public void removeDadosAdicionais(DadosAdicionaisPaciente dados){
        dadosAdicionais.remove(dados);
    }
    
    public void atualizaFuma(Paciente paciente, boolean novoFuma){
        int index = buscaIndiceDados(paciente);
        
        if(index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setFuma(novoFuma);
        }
    }
    
    public void atualizaBebe(Paciente paciente, boolean novoBebe){
        int index = buscaIndiceDados(paciente);
        
        if(index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setBebe(novoBebe);
        }
    }
    
    public void atualizaColesterol(Paciente paciente, boolean novoColesterol){
        int index = buscaIndiceDados(paciente);
        
        if(index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setColesterol(novoColesterol);
        }
    }
    
    public void atualizaDiabetes(Paciente paciente, boolean novoDiabetes){
        int index = buscaIndiceDados(paciente);
        
        if(index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setDiabetes(novoDiabetes);
        }
    }
    
    public void atualizaDoencaCardiaca(Paciente paciente, boolean novoDoencaCardiaca){
        int index = buscaIndiceDados(paciente);
        
        if(index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setDoencaCardiaca(novoDoencaCardiaca);
        }
    }
    
    public void atualizaCirurgias(Paciente paciente, String novaCirurgia){
        int index = buscaIndiceDados(paciente);
        
        if (index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setCirurgias(novaCirurgia);
        }
    }
    
    public void atualizaAlergias(Paciente paciente, String novaAlergia){
        int index = buscaIndiceDados(paciente);
        
        if(index < dadosAdicionais.size()){
            dadosAdicionais.get(index).setAlergias(novaAlergia);
        }
    }
}

