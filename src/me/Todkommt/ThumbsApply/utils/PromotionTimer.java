package me.Todkommt.ThumbsApply.utils;

import me.Todkommt.ThumbsApply.ThumbsApply;

public class PromotionTimer implements Runnable {

	private ThumbsApply plugin;
	
	public PromotionTimer(ThumbsApply plugin)
	{
		this.plugin = plugin;
	}
	
	public void run() {
		plugin.update();
	}
}
