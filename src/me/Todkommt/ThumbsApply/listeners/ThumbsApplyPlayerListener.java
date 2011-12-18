package me.Todkommt.ThumbsApply.listeners;

import me.Todkommt.ThumbsApply.Messaging;
import me.Todkommt.ThumbsApply.Phrase;
import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ThumbsApplyPlayerListener extends PlayerListener {

	private ThumbsApply plugin;
	
	public ThumbsApplyPlayerListener(ThumbsApply plugin) {
		this.plugin = plugin;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(!plugin.getPermissionsHandler().has(event.getPlayer(), "ThumbsApply.NotGuest"))
		{
			if(plugin.getConfig().getBoolean("options.timedPromotion"))
			{
				if(!ThumbsApply.timeToPromote.containsKey(event.getPlayer()))
				{
					ThumbsApply.timeToPromote.put(event.getPlayer(), plugin.getConfig().getInt("options.timeToPromote")/(60000/plugin.getConfig().getInt("options.tickDelay")));
				}
			}
			if(plugin.getConfig().getBoolean("options.joinMessageEnabled"))
			{
				if(plugin.getConfig().getBoolean("options.timedPromotion"))
				{
					String[] stuff = new String[2];
					stuff[0] = event.getPlayer().getName();
					stuff[1] = Integer.toString(plugin.getConfig().getInt("options.timeToPromote")*60000/plugin.getConfig().getInt("options.tickDelay"));
					Messaging.send(event.getPlayer(), Phrase.JOIN_MESSAGE_TIME.parse(stuff));
				}
				else
				{
				String[] stuff = new String[1];
				stuff[0] = event.getPlayer().getName();
				Messaging.send(event.getPlayer(), Phrase.JOIN_MESSAGE_PASSWORD.parse(stuff));
				}
			}
		}
	}
	
	public void onPlayerChat(PlayerChatEvent event)
	{
		if(!plugin.getConfig().getBoolean("options.chatBlockEnabled"))
		{
			return;
		}
		
		if(!plugin.getPermissionsHandler().has(event.getPlayer(), "ThumbsApply.NotGuest"))
		{
			Messaging.send(event.getPlayer(), Phrase.GUEST_CHAT.parse());
			event.setCancelled(true);
		}
	}
	
}
