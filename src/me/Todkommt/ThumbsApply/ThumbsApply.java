package me.Todkommt.ThumbsApply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.Todkommt.ThumbsApply.listeners.ThumbsApplyPlayerListener;
import me.Todkommt.ThumbsApply.permissions.GroupManagerHandler;
import me.Todkommt.ThumbsApply.permissions.PEX;
import me.Todkommt.ThumbsApply.permissions.Permissions3;
import me.Todkommt.ThumbsApply.permissions.PermissionsBukkit;
import me.Todkommt.ThumbsApply.permissions.PermissionsHandler;
import me.Todkommt.ThumbsApply.permissions.bPermissions;
import me.Todkommt.ThumbsApply.utils.PromotionTimer;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ThumbsApply extends JavaPlugin {

	public static Server server;
	private PermissionsHandler permissionsHandler;
	public Logger log;
	public ThumbsApplyPlayerListener playerListener;
	public File mainDir = new File("plugins/ThumbsApply");
	public File storageDir = new File(mainDir + File.separator + "storage");
	public FileConfiguration localizationConfig = null;
	public File localizationFile = null;
	public File localizationDir = new File(mainDir + File.separator + "localization");
	public File storageFile = new File(storageDir + File.separator + "save.dat");
	public static ThumbsApply plugin;
	public PromotionTimer timer = new PromotionTimer(this);
	public int tickThread = 0;
	public static HashMap<OfflinePlayer, HashMap<String, Integer>> timeToPromote = new HashMap<OfflinePlayer, HashMap<String, Integer>>();
	public File playerSaveFile = new File("plugins/ThumbsApply/SaveData" + File.separator + "SavedPlayers.dat");
	public int delay = 60000;
	public List<ThumbsApplyGroup> groups = new ArrayList<ThumbsApplyGroup>();
	public boolean timedPromotion = false;
	public boolean debug = false;
	
	public void onDisable() {
		timedSave(true);
		log.info("[ThumbsApply] disabled.");
	}

	public void debug(String msg)
	{
		if(debug)
		{
			log.info("[DEBUG] " + msg);
		}
	}
	
	public List<ThumbsApplyGroup> getGroups()
	{
		List<ThumbsApplyGroup> groups = new ArrayList<ThumbsApplyGroup>();
		Set<String> keys = getConfig().getConfigurationSection("options.groups").getKeys(false);
		log.info("[ThumbsApply]Loaded Groups:");
		for(String key : keys)
		{
			String pw = getConfig().getString("options.groups." + key);
			int time = 0;
			boolean isTimed = false;
			String password = "";
			String group = key;
			String world = "";
			debug("get groups key length = " + key.split("-").length);
			if(key.split("-").length == 2)
			{
				world = key.split("-")[0];
				group = key.split("-")[1];
			}		
			if(pw.startsWith("%"))
			{
				isTimed = true;
				try
				{
					time = Integer.parseInt(pw.substring(1));
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			}
			else password = pw;
			ThumbsApplyGroup tagroup = new ThumbsApplyGroup(time, isTimed, password, group, world);
			groups.add(tagroup);
			if(tagroup.isTimed)
			{
				log.info("- " + tagroup.group + " in world " + tagroup.world + " with time " + tagroup.time);
			}
			else log.info("- " + tagroup.group + " in world " + tagroup.world + " with password " + tagroup.password);
		}
		return groups;
	}
	
	@SuppressWarnings("unchecked")
	public void timedSave(boolean save)
	{
		if(save)
		{
			HashMap<String, HashMap<String, Integer>> saves = new HashMap<String, HashMap<String, Integer>>();
			Iterator<Entry<OfflinePlayer, HashMap<String, Integer>>> it = timeToPromote.entrySet().iterator();
			while(it.hasNext())
			{
				Entry<OfflinePlayer, HashMap<String, Integer>> entry = it.next();
				saves.put(entry.getKey().getName(), entry.getValue());
			}
			if(!storageFile.exists())
			{
				try {
					storageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			ObjectOutputStream oos = null;
			try
			{
				oos = new ObjectOutputStream(new FileOutputStream(storageFile));
				oos.writeObject(saves);
				oos.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			if(!storageFile.exists())
			{
				return;
			}
			ObjectInputStream ois = null;
			try
			{
				ois = new ObjectInputStream(new FileInputStream(storageFile));
				HashMap<String, HashMap<String, Integer>> saves = (HashMap<String, HashMap<String, Integer>>) ois.readObject();
				Iterator<Entry<String, HashMap<String, Integer>>> it = saves.entrySet().iterator();
				while(it.hasNext())
				{
					Entry<String, HashMap<String, Integer>> entry = it.next();
					OfflinePlayer player = getServer().getPlayer(entry.getKey());
					if(player == null)
					{
						player = getServer().getOfflinePlayer(entry.getKey());
					}
					timeToPromote.put(player, entry.getValue());
				}
				ois.close();
			}
			catch(IOException e)
			{
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onEnable()
	{
		mainDir.mkdir();
		localizationDir.mkdir();
		storageDir.mkdir();
		playerListener = new ThumbsApplyPlayerListener(this);
		log = Logger.getLogger("Minecraft");
		String mainPath = "options.";
		this.getConfig().addDefault(mainPath + "debugMode", false);
		this.getConfig().addDefault(mainPath + "chatBlockEnabled", false);
		this.getConfig().addDefault(mainPath + "joinMessageEnabled", true);
		this.getConfig().addDefault(mainPath + "tickDelay", 60000);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		reloadLocalizationConfig();
		delay = this.getConfig().getInt(mainPath + "tickDelay");
		debug = this.getConfig().getBoolean(mainPath + "debugMode");
		groups = getGroups();
		for(ThumbsApplyGroup group : groups)
		{
			if(group.isTimed)
				timedPromotion = true;
		}
		if(timedPromotion)
		{
			tickThread = getServer().getScheduler().scheduleSyncRepeatingTask(this, timer, delay/50, delay/50);
			log.info("[ThumbsApply]Timer enabled and running.");
		}
		plugin = this;
		registerHooks();
		server = this.getServer();
		setupPermissions();
		timedSave(false);
		if(timeToPromote == null)
		{
			timeToPromote = new HashMap<OfflinePlayer, HashMap<String, Integer>>();
		}
		log.info("[ThumbsApply] enabled.");
	}

	public void update()
	{
		debug("update start");
		Iterator<Entry<OfflinePlayer, HashMap<String, Integer>>> iterator = timeToPromote.entrySet().iterator();
		while(iterator.hasNext())
		{
			debug("update iterator start");
			Entry<OfflinePlayer, HashMap<String, Integer>> entry = iterator.next();
			OfflinePlayer pl = entry.getKey();
			if(pl.isOnline())
			{
				debug("update player is online");
				Player player = getServer().getPlayer(pl.getName());
				HashMap<String, Integer> times = entry.getValue();
				Iterator<Entry<String, Integer>> iterator1 = times.entrySet().iterator();
				while(iterator1.hasNext())
				{
					debug("update group found");
					Entry<String, Integer> group = iterator1.next();
					int time = group.getValue()-1;
					debug("update group time = " + time);
					times.put(group.getKey(), time);
					if(time <= 0)
					{
						debug("update time < 0");
						String world = "";
						for(ThumbsApplyGroup g : groups)
						{
							if(g.group == group.getKey())
							{
								world = g.world;
							}
						}
						debug("update world = " + world);
						permissionsHandler.setGroup(player, group.getKey(), world);
						Messaging.send(player, Phrase.SUCCESS.parse());
						times.remove(group.getKey());
						if(times.isEmpty())
						{
							timeToPromote.remove(player);
						}
					}
				}
				timeToPromote.put(entry.getKey(), times);
				timedSave(true);
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		if(command.getName().equalsIgnoreCase("applyreload"))
		{
			if(!permissionsHandler.has(sender, "thumbsapply.reload", ""))
			{
				Messaging.send(sender, "You don't have permission to do that.");
				return true;
			}
			reloadConfig();
			sender.sendMessage(ChatColor.GREEN + "Successfully reloaded ThumbsApply config.");
			return true;
		}
		if(command.getName().equalsIgnoreCase("applyrestart"))
		{
			if(!permissionsHandler.has(sender, "thumbsapply.restart", ""))
			{
				Messaging.send(sender, "You don't have permission to do that.");
				return true;
			}
			if(!timedPromotion)
			{
				Messaging.send(sender, "Timed promotion not enabled.");
				return true;
			}
			if(getServer().getScheduler().isCurrentlyRunning(tickThread))
			{
				getServer().getScheduler().cancelTask(tickThread);
				Messaging.send(sender, "Thread was already running");
			}
			getServer().getScheduler().scheduleSyncRepeatingTask(this, timer, delay/50, delay/50);
			Messaging.send(sender, "Thread was restarted.");
			return true;
		}
		if(getConfig().getBoolean("options.timedPromotion"))
		{
			if(permissionsHandler.has(sender, "ThumbsApply.NotGuest", ""))
			{
				Messaging.send(sender, Phrase.ALREADY_PROMOTED.parse());
				return true;
			}
			Messaging.send(sender, Phrase.TIMED_PROMOTION_ENABLED.parse());
			Messaging.send(sender, Phrase.TIME_LEFT.parse());
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
		
		if(timedPromotion) Messaging.send(sender, Phrase.TIMED_PROMOTION_ENABLED.parse());
		for(ThumbsApplyGroup group : groups)
		{
			if(!group.isTimed)
			{
				if(group.password.equalsIgnoreCase(args[0]))
				{
					if(permissionsHandler.has(sender, "group." + group.group, group.world))
					{
						Messaging.send(sender, Phrase.ALREADY_PROMOTED.parse());
						return true;
					}
					permissionsHandler.setGroup(sender, group.group, group.world);
					Messaging.send(sender, Phrase.SUCCESS.parse());
					return true;
				}
			}
			else
			{
				if(!permissionsHandler.has(sender, "group." + group.group, group.world))
				{
					Messaging.send(sender, Phrase.TIME_LEFT.parse());
					return true;
				}
			}
		}
		
		Messaging.send(sender, Phrase.WRONG_PASSWORD.parse());		
		return true;
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
	    localizationConfig.addDefault(main + "SUCCESS", "You were promoted to {togroup} successfully.");
	    localizationConfig.addDefault(main + "GUEST_CHAT", "You can't chat as a guest.");
	    localizationConfig.addDefault(main + "JOIN_MESSAGE_PASSWORD", "Hello, {player}. Please apply for user rank by using /apply password.");
	    localizationConfig.addDefault(main + "JOIN_MESSAGE_TIME", "Hello, {player}. You will be promoted to user rank in {timeleft} minutes.");
	    localizationConfig.addDefault(main + "WRONG_PASSWORD", "You entered the wrong password!");
	    localizationConfig.addDefault(main + "USAGE", "Usage: /apply password");
	    localizationConfig.addDefault(main + "ALREADY_PROMOTED", "You are already promoted!");
	    localizationConfig.addDefault(main + "THIS_IS_NOT_A_CONSOLE_COMMAND", "You must be a player to use that command.");
	    localizationConfig.addDefault(main + "TIMED_PROMOTION_ENABLED", "Your server admin has enabled timed promotion.");
	    localizationConfig.addDefault(main + "TIME_LEFT", "You have {timeleft} minutes left until you get promoted.");
	    localizationConfig.addDefault(main + "NULL_COMMAND", "This command is not available.");
	    localizationConfig.addDefault(main + "JOIN_MESSAGE_WEB", "Hello, {player}. Please apply for user rank on the homepage of this server.");
	    localizationConfig.addDefault(main + "TIME_TO_GO_CHAT", "You have {timeleft} minutes left before you can chat.");
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
	
	public static FileConfiguration APIgetConfig()
	{
		return null;
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
		pm.registerEvents(playerListener, this);
	}
	
	public int getSmallestInt(int int1, int... ints)
	{
		int small = int1;
		for(int i : ints)
		{
			if(i>small)
			{
				small = i;
			}
		}
		return small;
	}
}
