package me.Todkommt.ThumbsApply.permissions;

import org.bukkit.command.CommandSender;

public interface PermissionsHandler {
	public boolean has(CommandSender sender, String permission);
	public void setGroup(CommandSender sender, String group);
}
