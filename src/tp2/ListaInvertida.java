package tp2;

import java.io.*;
import java.util.*;
public class ListaInvertida {
 
    private String nomeArquivo;
    // hash que a chave é o termo e o valor e a lista de ids
    private HashMap<String, ArrayList<Integer>> dicionario;
    public ListaInvertida(String nomeArquivo){
        this.nomeArquivo = nomeArquivo;
        this.dicionario = new HashMap<>();
        carregar();
    }
    // insere um id na lista
    public void inserir(String termo, int id){
        // deixa tudo em minusculo para facilitar 
        termo = termo.toLowerCase().trim();

        // se o termo nao existe cria um novo
        if(!dicionario.containsKey(termo)){
            dicionario.put(termo, new ArrayList<>());
        }
        // adicionar o id na lista se o termo ja existe
        if(!dicionario.get(termo).contains(id)){
            dicionario.get(termo).add(id);
        }
    
    }
    // retorna a lista de ids associada a uma palavra
    public ArrayList<Integer> buscar(String palavra) {
        palavra = palavra.toLowerCase().trim();
        
        if (dicionario.containsKey(palavra)) {
            return dicionario.get(palavra);
        } else {
            return new ArrayList<>(); // retorna nada
        }
    }
    // salvar no arquivo
    public void salvar() throws IOException{
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(nomeArquivo));

        // escrever quantas palavras diferentes existem
        dos.writeInt(dicionario.size());

        // para cada palavra no dicionario 
        for(String palavra : dicionario.keySet()){
            dos.writeUTF(palavra); // salvar a apalavrra

            // pega a lista dos ids que pertencem a essa palvara
            ArrayList<Integer> listaID = dicionario.get(palavra);

            // salva a quantidade de ids
            dos.writeInt(listaID.size());

            // salvar cada um dos id
            for(int i =0; i < listaID.size(); i++){
                int id = listaID.get(i);
                dos.writeInt(id);
            }
        }
        dos.close();
        }
        // carregar o arquivo
        private void carregar() {
            File f = new File(nomeArquivo);
            if(!f.exists() || f.length() == 0) return; // se ja existe
            try{
                DataInputStream dis = new DataInputStream(new FileInputStream(f));

                // le a quantidade de palavra
                int totalPalavras = dis.readInt();
                for(int i =0; i < totalPalavras; i++){
                    // le a palavra para ver quantos ids tem
                    String palavra = dis.readUTF();
                    int quantidade = dis.readInt();

                    ArrayList<Integer> listaid = new ArrayList<>();

                    // le todos os ids da palavra
                    for(int j =0; j < quantidade; j++){
                        int id = dis.readInt();
                        listaid.add(id);
                    }
                    // guardar palavra e a lista montada
                    dicionario.put(palavra, listaid);
                }
                dis.close();
            }catch(Exception e){
                System.out.println("erro na lista invertida" + e.getMessage());
            }
        }
        public void processarTexto(String texto, int id, String separador) {
        String[] partes = texto.split(separador);
        for (String palavra : partes) {
            String palavraLimpa = "";
            for (int i = 0; i < palavra.length(); i++) {
                char c = palavra.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    palavraLimpa += c;
                }
            }
            if (palavraLimpa.length() > 0) {
                this.inserir(palavraLimpa, id); 
            }
        }
    }
}

