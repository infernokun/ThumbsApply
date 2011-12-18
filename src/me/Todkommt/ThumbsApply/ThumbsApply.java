package me.Todkommt.ThumbsApply;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.Todkommt.ThumbsApply.listeners.ThumbsApplyPlayerListener;
import me.Todkommt.ThumbsApply.permissions.GroupManagerHandler;
import me.Todkommt.ThumbsApply.permissions.PEX;
import me.Todkommt.ThumbsApply.permissions.Permissions3;
import me.Todkommt.ThumbsApply.permissions.PermissionsBukkit;
import me.Todkommt.ThumbsApply.permissions.PermissionsHandler;
import me.Todkommt.ThumbsApply.utils.PromotionTimer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
	FileConfiguration localizationConfig = null;
	File localizationFile = null;
	File localizationDir = new File("plugins/ThumbsApply/localization");
	public static ThumbsApply plugin;
	public Runnable timer = new PromotionTimer(this);
	public Thread tickthread = new Thread(timer);
	public static HashMap<Player, Integer> timeToPromote = new HashMap<Player, Integer>();
	public File playerSaveFile = new File("plugins/ThumbsApply/SaveData" + File.separator + "SavedPlayers.dat");
	public static int delay = 60000;
	
	public void onDisable() {
		log.info("[ThumbsApply] disabled.");
	}

	public void onEnable()
	{
		localizationDir.mkdir();
		playerListener = new ThumbsApplyPlayerListener(this);
		log = Logger.getLogger("Minecraft");
		String mainPath = "options.";
		this.getConfig().addDefault(mainPath + "promoteTo", "User");
		this.getConfig().addDefault(mainPath + "password", "default");
		this.getConfig().addDefault(mainPath + "chatBlockEnabled", false);
		this.getConfig().addDefault(mainPath + "joinMessageEnabled", true);
		this.getConfig().addDefault(mainPath + "timedPromotion", false);
		this.getConfig().addDefault(mainPath + "timeToPromote", 30);
		this.getConfig().addDefault(mainPath + "tickDelay", 60000);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		reloadLocalizationConfig();
		if(this.getConfig().getBoolean(mainPath + "timedPromotion"))
		{
			delay = this.getConfig().getInt(mainPath + "tickDelay");
			Player[] onlineplayers = this.getServer().getOnlinePlayers();
			for(int i=0; i<onlineplayers.length; i++)
			{
				if(!permissionsHandler.has(onlineplayers[i], "ThumbsApply.NotGuest"))
				{
					if(!timeToPromote.containsKey(onlineplayers[i]))
					{
						timeToPromote.put(onlineplayers[i], this.getConfig().getInt(mainPath + "timeToPromote")*60000/this.getConfig().getInt(mainPath + "tickDelay"));
					}
				}
			}
		}
		plugin = this;
		registerHooks();
		server = this.getServer();
		setupPermissions();
		if(this.getConfig().getBoolean(mainPath + "timedPromotion"))
		{
			if(!tickthread.isAlive())
			{
				tickthread.start();
			}
		}
		log.info("[ThumbsApply] enabled.");
	}

	public void update()
	{
		Iterator<Entry<Player, Integer>> iterator = timeToPromote.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry<Player, Integer> entry = (Map.Entry<Player, Integer>)iterator.next();
			if(entry.getKey().isOnline())
			{
			timeToPromote.put(entry.getKey(), entry.getValue()-1);
			}
			if(timeToPromote.get(entry.getKey()) <= 0)
			{
				getPermissionsHandler().setGroup(entry.getKey(), getConfig().getString("options.promoteTo"));
				timeToPromote.remove(entry.getKey());
				Messaging.send(entry.getKey(), Phrase.SUCCESS.parse());
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(getConfig().getBoolean("options.timedPromotion"))
		{
			if(permissionsHandler.has(sender, "ThumbsApply.NotGuest"))
			{
				Messaging.send(sender, Phrase.ALREADY_PROMOTED.parse());
				return true;
			}
			String[] args1 = new String[1];
			int timeleft = timeToPromote.get((Player)sender)/(60000/getConfig().getInt("options.tickDelay"));
			args1[0] = Integer.toString(timeleft);
			Messaging.send(sender, Phrase.TIMED_PROMOTION_ENABLED.parse());
			Messaging.send(sender, Phrase.TIME_LEFT.parse(args1));
			return true;
		}
		if(args.length != 1)
		{
			Messaging.send(sender, Phrase.USAGE.parse());
			return true;
		}
		
		if(!(sender instanceof Player))
		{
			Messaging.send(sender, Phrase.THIS_IS_NOT_A_CONSOLE_COMMAND.parse());
			return true;
		}
		
		if(permissionsHandler.has(sender, "ThumbsApply.NotGuest"))
		{
			Messaging.send(sender, Phrase.ALREADY_PROMOTED.parse());
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
	
	public void reloadLocalizationConfig() {
	    if (localizationFile == null) {
	    localizationFile = new File(localizationDir + File.separator + "localization.yml");
	    }
	    localizationConfig = YamlConfiguration.loadConfiguration(localizationFile);
	 
	    // Look for defaults in the jar
	    String main = "messages.";
	    localizationConfig.addDefault(main + "SUCCESS", "You were promoted successfully.");
	    localizationConfig.addDefault(main + "GUEST_CHAT", "You can't chat as a guest.");
	    localizationConfig.addDefault(main + "JOIN_MESSAGE_PASSWORD", "Hello, $0. Please apply for user rank by using /apply password.");
	    localizationConfig.addDefault(main + "JOIN_MESSAGE_TIME", "Hello, $0. You will be promoted to user rank in $1 minutes.");
	    localizationConfig.addDefault(main + "WRONG_PASSWORD", "You entered the wrong password!");
	    localizationConfig.addDefault(main + "USAGE", "Usage: /apply password");
	    localizationConfig.addDefault(main + "ALREADY_PROMOTED", "You are already promoted!");
	    localizationConfig.addDefault(main + "THIS_IS_NOT_A_CONSOLE_COMMAND", "You must be a player to use that command.");
	    localizationConfig.addDefault(main + "TIMED_PROMOTION_ENABLED", "You server admin has enabled timed promotion.");
	    localizationConfig.addDefault(main + "TIME_LEFT", "You have $0 minutes left until you get promoted.");
	    localizationConfig.options().copyDefaults(true);
	    try {
			localizationConfig.save(localizationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getLocalizationConfig() {
	    if (localizationConfig == null) {
	        reloadLocalizationConfig();
	    }
	    return localizationConfig;
	}
	
	public void saveLocalizationConfig() {
	    if (localizationConfig == null || localizationFile == null) {
	    return;
	    }
	    try {
	        localizationConfig.save(localizationFile);
	    } catch (IOException ex) {
	        Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + localizationFile, ex);
	    }
	}
	
	public void registerHooks()
	{
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Low, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Low, this);
	}
}
