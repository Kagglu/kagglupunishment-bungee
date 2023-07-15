package me.kagglu.kagglupunishment.utility;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import me.kagglu.kagglupunishment.KaggluPunishment;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ConnectedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.checkerframework.checker.units.qual.A;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

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
            if (!selectResultSet.next()) { // insert new player into database
                PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Uuid_lookup VALUES(?, ?);");
                insertStatement.setString(1, username);
                insertStatement.setString(2, uuid);
                insertStatement.execute();
            } else { // update username of player who changed name
                if (!selectResultSet.getString(1).equalsIgnoreCase(username)) {
                    PreparedStatement updateStatement = connection.prepareStatement("UPDATE Uuid_lookup SET username=? WHERE uid=?;");
                    updateStatement.setString(1, username);
                    updateStatement.setString(2, uuid);
                    updateStatement.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWarning(String reason, CommandSender warner, String warned) {
        String warned_uuid = "";

        try {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT uid FROM Uuid_lookup WHERE username=?;");
            selectStatement.setString(1, warned);
            ResultSet selectResultSet = selectStatement.executeQuery();
            if (!selectResultSet.next()) {
                warner.sendMessage(new TextComponent("§4§lPlayer not found!"));
            } else {
                warned_uuid = selectResultSet.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        try {
            PreparedStatement warnStatement = connection.prepareStatement("INSERT INTO Warns VALUES(?, ?, ?, ?);");
            warnStatement.setString(1, dtf.format(now));
            warnStatement.setString(2, warned_uuid);
            warnStatement.setString(3, warner.getName());
            warnStatement.setString(4, reason);
            warnStatement.execute();

            for (ProxiedPlayer proxy : KaggluPunishment.getInstance().getProxy().getPlayers()) {
                if (proxy.hasPermission("kagglupunishment.viewwarns")) {
                    proxy.sendMessage(new TextComponent("§c" + warned + " has been warned for: " + reason));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWarnings(CommandSender sender, String warnedUsername) {
        String warned_uuid = "";

        try {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT uid FROM Uuid_lookup WHERE username=?;");
            selectStatement.setString(1, warnedUsername);
            ResultSet selectResultSet = selectStatement.executeQuery();
            if (!selectResultSet.next()) {
                sender.sendMessage(new TextComponent("§4§lPlayer not found!"));
            } else {
                warned_uuid = selectResultSet.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement warnsStatement = connection.prepareStatement("SELECT * FROM Warns WHERE uid=?;");
            warnsStatement.setString(1, warned_uuid);
            ResultSet warnsResultSet = warnsStatement.executeQuery();
            int count = 1;
            ArrayList<TextComponent> warns = new ArrayList<>();
            while (warnsResultSet.next()) {
                String dateString = warnsResultSet.getString(1);
                LocalDate date = LocalDate.of(Integer.parseInt(dateString.substring(0, 4)), Integer.parseInt(dateString.substring(5, 7)), Integer.parseInt(dateString.substring(8, 10)));
                String season;
                if (date.compareTo(LocalDate.of(2023, 6, 26)) < 0) {
                    season = "§l§3S4§r§c";
                } else {
                    season = "§l§2S5§r§c";
                }
                warns.add(new TextComponent("§c" + count + ": " + warnsResultSet.getString(1) + " (" + season + ") by " +  warnsResultSet.getString(3) + ": " + warnsResultSet.getString(4)));
                count++;
            }
            if (warns.size() == 0) {
                sender.sendMessage(new TextComponent("§2§l" + warnedUsername + " has no warnings!"));
            } else if (warns.size() == 1) {
                sender.sendMessage(new TextComponent("§4§l" + warnedUsername + " has 1 warning:"));
            } else {
                sender.sendMessage(new TextComponent("§4§l" + warnedUsername + " has " + warns.size() + " warnings:"));
            }
            for (TextComponent textComponent : warns) {
                sender.sendMessage(textComponent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delWarn(int number, CommandSender sender, String warnedUsername) {
        String warned_uuid = "";

        try {
            PreparedStatement selectStatement = connection.prepareStatement("SELECT uid FROM Uuid_lookup WHERE username=?;");
            selectStatement.setString(1, warnedUsername);
            ResultSet selectResultSet = selectStatement.executeQuery();
            if (!selectResultSet.next()) {
                sender.sendMessage(new TextComponent("§4§lPlayer not found!"));
            } else {
                warned_uuid = selectResultSet.getString(1);
            }

            PreparedStatement warnsStatement = connection.prepareStatement("SELECT * FROM Warns WHERE uid=?;");
            warnsStatement.setString(1, warned_uuid);
            ResultSet warnsResultSet = warnsStatement.executeQuery();
            for (int i = 0; i < number; i++) {
                if (!warnsResultSet.next()) {
                    sender.sendMessage(new TextComponent("§4§lWarn " + number + " not found!"));
                    return;
                }
            }
            String date = warnsResultSet.getString(1);

            PreparedStatement delWarnStatement = connection.prepareStatement("DELETE FROM Warns WHERE timestamp=?;");
            delWarnStatement.setString(1, date);
            delWarnStatement.execute();

            sender.sendMessage(new TextComponent("§2§lWarn " + number + " of " + warnedUsername + " deleted!"));
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
