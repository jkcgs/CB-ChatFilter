package com.makzk.cb.chatfilter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
	private final String fileName;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration fileConfiguration;
    
    public Configuration(JavaPlugin plugin, String fileName) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not instantiated");
        }
        if (plugin.getDataFolder() == null) {
            throw new IllegalStateException();
        }

        this.plugin = plugin;
        this.fileName = fileName;
        configFile = new File(this.plugin.getDataFolder(), this.fileName);
    }

    public void reloadConfig() {
    	fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            fileConfiguration.setDefaults(defConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }

        return fileConfiguration;
    }

    public void saveConfig() {
        if ((fileConfiguration != null) && (configFile != null)) {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }
    
    // Shorthands for primitives
    public String string(String path){
        return (getConfig().isString(path)) ? 
            this.getConfig().getString(path) : path;
    }
    
    public boolean bool(String path){
        return getConfig().getBoolean(path);
    }
    
    public List<String> list(String path){
        return getConfig().getStringList(path);
    }
    
    public int intg(String path) {
    	return getConfig().getInt(path);
    }
}
