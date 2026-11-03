package tp2;

import java.io.*;
import java.text.SimpleDateFormat;

public class Carga {
    public static void csv(String steamcsv, Arquivo arquivo, ArvoreBmais arvore, ListaInvertida listaNomes, ListaInvertida listaGeneros) throws IOException {
        BufferedReader ler = new BufferedReader(new FileReader(steamcsv));

        ler.readLine(); // pular o cabeçalho do csv

        String linha;
        while ((linha = ler.readLine()) != null) {

            // dividir o csv
            String[] campos = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // utilizei isso por causa que o csv  infelizmente tem nome com , dentro ai complicou tudo e bugou tudo tbm
            // nome
            String nome = campos[1];
            // data
            String data = campos[2];
            // genero
            String genero = campos[9];
            // donos
            String donos = campos[16];
            // preço
            float preco = Float.parseFloat(campos[17]);

            long dataLancamento = 0;
            // tratamento de data
            try {
                dataLancamento = new SimpleDateFormat("yyyy-MM-dd").parse(data).getTime();
            } catch (Exception e) {
                System.out.println("data deuy erro");
            }
            // criando objeto
            Jogo jogo = new Jogo(0, nome, donos, dataLancamento, preco, genero);
            // deixar para arvore
            long offset = arquivo.create(jogo);
            // gravar o indice da arvore b+
            arvore.inserir(jogo.getId(), offset);
            // gravar a lista invertida de nomes
            listaNomes.processarTexto(nome, jogo.getId(), " ");
            listaGeneros.processarTexto(genero, jogo.getId(), ",");
        }
        ler.close();
        arvore.salvar();
        listaNomes.salvar();
        listaGeneros.salvar();
    }

}
