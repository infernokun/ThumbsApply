package me.Todkommt.ThumbsApply;

public enum Phrase {
	SUCCESS("SUCCESS"),
	GUEST_CHAT("GUEST_CHAT"),
	JOIN_MESSAGE_PASSWORD("JOIN_MESSAGE_PASSWORD"),
	WRONG_PASSWORD("WRONG_PASSWORD"),
	USAGE("USAGE"),
	ALREADY_PROMOTED("ALREADY_PROMOTED"),
	NULL_COMMAND("NULL_COMMAND"),
	UNKNOWN_ERROR("UNKNOWN_ERROR"),
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
		return ThumbsApply.instance.getLocalizationConfig().getString(confRoot + message);
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
