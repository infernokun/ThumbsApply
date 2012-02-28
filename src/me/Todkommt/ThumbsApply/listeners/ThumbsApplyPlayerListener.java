package me.Todkommt.ThumbsApply.listeners;

import java.util.HashMap;

import me.Todkommt.ThumbsApply.Messaging;
import me.Todkommt.ThumbsApply.Phrase;
import me.Todkommt.ThumbsApply.ThumbsApply;
import me.Todkommt.ThumbsApply.ThumbsApplyGroup;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ThumbsApplyPlayerListener implements Listener {

	private ThumbsApply plugin;
	
	public ThumbsApplyPlayerListener(ThumbsApply plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
			if(plugin.timedPromotion)
			{
				if(!ThumbsApply.timeToPromote.containsKey(event.getPlayer()))
				{
					HashMap<String, Integer> groups = new HashMap<String, Integer>();
					for(ThumbsApplyGroup group : plugin.groups)
					{
						if(group.isTimed && !plugin.getPermissionsHandler().has(event.getPlayer(), "group." + group.group, group.world))
							groups.put(group.group, group.time*(60000/plugin.delay));
					}
					ThumbsApply.timeToPromote.put((OfflinePlayer)event.getPlayer(), groups);
					plugin.timedSave(true);
				}
			}
		if(!plugin.getPermissionsHandler().has(event.getPlayer(), "ThumbsApply.NotGuest", ""))
		{
			if(plugin.getConfig().getBoolean("options.joinMessageEnabled"))
			{
				if(plugin.getConfig().getBoolean("options.timedPromotion"))
				{
					Messaging.send(event.getPlayer(), Phrase.JOIN_MESSAGE_TIME.parse());
				}
				else
				{
				Messaging.send(event.getPlayer(), Phrase.JOIN_MESSAGE_PASSWORD.parse());
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if(!plugin.getConfig().getBoolean("options.chatBlockEnabled"))
		{
			return;
		}
		
		if(!plugin.getPermissionsHandler().has(event.getPlayer(), "ThumbsApply.NotGuest", ""))
		{
			if(plugin.getConfig().getBoolean("options.timedPromotion"))
			{
				Messaging.send(event.getPlayer(), Phrase.TIME_TO_GO_CHAT.parse(Integer.toString(plugin.getConfig().getInt("options.timeToPromote")*60000/plugin.getConfig().getInt("options.tickDelay"))));
				event.setCancelled(true);
				return;
			}
			Messaging.send(event.getPlayer(), Phrase.GUEST_CHAT.parse());
			event.setCancelled(true);
		}
	}
	
}
