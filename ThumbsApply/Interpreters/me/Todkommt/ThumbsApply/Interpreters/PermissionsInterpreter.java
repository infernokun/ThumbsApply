package me.Todkommt.ThumbsApply.Interpreters;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class PermissionsInterpreter {

	private static Plugin permissionsPlugin = ThumbsApply.server.getPluginManager().getPlugin("Permissions");
	public static PermissionHandler permissionHandler = ((Permissions) permissionsPlugin).getHandler();
	
	public static boolean getPermissionNode(Player player, String node)
	{
		if(permissionHandler.has(player, node))
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
