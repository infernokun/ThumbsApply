// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 11.09.2011 21:55:06
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ThumbsApply.java

package me.Todkommt.ThumbsApply;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.*;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

// Referenced classes of package me.todkommt.thumbsapply:
//            ThumbsApplyPlayerListener

public class ThumbsApply extends JavaPlugin
{

    public ThumbsApply()
    {
        playerListener = null;
        log = Logger.getLogger("Minecraft");
    }

    private void setupPermissions()
    {
        if(permissionHandler != null)
            return;
        Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
        Plugin permissionsPlusPlugin = getServer().getPluginManager().getPlugin("PermissionsPlus");
        if(permissionsPlugin == null)
        {
            log.info("Permissions not found. Please install it.");
            return;
        }
        if(permissionsPlusPlugin == null)
        {
            log.info("PermissionsPlus not found. Please install it.");
            return;
        } else
        {
            permissionHandler = ((Permissions)permissionsPlugin).getHandler();
            log.info("Permissions and PermissionsPlus found. ThumbsApply should work.");
            return;
        }
    }

    public void onDisable()
    {
        log.info("ThumbsApply disabled.");
    }

    public void onEnable()
    {
    	
    	playerListener = new ThumbsApplyPlayerListener();
        new ColouredConsoleSender((CraftServer)getServer());
        registerHooks();
        (new File(mainDir)).mkdir();
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
                config.save();
                System.out.println("[ThumbsApply] => File created!");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        else {
        	loadFile();
        }
        setupPermissions();
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
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String args[])
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "This command must be accessed by a player!");
            return false;
        }
        command.getName();
        Player p = (Player)sender;
        if(command.getName().equalsIgnoreCase("apply"))
        {
            p.getName();
            World w = p.getWorld();
            w.getName();
            Boolean inGroup = inGroup(p);
            
            if(inGroup == true)
            {
                String password1 = "";
                ColouredConsoleSender console = new ColouredConsoleSender((CraftServer)getServer());
                if(args.length != 1)
                {
                    p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Wrong usage. Right: /apply <password>").toString());
                    return false;
                }
                password1 = args[0];
                if(password1.equalsIgnoreCase(password))
                {
                    Object name = p.getName();
                    getServer().dispatchCommand(console, (new StringBuilder()).append("groupset ").append(name).append((new StringBuilder(" ")).append(groupnametarget).toString()).append(" *").toString());
                    p.sendMessage((new StringBuilder()).append(ChatColor.GREEN).append("Success! You are now promoted.").toString());
                    return true;
                } else
                {
                    p.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Wrong password!").toString());
                    return false;
                }
            } else
            {
                p.sendMessage(ChatColor.RED + "You are already promoted.");
                return false;
            }
        } else
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

    public static boolean inGroup(Player player) {
		boolean group = permissionHandler.has(player, "thumbsapply.notguest");
        if(group == false) {
        	return true;
        }
        else return false;
    }
    
    private ThumbsApplyPlayerListener playerListener;
    public static PermissionHandler permissionHandler;
    static Logger log;
    static String mainDir;
    static File thumbsapply;
    static Configuration config;
    String password = "standard";
    String groupnametarget = "Builder";
    public static String loginmessageenabled = "true";
    public static String chatblockenabled = "false";
    public static int PermissionsPlugin;
    public static Boolean returned;

    public static Configuration local;
    
    static 
    {
        mainDir = "plugins/ThumbsApply";
        thumbsapply = new File((new StringBuilder(String.valueOf(mainDir))).append(File.separator).append("ThumbsApply.yml").toString());
        config = new Configuration(thumbsapply);
    }
}
