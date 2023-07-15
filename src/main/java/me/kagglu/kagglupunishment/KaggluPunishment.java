package me.kagglu.kagglupunishment;

import me.kagglu.kagglupunishment.utility.Database;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class KaggluPunishment extends Plugin {
    private static KaggluPunishment instance;
    public static Database database;
    private static Configuration configuration;

    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getLogger().info("Created plugin data folder: " + getDataFolder().mkdir());
        }
        try {
            File config = new File(getDataFolder(), "config.yml");
            if (!config.exists()) {
                FileOutputStream outputStream = new FileOutputStream(config);
                InputStream in = getResourceAsStream("config.yml");
                in.transferTo(outputStream);
            }
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (configuration.get("database_name") == null) {
            getLogger().info("KAGGLUPUNISHMENT-BUNGEE: ENTER DATABASE INFO IN CONFIG AND RELOAD PLUGIN");
        } else {
            database = new Database(configuration.getString("database_host"), configuration.getInt("database_port"), configuration.getString("database_user"), configuration.getString("database_pass"), configuration.getString("database_name"));
        }


        getProxy().getPluginManager().registerListener(this, new Events());
        getProxy().getPluginManager().registerCommand(this, new Warns());
        getProxy().getPluginManager().registerCommand(this, new Warn());
        getProxy().getPluginManager().registerCommand(this, new Delwarn());
        getProxy().getPluginManager().registerCommand(this, new kpReload());

        getLogger().info("LOADED: KAGGLUPUNISHMENT-BUNGEE");
    }

    @Override
    public void onDisable() {
        getLogger().info("DISABLED: KAGGLUPUNISHMENT-BUNGEE");
        database.close();
    }

    public static KaggluPunishment getInstance() {
        return instance;
    }

    public void reload() {
        if (configuration.get("database_name") == null) {
            getLogger().info("KAGGLUPUNISHMENT-BUNGEE: ENTER DATABASE INFO IN CONFIG AND RELOAD PLUGIN");
        } else {
            database = new Database(configuration.getString("database_host"), configuration.getInt("database_port"), configuration.getString("database_user"), configuration.getString("database_pass"), configuration.getString("database_name"));
        }
    }
}
