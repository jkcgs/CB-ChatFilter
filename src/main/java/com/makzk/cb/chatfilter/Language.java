package com.makzk.cb.chatfilter;

import java.io.File;
import org.bukkit.ChatColor;

public class Language {
	private ChatFilter p = ChatFilter.getInstance();
	private Configuration lang = null;
	
	public void load() {
		String confLang = p.config.string("lang");
		
    	// Check for language file
    	String langPath = "lang_" + confLang + ".yml";
    	File langFile = new File(langPath);
    	if(!langFile.isFile()) {
    		p.getLogger().warning("File for language '" + confLang + "' was not found, loading default one");
    		langPath = "lang.yml";
    	} else {
    		p.getLogger().info("Language: " + confLang);
    	}
    	
    	lang = new Configuration(p, langPath);
    	lang.reloadConfig();
	}
	
	/**
	 * Gets a string named by path
	 * @param path The path from the language file
	 * @return The translated path, from the language file
	 */
	public String str(String path) {
		return lang.string(path);
	}
	
	/**
	 * Checks if language file was loaded
	 * @return A boolean for each case
	 */
	public boolean isLoaded() {
		return lang != null;
	}
	
    public String colorize(String str){
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
