package me.kagglu.kagglupunishment;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;

public class Warns extends Command {
    public Warns() {
        super("Warns");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) { //check own warns, no permissions needed
            KaggluPunishment.getInstance().getProxy().getScheduler().runAsync(KaggluPunishment.getInstance(), new Runnable() {
                @Override
                public void run() {
                    KaggluPunishment.database.getWarnings(sender, sender.getName());
                }
            });
        } else { //check other players' warns, kagglupunishment.viewwarns needed
            if (!sender.hasPermission("kagglupunishment.viewwarns")) {
                sender.sendMessage(new TextComponent("§4§lYou don't have permission to see other players' warns."));
                return;
            }
            KaggluPunishment.getInstance().getProxy().getScheduler().runAsync(KaggluPunishment.getInstance(), new Runnable() {
                @Override
                public void run() {
                    KaggluPunishment.database.getWarnings(sender, args[0]);
                }
            });
        }
    }
}
