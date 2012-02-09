package me.Todkommt.ThumbsApply.permissions;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.bananaco.permissions.Permissions;
import de.bananaco.permissions.interfaces.PermissionSet;
import de.bananaco.permissions.worlds.WorldPermissionsManager;

public class bPermissions implements PermissionsHandler {

	public WorldPermissionsManager bPermsHandler = null;
	public ThumbsApply plugin;
	
	public bPermissions(ThumbsApply plugin)
	{
		bPermsHandler = Permissions.getWorldPermissionsManager();
		this.plugin = plugin;
	}
	
	public boolean has(CommandSender sender, String permission, String world) {
		return ((Player)sender).hasPermission(permission);
	}

	public void setGroup(CommandSender sender, String group, String world) {
		if(world == "")
		{
			world = plugin.getServer().getWorlds().get(0).getName();
		}
		PermissionSet bPermHandle = bPermsHandler.getPermissionSet(world);
		String oldGroup = bPermHandle.getGroups(sender.getName()).get(0);
		bPermHandle.addGroup((Player)sender, group);
		bPermHandle.removeGroup((Player)sender, oldGroup);
	}

}
