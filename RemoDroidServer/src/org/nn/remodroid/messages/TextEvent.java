package org.nn.remodroid.messages;

public class TextEvent extends AbstractRemoteMessage {

	private static final long serialVersionUID = 1L;

	private final String text;
	
	public TextEvent(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}
