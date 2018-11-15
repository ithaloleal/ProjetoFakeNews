package br.com.projeto.classes;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;

import static br.com.projeto.classes.Enum.*;

public class GeraArquivoArff {
    private static ConexaoBD conexaoBD = new ConexaoBD();

    public static File geraArquivo() {
        File arquivo = new File(ARFF.toString());
        try {
            Connection con = conexaoBD.getCon();

            PreparedStatement cmd1 = con.prepareStatement("select distinct on(message) message, class from twitter where class is not null and tp_class ");
            ResultSet result = cmd1.executeQuery();

            // Cabeçalho do arquivo Weka
            String exportacao = "@relation fakenewsTCC\n\n";
            exportacao += "@attribute message string\n";
            exportacao += "@attribute class {opiniao, fato, fake}\n\n";
            exportacao += "@data\n";
            System.out.println(exportacao);
            while (result.next()) {
                exportacao += "'" + Normalizer.normalize(result.getString("message").replaceAll("\n", " ").
                        replaceAll("'", ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") + "'," +
                        result.getString("class") + "\n";
                System.out.println(exportacao);
            }

            exportacao += leituraTxt(true);
            exportacao += leituraTxt(false);

            FileOutputStream f = new FileOutputStream(arquivo);
            f.write(exportacao.getBytes());
            f.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

        return arquivo;
    }

    public static String leituraTxt(boolean fake) throws IOException {
        String exportacao = "";
        File file = new File(fake ? CAMINHO_FAKE.toString() : CAMINHO_FATO.toString());
//        int limit = 0;
        for (File txt : file.listFiles()) {
            FileReader fileWriter = new FileReader(txt);
            BufferedReader lerArq = new BufferedReader(fileWriter);
            String linhaAtual;
            String texto = "";
            while ((linhaAtual = lerArq.readLine()) != null) {
                texto += Normalizer.normalize(linhaAtual.replaceAll("\n", " ").replaceAll("\t", " ").
                        replaceAll("'", ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            }
            if (!texto.trim().isEmpty()) {
                exportacao += "'" + texto + "', " + (fake ? "fake" : "fato") + "\n";
            }
            System.out.println(exportacao);
            fileWriter.close();
//            limit++;
//            if (limit > 100) {
//                break;
//            }
        }
        return exportacao;
    }
}
