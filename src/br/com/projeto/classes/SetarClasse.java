package br.com.projeto.classes;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SetarClasse {
    private static ConexaoBD conexaoBD = new ConexaoBD();

    public static void main(String[] args) {
        try {
            List<Long> ids = new ArrayList<>();
            List<String> classes = new ArrayList<>();

            PreparedStatement cmd1 = conexaoBD.getCon().prepareStatement("select * from twitter where class is null and is_retweet = false ORDER by retweet_count DESC ");
            ResultSet result = cmd1.executeQuery();
            int total = 1;
            while (result.next()) {
                Object[] options = {"Fato",
                        "Fake",
                        "Opinião", "Nenhum", "Excluir"};
                System.out.println(result.getString("message"));
                int n = JOptionPane.showOptionDialog(null, result.getString("message"), "info", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                System.out.println(String.valueOf(n));
                PreparedStatement cmd2 = conexaoBD.getCon().prepareStatement("UPDATE twitter SET class = ?, tp_class=? WHEre id_tweet=?");

                switch (n) {
                    case 0:
                        cmd2.setString(1, "fato");
                        break;
                    case 1:
                        cmd2.setString(1, "fake");
                        break;
                    case 2:
                        cmd2.setString(1, "opiniao");
                        break;
                    case 3:
                        cmd2.setString(1, "opiniao");
                        continue;
                    case 4:
                        System.out.println("Deletando gravação...");
                        PreparedStatement cmd3 = conexaoBD.getCon().prepareStatement("DELETE FROM twitter  WHEre id_tweet=? and class is null");
                        cmd3.setLong(1, result.getLong("id_tweet"));
                        cmd3.executeUpdate();
                        continue;
                }
                System.out.println("Iniciando gravação...");
                cmd2.setBoolean(2, true);
                cmd2.setLong(3, result.getLong("id_tweet"));
                cmd2.executeUpdate();
                System.out.println("Total......" + total++);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
