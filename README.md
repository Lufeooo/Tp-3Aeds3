## Descrição do Projeto
Este projeto implementa um sistema de gerenciamento de banco de dados baseado em arquivos binários (`jogos.db`). Ele permite a realização de operações CRUD (Create, Read, Update, Delete), Ordenação Externa (Intercalação Balanceada) para limpeza de registros excluídos, e agora com com indexação avançada. 

Para tornar as operações instantâneas (O(1)), o sistema utiliza uma Árvore B+ como índice primário (buscando direto pelo offset). Além disso, foram implementadas Listas Invertidas para permitir buscas combinadas rápidas baseadas no nome e no gênero do jogo. A base de dados utilizada contém informações sobre jogos da plataforma Steam.

## Novidades desta versão (TP2)
* Árvore B+ As buscas, exclusões (lápide) e atualizações agora vão direto na posição exata do arquivo usando o offset salvo na árvore (`indiceArvore.db`), sem precisar ler o arquivo sequencialmente.
* Listas Invertidas: Criamos dicionários que separam as palavras dos nomes dos jogos (`listaNomes.db`) e os gêneros (`listaGeneros.db`). Isso permite cruzar dados e encontrar jogos específicos muito mais rápido.

## Como rodar
Para rodar esse programa é necessário compilar os arquivos e executar o `Main.java`, escolhendo a opção que gostaria de testar no menu. 

1. Execute o main e escolha a **opção 1** (Carregar banco de dados). Ele vai ler o `steam.csv` e criar automaticamente o `jogos.db` e os arquivos de índice.
2. Com o banco carregado, você pode escolher a **opção 3** para buscar um jogo instantaneamente pelo ID.
3. Para testar a funcionalidade nova de listas invertidas, escolha a **opção 7** (Busca Combinada) e digite um gênero (ex: *Action*) e uma palavra do título (ex: *War*).

## Vídeo
https://drive.google.com/drive/folders/1HTXGPkVSoFkCHQ8BAwTenz3ihdq67cHc?usp=sharing
