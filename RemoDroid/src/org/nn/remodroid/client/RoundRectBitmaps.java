package org.nn.remodroid.client;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class RoundRectBitmaps {

	private Bitmap leftTop;
	private Bitmap leftBottom;
	private Bitmap rightTop;
	private Bitmap rightBottom;
	
	private Paint backgroundPaint = new Paint();
	private Paint borderPaint = new Paint();
	
	private int borderSize = 0;
	
	public RoundRectBitmaps(Resources resources, int resId, int backgroundColorId, int borderColorId, int borderSize) {
		leftTop = BitmapFactory.decodeResource(resources, resId);
		rightTop = rotateBitmap(leftTop, 90);
		rightBottom = rotateBitmap(rightTop, 90);
		leftBottom = rotateBitmap(rightBottom, 90);
		
		backgroundPaint.setColor(resources.getColor(backgroundColorId));
		borderPaint.setColor(resources.getColor(borderColorId));
		
		this.borderSize = borderSize;
	}
	
	public Bitmap getLeftTop() {
		return leftTop;
	}
	
	public Bitmap getLeftBottom() {
		return leftBottom;
	}
	
	public Bitmap getRightTop() {
		return rightTop;
	}
	
	public Bitmap getRightBottom() {
		return rightBottom;
	}
	
	public void draw(Canvas canvas, Rect rect) {
		draw(canvas, rect.left, rect.top, rect.width(), rect.height());
	}
	
	public void draw(Canvas canvas, float x, float y, int width, int height) {
		canvas.save();
		canvas.translate(x, y);
		
		canvas.drawBitmap(leftTop, 0, 0, null);
		canvas.drawBitmap(rightTop, width - rightTop.getWidth(), 0, null);
		canvas.drawBitmap(leftBottom, 0, height - leftBottom.getHeight(), null);
		canvas.drawBitmap(rightBottom, width - rightBottom.getWidth(), 
				height - rightBottom.getHeight(), null);
		
		canvas.drawRect(leftTop.getWidth(), 0, 
				width - rightTop.getWidth(), borderSize, borderPaint);
		canvas.drawRect(leftTop.getWidth(), borderSize, width - rightTop.getWidth(), 
				leftTop.getHeight(), backgroundPaint);

		canvas.drawRect(leftBottom.getWidth(), height - borderSize, 
				width - rightBottom.getWidth(), height, borderPaint);
		canvas.drawRect(leftBottom.getWidth(), height - leftBottom.getHeight(), 
				width - rightBottom.getWidth(), height - borderSize, backgroundPaint);
		
		canvas.drawRect(0, leftTop.getHeight(), borderSize, 
				height - leftBottom.getHeight(), borderPaint);
		canvas.drawRect(borderSize, leftTop.getHeight(), leftTop.getWidth(), 
				height - leftBottom.getHeight(), backgroundPaint);
		
		canvas.drawRect(width - borderSize, rightTop.getHeight(), width, 
				height - rightBottom.getHeight(), borderPaint);
		canvas.drawRect(width - rightTop.getWidth(), rightTop.getHeight(), 
				width - borderSize, height - rightBottom.getHeight(), backgroundPaint);
		
		canvas.drawRect(leftTop.getWidth(), leftTop.getHeight(), 
				width - rightBottom.getWidth(), height - rightBottom.getHeight(), 
				backgroundPaint);
		
		canvas.restore();
	}
	
	public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
		Matrix m = new Matrix();
		m.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
	}
}
