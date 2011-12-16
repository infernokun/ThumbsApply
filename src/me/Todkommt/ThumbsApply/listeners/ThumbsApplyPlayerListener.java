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
		if(!plugin.getConfig().getBoolean("options.joinMessageEnabled"))
		{
			return;
		}
		
		if(!plugin.getPermissionsHandler().has(event.getPlayer(), "ThumbsApply.NotGuest"))
		{
			String[] stuff = new String[2];
			stuff[0] = event.getPlayer().getName();
			stuff[1] = "apply";
			Messaging.send(event.getPlayer(), Phrase.JOIN_MESSAGE.parse(stuff));
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
