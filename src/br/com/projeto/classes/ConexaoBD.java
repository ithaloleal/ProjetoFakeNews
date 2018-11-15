package br.com.projeto.classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    private Connection con;

    public ConexaoBD() {
        try {
            String driver = "org.postgresql.Driver";
            String usuario = ""; // Usuario BD
            String senha = ""; // Senha BD
            String url = "jdbc:postgresql://localhost:5432/nome_BD";
            Class.forName(driver);
            con = DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getCon() {
        return con;
    }
}
