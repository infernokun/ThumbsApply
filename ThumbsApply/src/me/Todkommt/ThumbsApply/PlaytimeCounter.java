package me.Todkommt.ThumbsApply;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaytimeCounter implements Runnable{

	public void run() {
		ThumbsApply.log.info("Tick thread executing.");
		for(int i=0; 1<2; i++)
		{
			ThumbsApply.log.info("Tick Nr. " + (i+1));
			thread(ThumbsApply.server);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void thread(Server server) {
        CommandSender console = ThumbsApply.server.getConsoleSender();
		Player[] onlineplayers = server.getOnlinePlayers();
		for(int i=0; i<onlineplayers.length; i++)
		{
			String playername = onlineplayers[i].getName();
			if(!playername.equalsIgnoreCase("default"))
			{
			if(ThumbsApply.getPermissionNode(onlineplayers[i], "thumbsapply.notguest") == false)
			{
				if(ThumbsApply.playtime.get(playername) == null)
				{
					ThumbsApply.playtime.put(playername, 1);
				}
				else
				{
					String time = ThumbsApply.playtime.get(playername).toString();
					int time1 = Integer.parseInt(time);
					if( time1 >= ThumbsApply.timetopromote )
					{
						ThumbsApply.server.dispatchCommand(console, "permissions player setgroup " + playername + " " + ThumbsApply.groupnametarget);
						ThumbsApply.playtime.remove(playername);
					}
					ThumbsApply.playtime.put(playername, time1 + 1);
				}
			}
			else
			{
				if(ThumbsApply.playtime.get(playername) != null)
				{
					ThumbsApply.playtime.remove(playername);
				}
			}
		}
		ThumbsApply.writePlayTime();
		}
	}

}
