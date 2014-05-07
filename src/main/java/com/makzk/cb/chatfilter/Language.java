package com.makzk.cb.chatfilter;

import java.io.File;
import org.bukkit.ChatColor;

public class Language {
	private ChatFilter p = ChatFilter.getInstance();
	private Configuration lang = null;

	public void load() {
		// Checks if desired language file exists
		String langPath = String.format("lang_%s.yml", p.config.string("lang"));

		File langFile = new File(p.getDataFolder(), langPath);
		// If desired language file does not exists, use the default one
		// Will not check if language is "default", because it should be
		// always there for you :$
		if (!p.config.string("lang").equals("default") && !langFile.isFile()) {
			// Don't translate, we think that the language 
			// file could not be loaded
			p.getLogger().warning(
					String.format("Language file '%s' was not found.", langPath));
			langPath = "lang_default.yml";
		}

		// Loads language file
		p.getLogger().info("Language file: " + langPath);
		lang = new Configuration(p, langPath);
		lang.reloadConfig();
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
