package org.nn.remodroid.messages;

public class MouseMoveEvent extends AbstractRemoteMessage {

	private static final long serialVersionUID = 1L;

	private int xOffset;
	private int yOffset;
	
	public MouseMoveEvent(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public int getXOffset() {
		return xOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
}
