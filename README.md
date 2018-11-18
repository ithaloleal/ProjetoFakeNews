# ProjetoFakeNews
Foi desenvolvido um sistema protótipo, utilizando a linguagem de programação JAVA, capaz de classificar uma mensagem ou texto, utilizando aprendizagem de máquina, mineração de dados e textos.

# Classes
<b>GeracaoModeloAlgoritmos</b> - Utilizada para geração do modelo dos algoritmos<br>
<b>AvaliacaoClassificador</b> - Utilizada para avaliação do modelo<br>
<b>Classificador</b> - Utilizada para classificação de novo registros<br>
<b>ColetaAPI</b> - Utilizada para coleta de tweets<br>
<b>ConexaoBD</b> - Efetua conexão com BD para gravar tweets capturados<br>
<b>Enum</b> - Caminho dos arquivos utilizados<br>
<b>GeraArquivoArff</b> - Gera arquivo .ARFF utilizado pelo WEKA<br>
<b>SetarClasse</b> - Utilizada para setar classe manual de um tweet capturado, para posteriormente efetuar treinamento dos algoritimos<br>

# Algoritmos utilizados
NaiveBayes<br>
J48<br>
JRip<br>
IBk<br>
MultilayerPerceptron<br>

# Bibliotecas utilizadas
Twitter4j - http://twitter4j.org/en/index.html<br>
WEKA - https://www.cs.waikato.ac.nz/ml/weka/<br>

# Corpus extra utilizado (precisa fazer download)
https://github.com/roneysco/Fake.br-Corpus
