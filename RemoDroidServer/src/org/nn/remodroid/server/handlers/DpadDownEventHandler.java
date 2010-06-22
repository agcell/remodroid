package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.DpadDownEvent;

public class DpadDownEventHandler implements MessageHandler<DpadDownEvent> {

	@Override
	public void handleMessage(Robot robot, DpadDownEvent message) {
		robot.keyPress(KeyEvent.VK_DOWN);
		robot.keyRelease(KeyEvent.VK_DOWN);
	}
}
