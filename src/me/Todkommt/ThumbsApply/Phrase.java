package me.Todkommt.ThumbsApply;

public enum Phrase {
	SUCCESS("SUCCESS"),
	TIMED_PROMOTION_ENABLED("TIMED_PROMOTION_ENABLED"),
	TIME_LEFT("TIME_LEFT"),
	GUEST_CHAT("GUEST_CHAT"),
	JOIN_MESSAGE_TIME("JOIN_MESSAGE_TIME"),
	JOIN_MESSAGE_PASSWORD("JOIN_MESSAGE_PASSWORD"),
	WRONG_PASSWORD("WRONG_PASSWORD"),
	USAGE("USAGE"),
	ALREADY_PROMOTED("ALREADY_PROMOTED"),
	THIS_IS_NOT_A_CONSOLE_COMMAND("THIS_IS_NOT_A_CONSOLE_COMMAND");

	public String confRoot = "messages.";
	
	private String message;
	
	private Phrase(String message){
		this.message = message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	private String getMessage(){
		return ThumbsApply.plugin.getLocalizationConfig().getString(confRoot + message);
	}
	
	public String parse(String... params){
		String parsedMessage = getMessage();
		
		if (params != null){
			for (int i = 0; i < params.length; i++){
				parsedMessage = parsedMessage.replace("$" + i, params[i]);
			}
		}
		
		return parsedMessage;
	}
}
