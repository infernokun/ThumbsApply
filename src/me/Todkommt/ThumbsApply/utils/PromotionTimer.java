package me.Todkommt.ThumbsApply.utils;

import me.Todkommt.ThumbsApply.ThumbsApply;

public class PromotionTimer implements Runnable {

	private ThumbsApply plugin;
	
	public PromotionTimer(ThumbsApply plugin)
	{
		this.plugin = plugin;
	}
	
	private boolean threadDone = false;

    public void done() {
        threadDone = true;
    }
	
	public void run() {
		while(!threadDone)
		{
			plugin.update();
			try {
				Thread.sleep(plugin.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
