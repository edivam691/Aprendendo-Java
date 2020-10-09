package br.com.infox.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ModuloConexao {

    //Metodo responsável  por estabelecer conexão com banco de dados
    public static Connection conector() {
        Connection conexao = null;
        //a linha abaixo "chama" o driver
        String driver = "com.mysql.cj.jdbc.Driver";
        //armazena informacões referentes ao banco
        String url = "jdbc:mysql://localhost:3306/dbinfox?serverTimezone=UTC";
        String user = "root";
        String password = "admin";
        //estabelecendo conexão com o bancojdbc:mysql://localhost:3306/dbinfox?serverTimezone=UTC
        try {
            Class.forName(driver);

            conexao = DriverManager.getConnection(url, user, password);
            return conexao;

        } catch (ClassNotFoundException | SQLException e) {
            //a  linha abaixo ewsclarece o erro
            //System.err.print(e);
            return null;
        }
    }
}
