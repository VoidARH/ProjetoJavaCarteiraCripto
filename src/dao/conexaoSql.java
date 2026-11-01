/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author void
 */

/** Abre conexoes JDBC com o PostgreSQL. Ajuste URL, usuário e senha. */
public class conexaoSql {
    private static final String URL  = "jdbc:postgresql://localhost:5432/exchange";
    private static final String USER = "postgres";
    private static final String PASS = "SUA_SENHA_AQUI";

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
