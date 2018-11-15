package br.com.projeto.classes;

public enum Enum {
    ARFF("src/br/com/projeto/arrfs/fakenews.arff"),
    CAMINHO_FATO("src/br/com/projeto/utils/fato"),
    CAMINHO_FAKE("src/br/com/projeto/utils/fake"),
    STOPWORDS("src/br/com/projeto/utils/stopwords.txt"),
    CAMINHO_MODELS("src/br/com/projeto/models");

    private String caminho;

    Enum(String caminho) {
        this.caminho = caminho;
    }

    @Override
    public String toString() {
        return caminho;
    }
}
