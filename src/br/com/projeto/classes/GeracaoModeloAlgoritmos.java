package br.com.projeto.classes;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.*;

import static br.com.projeto.classes.Enum.ARFF;
import static br.com.projeto.classes.Enum.CAMINHO_MODELS;

public class GeracaoModeloAlgoritmos {

    public static void main(String[] args) throws Exception {

        File file = new File(ARFF.toString());
        if (!file.exists()) {
            file = GeraArquivoArff.geraArquivo();
        }
        ConverterUtils.DataSource ds = new ConverterUtils.DataSource(new FileInputStream(file));
        Instances instancias = ds.getDataSet();
        // Seta o atributo classe
        instancias.setClassIndex(instancias.numAttributes() - 1);

        System.out.println("########## Gerando Modelo - NAIVE BAYES ##########");
        NaiveBayes nb = new NaiveBayes();
        classificador(nb, instancias, "nb");

        System.out.println("########## Gerando Modelo - J48 ##########");
        J48 j48 = new J48();
        classificador(j48, instancias, "j48");

        System.out.println("########## Gerando Modelo - JRIP ##########");
        JRip jRip = new JRip();
        classificador(jRip, instancias, "jrip");

        System.out.println("########## Gerando Modelo - IBK ##########");
        IBk iBk = new IBk();
        classificador(iBk, instancias, "ibk");

        System.out.println("########## Gerando Modelo - MULTI LAYER PERCEPTRON ##########");
        MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();
        classificador(multilayerPerceptron, instancias, "mlp");

    }

    public static void classificador(Classifier classifier, Instances instancias, String nome) throws Exception {
        FilteredClassifier fc = AvaliacaoClassificador.aplicaFiltros(classifier, instancias);
        fc.buildClassifier(instancias);

        geraModelo(fc, nome);
    }

    public static void geraModelo(Classifier classifier, String nome) throws IOException {
        ObjectOutputStream classificador = new ObjectOutputStream(new FileOutputStream(CAMINHO_MODELS + "/" + nome + ".model"));
        classificador.writeObject(classifier);
        classificador.flush();
        classificador.close();
    }

}
