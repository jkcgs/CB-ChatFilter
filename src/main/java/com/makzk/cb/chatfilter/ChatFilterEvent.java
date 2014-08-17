package com.makzk.cb.chatfilter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFilterEvent implements Listener {
	private ChatFilter p = ChatFilter.getInstance();
	private MessageFilterer msg;
	private Map<String, String> lastMsg = new HashMap<String, String>();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// Check for global filter toggle
		if (p.getConf().bool("filterEnabled")) {
			// If the player has the ignore permission, and the usage of this is
			// enabled, then don't use the filter for this message.
			if (p.getConf().bool("useIgnorePermission")
					&& e.getPlayer().hasPermission("chatfilter.ignore")) {
				return;
			}

			// Get filters
			List<String> filters = p.getFilter().list("strings");
			// The map of specific filters
			Map<String, Object> specific = p.getFilter().getConfig().getConfigurationSection("specific").getValues(false);
			
			msg = new MessageFilterer(e.getMessage(), p.getConf().string("filterString"));
			msg.filterRegex(filters.toArray(new String[filters.size()]));
			
			// Specific filters processing
			Iterator<String> it = specific.keySet().iterator();
			while(it.hasNext()){
			  String key = (String) it.next();
			  msg.filterRegex(key, (String) specific.get(key));
			}
			
			// Repeated words filter
			if(p.getConf().bool("repeatedFilter")) {
				if(lastMsg.containsKey(e.getPlayer().getName())) {
					String msg = e.getMessage().trim().replaceAll("  ", " ");
					if(lastMsg.get(e.getPlayer().getName()).equals(msg)) {
						e.setCancelled(true);
						e.getPlayer().sendMessage("Don't send repeated messages!");
					}
				} else {
					lastMsg.put(e.getPlayer().getName(), "");
				}
			}

			if (p.getConf().bool("ipFilter")) {
				msg.filterIP();
			}

			if (p.getConf().bool("upcaseFilter")) {
				msg.filterUpcases(p.getConf().intg("upcaseMinAmount"),
						p.getConf().string("upcaseFilterPattern"));
			}

			if (msg.isFiltered()) {
				if (p.getConf().bool("logOriginalFilteredMessage")) {
					p.getLogger().info(
							String.format("%s: (%s) '%s'", 
									p.getLang().str("chatMsgBlocked"), 
									e.getPlayer().getName(), 
									e.getMessage()));
				}

				if(p.getConf().bool("kickOnFilter")) {
					String kickMsg = p.getConf().string("kickMessage");
					if(kickMsg.isEmpty()) {
						kickMsg = p.getLang().str("defaultKickMsg");
					}
					
					e.getPlayer().kickPlayer(kickMsg);
					e.setCancelled(true);
				} else if (p.getConf().bool("blockFilteredMessage")) {
					e.getPlayer().sendMessage(p.getLang().str("blockedMsgInfo"));
					e.setCancelled(true);
				} else {
					e.setMessage(msg.getFilteredMsg());
				}
			}

			// Delete class instance
			msg = null;
		}
	}
}
