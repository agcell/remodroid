package org.nn.remodroid.client;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class HorizontalPositionInfo extends ObjectsPositionInfo {

	public HorizontalPositionInfo(Bitmap button) {
		setLeft(new Rect(10, 380, 348, 468));
		setRight(new Rect(452, 380, 790, 468));
		setKeyboard(new Rect(366, 390, 438, 455));
		setVScrollbar(new Rect(747 - SOFFSET, 40 - SOFFSET, 757 + SOFFSET, 304 + SOFFSET));
		setHScrollbar(new Rect(40 - SOFFSET, 340 - SOFFSET, 700 + SOFFSET, 350 + SOFFSET));
		setButton(button);
	}
}
