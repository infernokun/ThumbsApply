// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 11.09.2011 21:55:06
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ThumbsApply.java

package me.Todkommt.ThumbsApply;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

import me.Todkommt.ThumbsApply.Interpreters.PermissionsBukkitInterpreter;
import me.Todkommt.ThumbsApply.Interpreters.PermissionsExInterpreter;
import me.Todkommt.ThumbsApply.Interpreters.PermissionsInterpreter;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

// Referenced classes of package me.todkommt.thumbsapply:
//            ThumbsApplyPlayerListener

@SuppressWarnings("deprecation")
public class ThumbsApply extends JavaPlugin
{

    public ThumbsApply()
    {
        playerListener = null;
        log = Logger.getLogger("Minecraft");
    }
    
    public static boolean getPermissionNode(Player player, String node)
    {
        if(server.getPluginManager().isPluginEnabled("Permissions") && server.getPluginManager().isPluginEnabled("PermissionsPlus"))
        {
        	return PermissionsInterpreter.getPermissionNode(player, node);
        }
        if(server.getPluginManager().isPluginEnabled("PermissionsEx"))
        {
        	return PermissionsExInterpreter.getPermissionNode(player, node);
        }
        if(server.getPluginManager().isPluginEnabled("PermissionsBukkit"))
        {
        	return PermissionsBukkitInterpreter.getPermissionNode(player, node);
        }
        return false;
    }
    
    public void onDisable()
    {
        log.info("ThumbsApply disabled.");
    }

    public void onEnable()
    {
    	server = this.getServer();
    	playerListener = new ThumbsApplyPlayerListener();
        registerHooks();
        (new File(mainDir)).mkdir();
    	if(!playtimefile.exists())
    	{
    		try
    		{
    			playtimefile.createNewFile();
    		}
    		catch(IOException e)
    		{
    			e.printStackTrace();
    		}
    		
    	}
    	else
    	{
    		loadPlayTime();
    	}
		PlaytimeCounter time = new PlaytimeCounter(); 
		Thread timethread = new Thread(time);
    	if(applytype.equalsIgnoreCase("time") && (timethread.isAlive() == false))
    	{
    		timethread.start(); 
    	}
        if(!thumbsapply.exists())
            try
            {
                String pw = "variable.password";
                String grpnmt = "variable.groupnametarget";
                String logmsgen = "option.loginmessagenabled";
                String chtblken = "option.chatblockenabled";
                thumbsapply.createNewFile();
                config.setProperty(pw, password);
                config.setProperty(grpnmt, groupnametarget);
                config.setProperty(logmsgen, loginmessageenabled);
                config.setProperty(chtblken, chatblockenabled);
                config.setProperty("variable.command", commandconf);
                config.setProperty("variable2.password", password2);
                config.setProperty("variable2.groupnametarget", groupnametarget2);
                config.setProperty("option.doublegroupsenabled", doublegroup);
                config.setProperty("option.applytype", applytype);
                config.setProperty("variable.timetopromote", timetopromote);
                config.save();
                System.out.println("[ThumbsApply] => File created!");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        else
        {
        	loadFile();
        }
        (new File(localizationDir)).mkdir();
        if(!localization.exists())
        {
        	try
        	{
        		String root = "localization";
        		localization.createNewFile();
        		locconf.setProperty(root + ".wrongsender", wrongsender);
        		locconf.setProperty(root + ".usage", usage);
        		locconf.setProperty(root + ".success", success);
        		locconf.setProperty(root + ".wrongpassword", wrongpw);
        		locconf.setProperty(root + ".alreadyapplied", alreadyapplied);
        		locconf.setProperty(root + ".loginmessage", loginmessage);
        		locconf.setProperty(root + ".chatrestricted", chatrestricted);
        		locconf.save();
        		log.info("[ThumbsApply] => Localization Files created!");
        	}
        	catch(IOException e)
        	{
        		e.printStackTrace();
        	}
        }
        else loadLocalizationFile();
        PluginDescriptionFile pdfFile = getDescription();
        log.info((new StringBuilder("ThumbsApply v")).append(pdfFile.getVersion()).append(" enabled.").toString());
    }

    public void loadFile()
    {
        String pw = "variable.password";
        String grpnmt = "variable.groupnametarget";
        String logmsgen = "option.loginmessageenabled";
        String chtblken = "option.chatblockenabled";
        config.load();
        password = config.getString(pw, password);
        groupnametarget = config.getString(grpnmt, groupnametarget);
        loginmessageenabled = config.getString(logmsgen, loginmessageenabled);
        chatblockenabled = config.getString(chtblken, chatblockenabled);
        commandconf = config.getString("variable.command", commandconf);
        password2 = config.getString("variables2.password", password2);
        groupnametarget2 = config.getString("variables2.groupnametarget", groupnametarget2);
        doublegroup = config.getString("option.doublegroupsenabled", doublegroup);
        applytype = config.getString("option.applytype", applytype);
        timetopromote = config.getInt("variable.timetopromote", timetopromote);
    }
    
    public void loadLocalizationFile()
    {
    	String root = "localization";
    	locconf.load();
    	wrongsender = locconf.getString(root + ".wrongsender", wrongsender);
    	usage = locconf.getString(root + ".usage", usage);
    	success = locconf.getString(root + ".success", success);
    	wrongpw = locconf.getString(root + ".wrongpassword", wrongpw);
    	alreadyapplied = locconf.getString(root + ".alreadyapplied", alreadyapplied);
    	loginmessage = locconf.getString(root + ".loginmessage", loginmessage);
    	chatrestricted = locconf.getString(root + ".chatrestricted", chatrestricted);
    }
    
    @SuppressWarnings("unchecked")
	public void loadPlayTime()
    {
    	try
    	{
    		FileInputStream f = new FileInputStream(playtimefile);  
    		ObjectInputStream s = new ObjectInputStream(f);  
    		playtime = (HashMap<String, Integer>)s.readObject();         
    		s.close();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    	catch(ClassNotFoundException e)
    	{
    		e.printStackTrace();
    	}
    }
    
    public static void writePlayTime()
    {
    	try
    	{
    		FileOutputStream f = new FileOutputStream(playtimefile);  
    		ObjectOutputStream s = new ObjectOutputStream(f);          
    		s.writeObject(playtime);
    		s.flush();
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    	}
    }

    public static void promote(CommandSender console, String name, int count)
    {
    	 if(server.getPluginManager().isPluginEnabled("Permissions") && server.getPluginManager().isPluginEnabled("PermissionsPlus"))
         {
         	PermissionsInterpreter.promote(console, name, count);
         }
         if(server.getPluginManager().isPluginEnabled("PermissionsEx"))
         {
        	 PermissionsExInterpreter.promote(console, name, count);
         }
         if(server.getPluginManager().isPluginEnabled("PermissionsBukkit"))
         {
        	 PermissionsBukkitInterpreter.promote(console, name, count);
         }
    }
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[])
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + wrongsender);
            return false;
        }
        command.getName();
        Player p = (Player)sender;
        if(command.getName().equalsIgnoreCase(commandconf))
        {
            p.getName();
            World w = p.getWorld();
            w.getName();
            Boolean inGroup = getPermissionNode(p, "thumbsapply.notguest");
            
            if(inGroup == true)
            {
                String password1 = "";
                CommandSender console = server.getConsoleSender();
                if(args.length != 1)
                {
                    p.sendMessage(ChatColor.RED + usage + commandconf + passwordmsg);
                    return false;
                }
                password1 = args[0];
                if(password1.equalsIgnoreCase(password))
                {
                    String name = p.getName();
                    promote(console, name, 1);
                    p.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append(success).toString());
                    return true;
                }
                if(password1.equalsIgnoreCase(password2) && doublegroup.equalsIgnoreCase("true"))
                {
                    String name = p.getName();
                    promote(console, name, 2);
                    p.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append(success).toString());
                    return true;
                }
                else
                {
                    p.sendMessage((new StringBuilder()).append(ChatColor.RED).append(wrongpw).toString());
                    return false;
                }
            }
            else
            {
                p.sendMessage(ChatColor.RED + alreadyapplied);
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public void registerHooks()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Priority.High, this);
    }
    
    private ThumbsApplyPlayerListener playerListener;
    static Logger log;
    static String mainDir;
    static String localizationDir;
    static File thumbsapply;
    static File localization;
    static Configuration config;
    static Configuration localizationconf;
    String password = "standard";
    public static String groupnametarget = "Builder";
    public static String loginmessageenabled = "true";
    public static String chatblockenabled = "false";
    public static int PermissionsPlugin;
    public static Boolean returned;
    public static String commandconf = "apply";
    public static String wrongsender = "This command must be accessed by a player!";
    public static String usage = "Wrong usage. Right: /";
    public static String success = "Success! You are now promoted.";
    public static String wrongpw = "Wrong password!";
    public static String alreadyapplied = "You have already been promoted!";
    public static String loginmessage = "Hello, Guest. Please unlock yourself by typing /";
    public static String passwordmsg = " <password>.";
    public static String chatrestricted = "You can't write as a guest. Please unlock yourself with /";
    public static String password2 = "default";
    public static String groupnametarget2 = "admin";
    public static String doublegroup = "false";
    public static Configuration locconf;
    public static HashMap<String, Integer> playtime = new HashMap<String, Integer>();
    public static Server server;
    public static File playtimefile;
    public static String applytype = "time";
    public static int timetopromote = 30;
    
    static 
    {
        mainDir = "plugins/ThumbsApply";
        localizationDir = mainDir + "/localization";
        thumbsapply = new File((new StringBuilder(String.valueOf(mainDir))).append(File.separator).append("ThumbsApply.yml").toString());
        config = new Configuration(thumbsapply);
        localization = new File(localizationDir + File.separator + "localization.yml");
        locconf = new Configuration(localization);
        playtimefile = new File(mainDir + File.separator + "playtime.dat");
    }
}
