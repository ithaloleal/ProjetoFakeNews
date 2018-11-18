package br.com.projeto.classes;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;

import static br.com.projeto.classes.Enum.ARFF;

public class Classificador {
    private static ConexaoBD conexaoBD = new ConexaoBD();

    public static void main(String args[]) {
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

            leituraParaclassificarRegistroBD(instancias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void leituraParaclassificarRegistroBD(Instances instancias) throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream ModeloNB = new ObjectInputStream(new FileInputStream(Enum.CAMINHO_MODELS + "/nb.model"));
        FilteredClassifier nb = (FilteredClassifier) ModeloNB.readObject();
        ModeloNB.close();

        ObjectInputStream ModeloJ48 = new ObjectInputStream(new FileInputStream(Enum.CAMINHO_MODELS + "/j48.model"));
        FilteredClassifier j48 = (FilteredClassifier) ModeloJ48.readObject();
        ModeloJ48.close();

        ObjectInputStream ModeloJrip = new ObjectInputStream(new FileInputStream(Enum.CAMINHO_MODELS + "/jrip.model"));
        FilteredClassifier jrip = (FilteredClassifier) ModeloJrip.readObject();
        ModeloJrip.close();

        ObjectInputStream ModeloIbk = new ObjectInputStream(new FileInputStream(Enum.CAMINHO_MODELS + "/ibk.model"));
        FilteredClassifier ibk = (FilteredClassifier) ModeloIbk.readObject();
        ModeloIbk.close();

        PreparedStatement cmd = conexaoBD.getCon().prepareStatement("SELECT * FROM twitter a left outer join naivebayes b on b.twitter_id" +
                " = a.id where class is null limit 100000");
        ResultSet result = cmd.executeQuery();
        while (result.next()) {
            try {
                Instance novo = new DenseInstance(instancias.numAttributes());
                novo.setDataset(instancias);
                novo.setValue(0, Normalizer.normalize(result.getString("message")
                        .replaceAll("\n", "").replaceAll("'", ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));

                double resultadoNb[] = nb.distributionForInstance(novo);
                double resultadoJ48[] = j48.distributionForInstance(novo);
                double resultadoJrip[] = jrip.distributionForInstance(novo);
                double resultadoIbk[] = ibk.distributionForInstance(novo);

                insereClassificacao(result.getLong("id"), "naivebayes", resultadoNb);
                insereClassificacao(result.getLong("id"), "j48", resultadoJ48);
                insereClassificacao(result.getLong("id"), "jrip", resultadoJrip);
                insereClassificacao(result.getLong("id"), "ibk", resultadoIbk);

                System.out.println(Normalizer.normalize(result.getString("message").replaceAll("\n", " ")
                        .replaceAll("'", ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void insereClassificacao(Long id, String tabela, double[] resultado) throws SQLException {
        PreparedStatement cmd1 = conexaoBD.getCon().prepareStatement("INSERT INTO " + tabela + " (twitter_id, opiniao, fato, fake) VALUES ( ?,?,?,?)");
        cmd1.setLong(1, id);
        cmd1.setDouble(2, resultado[0]);
        cmd1.setDouble(3, resultado[1]);
        cmd1.setDouble(4, resultado[2]);
        cmd1.executeUpdate();
    }
}
