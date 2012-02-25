package me.Todkommt.ThumbsApply.permissions;

import me.Todkommt.ThumbsApply.ThumbsApply;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupManagerHandler implements PermissionsHandler {

	public ThumbsApply plugin;
	
	public GroupManagerHandler(ThumbsApply plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean has(CommandSender sender, String permission, String world) {
		if(sender instanceof Player)
		{
			GroupManager gm = (GroupManager)plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if(gm != null)
			{
				if(world != "")
				{
					return gm.getWorldsHolder().getWorldPermissions(world).has((Player)sender, permission);
				}
				else
				{
					return gm.getWorldsHolder().getWorldPermissions((Player)sender).has((Player)sender, permission);
				}
			}
			else return false;
		}
		else return true;
	}

	public void setGroup(CommandSender sender, String group, String world) {
		CommandSender console = plugin.getServer().getConsoleSender();
		if(world == "")
			plugin.getServer().dispatchCommand(console, "manuadd " + sender.getName() + " " + group);
		else
		{
			plugin.getServer().dispatchCommand(console, "manselect " + world);
			plugin.getServer().dispatchCommand(console, "manuadd " + sender.getName() + " " + group);
		}
	}

}
