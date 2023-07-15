package me.kagglu.kagglupunishment;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Delwarn extends Command {
    public Delwarn() {
        super("Delwarn");
    }

    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("kagglupunishment.managewarns")) {
            sender.sendMessage(new TextComponent("§4§lYou don't have permission to do that."));
            return;
        }
        if (args.length < 2 || Integer.parseInt(args[1]) < 1) {
            sender.sendMessage(new TextComponent("§4§lInvalid syntax! Use: /delwarn (username) (warn number)"));
        }
        String warned= args[0];
        int number = Integer.parseInt(args[1]);
        KaggluPunishment.getInstance().getProxy().getScheduler().runAsync(KaggluPunishment.getInstance(), new Runnable() {
            @Override
            public void run() {
                KaggluPunishment.database.delWarn(number, sender, warned);
            }
        });
    }
}
