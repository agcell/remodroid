package org.nn.remodroid.client;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ScrollbarBitmaps {

	private Bitmap top;
	private Bitmap bottom;
	
	private Paint backgroundPaint = new Paint();
	private Paint borderPaint = new Paint();
	
	private final int borderSize;
	private final int buttonSize;
	
	public ScrollbarBitmaps(Resources resources, int resId, int backgroundColorId, int borderColorId, int borderSize, 
			int buttonSize) {
		
		top = BitmapFactory.decodeResource(resources, resId);
		bottom = RoundRectBitmaps.rotateBitmap(top, 180);
		
		backgroundPaint.setColor(resources.getColor(backgroundColorId));
		borderPaint.setColor(resources.getColor(borderColorId));
		
		this.borderSize = resources.getDimensionPixelSize(borderSize);
		this.buttonSize = resources.getDimensionPixelSize(buttonSize);
	}
	
	public void draw(Canvas canvas, float x, float y, int width, int height) {
		canvas.save();
		canvas.translate(x, y);

		canvas.drawBitmap(top, 0, 0, null);
		canvas.drawBitmap(bottom, 0, height - bottom.getHeight(), null);

		float startY = top.getHeight() + buttonSize;
		float stopY = height - bottom.getHeight() - buttonSize;

		canvas.drawRect(0, top.getHeight(), width, top.getHeight() + buttonSize, borderPaint);
		canvas.drawRect(0, stopY, width, stopY + buttonSize, borderPaint);
				
		canvas.drawRect(0, startY, borderSize, stopY, borderPaint);
		canvas.drawRect(width - borderSize, startY, width, stopY, borderPaint);
		//canvas.drawRect(borderSize, startY, width - borderSize, stopY, backgroundPaint);
		
		canvas.restore();
	}

	public int getWidth() {
		return top.getWidth();
	}
}
