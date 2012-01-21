package me.Todkommt.ThumbsApply;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messaging {

private static ChatColor color = ChatColor.YELLOW;
	
	public static ThumbsApply plugin;
	
	public static void setPlugin(ThumbsApply plugin)
	{
		Messaging.plugin = plugin;
	}

	public static void setColor(ChatColor configColor){
		color = configColor;
	}
	
	public static void sendWithPrefix(CommandSender sender, String message, String... params){
		send(sender, "[nSpleef] " + message, params);
	}
	
	public static void send(CommandSender sender, String message, String... params){
		if (params != null){
			for (int i = 0; i < params.length; i++){
				message = message.replace("$" + (i + 1), params[i]);
			}
		}
		
		message = message.replaceAll("\\{player\\}", sender.getName());
		if(ThumbsApply.timeToPromote.containsKey((Player)sender))
			if(ThumbsApply.timeToPromote.get((Player)sender) != null)
				message = message.replaceAll("\\{timeleft\\}", Integer.toString((ThumbsApply.timeToPromote.get((Player)sender)/(60000/plugin.getConfig().getInt("options.tickDelay")))));
		message = colorize(message);
		sender.sendMessage(color + message);
	}
	
	public static String colorize(String s){
        if(s == null) return null;
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
}
