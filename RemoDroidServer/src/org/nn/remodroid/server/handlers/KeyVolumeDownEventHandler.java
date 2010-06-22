package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.KeyVolumeDownEvent;

public class KeyVolumeDownEventHandler implements MessageHandler<KeyVolumeDownEvent> {

	@Override
	public void handleMessage(Robot robot, KeyVolumeDownEvent message) {
		robot.keyPress(KeyEvent.VK_PAGE_DOWN);
		robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
	}
}
