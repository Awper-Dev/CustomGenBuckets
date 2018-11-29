package me.awperdev.customgenbuckets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.awperdev.customgenbuckets.features.Event;
import me.awperdev.customgenbuckets.features.GenBucketItemStack;
import me.awperdev.customgenbuckets.features.commands.GiveCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

public class CustomGenbuckets extends JavaPlugin {

    private static Gson gson;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private HashMap<String, Integer> prices;

    public static Gson getGson() {
        return gson;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public HashMap<String, Integer> getPrices() {
        return this.prices;
    }

    @Override
    public void onEnable() {
        GsonBuilder gb = new GsonBuilder();
        gson = gb.setPrettyPrinting().create();
        try {
            createFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GenBucketItemStack(this);
        Bukkit.getLogger().info("GenBuckets enabled!");
        Bukkit.getServer().getPluginManager().registerEvents(new Event(this), this);
        getCommand("gengive").setExecutor(new GiveCommand());
        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
        }

        setupChat();
        setupPermissions();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private void createFiles() throws IOException {
        File file;
        if (!(this.getDataFolder().exists())) {
            this.getDataFolder().mkdir();
        }
        file = new File(this.getDataFolder(), "prices.json");
        if (!(file.exists())) {
            file.createNewFile();
            createDefaults(file);
        } else {
            loadPrices(file);
        }
    }

    private void createDefaults(File file) throws IOException {
        prices = new HashMap<>();
        prices.put("horizontal-sand", 100);
        prices.put("horizontal-obsidian", 100);
        prices.put("horizontal-cobblestone", 100);
        prices.put("vertical-sand", 500);
        prices.put("vertical-obsidian", 500);
        prices.put("vertical-cobblestone", 500);


        String json = gson.toJson(prices);
        FileUtils.write(file, json, "UTF-8", true);
    }

    private void loadPrices(File file) throws IOException {
        String json = FileUtils.readFileToString(file);
        Type mapType = new TypeToken<HashMap<String, Integer>>() {
        }.getType();

        prices = gson.fromJson(json, mapType);
        Bukkit.getLogger().info("prices loaded!");
    }


}
