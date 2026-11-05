package tp2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Huffman {

    // classe para representar os no de hufmman
    public static class NoHuffman {
        int simbolo;
        long frequencia;
        NoHuffman esq;
        NoHuffman dir;

        public NoHuffman(int simbolo, long frequencia) {
            this.simbolo = simbolo;
            this.frequencia = frequencia;
            this.esq = null;
            this.dir = null;
        }

        public NoHuffman(long frequencia, NoHuffman esq, NoHuffman dir) {
            this.simbolo = -1;
            this.frequencia = frequencia;
            this.esq = esq;
            this.dir = dir;
        }

        public boolean eFolha() {
            return (this.esq == null && this.dir == null);
        }
    }

    private static int byteParaPositivo(byte b) {
        return (b < 0) ? b + 256 : b;
    }

    // remover e devolver o no com menor frequencia
    private static NoHuffman extrairMenor(List<NoHuffman> lista) {
        int menor = 0;
        long menorFreq = lista.get(0).frequencia;
        for (int i = 1; i < lista.size(); i++) {
            if (lista.get(i).frequencia < menorFreq) {
                menorFreq = lista.get(i).frequencia;
                menor = i;
            }
        }
        return lista.remove(menor);
    }

    // construir a arvore
    private static NoHuffman construirArvore(long[] frequencia) {
        List<NoHuffman> lista = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            if (frequencia[i] > 0) {
                lista.add(new NoHuffman(i, frequencia[i]));
            }
        }
        if (lista.isEmpty()) {
            return null;
        }
        // caso existe apenas um tipo
        if (lista.size() == 1) {
            NoHuffman umso = lista.remove(0);
            return new NoHuffman(umso.frequencia, umso, null);
        }
        // copombina os dois nos com menores frequencias
        while (lista.size() > 1) {
            NoHuffman n1 = extrairMenor(lista);
            NoHuffman n2 = extrairMenor(lista);
            NoHuffman pai = new NoHuffman(n1.frequencia + n2.frequencia, n1, n2);
            lista.add(pai);
        }
        return lista.get(0);
    }

    // percore a arvore e gera codigos de bytes
    private static void gerarTabelaCodigo(NoHuffman no, String codigoAtual, String[] tabelaCodigo) {
        if (no == null) {
            return;
        }
        if (no.eFolha()) {
            tabelaCodigo[no.simbolo] = (codigoAtual.length() > 0) ? codigoAtual : "0";
            return;
        }
        gerarTabelaCodigo(no.esq, codigoAtual + "0", tabelaCodigo);
        gerarTabelaCodigo(no.dir, codigoAtual + "1", tabelaCodigo);
    }

    // comprimri o ficheiro de origiem em huffman
    public static void comprimir(String ficheiroOriginal, String Destino) throws IOException {
        File entrada = new File(ficheiroOriginal);
        byte[] dados = new byte[(int) entrada.length()];
        try (DataInputStream dis = new DataInputStream(new FileInputStream(entrada))) {
            dis.readFully(dados);
        }
        if (dados.length == 0) {
            new File(Destino).createNewFile();
            return;
        }
        // contagem de frequencia
        long[] frequencias = new long[256];
        for (int i = 0; i < dados.length; i++) {
            frequencias[byteParaPositivo(dados[i])]++;
        }
        // construçao da arvore e tabela
        NoHuffman raiz = construirArvore(frequencias);
        String[] tabela = new String[256];
        gerarTabelaCodigo(raiz, "", tabela);

        // gfravar no ficheiro
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(Destino)))) {
            dos.writeInt(dados.length);
            // total de byte

            int simbolos = 0;
            for (int i = 0; i < 256; i++) {
                if (frequencias[i] > 0) {
                    simbolos++;
                }
            }
            dos.writeInt(simbolos);
            for (int i = 0; i < 256; i++) {
                if (frequencias[i] > 0) {
                    dos.writeByte(i);
                    dos.writeInt((int) frequencias[i]);
                }
            }
            int byteatual = 0;
            int bitsPrenchidos = 0;
            for (int i = 0; i < dados.length; i++) {
                String codigo = tabela[byteParaPositivo(dados[i])];
                for (int j = 0; j < codigo.length(); j++) {
                    byteatual = (byteatual << 1);
                    if (codigo.charAt(j) == '1') {
                        byteatual = byteatual | 1;
                    }
                    bitsPrenchidos++;
                    if (bitsPrenchidos == 8) {
                        dos.writeByte(byteatual);
                        byteatual = 0;
                        bitsPrenchidos = 0;
                    }
                }
            }
            if (bitsPrenchidos > 0) {
                byteatual = byteatual << (8 - bitsPrenchidos);
                dos.writeByte(byteatual);
            }
        }
    }

    // descomprimir o huffman para os byte originais
    public static void descomprimir(String origem, String destino) throws IOException {
        File entrada = new File(origem);
        if (entrada.length() == 0) {
            new File(destino).createNewFile();
            return;
        }
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(entrada)));
                FileOutputStream fos = new FileOutputStream(destino)) {
            int total = dis.readInt();
            int simbolos = dis.readInt();

            long[] frequencia = new long[256];
            for (int i = 0; i < simbolos; i++) {
                int simbolo = byteParaPositivo(dis.readByte());
                int freq = dis.readInt();
                frequencia[simbolo] = freq;
            }
            NoHuffman raiz = construirArvore(frequencia);
            int bytesescrito = 0;
            NoHuffman NoAtual = raiz;
            while (bytesescrito < total) {
                int byteLido = dis.read();
                if (byteLido == -1) {
                    break;
                }
                for (int bit = 7; bit >= 0 && bytesescrito < total; bit--) {
                    int valorBit = (byteLido >> bit) & 1;
                    if (valorBit == 1) {
                        NoAtual = NoAtual.dir;
                    } else {
                        NoAtual = NoAtual.esq;
                    }
                    if (NoAtual.eFolha()) {
                        fos.write(NoAtual.simbolo);
                        bytesescrito++;
                        NoAtual = raiz;
                    }
                }
            }
        }

    }
}
