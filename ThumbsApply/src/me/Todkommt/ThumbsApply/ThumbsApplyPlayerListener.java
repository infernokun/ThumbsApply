// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 11.09.2011 21:55:01
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ThumbsApplyPlayerListener.java

package me.Todkommt.ThumbsApply;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;

public class ThumbsApplyPlayerListener extends PlayerListener
{
	public ThumbsApplyPlayerListener()
    {
    }

    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	Player player = event.getPlayer();
    	if( ThumbsApply.getPermissionNode(player, "thumbsapply.notguest"))
    	{
    		if(ThumbsApply.loginmessageenabled.equalsIgnoreCase("true"))
    		{
    		player.sendMessage(ChatColor.GREEN + ThumbsApply.loginmessage + ThumbsApply.commandconf + ThumbsApply.passwordmsg);
    	    }
    	}
    }

    public void onPlayerChat(PlayerChatEvent event)
    {
    	Player player = event.getPlayer();
    	if(ThumbsApply.getPermissionNode(player, "thumbsapply.notguest"))
    	{
    		if( ThumbsApply.chatblockenabled.equalsIgnoreCase("true"))
    		{
    		player.sendMessage(ChatColor.RED + ThumbsApply.chatrestricted + ThumbsApply.commandconf + ThumbsApply.passwordmsg);
    		event.setCancelled(true);
    		}
    	}
    }
}
