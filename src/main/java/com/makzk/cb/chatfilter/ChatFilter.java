package com.makzk.cb.chatfilter;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin intended to limit what players can say on the CraftBukkit server
 * chat.
 * 
 * @author Makzk <me@makzk.com>
 *
 */
public class ChatFilter extends JavaPlugin {
	private static ChatFilter instance;
	private Configuration config = null;
	private Configuration filter = null;
	private Language lang = null;

	@Override
	public void onEnable() {
		instance = this;

		// Load/initiate config
		config = new Configuration(instance, "config.yml");
		config.saveDefaultConfig();
		config.reloadConfig();

		// Load/initiate filters
		filter = new Configuration(instance, "filters.yml");
		filter.saveDefaultConfig();
		filter.reloadConfig();

		lang = new Language();
		lang.load();

		// Register command
		getCommand("chatfilter").setExecutor(new ChatFilterCommand());

		// Register chat event handler
		getServer().getPluginManager().registerEvents(new ChatFilterEvent(), this);

		// Check if config were loaded
		if (config == null || !lang.isLoaded()) {
			// This should not be translated, by logical reasons
			getLogger().severe("Configuration could not be loaded!");
			getServer().getPluginManager().disablePlugin(instance);
		} else {
			getLogger().info(lang.str("pluginLoaded"));
		}
	}

	@Override
	public void onDisable() {
		config.saveConfig();
		filter.saveConfig();
		getLogger().info(lang.str("pluginUnloaded"));
	}

	/**
	 * @return A static reference to the plugin instance.
	 */
	public static ChatFilter getInstance() {
		return instance;
	}

	/**
	 * Reloads configuration files.
	 */
	@Override
	public void reloadConfig() {
		config.reloadConfig();
		lang.load();
		
		// This will delete filters not saved before
		filter.reloadConfig();
	}

	/**
	 * Gives a description of the plugin
	 * 
	 * @return A full description of the plugin including name, version, and
	 *         authors.
	 */
	public String getFullDescription() {
		// Prepare authors list
		String authors = "";
		int nAuthors = getDescription().getAuthors().size();

		if (nAuthors > 0) {
			authors = lang.str("by") + " ";
		}

		for (int i = 0; i < nAuthors; i++) {
			authors += getDescription().getAuthors().get(i);
			if (i < nAuthors - 2) {
				authors += ", ";
			} else if (i < nAuthors - 1) {
				// if last author
				authors += " " + lang.str("and") +" ";
			}
		}

		return String.format("%s v%s, %s", getDescription().getName(),
				getDescription().getVersion(), authors);
	}

	public Configuration getConf() {
		return config;
	}

	public Configuration getFilter() {
		return filter;
	}

	public Language getLang() {
		return lang;
	}
}
