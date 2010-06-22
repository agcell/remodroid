package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.KeyDelPressedEvent;

public class KeyDelPressedEventHandler implements MessageHandler<KeyDelPressedEvent> {

	@Override
	public void handleMessage(Robot robot, KeyDelPressedEvent message) {
		robot.keyPress(KeyEvent.VK_BACK_SPACE);
		robot.keyRelease(KeyEvent.VK_BACK_SPACE);
	}
}
