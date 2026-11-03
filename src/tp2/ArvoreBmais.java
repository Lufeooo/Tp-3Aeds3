package tp2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ArvoreBmais {
    private int ordem;
    private No raiz;
    private String nomeArquivo;

    // Classe interna do No
    private static class No {
        boolean folha;
        List<Integer> chaves;
        List<Long> offsets; // Usado apenas nas folhas: aponta para jogos.db
        List<No> filhos; // Usado apenas nos nós internos
        No proxima; // Encadeamento entre folhas

        public No(boolean folha) {
            this.folha = folha;
            this.chaves = new ArrayList<>();
            this.offsets = new ArrayList<>();
            this.filhos = new ArrayList<>();
            this.proxima = null;

        }
    }

    // geral
    public ArvoreBmais(String Arquivo, int ordem) throws IOException {
        this.nomeArquivo = Arquivo;
        this.ordem = Math.max(ordem, 3); // so pra ser sempre maior que 3 a ordem
        this.raiz = new No(true);
        carregar();
    }

    // buscar retornar o off set do registro
    public long buscar(int id) {
        No folha = encontrarFolha(raiz, id);
        // pesquisar todas folhas
        for (int i = 0; i < folha.chaves.size(); i++) {
            if (folha.chaves.get(i) == id) {
                return folha.offsets.get(i);
            }
        }
        // erro
        return -1;
    }

    // percorrer ate achar a folha
    private No encontrarFolha(No atual, int id) {
        if (atual.folha) {
            return atual;
        }
        int i = 0;
        while (i < atual.chaves.size() && id >= atual.chaves.get(i)) {
            i++;
        }
        return encontrarFolha(atual.filhos.get(i), id);
    }

    // inserçao
    public void inserir(int id, long offset) throws IOException {
        No folha = encontrarFolha(raiz, id);
        // se o id ja existe atualizar o offset so
        for (int i = 0; i < folha.chaves.size(); i++) {
            if (folha.chaves.get(i) == id) {
                folha.offsets.set(i, offset);
                //salvar();
                return;
            }
        }
        // inserçao ordenada
        int pos = 0;
        // caminhando ate inserir na posiçao certa
        while (pos < folha.chaves.size() && folha.chaves.get(pos) < id) {
            pos++;
        }
        // inserir
        folha.chaves.add(pos, id);
        folha.offsets.add(pos, offset);
        // se a folha estoura
        if (folha.chaves.size() >= ordem) {
            dividirFolha(folha);
        }
        //salvar();
    }

    // metodo para dividir as folha
    private void dividirFolha(No folha) {
        No nova = new No(true);
        int meio = folha.chaves.size() / 2;

        // passar os dps do meio
        nova.chaves.addAll(folha.chaves.subList(meio, folha.chaves.size()));
        nova.offsets.addAll(folha.offsets.subList(meio, folha.chaves.size()));
        // folha original arrumar
        folha.chaves = new ArrayList<>(folha.chaves.subList(0, meio));
        folha.offsets = new ArrayList<>(folha.offsets.subList(0, meio));

        // apontar
        nova.proxima = folha.proxima;
        folha.proxima = nova;
        int numeroSubiu = nova.chaves.get(0);
        if (folha == raiz) {
            No novaRaiz = new No(false);
            novaRaiz.chaves.add(numeroSubiu);
            novaRaiz.filhos.add(folha);
            novaRaiz.filhos.add(nova);
            raiz = novaRaiz;
        } else {
            inserirPai(raiz, numeroSubiu, folha, nova);
        }
    }

    // inserir pai metodo
    private void inserirPai(No pai, int chave, No filhoesq, No filhodir) {
        No paiReal = acharPai(raiz, filhoesq);
        if (paiReal == null)
            return;

        int pos = 0;
        while (pos < paiReal.chaves.size() && paiReal.chaves.get(pos) < chave) {
            pos++;
        }
        paiReal.chaves.add(pos, chave);
        paiReal.filhos.add(pos + 1, filhodir);

        if (paiReal.chaves.size() >= ordem) {
            dividirInterno(paiReal);
        }
    }

    // achar o pai
    private No acharPai(No atual, No certo) {
        if (atual.folha || atual.filhos.isEmpty())
            return null;
        for (No filho : atual.filhos) {
            if (filho == certo) {
                return atual;
            }
        }
        for (No filho : atual.filhos) {
            No p = acharPai(filho, certo);
            if (p != null)
                return p;
        }
        return null;
    }

    private void dividirInterno(No folha) {
        No nova = new No(false);
        int meio = folha.chaves.size() / 2;
        int chavePromo = folha.chaves.get(meio);

        // passar os dps do meio
        nova.chaves.addAll(folha.chaves.subList(meio+1, folha.chaves.size()));
        nova.filhos.addAll(folha.filhos.subList(meio +1, folha.filhos.size()));
        // folha original arrumar
        folha.chaves = new ArrayList<>(folha.chaves.subList(0, meio));
        folha.filhos = new ArrayList<>(folha.filhos.subList(0, meio+1));

        // apontar
        if (folha == raiz) {
            No novaRaiz = new No(false);
            novaRaiz.chaves.add(chavePromo);
            novaRaiz.filhos.add(folha);
            novaRaiz.filhos.add(nova);
            raiz = novaRaiz;
        } else {
            inserirPai(raiz, chavePromo, folha, nova);
        }

    }
    // gravar os pares da arvore no arquivo de indice
    public void salvar() throws IOException{
        File f = new File(nomeArquivo);
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(f))) {
            // Percorre a lista encadeada de folhas
            No folha = raiz;
            while (!folha.folha) {
                folha = folha.filhos.get(0);
            }
            int total = 0;
            No aux = folha;
            while(aux!=null){
                total+= aux.chaves.size();
                aux = aux.proxima;
            }
            dos.writeInt(total);
            // escrever cada par id e o offset
            aux = folha; while(aux!=null){
                for(int i =0; i < aux.chaves.size(); i++){
                    dos.writeInt(aux.chaves.get(i));
                    dos.writeLong(aux.offsets.get(i));
                }
                aux = aux.proxima;
            }
        }
    }
    // carregar rescostruir o indice a partir do arquivo
    private void carregar(){
        File f = new File(nomeArquivo);
        if(!f.exists() || f.length() ==0) return;
        try(DataInputStream dis = new DataInputStream(new FileInputStream(f))){
            int total = dis.readInt();
            for(int i =0; i < total; i++){
                int id = dis.readInt();
                long offset = dis.readLong();
                inserirSemSalvar(id, offset);
            }
        }catch (Exception e){
            this.raiz = new No(true);
        }
    }
    // metodo auxiliar do carregar
    private void inserirSemSalvar(int id, long offset){
        No folha = encontrarFolha(raiz, id);
        int pos =0;
        while(pos < folha.chaves.size() && folha.chaves.get(pos) < id){
            pos++;
        }
        folha.chaves.add(pos, id);
        folha.offsets.add(pos, offset);

        if(folha.chaves.size() >= ordem){
            dividirFolha(folha);
        }
    }
    
}
