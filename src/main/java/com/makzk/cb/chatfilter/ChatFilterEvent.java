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
		if (p.config.bool("filterEnabled")) {
			// If the player has the ignore permission, and the usage of this is
			// enabled, then don't use the filter for this message.
			if (p.config.bool("useIgnorePermission")
					&& e.getPlayer().hasPermission("chatfilter.ignore")) {
				return;
			}

			List<String> filters = p.filter.list("filters");
			msg = new MessageFilterer(e.getMessage(), p.config.string("filterString"));
			msg.filterRegex(filters.toArray(new String[filters.size()]));

			if (p.config.bool("ipFilter")) {
				msg.filterIP();
			}

			if (p.config.bool("upcaseFilter")) {
				msg.filterUpcases(p.config.intg("upcaseMinAmount"),
						p.config.string("upcaseFilterPattern"));
			}

			if (msg.isFiltered()) {
				if (p.config.bool("logOriginalFilteredMessage")) {
					p.getLogger().info(
							String.format("%s: (%s) '%s'", p.lang
									.str("chatMsgBlocked"), e.getPlayer()
									.getName(), e.getMessage()));
				}

				if (p.config.bool("blockFilteredMessage")) {
					e.getPlayer().sendMessage(p.lang.str("blockedMsgInfo"));
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
