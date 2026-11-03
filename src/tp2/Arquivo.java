package tp2;

import java.io.*;

public class Arquivo {

    private RandomAccessFile arquivo;

    public Arquivo(String steam) throws IOException {
        arquivo = new RandomAccessFile(steam, "rw");
        if (arquivo.length() == 0) {
            arquivo.writeInt(0);
        }
    }

    public int getUltimoId() throws IOException {
        arquivo.seek(0);
        return arquivo.readInt();
    }

    public void setUltimoId(int id) throws IOException {
        arquivo.seek(0);
        arquivo.writeInt(id);
    }

    // CRUD AQUI EM BAIXO a
    // create
    /* create antigo 
    public void create(Jogo jogo) throws IOException {
        // Pegar o ultimo id que e utilizado e gera o prox
        int novoId = getUltimoId() + 1;

        // definir o id do jogo
        jogo.setId(novoId);
        // fazer o jogo em vetor
        byte[] vetor = jogo.byteparaArray();
        // vai para o final
        arquivo.seek(arquivo.length());
        // lapide 0 = registro valido / 1 lapide
        arquivo.writeByte(0);
        // tamanho do vetor
        arquivo.writeInt(vetor.length);

        // dados do jog
        arquivo.write(vetor);
        // atualizar o ultimo id
        setUltimoId(novoId);
    }
    */
   public long create(Jogo jogo)throws IOException{
    // Pegar o ultimo id que e utilizado e gera o prox
    int novoid = getUltimoId() +1;
    // definir o id do jogo
        jogo.setId(novoid);

        byte[] vetor = jogo.byteparaArray();

        // posicao final do registro
        long posicaoFinal = arquivo.length();
        arquivo.seek(posicaoFinal);

        arquivo.writeByte(0); // lapide
        arquivo.writeInt(vetor.length); // tamanho
        arquivo.write(vetor); // dado
        setUltimoId(novoid);

        return posicaoFinal;
   }
   // leituyra direta usando endereço arvore B
   public Jogo LerArvore(long pos) throws IOException{
    if(pos < 4 || pos >= arquivo.length()) return null;
    arquivo.seek(pos);
    byte lapide = arquivo.readByte();
    int tamanho = arquivo.readInt();


    if(lapide ==1){
        return null;
    }
    byte[] vetor = new byte[tamanho];
    arquivo.readFully(vetor);
    Jogo jogo = new Jogo();
    jogo.arrayParaByte(vetor);
    
    return jogo;
   }
    // delete com indice
   public boolean Deletar(long pos) throws IOException{
    if(pos < 4 || pos >= arquivo.length()) return false;

    arquivo.seek(pos);
    arquivo.writeByte(1);
    return true;
   }

    // read
    public Jogo read(int id) throws IOException {
        // primeiro pular o cabeçalho
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            // lendo a lapide
            byte lapide = arquivo.readByte();
            // lendo o tamanho do registro
            int tamanho = arquivo.readInt();

            // le o vetor
            byte[] vetor = new byte[tamanho];
            arquivo.readFully(vetor);
            // vemos se o registro e valido
            if (lapide == 0) {
                // reconstruir para ficar facil de pegar o id
                Jogo jogo = new Jogo();
                jogo.arrayParaByte(vetor);
                // verificar se o id e o certo
                if (jogo.getId() == id) {
                    return jogo;
                }
            }
        }
        // nao encontrou
        return null;
    }

    // delete
    public boolean delete(int id) throws IOException {
        // primeiro pular o cabeçalho
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            // guarda a posicao da lapide para dps trocar com 1 para ficar como lapide
            long posicaoLapide = arquivo.getFilePointer();
            // lendo a lapide
            byte lapide = arquivo.readByte();
            // le o tamanho
            int tamanho = arquivo.readInt();
            // le o vetor
            byte[] vetor = new byte[tamanho];
            arquivo.readFully(vetor);
            // se a lapide for real ne registro valido
            if (lapide == 0) {
                // mesma coisa do read
                Jogo jogo = new Jogo();
                jogo.arrayParaByte(vetor);
                // id for igual
                if (jogo.getId() == id) {
                    // acessando a posicao da lapide
                    arquivo.seek(posicaoLapide);
                    // mudando para lapide como verdade, marcando como excluido
                    arquivo.writeByte(1);

                    return true;
                }
            }
        }

        // nao acho
        return false;
    }

    // update
    public boolean update(Jogo novoJogo) throws IOException {
        // primeiro pular o cabeçalho
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            // guarda a posicao da lapide para dps trocar com 1 para ficar como lapide
            long posicaoLapide = arquivo.getFilePointer();
            // lendo a lapide
            byte lapide = arquivo.readByte();
            // guarda a posicao do tamanho
            long posicaoTamanho = arquivo.getFilePointer();
            // le o tamanho
            int tamanho = arquivo.readInt();
            // le os dados antigos pq vai atualizar
            byte[] vetorAntigo = new byte[tamanho];
            arquivo.readFully(vetorAntigo);

            // ve se o registro e valido
            if (lapide == 0) {
                Jogo jogoAntigo = new Jogo();
                jogoAntigo.arrayParaByte(vetorAntigo);

                // encontrou?
                if (jogoAntigo.getId() == novoJogo.getId()) {

                    // criando nopvo objeto
                    byte[] vetorNovo = novoJogo.byteparaArray();
                    // se o tamanho for igual o do antigo
                    if (vetorNovo.length == tamanho) {
                        // acessar o lugar
                        arquivo.seek(posicaoTamanho + 4);
                        // sobscrever os dados
                        arquivo.write(vetorNovo);
                    } else {
                        // tamanho diferente
                        // deixar como lapide o antigo pq se nao da muito trabalho que nem foi dito em
                        // aula exclui
                        // o antigo e faz outro
                        arquivo.seek(posicaoLapide);
                        arquivo.writeByte(1);

                        // ir para o final
                        arquivo.seek(arquivo.length());

                        // criar o novo registro
                        arquivo.writeByte(0);
                        arquivo.writeInt(vetorNovo.length);
                        arquivo.write(vetorNovo);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // criar metodo para ordenar usando insertion sort
    public void ordenar(Jogo[] jogo, int quantidade) {
        // fazer o insertion normal
        for (int i = 1; i < quantidade; i++) {
            Jogo atual = jogo[i];

            int j = i - 1;
            // insertion
            while (j >= 0 && jogo[j].getId() > atual.getId()) {
                jogo[j + 1] = jogo[j];
                j--;

            }
            jogo[j + 1] = atual;
        }
    }

    // metodo para criar bloco
    public void criarBlocos(int tamanho) throws IOException {
        // Apagar blocos antigos
        int blocoAntigo = 0;

        while (new File("bloco" + blocoAntigo + ".db").exists()) {
            File arquivoBloco = new File("bloco" + blocoAntigo + ".db");
            arquivoBloco.delete();
            blocoAntigo++;
        }
        // vetor pros jogos
        Jogo[] jogos = new Jogo[tamanho];
        // quantidade do vetor
        int quantidade = 0;
        // numero de blocos
        int numeroBloco = 0;
        // comçar o registro
        arquivo.seek(4);

        // jogo.db inteiroo
        while (arquivo.getFilePointer() < arquivo.length()) {
            // lapide
            byte lapide = arquivo.readByte();

            // tamanho
            int tamanhoRegistro = arquivo.readInt();

            byte[] vetor = new byte[tamanhoRegistro];

            arquivo.readFully(vetor);
            if (lapide == 0) {
                Jogo jogo = new Jogo();
                jogo.arrayParaByte(vetor);

                jogos[quantidade] = jogo;
                quantidade++;
                // bloco cheio
                if (quantidade == tamanho) {
                    // ordenar
                    ordenar(jogos, quantidade);
                    // nome do arquivo
                    String nomeBloco = "bloco" + numeroBloco + ".db";
                    // criar o arquivo temp
                    RandomAccessFile temp = new RandomAccessFile(nomeBloco, "rw");
                    // apagar tudo que tinha antes, estava dando um bug entao serve para arrumar
                    temp.setLength(0);
                    // gravar os jogos ordenados
                    for (int i = 0; i < quantidade; i++) {
                        byte[] vetorr = jogos[i].byteparaArray();
                        // escrever lapide
                        temp.writeByte(0);

                        // tamanho do registro
                        temp.writeInt(vetorr.length);

                        // o resto
                        temp.write(vetorr);
                    }
                    temp.close();
                    // pra falar que crio os arquvio System.out.println(nomeBloco + " criado com " +
                    // quantidade + " jogos.");

                    // o proximo bloco
                    jogos = new Jogo[tamanho];
                    quantidade = 0;
                    numeroBloco++;
                }
            }
        }
        // se sobrar algum jogo tipo 2701 ai o 1 vai
        if (quantidade > 0) {
            // ordenar o resto
            ordenar(jogos, quantidade);

            String nomeBloco = "bloco" + numeroBloco + ".db";

            RandomAccessFile temp = new RandomAccessFile(nomeBloco, "rw");
            // remover se tiver cois aantiga
            temp.setLength(0);

            for (int i = 0; i < quantidade; i++) {

                byte[] dados = jogos[i].byteparaArray();
                // lapide
                temp.writeByte(0);
                // tamanho
                temp.writeInt(dados.length);
                // resto
                temp.write(dados);
            }

            temp.close();

            // System.out.println(nomeBloco + " criado com " + quantidade + " jogos.");
        }
    }

    // Le o próximo jogo de um arquivo de bloco
    public Jogo lerJogoBloco(RandomAccessFile bloco) throws IOException {

        // chegamos ao final do arquivo
        if (bloco.getFilePointer() >= bloco.length()) {
            return null;
        }

        // lapide
        byte lapide = bloco.readByte();

        // tamanho do registro
        int tamanhoRegistro = bloco.readInt();

        // criar vetor
        byte[] vetor = new byte[tamanhoRegistro];

        // le os byte
        bloco.readFully(vetor);

        // ver se esta deletado
        if (lapide == 1) {
            return lerJogoBloco(bloco);
        }

        // byte pra jogo
        Jogo jogo = new Jogo();
        jogo.arrayParaByte(vetor);

        return jogo;
    }
    // juntar o antigo com o novo
    // serve para o jogos ordenados virar jogos.db
public void substituirArquivoOrdenado() throws IOException {

    arquivo.close();

    File antigo = new File("jogos.db");
    File novo = new File("jogos_ordenados.db");

    if (!antigo.delete()) {
       // throw new IOException("Erro");
    }

    if (!novo.renameTo(antigo)) {
        //throw new IOException("Erro");
    }

    arquivo = new RandomAccessFile("jogos.db", "rw");
}

    // fazer o merge dos blocos
public void intercalarBlocos(int caminhos) throws IOException {

    // Conta quantos blocos foram criados
    int quantidadeBlocos = 0;
    // contar quantos existem
    while (new File("bloco" + quantidadeBlocos + ".db").exists()) {
        quantidadeBlocos++;
    }

    // Verifica se o numero de caminhos é valido
    if (caminhos < 2) {
        throw new IllegalArgumentException("caminho tem que ser 2.");
    }

    // Se nao existem blocos não ha nada para ordenar
    if (quantidadeBlocos == 0) {
        return;
    }

    
     // Caso exista apenas um bloco,ele já está ordenado
    if (quantidadeBlocos == 1) {

        File bloco = new File("bloco0.db");
        File ordenado = new File("jogos_ordenados.db");

        if (ordenado.exists()) {
            ordenado.delete();
        }
        // leitura 
        RandomAccessFile entrada =new RandomAccessFile(bloco, "r");

        // leitura e escrita
        RandomAccessFile saida =new RandomAccessFile(ordenado, "rw");

        saida.setLength(0);

        // Escreve o cabeçalho
        int ultimoId = getUltimoId();
        saida.writeInt(ultimoId);

        // Copia os registros
        byte[] buffer = new byte[8192];
        int lidos;

        // passar para o arquivo ordenado
        while ((lidos = entrada.read(buffer)) != -1) {
            saida.write(buffer, 0, lidos);
        }

        entrada.close();
        saida.close();

        // Apaga o bloco temporário
        if (!bloco.delete()) {
            throw new IOException("Erro ao apagar bloco");
        }

        // troca o arquivo antigo pelo ordenado
        substituirArquivoOrdenado();

        System.out.println("Merge concluído.");
        return;
    }

     //enquanto houver mais de um bloco,fazemos novas rodadas de intercalação.
  
    int rodada = 0;

    while (quantidadeBlocos > 1) {

        int novosBlocos = 0;
        int blocoAtual = 0;

        while (blocoAtual < quantidadeBlocos) {

            // Quantos blocos participarão deste merge
            int quantidadeParaIntercalar =Math.min(caminhos,quantidadeBlocos - blocoAtual);

            RandomAccessFile[] blocos =new RandomAccessFile[quantidadeParaIntercalar];

            Jogo[] atuais =new Jogo[quantidadeParaIntercalar];

            // Abre os blocos
            for (int i = 0; i < quantidadeParaIntercalar;i++) {
                String nome ="bloco" + (blocoAtual + i) + ".db";

                blocos[i] =new RandomAccessFile(nome, "r");

                atuais[i] =lerJogoBloco(blocos[i]);
            }

            // Arquivo temporário de saída
            String nomeSaida ="merge" + rodada + "_" + novosBlocos + ".db";

            RandomAccessFile saida =new RandomAccessFile(nomeSaida, "rw");

            saida.setLength(0);

            int restantes = quantidadeParaIntercalar;

            // Intercala os blocos
            while (restantes > 0) {

                int menorIndice = -1;

                // Procura o menor ID
                for (int i = 0; i < quantidadeParaIntercalar;i++) {

                    if (atuais[i] != null) {

                        if (menorIndice == -1) {
                            menorIndice = i;
                        }
                        else if (atuais[i].getId()< atuais[menorIndice].getId()) {
                            // encontrou id menor atualiza passando ele como menor
                            menorIndice = i;
                        }
                    }
                }

                // Pega o menor jogo
                Jogo menor = atuais[menorIndice];

                byte[] vetor = menor.byteparaArray();

                // Grava o jogo
                //lapide
                saida.writeByte(0);
                //tamanho
                saida.writeInt(vetor.length);
                // dados o resto
                saida.write(vetor);

                // Le o próximo jogo daquele bloco
                atuais[menorIndice] =lerJogoBloco( blocos[menorIndice]);
                // Se acabou o bloco
                if (atuais[menorIndice] == null) {
                    restantes--;
                }
            }

            // Fecha os blocos
            for (int i = 0; i < quantidadeParaIntercalar;i++) {
                blocos[i].close();
            }

            saida.close();

            // Apaga os blocos antigos
            for (int i = 0; i < quantidadeParaIntercalar;i++) {

                File arquivoBloco =new File("bloco"+ (blocoAtual + i)+ ".db");

                arquivoBloco.delete();
            }

            novosBlocos++;
            blocoAtual += quantidadeParaIntercalar;
        }

        
         // Os arquivos merge viram os blocos
        for (int i = 0; i < novosBlocos; i++) {

            File antigo =new File("merge"+ rodada+ "_"+ i+ ".db");

            File novo = new File("bloco" + i + ".db");

            if (!antigo.renameTo(novo)) { throw new IOException("Erro ao renomear");
            }
        }

        quantidadeBlocos = novosBlocos;
        rodada++;
    }
    // aqui saiu do while la de cima

    
      //Sobra  apenas bloco0.db e ele fica com tudo ordenado la entao pronto
     

    File blocoFinal =new File("bloco0.db");

    File arquivoOrdenado =new File("jogos_ordenados.db");

    if (arquivoOrdenado.exists()) {
        arquivoOrdenado.delete();
    }

    RandomAccessFile entrada =new RandomAccessFile(blocoFinal, "r");

    RandomAccessFile saida =new RandomAccessFile(arquivoOrdenado, "rw");

    saida.setLength(0);

    // cabeçalho do arquivo principal
    int ultimoId = getUltimoId();
    saida.writeInt(ultimoId);

    // copia todos os registros do bloco final
    byte[] buffer = new byte[8192];
    int lidos;

    // salva tudo no arquivo
    while ((lidos = entrada.read(buffer)) != -1) {
        saida.write(buffer, 0, lidos);
    }

    entrada.close();
    saida.close();

    // apaga o bloco final
    if (!blocoFinal.delete()) {
        throw new IOException("Erro ao apagar bloco final.");
    }
     // isso aqui e so pra o jogos_ordenados ficar como jogos.db para sempre utilizarmos o ordenado
    substituirArquivoOrdenado();

    System.out.println("Merge concluído.");
}
// criar um arquivo para dar close para evitar algum tipo de erro
public void fechar() throws IOException {
        if (arquivo != null) {
            arquivo.close();
        }
    }
    // metodo para atualizar na arvore b+
    public long updateByOffset(long offset, Jogo novoJogo) throws IOException {
        if (offset < 4 || offset >= arquivo.length()) return -1;

        arquivo.seek(offset);
        byte lapide = arquivo.readByte();
        int tamanhoAntigo = arquivo.readInt();

        if (lapide == 1) return -1; // registro ja estava deletado

        byte[] vetorNovo = novoJogo.byteparaArray();

        // Se o novo registro couber no espaço antigo subscreve no mesmo lugar
        if (vetorNovo.length <= tamanhoAntigo) {
            arquivo.seek(offset + 5); // pula o byte da lapide e o int do tamanho
            arquivo.write(vetorNovo);
            return offset; // o offset continua o mesmo
        } else {
            // Se for maior marca o antigo como lápide 
            arquivo.seek(offset);
            arquivo.writeByte(1);

            // e grava o novo no final do arquivo
            long novoOffset = arquivo.length();
            arquivo.seek(novoOffset);
            arquivo.writeByte(0);
            arquivo.writeInt(vetorNovo.length);
            arquivo.write(vetorNovo);
            
            return novoOffset; // Retorna a nova posição para a arvore B+ ser atualizada
        }
    }
}
