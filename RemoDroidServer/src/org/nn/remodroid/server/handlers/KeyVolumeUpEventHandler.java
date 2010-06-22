package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.KeyVolumeUpEvent;

public class KeyVolumeUpEventHandler implements MessageHandler<KeyVolumeUpEvent> {

	@Override
	public void handleMessage(Robot robot, KeyVolumeUpEvent message) {
		robot.keyPress(KeyEvent.VK_PAGE_UP);
		robot.keyRelease(KeyEvent.VK_PAGE_UP);

	}
}
