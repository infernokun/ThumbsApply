package me.Todkommt.ThumbsApply.permissions;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsBukkit implements PermissionsHandler {

	public ThumbsApply plugin;
	
	public PermissionsBukkit(ThumbsApply plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean has(CommandSender sender, String permission) {
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			if(player.hasPermission(permission))
			{
				return true;
			}
			else return false;
		}
		else return true;
	}

	public void setGroup(CommandSender sender, String group) {
		CommandSender console = plugin.getServer().getConsoleSender();
		plugin.getServer().dispatchCommand(console, "permissions player setgroup " + sender.getName() + " " + group);
	}
}
