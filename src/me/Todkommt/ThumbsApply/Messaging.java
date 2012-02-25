package me.Todkommt.ThumbsApply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.Todkommt.ThumbsApply.utils.MsgBuffer;
import me.Todkommt.ThumbsApply.utils.Replacement;
import me.Todkommt.ThumbsApply.utils.ThumbsApplyModule;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messaging {

private static ChatColor color = ChatColor.YELLOW;
	
	public static ThumbsApply plugin;
	public static HashMap<ThumbsApplyModule, HashMap<String, Replacement>> replacements = new HashMap<ThumbsApplyModule, HashMap<String, Replacement>>();
	public static List<String> joinMessages = new ArrayList<String>();
	public static HashMap<CommandSender, MsgBuffer> msgBuffer = new HashMap<CommandSender, MsgBuffer>();
	
	public static void sendMsgBuffer()
	{
		for(Entry<CommandSender, MsgBuffer> entry : msgBuffer.entrySet())
		{
			MsgBuffer buffer = entry.getValue();
			CommandSender sender = entry.getKey();
			send(sender, buffer.msg, buffer.module);
		}
		msgBuffer.clear();
	}
	
	public static void setPlugin(ThumbsApply plugin)
	{
		Messaging.plugin = plugin;
	}

	public static void setColor(ChatColor configColor){
		color = configColor;
	}
	
	public static void sendWithPrefix(CommandSender sender, String message, ThumbsApplyModule... params){
		send(sender, "[ThumbsApply] " + message, params);
	}
	
	public static void send(CommandSender sender, String message, ThumbsApplyModule... params){
		
		message = message.replaceAll("\\{player\\}", sender.getName());
		if(params.length == 1)
		{
			ThumbsApplyModule module = params[0];
			if(module != null)
			{
				message = message.replaceAll("\\{group\\}", module.group);
				message = message.replaceAll("\\{world\\}", module.world);
				if(replacements.containsKey(module))
				{
					for(Entry<String, Replacement> entry : replacements.get(module).entrySet())
					{
						String key = entry.getKey();
						Replacement method = entry.getValue();
						message = message.replaceAll(key, (String)method.replace(sender));
					}
				}
			}
		}
		message = colorize(message);
		sender.sendMessage(message);
	}
	
	public static String localize(String key)
	{
		if(ThumbsApply.instance.externalLocals.containsKey(key))
		{
			String msg = ThumbsApply.instance.externalLocals.get(key);
			return msg;
		}
		else return key;
	}
	
	public static void sendJoinMessage(CommandSender sender)
	{
		for(String msg : joinMessages)
		{
			sender.sendMessage(color + msg);
		}
	}
	
	public static String colorize(String s){
        if(s == null) return null;
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
}
