package me.Todkommt.ThumbsApply;

import java.util.logging.Logger;

import me.Todkommt.ThumbsApply.listeners.ThumbsApplyPlayerListener;
import me.Todkommt.ThumbsApply.permissions.GroupManagerHandler;
import me.Todkommt.ThumbsApply.permissions.PEX;
import me.Todkommt.ThumbsApply.permissions.Permissions3;
import me.Todkommt.ThumbsApply.permissions.PermissionsBukkit;
import me.Todkommt.ThumbsApply.permissions.PermissionsHandler;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ThumbsApply extends JavaPlugin {

	public static Server server;
	private PermissionsHandler permissionsHandler;
	private Logger log;
	public ThumbsApplyPlayerListener playerListener;
	
	public void onDisable() {
		log.info("[ThumbsApply] disabled.");
	}

	public void onEnable() {
		playerListener = new ThumbsApplyPlayerListener(this);
		log = Logger.getLogger("Minecraft");
		String mainPath = "options.";
		this.getConfig().addDefault(mainPath + "promoteTo", "User");
		this.getConfig().addDefault(mainPath + "password", "default");
		this.getConfig().addDefault(mainPath + "chatBlockEnabled", false);
		this.getConfig().addDefault(mainPath + "joinMessageEnabled", true);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		registerHooks();
		server = this.getServer();
		setupPermissions();
		log.info("[ThumbsApply] enabled.");
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(args.length != 1)
		{
			Messaging.send(sender, Phrase.USAGE.parse());
			return true;
		}
		
		if(!this.getConfig().getString("options.password").equalsIgnoreCase(args[0]))
		{
			Messaging.send(sender, Phrase.WRONG_PASSWORD.parse());
			return true;
		}
		
		permissionsHandler.setGroup(sender, this.getConfig().getString("options.promoteTo"));
		Messaging.send(sender, Phrase.SUCCESS.parse());
		return true;
	}
	
	private void setupPermissions(){
		Plugin permissions = getServer().getPluginManager().getPlugin("Permissions");
		
		Plugin PEX = getServer().getPluginManager().getPlugin("PermissionsEx");
		
		Plugin GroupManager = getServer().getPluginManager().getPlugin("GroupManager");
		
		if(PEX != null)
		{
			permissionsHandler = new PEX(this);
			log.info("[ThumbsApply] PEX System activated.");
		}
		else if(GroupManager != null)
		{
			permissionsHandler = new GroupManagerHandler(this);
			log.info("[ThumbsApply] GroupManager System activated.");
		}
		else if (permissions != null && permissions.getDescription().getVersion().startsWith("3"))
		{
			permissionsHandler = new Permissions3(permissions, this);
			log.info("[ThumbsApply] Permissions3 System activated.");
		}
		else
		{
			permissionsHandler = new PermissionsBukkit(this);
			log.info("[ThumbsApply] PermissionsBukkit System activated.");
		}
	}
	
	public PermissionsHandler getPermissionsHandler()
	{
		return permissionsHandler;
	}
	
	public void registerHooks()
	{
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Low, this);
	}
}
