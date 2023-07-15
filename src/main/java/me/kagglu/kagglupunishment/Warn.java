package me.kagglu.kagglupunishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Warn extends Command {
    public Warn() {
        super("Warn");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kagglupunishment.managewarns")) {
            sender.sendMessage(new TextComponent("§4§lYou don't have permission to do that."));
            return;
        }
        if (args.length < 1) {
            sender.sendMessage(new TextComponent("§4§lInvalid syntax! Use: /warn (username) [reason]"));
        }
        String warned = args[0];
        String reason;
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            reason = sb.toString();
        } else {
            reason = "";
        }
        KaggluPunishment.getInstance().getProxy().getScheduler().runAsync(KaggluPunishment.getInstance(), new Runnable() {
            @Override
            public void run() {
                KaggluPunishment.database.sendWarning(reason, sender, warned);
            }
        });
    }
}
