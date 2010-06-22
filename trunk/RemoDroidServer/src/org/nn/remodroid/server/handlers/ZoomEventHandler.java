package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.ZoomEvent;

public class ZoomEventHandler implements MessageHandler<ZoomEvent> {

	@Override
	public void handleMessage(Robot robot, ZoomEvent message) {
		System.out.println("ZoomEventHandler " + message.getScale());

		int keyCode = KeyEvent.VK_PLUS;
		if (message.getScale() < 0) {
			keyCode = KeyEvent.VK_MINUS;
		}
		
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(keyCode);
		robot.keyRelease(keyCode);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}
}
