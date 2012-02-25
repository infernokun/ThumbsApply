package me.Todkommt.ThumbsApply;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.Todkommt.ThumbsApply.listeners.ThumbsApplyPlayerListener;
import me.Todkommt.ThumbsApply.modules.ModulePassword;
import me.Todkommt.ThumbsApply.permissions.GroupManagerHandler;
import me.Todkommt.ThumbsApply.permissions.PEX;
import me.Todkommt.ThumbsApply.permissions.Permissions3;
import me.Todkommt.ThumbsApply.permissions.PermissionsBukkit;
import me.Todkommt.ThumbsApply.permissions.PermissionsHandler;
import me.Todkommt.ThumbsApply.permissions.bPermissions;
import me.Todkommt.ThumbsApply.utils.TAClassHandler;
import me.Todkommt.ThumbsApply.utils.ThumbsApplyModule;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ThumbsApply extends JavaPlugin {

	public static ThumbsApply instance;
	public Logger log;
	public FileConfiguration localizationConfig;
	public PermissionsHandler permissionsHandler;
	public File mainDir = new File("plugins/ThumbsApply");
	public File localizationDir = new File(mainDir + "/localization");
	public File localizationFile = new File(localizationDir + "/local.yml");
	public File moduleDir = new File(mainDir + "/modules");
	public File logFile = new File(mainDir + "/log.txt");
	public HashMap<String, String> externalLocals = new HashMap<String, String>();
	public List<ThumbsApplyGroup> groups = new ArrayList<ThumbsApplyGroup>();
	public List<Class<?>> modules = new ArrayList<Class<?>>();
	public ThumbsApplyPlayerListener playerListener;
	
	public void onDisable() {
		
	}

	public void appendToLog(String key)
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.write(key);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void onEnable() {
		mainDir.mkdir();
		moduleDir.mkdir();
		localizationDir.mkdir();
		instance = this;
		log = Logger.getLogger("Minecraft");
		setupPermissions();
		setupDefaults();
		playerListener = new ThumbsApplyPlayerListener(this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		try {
			modules = TAClassHandler.loadClasses(moduleDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadGroups();
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				update();
			}
		}, getConfig().getInt("options.tickDelay")/50, getConfig().getInt("options.tickDelay")/50);
		reloadLocalizationConfig();
		log.info("[ThumbsApply] Loaded Localization.");
		log.info("[ThumbsApply] Started tick thread with tick delay " + getConfig().getInt("options.tickDelay") + ".");
		log.info("[ThumbsApply] Loading process completed.");
	}
	
	private void loadGroups()
	{
		Set<String> keys = null;
		try
		{
		keys = getConfig().getConfigurationSection("groups").getKeys(false);
		}
		catch(NullPointerException e)
		{
			log.info("[ThumbsApply] No groups present. Generating default groups. Please configure them correctly.");
			getConfig().set("groups.world-User", "default");
			getConfig().set("groups.world-Mod", "%1000");
			saveConfig();
			reloadConfig();
			keys = getConfig().getConfigurationSection("groups").getKeys(false);
		}
		log.info("[ThumbsApply] Groups Loaded:");
		for(String key : keys)
		{
			String value = getConfig().getString("groups." + key);
			String world = "";
			String group = key;
			ThumbsApplyModule module = null;
			if(key.contains("-"))
			{
				world = key.split("-")[0];
				group = key.split("-")[1];
			}
			char prefix = value.charAt(0);
			String newvalue = value.substring(1);
			List<ThumbsApplyModule> mods = new ArrayList<ThumbsApplyModule>();
			for(Class<?> cls : modules)
			{
				try {
					mods.add((ThumbsApplyModule)cls.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			for(ThumbsApplyModule mod : mods)
			{
				if(mod.prefix == prefix)
				{
					module = mod;
					break;
				}
			}
			if(module == null)
			{
				module = new ModulePassword();
				module.group = group;
				module.world = world;
				module.onLoad(value);
			}
			else
			{
				module.group = group;
				module.world = world;
				module.onLoad(newvalue);
			}
			
			ThumbsApplyGroup tagroup = new ThumbsApplyGroup(module, group, world);
			groups.add(tagroup);
			log.info("- " + tagroup.group +  " in world " + tagroup.world + " with method " + tagroup.method.getClass().getSimpleName() + " and value " + tagroup.method.value);
		}
	}
	
	private void setupPermissions(){
		Plugin permissions = getServer().getPluginManager().getPlugin("Permissions");
		
		Plugin PEX = getServer().getPluginManager().getPlugin("PermissionsEx");
		
		Plugin GroupManager = getServer().getPluginManager().getPlugin("GroupManager");
		
		Plugin bPermissions = getServer().getPluginManager().getPlugin("bPermissions");
		
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
		else if(permissions != null && permissions.getDescription().getVersion().startsWith("3"))
		{
			permissionsHandler = new Permissions3(permissions, this);
			log.info("[ThumbsApply] Permissions3 System activated.");
		}
		else if(bPermissions != null)
		{
			permissionsHandler = new bPermissions(this);
			log.info("[ThumbsApply] bPermissions System activated.");
		}
		else
		{
			permissionsHandler = new PermissionsBukkit(this);
			log.info("[ThumbsApply] PermissionsBukkit System activated.");
		}
	}
	
	public void setupDefaults()
	{
		String main = "options.";
		getConfig().addDefault(main + "chatBlockEnabled", false);
		getConfig().addDefault(main + "joinMessageEnabled", true);
		getConfig().addDefault(main + "tickDelay", 60000);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(command.getName().equalsIgnoreCase("apply"))
		{
			for(ThumbsApplyGroup group : groups)
			{
				boolean res = group.method.onCommand(sender, args);
				if(res)
				{
					break;
				}
			}
			Messaging.sendMsgBuffer();
			return true;
		}
		else return false;
	}
	
	public void reloadLocalizationConfig() {
	    if (localizationFile == null) {
	    localizationFile = new File(localizationDir + File.separator + "localization.yml");
	    }
	    localizationConfig = YamlConfiguration.loadConfiguration(localizationFile);
	 
	    String main = "messages.";
	    Iterator<Entry<String, String>> iterate = externalLocals.entrySet().iterator();
	    while(iterate.hasNext())
	    {
	    	Entry<String, String> entry = iterate.next();
	    	localizationConfig.addDefault(main + entry.getKey(), entry.getValue());
	    }
	    localizationConfig.addDefault(main + "SUCCESS", "You were promoted to {group} successfully.");
	    localizationConfig.addDefault(main + "GUEST_CHAT", "You can't chat as a guest.");
	    localizationConfig.addDefault(main + "WRONG_PASSWORD", "You entered the wrong password!");
	    localizationConfig.addDefault(main + "USAGE", "Usage: /apply password");
	    localizationConfig.addDefault(main + "ALREADY_PROMOTED", "You are already promoted!");
	    localizationConfig.addDefault(main + "THIS_IS_NOT_A_CONSOLE_COMMAND", "You must be a player to use that command.");
	    localizationConfig.addDefault(main + "NULL_COMMAND", "This command is not available.");
	    localizationConfig.addDefault(main + "UNKNOWN_ERROR", "An unknown error occured.");
	    localizationConfig.options().copyDefaults(true);
	    try {
			localizationConfig.save(localizationFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    for(Entry<String, String> entry : externalLocals.entrySet())
	    {
	    	String value = localizationConfig.getString(entry.getKey());
	    	externalLocals.put(entry.getKey(), value);
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
	
	public void update()
	{
		for(ThumbsApplyGroup group : groups)
		{
			group.method.onUpdate(getConfig().getInt("options.tickDelay"));
		}
		Messaging.sendMsgBuffer();
	}
}
