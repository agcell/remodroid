package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.DpadRightEvent;

public class DpadRightEventHandler implements MessageHandler<DpadRightEvent> {

	@Override
	public void handleMessage(Robot robot, DpadRightEvent message) {
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
	}
}
