/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package base;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Tsiory
 */
public class Connect {

    static Connection connect;
    public String dbname;

    public Connection getConnect() throws Exception {
        try {
            /*Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");*/
            String url = "jdbc:postgresql://127.0.0.1:5432/" + this.dbname;
            String user = "postgres";
            String mdp = "mdpprom13";
            connect = DriverManager.getConnection(url, user, mdp);
            connect.setAutoCommit(false);
            return connect;
        } catch (SQLException e) {
        }
        return null;
    }

    public void closeConnection(Connection connexion) {
        try {
            connexion.close();
        } catch (SQLException e) {
        }
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }
}
