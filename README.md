## Descrição do Projeto

Este projeto implementa um sistema de gerenciamento de banco de dados baseado em arquivos binários (jogos.db). Ele permite a realização de operações CRUD (Create, Read, Update, Delete), Ordenação Externa (Intercalação Balanceada) para limpeza de registros excluídos, indexação avançada (Árvore B+ e Listas Invertidas) e agora conta com algoritmos de compressão de dados e casamento de padrões.

Para tornar as operações de busca textual eficientes, o sistema implementa algoritmos que evitam a força bruta. Além disso, foram integrados dois métodos de compressão sem perdas (Huffman e LZW) para reduzir o espaço ocupado pelo arquivo em disco com versionamento e restauração segura. A base de dados utilizada contém informações sobre jogos da plataforma Steam.

## Novidades desta versão (TP3)

Casamento de Padrões: Implementação dos algoritmos KMP (Knuth-Morris-Pratt) e Boyer-Moore para buscar palavras ou termos nos nomes dos jogos, medindo e comparando o tempo de execução de cada um em milissegundos.
Compressão de Dados (Huffman e LZW): O sistema agora permite compactar todo o arquivo jogos.db utilizando os dois algoritmos simultaneamente, gerando arquivos versionados (jogos Huffman CompressaoX e jogos LZW CompressaoX) e exibindo a taxa de redução e o tempo gasto.
Descompressão e Restauração: Possibilidade de escolher uma versão compactada para restaurar a base de dados original, substituindo o arquivo ativo e garantindo a integridade dos registros.

## Como rodar

1. Para rodar esse programa é necessário compilar os arquivos e executar o Main.java, escolhendo a opção que gostaria de testar no menu.
2. Execute o Main e escolha a opção 1 (Carregar banco de dados). Ele vai ler o steam.csv e criar automaticamente o jogos.db e os arquivos de índice.
3. Para testar a funcionalidade de casamento de padrões, escolha a opção 8 (Casamento de Padrões), digite um termo (ex: Counter-Strike ou War) e veja a comparação de tempo entre KMP e Boyer-Moore.
4. Para compactar a base de dados, escolha a opção 9 (Compactar base de dados) e digite o número da versão (ex: 1). O sistema gerará os arquivos compactados por Huffman e LZW e exibirá o comparativo de desempenho.
 5. Para testar a restauração, escolha a opção 10 (Descompactar versão da base de dados), selecione o algoritmo, informe a versão e confirme a substituição da base.
6. Com a base restaurada, você pode usar a opção 3 para ler um jogo por ID e confirmar que os dados permanecem íntegros.

## Vídeo
https://drive.google.com/file/d/1bRLZv3NzKDtf-poBSGC-BDVG_tmz8PM3/view?usp=sharing
