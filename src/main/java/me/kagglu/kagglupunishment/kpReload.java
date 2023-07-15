package me.kagglu.kagglupunishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class kpReload extends Command {
    public kpReload() {
        super("kagglupunishmentReload");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kagglupunishment.kpreload")) {
            sender.sendMessage(new TextComponent("§4§lYou don't have permission to do that."));
            return;
        }
        KaggluPunishment.getInstance().reload();
    }
}
