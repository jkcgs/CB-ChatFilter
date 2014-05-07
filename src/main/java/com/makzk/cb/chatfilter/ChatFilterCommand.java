package com.makzk.cb.chatfilter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatFilterCommand implements CommandExecutor {
	private ChatFilter p = ChatFilter.getInstance();

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (!sender.hasPermission("chatfilter.command")) {
			sender.sendMessage(p.lang.str("noPermission"));
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(p.getFullDescription());
		} else {
			String sub = args[0];

			if (sub.equals("help")) {
				List<String> help = new ArrayList<String>();
				help.add(p.getFullDescription());
				help.add(String.format("/%s help - %s", cmd.getName(),
						p.lang.str("helpDescription")));
				help.add(String.format("/%s reload - %s", cmd.getName(),
						p.lang.str("reloadDescription")));
				help.add(String.format("/%s save - %s", cmd.getName(),
						p.lang.str("saveDescription")));
				help.add(String.format("/%s toggle - %s", cmd.getName(),
						p.lang.str("toggleDescription")));
				help.add(String.format("/%s toggle-status - %s", cmd.getName(),
						p.lang.str("togglestatusDescription")));
				help.add(String.format("/%s add - %s", cmd.getName(),
						p.lang.str("addDescription")));
				help.add(String.format("/%s remove - %s", cmd.getName(),
						p.lang.str("removeDescription")));
				sender.sendMessage(help.toArray(new String[help.size()]));
			}

			// Reload the configuration and filters
			if (sub.equals("reload")) {
				p.reloadConfig();
				p.getLogger().info(p.lang.str("configReloaded"));

				// If player used the command, will see the reloaded message too
				if (sender instanceof Player) {
					sender.sendMessage(p.lang.str("configReloaded"));
				}
			}

			if (sub.equals("save")) {
				senderLog(sender, p.lang.str("savingConfig"));

				p.config.saveDefaultConfig();
				p.config.saveConfig();
				p.filter.saveDefaultConfig();
				p.filter.saveConfig();

				senderLog(sender, p.lang.str("savedConfig"));
			}

			if (sub.equals("toggle")) {
				String toggle = args.length < 2 ? "global" : args[1].toLowerCase();
				String toggleName = null;

				if (toggle.equals("help")) {
					sender.sendMessage(p.lang.str("availableToggles"));
					return true;
				}

				if (toggle.equals("global")) {
					toggleName = "filterEnabled";
				} else if (toggle.equals("upcase")) {
					toggleName = "upcaseFilter";
				} else if (toggle.equals("ip")) {
					toggleName = "ipFilter";
				} else if (toggle.equals("blockfiltered")) {
					toggleName = "toggleFilteredMessage";
				} else {
					senderLog(sender, String.format(p.lang.str("toggleNotFound"), cmd.getName()));
				}

				if (toggleName != null) {
					// Toggle it
					p.config.getConfig().set(toggleName, !p.config.bool(toggleName));

					// Log new toggle status
					String status = boolToStatus(p.config.bool(toggleName));
					senderLog(sender, p.lang.str("toggleStatus") + " " + status);
				}
			}
			
			if(sub.equals("toggle-status")) {
				// global, upcase, ip, blockfiltered
				List<String> toggleStatus = new ArrayList<String>();
				toggleStatus.add(p.lang.str("toggleStatusTitle") + ":");
				toggleStatus.add("global: " + boolToStatus(p.config.bool("filterEnabled")));
				toggleStatus.add("upcase: " + boolToStatus(p.config.bool("upcaseFilter")));
				toggleStatus.add("ip: " + boolToStatus(p.config.bool("filterEnabled")));
				toggleStatus.add("blockfiltered: " + boolToStatus(p.config.bool("blockFilteredMessaged")));
				
				sender.sendMessage(toggleStatus.toArray(new String[toggleStatus.size()]));
			}

			if (sub.equals("add")) {
				if (args.length < 2) {
					sender.sendMessage(p.lang.str("usage") + ": /" + cmd.getName() + " add <string>");
					return true;
				}

				List<String> list = p.filter.list("filters");
				if (!list.contains(args[1])) {
					list.add(args[1]);
					p.filter.getConfig().set("filters", list);

					senderLog(sender, p.lang.str("newFilterAdded") + ": '" + args[1] + "'");
				} else {
					sender.sendMessage(p.lang.str("filterAlreadyExists"));
				}
			}

			if (sub.equals("remove")) {
				if (args.length < 2) {
					sender.sendMessage(p.lang.str("usage") + ": /" + cmd.getName() + " remove <string>");
					return true;
				}

				List<String> list = p.filter.list("filters");
				if (list.remove(args[1])) {
					p.filter.getConfig().set("filters", list);
					senderLog(sender, p.lang.str("filterRemoved") + ": '" + args[1] + "'");
				} else {
					sender.sendMessage(p.lang.str("filterNotExists"));
				}
			}
		}
		return true;
	}

	/**
	 * Logs a message to the console. If the sender is a player, send him the
	 * message too.
	 * 
	 * @param sender
	 *            The sender, could be the console.
	 * @param msg
	 *            The message to log and informate.
	 */
	public void senderLog(CommandSender sender, String msg) {
		p.getLogger().info(msg);

		if (sender instanceof Player) {
			sender.sendMessage(msg);
		}
	}
	
	/**
	 * Returns the language string for enabled and disabled, with a boolean
	 * @param status The boolean to translate
	 * @return The language string for "enabled" if status is true, string for "disabled" if false.
	 */
	public String boolToStatus(boolean status) {
		return p.lang.str(status ? "enabled" : "disabled");
	}
}
