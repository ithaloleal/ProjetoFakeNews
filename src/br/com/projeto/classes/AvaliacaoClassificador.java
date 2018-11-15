package br.com.projeto.classes;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.stopwords.WordsFromFile;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Reorder;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Optional;

import static br.com.projeto.classes.Enum.ARFF;
import static br.com.projeto.classes.Enum.STOPWORDS;

/*
    Classe possui finalidade de avaliar os algoritimos
 */
public class AvaliacaoClassificador {

    public static void main(String[] args) {
        try {
            File file = new File(ARFF.toString());
            if (!file.exists()) {
                file = GeraArquivoArff.geraArquivo();
            }

            ConverterUtils.DataSource ds = null;
            ds = new ConverterUtils.DataSource(new FileInputStream(file));

            Instances instancias = ds.getDataSet();
            // Seta o atributo classe
            instancias.setClassIndex(instancias.numAttributes() - 1);

            NaiveBayes nb = new NaiveBayes();
            avaliador(nb, instancias, 4);
            avaliador(nb, instancias, 6);

            J48 j48 = new J48();
            avaliador(j48, instancias,9);
            avaliador(j48, instancias,10);

            JRip jRip = new JRip();
            avaliador(jRip, instancias, 2);
            avaliador(jRip, instancias, 4);

            IBk iBk = new IBk();
            avaliador(iBk, instancias,6);
            avaliador(iBk, instancias,8);

            MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();
            avaliador(multilayerPerceptron, instancias);
            avaliador(multilayerPerceptron, instancias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void avaliador(Classifier classifier, Instances instances) throws Exception {
        // Recebe o classificador com os atributos filtrados
        FilteredClassifier tmp = aplicaFiltros(classifier, instances);

        int vezes = 10; // numero de vezes que vai ser realizado o teste
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= vezes; i++) {
            int finalI = i;
            threads.add(
                    // Foi criada a Thread para poder executar mais de um teste ao mesmo tempo, reduzindo o tempo de espera
                    new Thread(() -> {
                        try {
                            System.out.println("====== Inicio - Seed: " + finalI + " - " + tmp.getClassifier().toString() + " ======");
                            Evaluation eval = new Evaluation(instances);
                            // A cada teste o Random seed soma + 1
                            eval.crossValidateModel(tmp, instances, 10, new Debug.Random(finalI));
                            System.out.println("====== " + tmp.getClassifier().toString() + " ======");
                            System.out.println("Seed " + finalI + ":" + String.valueOf(eval.pctCorrect()).replace('.', ','));
                            Optional<Thread> first = threads.stream().filter(e -> e.getState() == Thread.State.NEW).findFirst();
                            first.get().start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
        }
        // Inicia o teste, executa 3 testes por vez
        threads.get(0).start();
        threads.get(1).start();
        threads.get(2).start();
    }

    public static void avaliador(Classifier classifier, Instances instances, int seed) throws Exception {
        // Recebe o classificador com os atributos filtrados
        FilteredClassifier tmp = aplicaFiltros(classifier, instances);

        new Thread(() -> {
            try {
                System.out.println("====== Inicio - Seed: " + seed + " - " + tmp.getClassifier().toString() + " ======");
                Evaluation eval = new Evaluation(instances);
                // A cada teste o Random seed soma + 1
                eval.crossValidateModel(tmp, instances, 10, new Debug.Random(seed));
                System.out.println("====== " + tmp.getClassifier().toString() + " ======");
                System.out.println("Seed " + seed + ":" + String.valueOf(eval.pctCorrect()).replace('.', ','));
                double[][] confusionMatrix = eval.confusionMatrix();
                System.out.println(confusionMatrix[0][0] + " - " + confusionMatrix[0][1] + " - " + confusionMatrix[0][2]);
                System.out.println(confusionMatrix[1][0] + " - " + confusionMatrix[1][1] + " - " + confusionMatrix[1][2]);
                System.out.println(confusionMatrix[2][0] + " - " + confusionMatrix[2][1] + " - " + confusionMatrix[2][2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static FilteredClassifier aplicaFiltros(Classifier classifier, Instances instancias) throws Exception {
        // Converte a string em um veotr de palavras
        StringToWordVector stringToWordVector = new StringToWordVector();
        stringToWordVector.setInputFormat(instancias); // seta a base de dados (arquivo .ARFF)
        // Carrega a lista de stopwords
        WordsFromFile wordsFromFile = new WordsFromFile();
        wordsFromFile.setStopwords(new File(STOPWORDS.toString()));
        // Converte tudo para minúsculo
        stringToWordVector.setLowerCaseTokens(true);
        // Define o atributo a ser aplicado o filtro
        stringToWordVector.setAttributeIndices("first");
        // Inverte a ordem dos atributos
        Reorder reorder = new Reorder();
        reorder.setAttributeIndices("last-first");
        reorder.setInputFormat(instancias);
        // Aplica os filtros
        MultiFilter mf = new MultiFilter();
        Filter[] filters = new Filter[2];
        filters[0] = stringToWordVector;
        filters[1] = reorder;
        // Seta os filtros
        mf.setFilters(filters);
        // Efetua a classificação
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(mf);
        fc.setClassifier(classifier);

        return fc;
    }
}
