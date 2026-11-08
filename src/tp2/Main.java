package tp2;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {

        Scanner scanf = new Scanner(System.in);

        Arquivo arquivo = new Arquivo("jogos.db");
        // iniciar a arvore b+ agora
        ArvoreBmais arvore = new ArvoreBmais("indiceArvore", 8);
        // iniciar as listas
        ListaInvertida listaNomes = new ListaInvertida("listaNomes.db");
        ListaInvertida listaGeneros = new ListaInvertida("listaGeneros.db");

        // Opção escolhida pelo usuário
        int opcao = -1;

        // escolheu 0 cabo
        while (opcao != 0) {

            System.out.println("1 - Carregar banco de dados (CSV)");
            System.out.println("2 - Criar jogo");
            System.out.println("3 - Ler jogo");
            System.out.println("4 - Atualizar jogo");
            System.out.println("5 - Deletar jogo");
            System.out.println("6 - Ordenar arquivo");
            System.out.println("7 - Busca Combinada");
            System.out.println("8 - Casamento de Padroes (KMP vs Boyer-Moore)");
            System.out.println("9 - Compactar base de dados (Huffman e LZW)");
            System.out.println("10 - Descompactar versao da base de dados");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanf.nextInt();

            // executa a opção escolhida
            switch (opcao) {

                case 1:
                    // lista de caminhos para o programa tentar encontrar o arquivo
                    // isso e importante para conseguir rodar em outros pc, pq quando dava git clone
                    // nao rodava
                    String[] caminhosPossiveis = {
                            "steam.csv",
                            "Tp-3Aeds3/steam.csv",
                            "../steam.csv",
                            "/tmp/steam.csv"
                    };

                    String caminhoCorreto = null;

                    // testar o caminho ate achar o certo
                    for (String caminho : caminhosPossiveis) {
                        if (new File(caminho).exists()) {
                            caminhoCorreto = caminho;
                            break;
                        }
                    }

                    // deu erro nao conseguiu criar por causa de erro
                    if (caminhoCorreto == null) {
                        System.out.println("Erro: csv nao encontrado");
                    } else {
                        // Roda a carga com a arvore e a lista
                        Carga.csv(caminhoCorreto, arquivo, arvore, listaNomes, listaGeneros);
                        System.out.println("Carga finalizada!");
                    }
                    break;

                case 2:

                    // o nome
                    System.out.print("Digite o nome do jogo: ");
                    scanf.nextLine(); // limpa
                    String nome = scanf.nextLine();

                    // pedir a quantidade de donos
                    System.out.print("Digite a quantidade de donos: xxxxx-xxxxx ");
                    String donos = scanf.nextLine();

                    // a data
                    System.out.print("Digite a data de lançamento (yyyy-MM-dd): ");
                    String data = scanf.nextLine();

                    // converter a data para long
                    long dataLancamento = 0;

                    try {
                        dataLancamento = new java.text.SimpleDateFormat("yyyy-MM-dd")
                                .parse(data)
                                .getTime();
                    } catch (Exception e) {
                        System.out.println("Data invalida.");
                        break;
                    }

                    // pedir o preço
                    System.out.print("Digite o preço: ");
                    // fazer aceitar virgula tbm
                    String precoTexto = scanf.nextLine();

                    precoTexto = precoTexto.replace(",", ".");
                    float preco = Float.parseFloat(precoTexto);

                    // pedir o genero
                    System.out.print("Digite o genero: ");
                    String genero = scanf.nextLine();

                    // criar o objeto o id começa com 0 porque o create() vai gerar o id certoc
                    Jogo novoJogo = new Jogo(
                            0,
                            nome,
                            donos,
                            dataLancamento,
                            preco,
                            genero);

                    // pegar o offset e retornar para salvar na arvore
                    long offset = arquivo.create(novoJogo);
                    arvore.inserir(novoJogo.getId(), offset);
                    arvore.salvar();
                    listaNomes.processarTexto(novoJogo.getNome(), novoJogo.getId(), " ");
                    listaGeneros.processarTexto(novoJogo.getGenero(), novoJogo.getId(), ",");
                    listaNomes.salvar();
                    listaGeneros.salvar();

                    System.out.println("Jogo criado com sucesso");
                    System.out.println("ID gerado: " + novoJogo.getId());

                    break;

                case 3:
                    System.out.print("Digite o ID do jogo: ");
                    int id = scanf.nextInt();

                    // procura o jogo na árvore
                    long offsetBusca = arvore.buscar(id);

                    if (offsetBusca != -1) {
                        Jogo jogo = arquivo.LerArvore(offsetBusca);

                        // Verificar se o jogo retornado não esta deletado lapide 1
                        if (jogo != null) {
                            System.out.println("ID: " + jogo.getId());
                            System.out.println("Nome: " + jogo.getNome());
                            System.out.println("Donos: " + jogo.getDonos());
                            String dataFormatada = new java.text.SimpleDateFormat("dd/MM/yyyy")
                                    .format(new java.util.Date(jogo.getDataLancamento()));
                            System.out.println("Data: " + dataFormatada);
                            System.out.println("Preço: $" + jogo.getPreco());
                            System.out.println("Gênero: " + jogo.getGenero());
                        } else {
                            // Se a arvore tem o offset mas o LerArvore retornou null
                            System.out.println("Este jogo foi deletado.");
                        }

                    } else {
                        System.out.println("Jogo nao encontrado.");
                    }
                    break;

                case 4:
                    System.out.print("Digite o ID do jogo para atualizar: ");
                    int idUpdate = scanf.nextInt();

                    // Busca a posição instantaneamente na Árvore
                    long offsetAtual = arvore.buscar(idUpdate);

                    if (offsetAtual == -1) {
                        System.out.println("Jogo nao encontrado.");
                        break;
                    }

                    // Lê o jogo diretamente usando o offset
                    Jogo jogoUpdate = arquivo.LerArvore(offsetAtual);
                    scanf.nextLine(); // Limpa o buffer

                    System.out.println("Nome atual: " + jogoUpdate.getNome());
                    System.out.print("Digite o novo nome: ");
                    String novoNome = scanf.nextLine();

                    System.out.println("Donos atuais: " + jogoUpdate.getDonos());
                    System.out.print("Digite os novos donos (xxxxx-xxxxx): ");
                    String novosDonos = scanf.nextLine();

                    String dataForm = new java.text.SimpleDateFormat("dd/MM/yyyy")
                            .format(new java.util.Date(jogoUpdate.getDataLancamento()));
                    System.out.println("Data atual: " + dataForm);
                    System.out.print("Digite a nova data (yyyy-MM-dd): ");
                    String novaData = scanf.nextLine();

                    long novaDataLancamento = jogoUpdate.getDataLancamento();
                    try {
                        novaDataLancamento = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(novaData).getTime();
                    } catch (Exception e) {
                        System.out.println("Data inválida. Mantendo a antiga.");
                    }

                    System.out.println("Preço atual: " + jogoUpdate.getPreco());
                    System.out.print("Digite o novo preço: ");
                    String precoStr = scanf.nextLine().replace(",", ".");
                    float novoPreco = precoStr.isEmpty() ? jogoUpdate.getPreco() : Float.parseFloat(precoStr);

                    System.out.println("Gênero atual: " + jogoUpdate.getGenero());
                    System.out.print("Digite o novo gênero: ");
                    String novoGenero = scanf.nextLine();

                    // Aplica as mudanças no objeto
                    jogoUpdate.setNome(novoNome);
                    jogoUpdate.setDonos(novosDonos);
                    jogoUpdate.setDataLancamento(novaDataLancamento);
                    jogoUpdate.setPreco(novoPreco);
                    jogoUpdate.setGenero(novoGenero);

                    // Chama a atualização direta pelo offset
                    long novoOffset = arquivo.updateByOffset(offsetAtual, jogoUpdate);

                    if (novoOffset != -1) {
                        // Se o registro aumentou e foi movido para o fim do arquivo, a árvore tem de
                        // ser atualizada com o novo offset
                        if (novoOffset != offsetAtual) {
                            arvore.inserir(idUpdate, novoOffset);
                            arvore.salvar();
                        }
                        System.out.println("Jogo atualizado com sucesso!");
                    } else {
                        System.out.println("Erro ao atualizar o jogo.");
                    }
                    break;

                case 5:

                    // ID do jogo
                    System.out.print("Digite o ID do jogo: ");
                    int idDelete = scanf.nextInt();

                    // buscar a posiçao da arvore
                    long offsetDelete = arvore.buscar(idDelete);
                    if (offsetDelete != -1) {
                        boolean deletou = arquivo.Deletar(offsetDelete);
                        if (deletou) {
                            System.out.println("Jogo deletado com sucesso");
                            // exclusao preguiçosa
                            // O offset continua la mas o arquivo esta como lapide
                        } else {
                            System.out.println("Erro ao deletar.");
                        }
                    } else {
                        System.out.println("Jogo nao encontrado no indice.");
                    }
                    break;

                case 6:
                    System.out.println("Ordenação externa");
                    System.out.print("Digite o número de caminhos: ");
                    int caminhos = scanf.nextInt();

                    // quantidade maxima de registros que podem ficar na memoria
                    System.out.print("Digite o máximo de registros em memoria: ");
                    int tamanhoMemoria = scanf.nextInt();

                    // cria os blocos ja ordenados
                    arquivo.criarBlocos(tamanhoMemoria);

                    // faz o merge dos blocos
                    arquivo.intercalarBlocos(caminhos);

                    System.out.println("Ordenado");

                    break;
                case 7:
                    System.out.println("Busca Combinada Genero e Nome ");

                    System.out.print("Digite um gênero Ex: Action, Indie: ");
                    scanf.nextLine();
                    String buscaGenero = scanf.nextLine();

                    System.out.print("Digite uma palavra do nome do jogo Ex: War, Simulator: ");
                    String buscaNome = scanf.nextLine();

                    // Busca as listas de IDs nas duas estruturas
                    java.util.ArrayList<Integer> idsGenero = listaGeneros.buscar(buscaGenero);
                    java.util.ArrayList<Integer> idsNome = listaNomes.buscar(buscaNome);

                    if (idsGenero.isEmpty()) {
                        System.out.println("Nenhum jogo encontrado para o genero: " + buscaGenero);
                        break;
                    }
                    if (idsNome.isEmpty()) {
                        System.out.println("Nenhum jogo encontrado contendo a palavra: " + buscaNome);
                        break;
                    }

                    // Fazer a interseção (pegar apenas os IDs que estão nas DUAS listas)
                    java.util.ArrayList<Integer> idsEncontrados = new java.util.ArrayList<>();
                    for (int idGenero : idsGenero) {
                        if (idsNome.contains(idGenero)) {
                            idsEncontrados.add(idGenero);
                        }
                    }

                    // Mostrar os resultados
                    if (idsEncontrados.isEmpty()) {
                        System.out.println("Nenhum jogo atende aos dois criterios ao mesmo tempo.");
                    } else {
                        System.out.println("\nJogos encontrados (" + idsEncontrados.size() + "):");
                        for (int idEncontrado : idsEncontrados) {
                            // Busca instantânea na árvore para pegar os dados e imprimir
                            long offsetJogo = arvore.buscar(idEncontrado);
                            if (offsetJogo != -1) {
                                Jogo jogoEncontrado = arquivo.LerArvore(offsetJogo);
                                if (jogoEncontrado != null) {
                                    System.out.println("ID: " + jogoEncontrado.getId() + " | Nome: "
                                            + jogoEncontrado.getNome() + " | Gênero: " + jogoEncontrado.getGenero());
                                }
                            }
                        }
                    }
                    break;
                case 8:
                    System.out.println("\n Casamento de Padroes ");
                    System.out.print("Digite o padrao de texto a ser procurado nos nomes dos jogos: ");
                    scanf.nextLine();
                    String padraoBusca = scanf.nextLine();

                    // KMP
                    long inicioKmp = System.currentTimeMillis();
                    int ocorrenciasKmp = 0;
                    List<Jogo> todosJogosKmp = arquivo.lerTodos();
                    for (Jogo j : todosJogosKmp) {
                        List<Integer> posicoes = CasamentoPadroes.buscarKMP(j.getNome().toLowerCase(),
                                padraoBusca.toLowerCase());
                        if (!posicoes.isEmpty()) {
                            ocorrenciasKmp++;
                        }
                    }
                    long fimKmp = System.currentTimeMillis();

                    // Boyer-Moore
                    long inicioBm = System.currentTimeMillis();
                    int ocorrenciasBm = 0;
                    List<Jogo> todosJogosBm = arquivo.lerTodos();
                    for (Jogo j : todosJogosBm) {
                        List<Integer> posicoes = CasamentoPadroes.buscarMoore(j.getNome().toLowerCase(),
                                padraoBusca.toLowerCase());
                        if (!posicoes.isEmpty()) {
                            ocorrenciasBm++;
                            System.out.println("Encontrado no jogo [ID " + j.getId() + "]: " + j.getNome());
                        }
                    }
                    long fimBm = System.currentTimeMillis();

                    System.out.println("\nResultados ");
                    System.out.println("Jogos que contem o padrao: " + ocorrenciasBm);
                    System.out.println("Tempo KMP: " + (fimKmp - inicioKmp) + " ms");
                    System.out.println("Tempo Boyer-Moore: " + (fimBm - inicioBm) + " ms");
                    break;

                case 9:
                    System.out.println("\nCompactacao da Base de Dados ");
                    File dbOriginal = new File("jogos.db");
                    if (!dbOriginal.exists() || dbOriginal.length() == 0) {
                        System.out.println("Erro: Base de dados vazia ou inexistente");
                        break;
                    }

                    System.out.print("Informe a versao da compressao : ");
                    int versaoComp = scanf.nextInt();

                    String arqHuffman = "jogos Huffman Compressao" + versaoComp;
                    String arqLzw = "jogos LZW Compressao" + versaoComp;
                    long tamOrig = dbOriginal.length();

                    // Huffman
                    System.out.println("\nExecutando Huffman");
                    long t0H = System.currentTimeMillis();
                    Huffman.comprimir("jogos.db", arqHuffman);
                    long t1H = System.currentTimeMillis();
                    long tempoHuffman = t1H - t0H;
                    long tamH = new File(arqHuffman).length();
                    double ganhoH = ((double) (tamOrig - tamH) / tamOrig) * 100.0;

                    // LZW
                    System.out.println("Executando LZW");
                    long t0L = System.currentTimeMillis();
                    LZW.comprimir("jogos.db", arqLzw);
                    long t1L = System.currentTimeMillis();
                    long tempoLzw = t1L - t0L;
                    long tamL = new File(arqLzw).length();
                    double ganhoL = ((double) (tamOrig - tamL) / tamOrig) * 100.0;

                    System.out.println("\n= RESULTADO DA COMPRESSAO");
                    System.out.println("Tamanho Original: " + tamOrig + " bytes");
                    System.out.printf("\nHuffman: %d bytes | Reducao: %.2f%% | Tempo: %d ms\n", tamH, ganhoH,
                            tempoHuffman);
                    System.out.printf("LZW:     %d bytes | Reducao: %.2f%% | Tempo: %d ms\n", tamL, ganhoL, tempoLzw);

                    if (tamH < tamL) {
                        System.out.println("\nMelhor taxa de compressao: Huffman");
                    } else {
                        System.out.println("\nMelhor taxa de compressao: LZW");
                    }
                    break;

                case 10:
                    System.out.println("\n Descompressao da Base de Dados ");
                    System.out.println("1 - Huffman");
                    System.out.println("2 - LZW");
                    System.out.print("Escolha o algoritmo: ");
                    int algDesc = scanf.nextInt();

                    System.out.print("Informe a versao (X): ");
                    int versaoDesc = scanf.nextInt();

                    String arqCompactado = (algDesc == 1)
                            ? "jogos Huffman Compressao" + versaoDesc
                            : "jogos LZW Compressao" + versaoDesc;

                    File fComp = new File(arqCompactado);
                    if (!fComp.exists()) {
                        System.out.println("Erro: Arquivo '" + arqCompactado + "' nao encontrado.");
                        break;
                    }

                    arquivo.fechar();
                    String arqTemp = "jogos_descomprimido.db";
                    long t0Desc = System.currentTimeMillis();

                    if (algDesc == 1) {
                        Huffman.descomprimir(arqCompactado, arqTemp);
                    } else {
                        LZW.descomprimir(arqCompactado, arqTemp);
                    }
                    long t1Desc = System.currentTimeMillis();

                    File dbAtual = new File("jogos.db");
                    if (dbAtual.exists()) {
                        dbAtual.delete();
                    }
                    new File(arqTemp).renameTo(dbAtual);

                    arquivo = new Arquivo("jogos.db");

                    System.out.println("\nDescompressao concluida com sucesso!");
                    System.out.println("Tempo: " + (t1Desc - t0Desc) + " ms");
                    System.out.println("Base 'jogos.db' restaurada.");
                    break;

                case 0:
                    // fechar o manipulador
                    arquivo.fechar();
                    break;

                default:
                    System.out.println("erro");
            }
        }

        scanf.close();
    }

}