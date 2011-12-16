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
	
	public boolean has(CommandSender sender, String permission) {
		if(sender instanceof Player)
		{
			GroupManager gm = (GroupManager)plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if(gm != null)
			{
				if(gm.getWorldsHolder().getWorldPermissions((Player)sender).has((Player)sender, permission))
				{
					return true;
				}
				else return false;
			}
			else return false;
		}
		else return true;
	}

	public void setGroup(CommandSender sender, String group) {
		CommandSender console = plugin.getServer().getConsoleSender();
		plugin.getServer().dispatchCommand(console, "manuadd " + sender.getName() + " " + group);
	}

}
