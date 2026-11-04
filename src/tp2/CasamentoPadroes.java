package tp2;

import java.util.ArrayList;
import java.util.List;

public class CasamentoPadroes {

    // algoritimo de knth kmp
    private static int[] computar(String padrao){
        int m =padrao.length();
        int[] lps = new int[m];
        int i = 1;
        // comprimento do prefixo ant
        int len = 0; 

        lps[0] = 0;
        while(i < m){
            if(padrao.charAt(i) == padrao.charAt(len)){
                len++;
                lps[i] = len;
                i++;
            }else{
                if(len!=0){
                    // recuar para o anterior
                    len = lps[len-1];
                }else{
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
    // procurar agora no kmp
    public static List<Integer> buscarKMP(String texto, String padrao){
        List<Integer> ocorre = new ArrayList<>();
        if(padrao == null || padrao.isEmpty() || texto == null|| texto.isEmpty()){
            return ocorre;
        }
        int n = texto.length();
        int m = padrao.length();
        int[] lps = computar(padrao);
        int i = 0; //indice texto
        int j =0; // indice padrao
        while (i < n) {
            if (padrao.charAt(j) == texto.charAt(i)) {
                i++;
                j++;
            }
        if(j==m){
            // padrao encontrado
            ocorre.add(i-j);
            j = lps[j-1];
        }else if(i<n && padrao.charAt(j) != texto.charAt(i)){
            if(j!=0){
                j = lps[j-1];
            }else{
                i++;
            }
        }
    }
        return ocorre;
    }
    // algoritimo boyer-moore
    private static  final int Alfabeto = 256;

    // processar tabela para o caractere
    private static int[] computar2(String padrao){
        int[] mau = new int[Alfabeto];
        int m = padrao.length();

        // iniciar entrada com -1
        for(int i =0; i < Alfabeto; i++){
            mau[i] = -1;
        }
        // registra a ultima posicao de ocorrencia de cada caracter 
        for(int i =0; i < m; i++){
            char c = padrao.charAt(i);
            if(c < Alfabeto){
                mau[c] = i;
            }
        }
        return mau;
    }
    // procurar usando boyer moore
    public static List<Integer> buscarMoore(String texto, String padrao){
        List<Integer> ocorrencias = new ArrayList<>();
        if (padrao == null || padrao.isEmpty() || texto == null || texto.isEmpty()) {
            return ocorrencias;
        }
        int n = texto.length();
        int m = padrao.length();
        int[] mau = computar2(padrao);
        // deslocamento
        int s = 0; 
        while(s <= (n-m)){
            int j = m-1;
            // percorre o padrao da direita para esq
            while(j >= 0 && padrao.charAt(j) == texto.charAt(s+j)){
                j--;
            }
            // se o padrao coincidiu
            if(j < 0){
                ocorrencias.add(s);
                // proximo salto
                char proximo = ( s+m < n) ? texto.charAt(s+m) : 0;
                int salto = (proximo < Alfabeto) ? mau[proximo] : -1;
                s+= (s+m < n) ? m - salto: 1;
            }else{
                //salto baseado na regra do mau
                char charErrado = texto.charAt(s+j);
                int posPadrao = (charErrado < Alfabeto) ? mau[charErrado] : -1;
                s += Math.max(1, j-posPadrao);
            }
        }
        return ocorrencias;
    }
}
