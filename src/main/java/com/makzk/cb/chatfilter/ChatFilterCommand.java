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
			sender.sendMessage(p.getLang().str("noPermission"));
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(p.getFullDescription());
			sender.sendMessage(String.format(p.getLang().str("typeForHelp"), cmd.getName()));
		} else {
			String sub = args[0];

			// Reload the configuration and filters
			if (sub.equals("reload")) {
				p.reloadConfig();
				p.getLogger().info(p.getLang().str("configReloaded"));

				// If player used the command, will see the reloaded message too
				if (sender instanceof Player) {
					sender.sendMessage(p.getLang().str("configReloaded"));
				}
			}

			// Saves the configuration
			else if (sub.equals("save")) {
				senderLog(sender, p.getLang().str("savingConfig"));

				p.getConf().saveDefaultConfig();
				p.getConf().saveConfig();
				p.getFilter().saveDefaultConfig();
				p.getFilter().saveConfig();

				senderLog(sender, p.getLang().str("savedConfig"));
			}

			// Activate/deactivate toggles
			else if (sub.equals("toggle")) {
				String toggle = args.length < 2 ? "global" : args[1].toLowerCase();
				String toggleName = null;

				if (toggle.equals("help")) {
					sender.sendMessage(p.getLang().str("availableToggles"));
					return true;
				}

				if (toggle.equals("global")) {
					toggleName = "filterEnabled";
				} else if (toggle.equals("upcase")) {
					toggleName = "upcaseFilter";
				} else if (toggle.equals("ip")) {
					toggleName = "ipFilter";
				} else if (toggle.equals("blockfiltered")) {
					toggleName = "blockFilteredMessage";
				} else {
					senderLog(sender, String.format(p.getLang().str("toggleNotFound"), cmd.getName()));
				}

				if (toggleName != null) {
					// Toggle it
					p.getConf().getConfig().set(toggleName, !p.getConf().bool(toggleName));

					// Log new toggle status
					String status = boolToStatus(p.getConf().bool(toggleName));
					senderLog(sender, p.getLang().str("toggleStatus") + " " + status);
				}
			}
			
			// Check toggles status
			else if(sub.equals("toggle-status")) {
				// global, upcase, ip, blockfiltered
				List<String> toggleStatus = new ArrayList<String>();
				toggleStatus.add(p.getLang().str("toggleStatusTitle") + ":");
				toggleStatus.add("global: " + boolToStatus(p.getConf().bool("filterEnabled")));
				toggleStatus.add("upcase: " + boolToStatus(p.getConf().bool("upcaseFilter")));
				toggleStatus.add("ip: " + boolToStatus(p.getConf().bool("filterEnabled")));
				toggleStatus.add("blockfiltered: " + boolToStatus(p.getConf().bool("blockFilteredMessaged")));
				
				sender.sendMessage(toggleStatus.toArray(new String[toggleStatus.size()]));
			}
			
			// List actual filters
			else if(sub.equals("list")) {
				List<String> filters = p.getFilter().list("filters");
				if(filters.size() > 0) {
					sender.sendMessage(p.getLang().str("listOfFilters"));
					sender.sendMessage(filters.toArray(new String[filters.size()]));
				} else {
					sender.sendMessage(p.getLang().str("noFilters"));
				}
			}

			// Add an epic filter
			else if (sub.equals("add")) {
				if (args.length < 2) {
					sender.sendMessage(p.getLang().str("usage") + ": /" + cmd.getName() + " add <string>");
					return true;
				}

				List<String> list = p.getFilter().list("filters");
				if (!list.contains(args[1])) {
					list.add(args[1]);
					p.getFilter().getConfig().set("filters", list);

					senderLog(sender, p.getLang().str("newFilterAdded") + ": '" + args[1] + "'");
				} else {
					sender.sendMessage(p.getLang().str("filterAlreadyExists"));
				}
			}

			// Removes an epic filter
			else if (sub.equals("remove")) {
				if (args.length < 2) {
					sender.sendMessage(p.getLang().str("usage") + ": /" + cmd.getName() + " remove <string>");
					return true;
				}

				List<String> list = p.getFilter().list("filters");
				if (list.remove(args[1])) {
					p.getFilter().getConfig().set("filters", list);
					senderLog(sender, p.getLang().str("filterRemoved") + ": '" + args[1] + "'");
				} else {
					sender.sendMessage(p.getLang().str("filterNotExists"));
				}
			} else {
				List<String> help = new ArrayList<String>();
				if (!sub.equals("help")) {
					sender.sendMessage(p.getLang().str("subNoExists"));
				} else {
					help.add(p.getFullDescription());
				}
				
				help.add(String.format("/%s help - %s", cmd.getName(),
						p.getLang().str("helpDescription")));
				help.add(String.format("/%s reload - %s", cmd.getName(),
						p.getLang().str("reloadDescription")));
				help.add(String.format("/%s save - %s", cmd.getName(),
						p.getLang().str("saveDescription")));
				help.add(String.format("/%s toggle - %s", cmd.getName(),
						p.getLang().str("toggleDescription")));
				help.add(String.format("/%s toggle-status - %s", cmd.getName(),
						p.getLang().str("togglestatusDescription")));
				help.add(String.format("/%s list - %s", cmd.getName(),
						p.getLang().str("listDescription")));
				help.add(String.format("/%s add - %s", cmd.getName(),
						p.getLang().str("addDescription")));
				help.add(String.format("/%s remove - %s", cmd.getName(),
						p.getLang().str("removeDescription")));
				sender.sendMessage(help.toArray(new String[help.size()]));
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
		return p.getLang().str(status ? "enabled" : "disabled");
	}
}
