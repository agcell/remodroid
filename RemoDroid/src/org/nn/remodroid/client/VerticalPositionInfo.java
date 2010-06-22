package org.nn.remodroid.client;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class VerticalPositionInfo extends ObjectsPositionInfo {

	public VerticalPositionInfo(Bitmap button) {
		setLeft(new Rect(10, 698, 190, 785));
		setRight(new Rect(290, 698, 467, 785));
		setKeyboard(new Rect(204, 710, 276, 777));
		setVScrollbar(new Rect(420 - SOFFSET, 45 - SOFFSET, 430 + SOFFSET, 595 + SOFFSET));
		setHScrollbar(new Rect(56 - SOFFSET, 644 - SOFFSET, 406 + SOFFSET, 654 + SOFFSET));
		setButton(button);
	}
}
