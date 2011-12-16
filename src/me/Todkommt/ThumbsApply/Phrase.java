package me.Todkommt.ThumbsApply;

public enum Phrase {
	SUCCESS("You were promoted successfully."),
	GUEST_CHAT("You can't chat as a guest."),
	JOIN_MESSAGE("Hello, $0. Please apply for user rank by using /$1 password."),
	WRONG_PASSWORD("You entered the wrong password!"),
	USAGE("Usage: /apply password"),
	ALREADY_PROMOTED("You are already promoted!"),
	NULL_COMMAND("That command doesn't exist!"),
	THIS_IS_NOT_A_CONSOLE_COMMAND("You must be a player to use that command."),
	HELP_PAGE("Commands: (Page $0)");
	
	private String message;
	
	private Phrase(String message){
		this.message = message;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	private String getMessage(){
		return message;
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
