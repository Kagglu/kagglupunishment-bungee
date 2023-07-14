package me.kagglu.kagglupunishment;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class Events implements Listener {
    @EventHandler
    public void onPostLogin(PostLoginEvent ple) {
        KaggluPunishment.getInstance().getProxy().getLogger().info(ple.getPlayer().getUniqueId().toString());
        KaggluPunishment.getInstance().getProxy().getScheduler().runAsync(KaggluPunishment.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        KaggluPunishment.database.handleUUID(ple.getPlayer().getUniqueId().toString(), ple.getPlayer().getDisplayName());
                    }
                });
    }
}
