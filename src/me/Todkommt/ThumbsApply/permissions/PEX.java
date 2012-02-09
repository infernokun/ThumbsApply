package me.Todkommt.ThumbsApply.permissions;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PEX implements PermissionsHandler {

	public PermissionManager pexHandler;
	public ThumbsApply plugin;
	
	public PEX(ThumbsApply plugin){
		this.pexHandler = PermissionsEx.getPermissionManager();
		this.plugin = plugin;
	}
	
	public boolean has(CommandSender sender, String permission, String world){
		if (sender instanceof Player){
			Player player = (Player) sender;
			if(world != "")
				return pexHandler.has(player, permission);
			else
				return pexHandler.has(player, permission, world);
		} else {
			return true;
		}
	}
	
	public void setGroup(CommandSender sender, String groupname, String world)
	{
		CommandSender console = plugin.getServer().getConsoleSender();
		world = " " + world;
		plugin.getServer().dispatchCommand(console, "pex user " + sender.getName() + " group set " + groupname + world);
	}
}
