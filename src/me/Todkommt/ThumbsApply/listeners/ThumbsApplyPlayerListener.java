package me.Todkommt.ThumbsApply.listeners;

import me.Todkommt.ThumbsApply.Messaging;
import me.Todkommt.ThumbsApply.Phrase;
import me.Todkommt.ThumbsApply.ThumbsApply;
import me.Todkommt.ThumbsApply.ThumbsApplyGroup;

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
		if(!plugin.permissionsHandler.has(event.getPlayer(), "ThumbsApply.NotGuest", ""))
		{
			if(plugin.getConfig().getBoolean("options.joinMessageEnabled"))
			{
				Messaging.sendJoinMessage(event.getPlayer());
			}
		}
		for(ThumbsApplyGroup group : plugin.groups)
		{
			group.method.onPlayerJoin(event);
		}
		Messaging.sendMsgBuffer();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if(plugin.getConfig().getBoolean("options.chatBlockEnabled"))
		{
			if(!plugin.permissionsHandler.has(event.getPlayer(), "ThumbsApply.NotGuest", ""))
			{
				Messaging.send(event.getPlayer(), Phrase.GUEST_CHAT.parse());
				event.setCancelled(true);
			}
		}
		
		for(ThumbsApplyGroup group : plugin.groups)
		{
			group.method.onPlayerChat(event);
		}
		Messaging.sendMsgBuffer();
	}
	
}
