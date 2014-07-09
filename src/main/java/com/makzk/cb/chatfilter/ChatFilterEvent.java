package com.makzk.cb.chatfilter;

import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatFilterEvent implements Listener {
	private ChatFilter p = ChatFilter.getInstance();
	private MessageFilterer msg;

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

			List<String> filters = p.getFilter().list("filters");
			msg = new MessageFilterer(e.getMessage(), p.getConf().string("filterString"));
			msg.filterRegex(filters.toArray(new String[filters.size()]));

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
							String.format("%s: (%s) '%s'", p.getLang()
									.str("chatMsgBlocked"), e.getPlayer()
									.getName(), e.getMessage()));
				}

				if(p.getConf().bool("kickOnFilter")) {
					e.getPlayer().kickPlayer(p.getConf().string("kickMessage"));
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
