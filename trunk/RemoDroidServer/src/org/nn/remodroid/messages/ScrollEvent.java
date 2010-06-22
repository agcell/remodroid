package org.nn.remodroid.messages;

public abstract class ScrollEvent extends AbstractRemoteMessage {

	private static final long serialVersionUID = 1L;

	private final int rate;
	
	protected ScrollEvent(int rate) {
		this.rate = rate;
	}
	
	public int getRate() {
		return rate;
	}
}
