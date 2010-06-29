package org.nn.remodroid.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.nn.remodroid.messages.CloseCurrentAppEvent;
import org.nn.remodroid.messages.DpadCenterEvent;
import org.nn.remodroid.messages.DpadDownEvent;
import org.nn.remodroid.messages.DpadLeftEvent;
import org.nn.remodroid.messages.DpadRightEvent;
import org.nn.remodroid.messages.DpadUpEvent;
import org.nn.remodroid.messages.HScrollEvent;
import org.nn.remodroid.messages.KeyDelPressedEvent;
import org.nn.remodroid.messages.KeyPressedEvent;
import org.nn.remodroid.messages.KeyVolumeDownEvent;
import org.nn.remodroid.messages.KeyVolumeUpEvent;
import org.nn.remodroid.messages.MouseLeftButtonClickedEvent;
import org.nn.remodroid.messages.MouseMoveEvent;
import org.nn.remodroid.messages.MouseRightButtonClickedEvent;
import org.nn.remodroid.messages.RemoteMessage;
import org.nn.remodroid.messages.TextEvent;
import org.nn.remodroid.messages.VScrollEvent;
import org.nn.remodroid.messages.ZoomEvent;
import org.nn.remodroid.server.RemoDroidServer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class RemoDroidActivity extends Activity implements Runnable {
    
	private static final String CLASSTAG = RemoDroidActivity.class.getSimpleName();
	
	private static final int SERVER_PORT = RemoDroidServer.SERVER_PORT;
	
	private BlockingQueue<RemoteMessage> messages = new LinkedBlockingQueue<RemoteMessage>();
	
	private static final boolean debug = false;
	
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private GraphicsView view = null;
	
	private InetAddress serverAddress;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        serverAddress = (InetAddress) getIntent().getExtras().get(SelectServerActivity.SERVER_ADDRESS);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        GraphicsView view = new GraphicsView(this);
        setContentView(view);
        
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerAccelerometerListener(view);
        
        new Thread(this).start();
    }

    @Override
    protected void onPause() {
    	if (sensor != null) {
    		sensorManager.unregisterListener(view);
    	}
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	registerAccelerometerListener(view);
    }
    
    private void sendMessage(RemoteMessage message) {
    	messages.offer(message);
    }
    
    private void registerAccelerometerListener(SensorEventListener listener) {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
        	Log.d(CLASSTAG, "registerAccelerometer");
        	sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    
	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			while (true) {
		    	try {
		        	RemoteMessage message = messages.take();
		        	if (message.isExitMessage()) {
		        		break;
		        	}
		
					ObjectOutputStream os = null;
					try {
						ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
						os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
						os.writeObject(message);
						os.flush();
				      
						byte[] buffer = byteStream.toByteArray();
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, SERVER_PORT);
						socket.send(packet);
					} catch (IOException e) {
						Log.e(Constants.LOGTAG, " " + CLASSTAG + " IOException calling socket", e);
					} finally {
						if (os != null) {
							try {
								os.close();
							} catch (IOException e) {
								Log.e(Constants.LOGTAG, " " + CLASSTAG + " IOException closing output stream (os)", e);
							}
						}
					}
		    	} catch (InterruptedException e) {
					Log.e(Constants.LOGTAG, " " + CLASSTAG + " InterruptedException while extracting new message", e);
					break;
		    	}
			}
		} catch (SocketException e) {
			Log.e(Constants.LOGTAG, " " + CLASSTAG + " SocketException while creating socket", e);
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
	
	private class GraphicsView extends View implements SensorEventListener {

		private static final int MODE_NONE = 0;
		private static final int MODE_DRAG = 1;
		private static final int MODE_VSCROLL = 2;
		private static final int MODE_HSCROLL = 3;
		private static final int MODE_ZOOM = 4;
		
		private static final int SCROLL_RATE = 50;
		private static final int BORDER_SIZE = 5;
		
		private ObjectsPositionInfo verticalInfo = null;
		private ObjectsPositionInfo horizontalInfo = null;
		
		private float startX;
		private float startY;
		private long startEventTime;
		private long clickStartTime;
		
		private int mode = MODE_NONE;
		
		private final Paint rectPaint = new Paint();
		private final Paint backgroundPaint = new Paint();
		private final Paint borderPaint = new Paint();
		
		private ObjectsPositionInfo positionInfo = null;
		
		private double totalForcePrev = 0;
		private long lastShake = 0;
		private long lastAccelEvent = 0;
		private int shakeCount = 0;
		
		private float pichAndZoomDistance = 0.0f;
		
		private boolean drawLeftButton;
		private boolean drawRightButton;
		
		private Bitmap leftTopCorner = null;
		private Bitmap rightTopCorner = null;
		private Bitmap leftBottomCorner = null;
		private Bitmap rightBottomCorner = null;
		
		public GraphicsView(Context context) {
			super(context);
			setFocusable(true);
			setFocusableInTouchMode(true);
			
			rectPaint.setColor(getResources().getColor(R.color.rect));
			rectPaint.setAlpha(128);
			
			verticalInfo = new VerticalPositionInfo(BitmapFactory
					.decodeResource(getResources(), R.drawable.button_v));
			horizontalInfo = new HorizontalPositionInfo(BitmapFactory
					.decodeResource(getResources(), R.drawable.button_h));
			
			leftTopCorner = BitmapFactory.decodeResource(getResources(), R.drawable.background_corner);
			displaySizeInfo(leftTopCorner);
			rightTopCorner = rotateBitmap(leftTopCorner, 90);
			displaySizeInfo(rightTopCorner);
			rightBottomCorner = rotateBitmap(rightTopCorner, 90);
			displaySizeInfo(rightBottomCorner);
			leftBottomCorner = rotateBitmap(rightBottomCorner, 90);
			displaySizeInfo(leftBottomCorner);
			
			backgroundPaint.setColor(getResources().getColor(R.color.background));
			borderPaint.setColor(getResources().getColor(R.color.border));
		}
		
		private void displaySizeInfo(Bitmap bitmap) {
			Log.d(CLASSTAG, "### w=" + bitmap.getWidth() + "; h=" + bitmap.getHeight());
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

//			RectF rect = new RectF();
//			rect.set(0, 0, getWidth(), getHeight());
//			canvas.drawRoundRect(rect, 25.0f, 25.0f, backgroundPaint);
			
			canvas.drawBitmap(leftTopCorner, 0, 0, null);
			canvas.drawBitmap(rightTopCorner, getWidth() - rightTopCorner.getWidth(), 0, null);
			canvas.drawBitmap(leftBottomCorner, 0, getHeight() - leftBottomCorner.getHeight(), null);
			canvas.drawBitmap(rightBottomCorner, getWidth() - rightBottomCorner.getWidth(), 
					getHeight() - rightBottomCorner.getHeight(), null);
			
			canvas.drawRect(leftTopCorner.getWidth(), 0, 
					getWidth() - rightTopCorner.getWidth(), BORDER_SIZE, borderPaint);
			canvas.drawRect(leftTopCorner.getWidth(), BORDER_SIZE, getWidth() - rightTopCorner.getWidth(), 
					leftTopCorner.getHeight(), backgroundPaint);

			canvas.drawRect(leftBottomCorner.getWidth(), getHeight() - BORDER_SIZE, 
					getWidth() - rightBottomCorner.getWidth(), getHeight(), borderPaint);
			canvas.drawRect(leftBottomCorner.getWidth(), getHeight() - leftBottomCorner.getHeight(), 
					getWidth() - rightBottomCorner.getWidth(), getHeight() - BORDER_SIZE,backgroundPaint);
			
			canvas.drawRect(0, leftTopCorner.getHeight(), BORDER_SIZE, 
					getHeight() - leftBottomCorner.getHeight(), borderPaint);
			canvas.drawRect(BORDER_SIZE, leftTopCorner.getHeight(), leftTopCorner.getWidth(), 
					getHeight() - leftBottomCorner.getHeight(), backgroundPaint);
			
			canvas.drawRect(getWidth() - BORDER_SIZE, rightTopCorner.getHeight(), getWidth(), 
					getHeight() - rightBottomCorner.getHeight(), borderPaint);
			canvas.drawRect(getWidth() - rightTopCorner.getWidth(), rightTopCorner.getHeight(), 
					getWidth() - BORDER_SIZE, getHeight() - rightBottomCorner.getHeight(), backgroundPaint);
			
			canvas.drawRect(leftTopCorner.getWidth(), leftTopCorner.getHeight(), 
					getWidth() - rightBottomCorner.getWidth(), getHeight() - rightBottomCorner.getHeight(), 
					backgroundPaint);
			
			if (drawLeftButton) {
				canvas.drawBitmap(positionInfo.getButton(), positionInfo.getLeft().left, 
						positionInfo.getLeft().top, null);
			}
			
			if (drawRightButton) {
				canvas.drawBitmap(positionInfo.getButton(), positionInfo.getRight().left, 
						positionInfo.getRight().top, null);				
			}
			
			if (debug) {
				//ObjectsPositionInfo positionInfo = getPositionInfo();
				canvas.drawRect(positionInfo.getLeft(), rectPaint);
				canvas.drawRect(positionInfo.getRight(), rectPaint);
				canvas.drawRect(positionInfo.getKeyboard(), rectPaint);
				canvas.drawRect(positionInfo.getVScrollbar(), rectPaint);
				canvas.drawRect(positionInfo.getHScrollbar(), rectPaint);
			}
		}
		
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			positionInfo = getPositionInfo();
//			if (w < h) {
//				setBackgroundResource(R.drawable.background_v);
//			} else {
//				setBackgroundResource(R.drawable.background_h);
//			}
			super.onSizeChanged(w, h, oldw, oldh);
		}
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			long time = event.getEventTime();
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				handleActionDown(event, time);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				handleActionPointerDown(event, time);
				break;
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
				handleActionUp(event, time);
				break;
			case MotionEvent.ACTION_MOVE:
				handleActionMove(event, time);
				break;
			}
			
			return true;
		}
		
		private void handleActionDown(MotionEvent event, long time) {
			startX = event.getX();
			startY = event.getY();
			startEventTime = event.getEventTime();
			clickStartTime = startEventTime;
			
			int x = (int) startX;
			int y = (int) startY;
			
			if (positionInfo.getVScrollbar().contains(x, y)) {
				mode = MODE_VSCROLL;
				Log.d(CLASSTAG, String.format("vscroll start (%f, %f)", startX, startY));
			} else if (positionInfo.getHScrollbar().contains(x, y)) {
				mode = MODE_HSCROLL;
				Log.d(CLASSTAG, String.format("hscroll start (%f, %f)", startX, startY));
			} else {
				if (positionInfo.getLeft().contains(x, y)) {
					drawLeftButton = true;
					invalidate(positionInfo.getLeft());
				}

				if (positionInfo.getRight().contains(x, y)) {
					drawRightButton = true;
					invalidate(positionInfo.getRight());
				}
				
				mode = MODE_DRAG;
				Log.d(CLASSTAG, String.format("drag start (%f, %f)", startX, startY));
			}
		}
		
		private void handleActionPointerDown(MotionEvent event, long time) {
			pichAndZoomDistance = spacing(event);
			if (pichAndZoomDistance > 10.0f) {
				mode = MODE_ZOOM;
			}
		}
		
		private void handleActionUp(MotionEvent event, long time) {
			Log.d(CLASSTAG, String.format("dtime: %d", time - clickStartTime));
			
			int x = (int) event.getX();
			int y = (int) event.getY();
			
			mode = MODE_NONE;
			
			if (time - clickStartTime < 200) {
				if (positionInfo.getRight().contains(x, y)) {
					Log.d(CLASSTAG, "Sending right click event...");
					sendMessage(new MouseRightButtonClickedEvent());
				} else if (positionInfo.getKeyboard().contains(x, y)) {
					showSoftKeyboard();
				} else {
					Log.d(CLASSTAG, "Sending left click event...");
					sendMessage(new MouseLeftButtonClickedEvent());
				}
			}
			
			if (drawLeftButton) {
				drawLeftButton = false;
				invalidate(positionInfo.getLeft());
			}

			if (drawRightButton) {
				drawRightButton = false;
				invalidate(positionInfo.getRight());
			}
			
			Log.d(CLASSTAG, String.format("drag stop (%f, %f)", event.getX(), event.getY()));
		}
		
		private void handleActionMove(MotionEvent event, long time) {
			float x = event.getX();
			float y = event.getY();
			
			switch (mode) {
			case MODE_DRAG:
				handleActionMoveDrag(event, time, x, y);
				break;
			case MODE_VSCROLL:
				handleActionMoveVScroll(event, time, x, y);
				break;
			case MODE_HSCROLL:
				handleActionMoveHScroll(event, time, x, y);
				break;
			case MODE_ZOOM:
				handleActionMoveZoom(event, time);
				break;
			}
		}
		
		private void handleActionMoveDrag(MotionEvent event, long time, float x, float y) {			
			float dx = x - startX;
			float dy = y - startY;
			if (Math.abs(dx) >= 2.0f || Math.abs(dy) >= 2.0f) {
				Log.d(CLASSTAG, String.format("fOffset (%f, %f)", x - startX, y - startY));
				
				float speed = (time - startEventTime) / 15.0f;
				Log.d(CLASSTAG, String.format("speed %f", speed));			
				
				speed = 1.6f;
				int xOffset = (int) (dx * speed);
				int yOffset = (int) (dy * speed);
				//Log.d(CLASSTAG, String.format("iOffset (%d, %d)", xOffset, yOffset));
				
				messages.offer(new MouseMoveEvent(xOffset, yOffset));
				
				startX = x;
				startY = y;
				startEventTime = time;
			}
		}
		
		private void handleActionMoveHScroll(MotionEvent event, long time, float x, float y) {
			int dx = (int) (x - startX);
			int cx = dx / SCROLL_RATE;
			//Log.d(CLASSTAG, String.format("hScroll: cx = %d, startX = %f, x = %f", cx, startX, x));
			if (cx != 0) {
				long dTime = time - startEventTime;
				if (dTime < 100) {
					cx *= 4;
				} else if (dTime < 200) {
					cx *= 2;
				}
				sendMessage(new HScrollEvent(cx));
				startX = x - (dx % SCROLL_RATE);
				startEventTime = time;
			}
		}

		private void handleActionMoveVScroll(MotionEvent event, long time, float x, float y) {
			int dy = (int) (y - startY);
			int cy = dy / SCROLL_RATE;
			//Log.d(CLASSTAG, String.format("vScroll: cy = %d, startY = %f, y = %f", cy, startY, y));
			if (cy != 0) {
				long dTime = time - startEventTime;
				if (dTime < 100) {
					cy *= 4;
				} else if (dTime < 200) {
					cy *= 2;
				}
				sendMessage(new VScrollEvent(cy));
				startY = y - (dy % SCROLL_RATE);
				startEventTime = time;
			}
		}
		
		private void handleActionMoveZoom(MotionEvent event, long time) {
			float newDistance = spacing(event);
			if (newDistance > 10.0f) {
				float scale = newDistance / pichAndZoomDistance;
				int iScale = 1;
				if (newDistance < pichAndZoomDistance) {
					scale = pichAndZoomDistance / newDistance;
					iScale = -1;
				}
				
				int s = (int) scale;
				if (s > 1) {
					iScale *= (int) scale;
					sendMessage(new ZoomEvent(iScale - 1));
					pichAndZoomDistance = newDistance;
				}
			}
		}
		
		private float spacing(MotionEvent event) {
			if (event.getPointerCount() <= 1) {
				return 0.0f;
			}
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}
		
		@Override
		public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
		}

		@Override
		public void onSensorChanged(SensorEvent paramSensorEvent) {
			if (paramSensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				long time = paramSensorEvent.timestamp;
				
				if (lastShake == 0) {
					lastShake = time;
					lastAccelEvent = time;
					return;
				}
				
				if (time - lastAccelEvent <= 20) {
					return;
				}
				lastAccelEvent = time;
				
				double forceThreshHold = 1.3f;
				double totalForce = 0.0f;
				float values[] = paramSensorEvent.values;
				
				totalForce += Math.pow(values[SensorManager.DATA_X]/SensorManager.GRAVITY_EARTH, 2.0);
				totalForce += Math.pow(values[SensorManager.DATA_Y]/SensorManager.GRAVITY_EARTH, 2.0);
				totalForce += Math.pow(values[SensorManager.DATA_Z]/SensorManager.GRAVITY_EARTH, 2.0);
				totalForce = Math.sqrt(totalForce);
	
				//Log.d(CLASSTAG, String.format("onSensorChanged: totalForce = %f, totalForcePrev = %f", totalForce, totalForcePrev));
				boolean updateForce = true;
				if ((totalForce < forceThreshHold) && (totalForcePrev > forceThreshHold)) {
					if (time - lastShake > 500) {
						lastShake = time;
						shakeCount += 1;
						if (shakeCount >= 4) {
							shakeCount = 0;
							totalForcePrev = 0;
							updateForce = false;
							sendMessage(new CloseCurrentAppEvent());
						}
					}
				}

				if (updateForce){
					totalForcePrev = totalForce;
				}
			}
		}
		
		@Override
		public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
			outAttrs.inputType |= EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE;
			return new BaseInputConnection(this, false) {
				
				@Override
				public boolean setComposingText(CharSequence text, int newCursorPosition) {
					messages.offer(new TextEvent(text.toString()));
					return super.setComposingText(text, newCursorPosition);
				}
				@Override
				public boolean performEditorAction(int actionCode) {
					messages.offer(new KeyPressedEvent('\n'));
					return true;
				}
			};
		}
		
		@Override
		public boolean onCheckIsTextEditor() {
			return true;
		}
		
		private ObjectsPositionInfo getPositionInfo() {  
			if (getWidth() > getHeight()) {
				return horizontalInfo;
			}
			
			return verticalInfo;
		}
		
		private void showSoftKeyboard() {
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
		}
		
		private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
			Matrix m = new Matrix();
			m.postRotate(90);
			return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("# key pressed " + keyCode);
		if (KeyEvent.isModifierKey(keyCode)) {
			return true;
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_MENU:
			return super.onKeyDown(keyCode, event);
		case KeyEvent.KEYCODE_VOLUME_UP:
			Log.i(CLASSTAG, "Volume up pressed");
			sendMessage(new KeyVolumeUpEvent());
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			Log.i(CLASSTAG, "Volume down pressed");
			sendMessage(new KeyVolumeDownEvent());
			break;
		case KeyEvent.KEYCODE_DEL:
			Log.i(CLASSTAG, "Del pressed");
			sendMessage(new KeyDelPressedEvent());
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			sendMessage(new DpadCenterEvent());
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			sendMessage(new DpadUpEvent());
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			sendMessage(new DpadDownEvent());
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			sendMessage(new DpadLeftEvent());
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			sendMessage(new DpadRightEvent());
			break;
		default:
			Log.d(CLASSTAG, "key label: " + event.getDisplayLabel() + " " + event.isShiftPressed());
			sendMessage(new KeyPressedEvent(event.getDisplayLabel(), event.isShiftPressed()));
		}
		
		return true;
	}	
}