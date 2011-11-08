package me.Todkommt.ThumbsApply.Interpreters;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsBukkitInterpreter {

	public static boolean getPermissionNode(Player player, String node)
	{
		if(player.hasPermission(node))
		{
			return true;
		}
		return false;
	}
	
	public static void promote(CommandSender console, String name, int count)
	{
		switch(count)
		{
		case 1:
			ThumbsApply.server.dispatchCommand(console, "permissions player setgroup " + name + " " + ThumbsApply.groupnametarget);
		case 2:
			ThumbsApply.server.dispatchCommand(console, "permissions player setgroup " + name + " " + ThumbsApply.groupnametarget2);
		}
	}
	
}
