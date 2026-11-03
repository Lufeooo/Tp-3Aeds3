package tp2;

import java.io.*;

public class Jogo {
    private int id; // sera controlado pelo arquivo
    private String nome; // String de tamanho variavel
    private String donos; // String fixa
    private long dataLancamento; // Data
    private float preco; // Float 
    private String genero; // Lista com seperador

    // modulo geral
    public Jogo(){
        this.id = -1;
        this.nome ="";
        this.donos= "00000000-00000000";
        this.dataLancamento = 0;
        this.preco = 0f;
        this.genero = "";
    }
    // construtor
    public Jogo(int id, String nome, String donos, long data, float preco, String genero){
        this.id = id;
        this.nome = nome;
        this.donos = donos;
        this.dataLancamento = data;
        this.preco = preco;
        this.genero = genero;
    }
    // set e gets para pegar as informaçoes
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
        public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDonos() {
        return donos;
    }

    public void setDonos(String donos) {
        this.donos = donos;
    }

    public long getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(long dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public float getPreco() {
        return preco;
    }

    public void setPreco(float preco) {
        this.preco = preco;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
    // vamos utilizar isso aqui, para no geral fazer o jogo atual ficar um vetor de byte
    public byte[] byteparaArray() throws IOException{
        ByteArrayOutputStream entra = new ByteArrayOutputStream();
        DataOutputStream escreve = new DataOutputStream(entra);

        // os 2 primeiros
        escreve.writeInt(id);
        escreve.writeUTF(nome);
        // agora vamos fazer o tamanho fixo 20 caracteres 
        for(int i =0; i <20; i++){
             if(i < this.donos.length()){
                escreve.writeChar(this.donos.charAt(i));
             }
             else{
                escreve.writeChar(' ');
             }
        }
        // escrevendo o resto dos parametros
        escreve.writeLong(dataLancamento);
        escreve.writeFloat(preco);
        escreve.writeUTF(genero);

        return entra.toByteArray();
    }
    // reconstruir atributos a partir de um vetor de byte
    public void arrayParaByte(byte[] vetor) throws IOException{
        ByteArrayInputStream entrada = new ByteArrayInputStream(vetor);
        DataInputStream lendo = new DataInputStream(entrada); 

        this.id = lendo.readInt();
        this.nome = lendo.readUTF();
        String temp =""; // string fixaz
        for(int i =0;i < 20; i++){
            temp += lendo.readChar();
        }
        this.donos = temp.trim(); // usando pq estava com " " adicional entao removendo
        this.dataLancamento = lendo.readLong();
        this.preco = lendo.readFloat();
        this.genero = lendo.readUTF();

    }
    
}
