package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.DpadCenterEvent;

public class DpadCenterEventHandler implements MessageHandler<DpadCenterEvent> {

	@Override
	public void handleMessage(Robot robot, DpadCenterEvent message) {
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}
}
