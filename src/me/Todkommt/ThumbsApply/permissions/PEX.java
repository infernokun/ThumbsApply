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
	
	public boolean has(CommandSender sender, String permission){
		if (sender instanceof Player){
			Player player = (Player) sender;
			
			return pexHandler.has(player, permission);
		} else {
			return true;
		}
	}
	
	public void setGroup(CommandSender sender, String groupname)
	{
		CommandSender console = plugin.getServer().getConsoleSender();
		plugin.getServer().dispatchCommand(console, "pex user " + sender.getName() + " group set " + groupname);
	}
}
