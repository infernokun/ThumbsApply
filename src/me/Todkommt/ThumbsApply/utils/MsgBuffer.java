package me.Todkommt.ThumbsApply.utils;

public class MsgBuffer {

	public String msg;
	public int priority;
	public ThumbsApplyModule module;
	
	public MsgBuffer(String msg, int priority, ThumbsApplyModule module)
	{
		this.msg = msg;
		this.priority = priority;
		this.module = module;
	}
}
