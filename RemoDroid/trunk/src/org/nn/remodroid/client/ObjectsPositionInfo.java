package org.nn.remodroid.client;

import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class ObjectsPositionInfo {

	protected static final int SOFFSET = 30;
	
	private Rect left = null;
	private Rect right = null;
	private Rect keyboard = null;
	private Rect vScrollbar = null;
	private Rect hScrollbar = null;
	private Bitmap button = null;
	
	protected ObjectsPositionInfo() {
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

	public Bitmap getButton() {
		return button;
	}
	
	protected void setLeft(Rect left) {
		this.left = left;
	}

	protected void setRight(Rect right) {
		this.right = right;
	}

	protected void setKeyboard(Rect keyboard) {
		this.keyboard = keyboard;
	}

	protected void setVScrollbar(Rect vScrollbar) {
		this.vScrollbar = vScrollbar;
	}

	protected void setHScrollbar(Rect hScrollbar) {
		this.hScrollbar = hScrollbar;
	}
	
	protected void setButton(Bitmap button) {
		this.button = button;
	}
}
