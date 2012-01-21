package me.Todkommt.ThumbsApply.permissions;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Permissions3 implements PermissionsHandler {

private PermissionHandler p3Handler;
private ThumbsApply plugin;
	
	public Permissions3(Plugin p3plugin, ThumbsApply plugin){
		p3Handler = ((Permissions) p3plugin).getHandler();
		this.plugin = plugin;
	}
	
	public boolean has(CommandSender sender, String permission) {
		if (sender instanceof Player){
			Player player = (Player) sender;
			
			return p3Handler.has(player, permission);
		} else {
			return true;
		}
	}
	
	public String getGroup(String name, String world) {
		return p3Handler.getPrimaryGroup(world, name);
	}
	
	public String getPrefix(String name, String world) {
		return p3Handler.getUserPrefix(world, name);
	}
	
	public String getSuffix(String name, String world) {
		return p3Handler.getUserSuffix(world, name);
	}

	public void setGroup(CommandSender sender, String group) {
		CommandSender console = plugin.getServer().getConsoleSender();
		String oldGroup = p3Handler.getPrimaryGroup(sender.getName(), sender.getName());
		plugin.getServer().dispatchCommand(console, "pr " + sender.getName() + " parents add " + group);
		plugin.getServer().dispatchCommand(console, "pr " + sender.getName() + " parents remove " + oldGroup);
	}
	
}
