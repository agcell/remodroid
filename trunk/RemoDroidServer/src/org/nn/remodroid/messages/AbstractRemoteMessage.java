package org.nn.remodroid.messages;

public abstract class AbstractRemoteMessage implements RemoteMessage {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean isExitMessage() {
		return false;
	}
}
