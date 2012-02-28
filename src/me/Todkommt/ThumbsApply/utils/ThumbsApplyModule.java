package me.Todkommt.ThumbsApply.utils;

import java.util.HashMap;

import me.Todkommt.ThumbsApply.Messaging;
import me.Todkommt.ThumbsApply.ThumbsApply;
import me.Todkommt.ThumbsApply.permissions.PermissionsHandler;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ThumbsApplyModule {

	public char prefix;
	public String world;
	public String group;
	public String value;
	public ThumbsApplyModule module;
	
	public void log(String message)
	{
		ThumbsApply.instance.log.info("[ThumbsApply] " + message);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		
	}
	
	public void onPlayerChat(PlayerChatEvent event)
	{
		
	}
	
	public FileConfiguration getConfig()
	{
		return ThumbsApply.instance.getConfig();
	}
	
	public void registerConfigOption(String key, Object b)
	{
		ThumbsApply.instance.getConfig().addDefault("options." + key, b);
		ThumbsApply.instance.getConfig().options().copyDefaults(true);
		ThumbsApply.instance.saveConfig();
	}
	
	public void sendMessage(CommandSender sender, String message, int priority)
	{
		if(Messaging.msgBuffer.containsKey(sender))
		{
			MsgBuffer buffer = Messaging.msgBuffer.get(sender);
			if(buffer.priority <= priority)
			{
				buffer.priority = priority;
				buffer.msg = message;
				buffer.module = module;
			}
			Messaging.msgBuffer.put(sender, buffer);
		}
		else Messaging.msgBuffer.put(sender, new MsgBuffer(message, priority, module));
	}
	
	public void sendLocalizedMessage(CommandSender sender, String key, int priority)
	{
		String message = Messaging.localize(key);
		if(Messaging.msgBuffer.containsKey(sender))
		{
			MsgBuffer buffer = Messaging.msgBuffer.get(sender);
			if(buffer.priority <= priority)
			{
				buffer.priority = priority;
				buffer.msg = message;
				buffer.module = module;
			}
			Messaging.msgBuffer.put(sender, buffer);
		}
		else Messaging.msgBuffer.put(sender, new MsgBuffer(message, priority, module));
	}
	
	public String localize(String key)
	{
		return Messaging.localize(key);
	}
	
	public void onLoad(String value)
	{
		this.value = value;
	}
	
	public void onUpdate(int elapsedTime)
	{
		
	}
	
	public boolean onCommand(CommandSender sender, String[] args)
	{
		return false;
	}
	
	public void promotePlayer(CommandSender player)
	{
		PermissionsHandler permHandle = ThumbsApply.instance.permissionsHandler;
		permHandle.setGroup(player, group, world);
	}
	
	public boolean inGroup(CommandSender player)
	{
		PermissionsHandler permHandle = ThumbsApply.instance.permissionsHandler;
		return permHandle.has(player, "group." + group, world);
	}
	
	public void registerAsListener(Listener module)
	{
		ThumbsApply.instance.getServer().getPluginManager().registerEvents(module, ThumbsApply.instance);
	}
	
	public void registerLocalization(String name, String text)
	{
		ThumbsApply.instance.externalLocals.put(name, text);
	}
	
	public void registerReplacement(String variable, Replacement method)
	{
		if(Messaging.replacements.containsKey(module))
		{
			HashMap<String, Replacement> replacements = Messaging.replacements.get(module);
			replacements.put(variable, method);
			Messaging.replacements.put(module, replacements);
		}
		else
		{
			HashMap<String, Replacement> replacements = new HashMap<String, Replacement>();
			replacements.put(variable, method);
			Messaging.replacements.put(module, replacements);
		}
	}
	
	public void registerJoinMessage(String message, boolean isOverride)
	{
		if(isOverride)
		{
			Messaging.joinMessages.clear();
			Messaging.joinMessages.add(new JoinMessage(message, module));
		}
		else Messaging.joinMessages.add(new JoinMessage(message, module));
	}
	
	public String getLocal(String key)
	{
		return ThumbsApply.instance.externalLocals.get(key);
	}
	
	public Server getServer()
	{
		return ThumbsApply.instance.getServer();
	}
	
	public void fileLog(String key)
	{
		ThumbsApply.instance.appendToLog(key);
	}
}