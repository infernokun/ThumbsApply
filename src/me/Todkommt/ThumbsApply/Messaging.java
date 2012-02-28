package me.Todkommt.ThumbsApply;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
/*		if(ThumbsApply.timeToPromote.containsKey((OfflinePlayer)sender))
		{
			int time = 0;
			String group = "";
			Iterator<Entry<String, Integer>> it = ThumbsApply.timeToPromote.get((OfflinePlayer)sender).entrySet().iterator();
			while(it.hasNext())
			{
				Entry<String, Integer> entry = it.next();
				if(entry.getValue() < time || time == 0)
				{
					time = entry.getValue();
					group = entry.getKey();
				}
			}
			message = message.replaceAll("\\{timeleft\\}", Integer.toString(time*(60000/plugin.delay)));
			message = message.replaceAll("\\{group\\}", group);
		} */
		message = colorize(message);
		sender.sendMessage(color + message);
	}
	
	public static String colorize(String s){
        if(s == null) return null;
        return s.replaceAll("&([0-9a-f])", "\u00A7$1");
    }
}
