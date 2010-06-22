package org.nn.remodroid.messages;

public class ExitEvent implements RemoteMessage {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isExitMessage() {
		return true;
	}
}
