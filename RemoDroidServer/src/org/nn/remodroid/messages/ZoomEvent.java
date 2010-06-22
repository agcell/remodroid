package org.nn.remodroid.messages;

public class ZoomEvent extends AbstractRemoteMessage {

	private static final long serialVersionUID = 1L;

	private final int scale;
	
	public ZoomEvent(int scale) {
		this.scale = scale;
	}
	
	public int getScale() {
		return scale;
	}
}
