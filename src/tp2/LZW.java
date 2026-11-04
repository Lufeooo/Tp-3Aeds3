package tp2;

import java.io.*;
import java.util.*;

// codigo de lzw
public class LZW {
    private static final int TAMANHO_DICIONARIO = 4096;

    // converte byte oara sinal para valor positivp
    private static int byteParaPositivo(byte b) {
        return (b < 0) ? b + 256 : b;
    }

    // comprimir o ficheiro

    public static void comprimir(String ficheiro, String Destino) throws IOException{
        File Entrada = new File(ficheiro);
        byte[] dados = new byte[(int)Entrada.length()];

       try (DataInputStream dis = new DataInputStream(new FileInputStream(Entrada))) {
            dis.readFully(dados);
        }
        if(dados.length ==0){
            new File(Destino).createNewFile();
            return;
        }
        // tabela hash para os simbolos 
        Map<Long, Integer> dicionario = new HashMap<>();
        int proximoCodigo = 256;
        List<Integer> codigos = new ArrayList<>();
        // o primeiro byte torna prefixo
        int prefixo = byteParaPositivo(dados[0]);
        for(int i =1; i < dados.length; i++){
            int b = byteParaPositivo(dados[i]);
            // chave unica
            long chave = ((long) prefixo * 256) + b;
            if(dicionario.containsKey(chave)){
                prefixo = dicionario.get(chave);
            }else{
                codigos.add(prefixo);
            if(proximoCodigo < TAMANHO_DICIONARIO){
                dicionario.put(chave, proximoCodigo);
                proximoCodigo++;
            }
            prefixo = b;
        }
    }
    // adicionar o ultimo
    codigos.add(prefixo);
    
    // Grava no ficheiro de destino
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(Destino))) {
            dos.writeInt(codigos.size());
            for (int cod : codigos) {
                dos.writeShort((short) cod); // 2 bytes por codigo
            }
        }
    }
    // descomprimir o ficheiro restaurando os bytes
    public static void descomprimir(String ficheiroOrigem, String ficheiroDestino) throws IOException {
        File fEntrada = new File(ficheiroOrigem);
        if (fEntrada.length() == 0) {
            new File(ficheiroDestino).createNewFile();
            return;
        }

        List<Integer> codigos = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(fEntrada))) {
            int total = dis.readInt();
            for (int i = 0; i < total; i++) {
                short valor = dis.readShort();
                // Converte short negativo para inteiro sem sinal
                int codigoLimpo = (valor < 0) ? valor + 65536 : valor;
                codigos.add(codigoLimpo);
            }
        }

        if (codigos.isEmpty()) {
            new File(ficheiroDestino).createNewFile();
            return;
        }

        // Vetores para reconstruir as sequencias
        int[] prefixos = new int[TAMANHO_DICIONARIO];
        byte[] sufixos = new byte[TAMANHO_DICIONARIO];

        for (int i = 0; i < 256; i++) {
            prefixos[i] = -1;
            sufixos[i] = (byte) i;
        }
        int proximoCodigo = 256;

        ByteArrayOutputStream bufferSaida = new ByteArrayOutputStream();

        int codigoVelho = codigos.get(0);
        bufferSaida.write(sufixos[codigoVelho]);

        byte primeiroByte = sufixos[codigoVelho];

        byte[] pilha = new byte[TAMANHO_DICIONARIO];

        for (int i = 1; i < codigos.size(); i++) {
            int codigoNovo = codigos.get(i);
            int tempCodigo = codigoNovo;
            int topoPilha = 0;

            if (codigoNovo >= proximoCodigo) {
                pilha[topoPilha] = primeiroByte;
                topoPilha++;
                tempCodigo = codigoVelho;
            }

            while (tempCodigo >= 256) {
                pilha[topoPilha] = sufixos[tempCodigo];
                topoPilha++;
                tempCodigo = prefixos[tempCodigo];
            }
            primeiroByte = sufixos[tempCodigo];
            pilha[topoPilha] = primeiroByte;
            topoPilha++;

            while (topoPilha > 0) {
                topoPilha--;
                bufferSaida.write(pilha[topoPilha]);
            }

            if (proximoCodigo < TAMANHO_DICIONARIO) {
                prefixos[proximoCodigo] = codigoVelho;
                sufixos[proximoCodigo] = primeiroByte;
                proximoCodigo++;
            }

            codigoVelho = codigoNovo;
        }

        try (FileOutputStream fos = new FileOutputStream(ficheiroDestino)) {
            fos.write(bufferSaida.toByteArray());
        }
    
    
    }
}

