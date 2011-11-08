package me.Todkommt.ThumbsApply.Interpreters;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExInterpreter {

	static PermissionManager permissionsManager = PermissionsEx.getPermissionManager();
	
	public static boolean getPermissionNode(Player player, String node)
	{
		if(permissionsManager.has(player, node))
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
			ThumbsApply.server.dispatchCommand(console, "pex user " + name + " group set " + ThumbsApply.groupnametarget);
		case 2:
			ThumbsApply.server.dispatchCommand(console, "pex user " + name + " group set " + ThumbsApply.groupnametarget2);
		}
	}
	
}
