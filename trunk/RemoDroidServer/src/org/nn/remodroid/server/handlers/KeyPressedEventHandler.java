package org.nn.remodroid.server.handlers;

import java.awt.Robot;

import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.KeyPressedEvent;

public class KeyPressedEventHandler implements MessageHandler<KeyPressedEvent> {

	@Override
	public void handleMessage(Robot robot, KeyPressedEvent message) {
		if (message.isShiftPressed()) {
			robot.keyPress(KeyEvent.VK_SHIFT);
		}

		if (message.isCtrlPressed()) {
			robot.keyPress(KeyEvent.VK_CONTROL);
		}
		
		if (message.isAltPressed()) {
			robot.keyPress(KeyEvent.VK_ALT);
		}
		
		int keyCode = AWTKeyStroke.getAWTKeyStroke(message.getKeyChar(), 0).getKeyCode();
		System.out.println("-> KeyPressed: " + message.getKeyChar() + " " + keyCode);
		
		robot.keyPress(keyCode);
		robot.keyRelease(keyCode);
		
		if (message.isShiftPressed()) {
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}

		if (message.isCtrlPressed()) {
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}
		
		if (message.isAltPressed()) {
			robot.keyRelease(KeyEvent.VK_ALT);
		}
	}
}
