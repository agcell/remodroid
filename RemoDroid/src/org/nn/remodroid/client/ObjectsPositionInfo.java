package org.nn.remodroid.client;

import android.graphics.Rect;

public class ObjectsPositionInfo {

	protected static final int SOFFSET = 30;
	
	private final int buttonHeight;
	private final int buttonOffset;
	private final int keyboardWidth;
	private final int keyboardHeight;
	
	private Rect left = new Rect();
	private Rect right = new Rect();
	private Rect keyboard = new Rect();
	private Rect vScrollbar = new Rect();
	private Rect hScrollbar = new Rect();
	
	public ObjectsPositionInfo(int buttonHeight, int buttonOffset, int keyboardWidth, int keyboardHeight) {
		this.buttonHeight = buttonHeight;
		this.buttonOffset = buttonOffset;
		this.keyboardWidth = keyboardWidth;
		this.keyboardHeight = keyboardHeight;
	}
	
	public Rect getLeft() {
		return left;
	}
	
	public Rect getRight() {
		return right;
	}
	
	public Rect getKeyboard() {
		return keyboard;
	}
	
	public Rect getVScrollbar() {
		return vScrollbar;
	}
	
	public Rect getHScrollbar() {
		return hScrollbar;
	}
	
	public void update(int width, int height) {
		int buttonWidth = (width - keyboardWidth - 3 * buttonOffset) / 2;
		
		int x = buttonOffset;
		int y = height - buttonHeight - buttonOffset;
		left.set(x, y, x + buttonWidth, y + buttonHeight);
		
		x = width - buttonWidth - buttonOffset;
		right.set(x, y, x + buttonWidth, y + buttonHeight);
		
		y = y + (buttonHeight - keyboardHeight) / 2;
		x = (width - keyboardWidth) / 2;
		keyboard.set(x, y, x + keyboardWidth, y + keyboardHeight);
	}
}
