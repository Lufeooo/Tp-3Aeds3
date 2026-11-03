package tp2;

import java.io.File;
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
                            "Tp-2Aeds3/steam.csv",
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
                            String dataFormatada = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date(jogo.getDataLancamento()));
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
                        // Se o registro aumentou e foi movido para o fim do arquivo, a árvore tem de ser atualizada com o novo offset
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
                            // O offset continua la  mas o arquivo esta como lapide
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
                                    System.out.println("ID: " + jogoEncontrado.getId() + " | Nome: " + jogoEncontrado.getNome() + " | Gênero: " + jogoEncontrado.getGenero());
                                }
                            }
                        }
                    }
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