package com.makzk.cb.chatfilter;

import java.io.File;
import org.bukkit.ChatColor;

public class Language {
	private ChatFilter p = ChatFilter.getInstance();
	private Configuration lang = null;

	public void load() {
		// Checks if desired language file exists
		String langPath = String.format("lang_%s.yml", p.getConf().string("lang"));
		
		// Try to load a default file from plugin jar
		lang = new Configuration(p, langPath);
		lang.reloadConfig();
		
		// If the default file could not be loaded, try with external file
		if(lang.getConfig() == null) {
			File langFile = new File(p.getDataFolder(), langPath);
			if(!langFile.isFile()) {
				p.getLogger().warning(
						String.format("Language file '%s' was not found.", langPath));
				langPath = "lang_default.yml";
			}
		}

		// Show which language file was loaded
		p.getLogger().info("Language file: " + langPath);
		if(lang == null) {
			lang = new Configuration(p, langPath);
			lang.reloadConfig();
		}
	}

	/**
	 * Gets a string named by path
	 * 
	 * @param path
	 *            The path from the language file
	 * @return The translated path, from the language file
	 */
	public String str(String path) {
		return colorize(lang.string(path));
	}

	/**
	 * Checks if language file was loaded
	 * 
	 * @return A boolean for each case
	 */
	public boolean isLoaded() {
		return lang != null;
	}

	/**
	 * Format the color codes to the Bukkit way
	 * 
	 * @param str
	 *            The parameter to colorize
	 * @return The color-formatted string
	 */
	public String colorize(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}
}
