package me.kagglu.kagglupunishment.utility;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.kagglu.kagglupunishment.KaggluPunishment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {
    String databaseAddress; // address
    int databasePort;
    String databaseUser;
    String databasePass;
    String databaseName;
    Connection connection;

    public Database(String address, int port, String user, String pass, String name) {
        databaseAddress = address;
        databasePort = port;
        databaseUser = user;
        databasePass = pass;
        databaseName = name;
        connect();
    }

    private void connect() {
        MysqlDataSource source = new MysqlConnectionPoolDataSource();
        source.setServerName(databaseAddress);
        source.setPort(databasePort);
        source.setUser(databaseUser);
        source.setPassword(databasePass);
        source.setDatabaseName(databaseName);

        try {
            connection = source.getConnection();
            if (connection.isValid(1)) {
                KaggluPunishment.getInstance().getProxy().getLogger().info("KAGGLUPUNISHMENT: DATABASE CONNECTION ESTABLISHED");
            }
            PreparedStatement createUuidLookupStatement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS Uuid_lookup (\n" +
                            "\tusername VARCHAR(16),\n" +
                            "\tuid CHAR(36)\n" +
                            ");");
            createUuidLookupStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            KaggluPunishment.getInstance().getProxy().getLogger().info(e.getMessage());
        }
    }

    public void handleUUID(String uuid, String username) {
        try {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT username FROM Uuid_lookup WHERE uid=?;");
            selectStatement.setString(1, uuid);
            ResultSet selectResultSet = selectStatement.executeQuery();
            if (!selectResultSet.next()) {
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Uuid_lookup VALUES(?, ?);");
                insertStatement.setString(1, username);
                insertStatement.setString(2, uuid);
                insertStatement.execute();
            } else {
                KaggluPunishment.getInstance().getProxy().getLogger().info("existed already: " + selectResultSet.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
         connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
