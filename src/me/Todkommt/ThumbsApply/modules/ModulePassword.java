package me.Todkommt.ThumbsApply.modules;

import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerChatEvent;

import me.Todkommt.ThumbsApply.Phrase;
import me.Todkommt.ThumbsApply.utils.Replacement;
import me.Todkommt.ThumbsApply.utils.ThumbsApplyModule;

public class ModulePassword extends ThumbsApplyModule {
	
	public String password;
	
	public void onLoad(String value)
	{
		this.value = value;
		this.password = value;
		this.module = this;
		registerReplacement("\\{password\\}", new Replacement() {
			
			public String replace(CommandSender sender) {
				return password;
			}
		});
		registerLocalization("PASSWORD_SHARING", "You are not allowed to share the password!");
		registerConfigOption("blockPasswordInChat", true);
	}
	
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if(args.length != 1)
		{
			sendMessage(sender, Phrase.USAGE.parse(), 10);
			return false;
		}
		
		if(inGroup(sender))
		{
			sendMessage(sender, Phrase.ALREADY_PROMOTED.parse(), 0);
			return false;
		}
		
		if(!args[0].equalsIgnoreCase(password))
		{
			sendMessage(sender, Phrase.WRONG_PASSWORD.parse(), 100);
			return false;
		}
		
		promotePlayer(sender);
		fileLog(sender.getName() + " - " + this.getClass().getSimpleName());
		sendMessage(sender, Phrase.SUCCESS.parse(), 1000);
		return true;
	}
	
	public void onPlayerChat(PlayerChatEvent event)
	{
		if(!getConfig().getBoolean("options.blockPasswordInChat"))
			return;
		if(event.getMessage().contains(password))
		{
			sendMessage(event.getPlayer(), localize("PASSWORD_SHARING"), 1000);
			event.setCancelled(true);
		}
	}
}
