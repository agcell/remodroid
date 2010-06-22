package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.DpadUpEvent;

public class DpadUpEventHandler implements MessageHandler<DpadUpEvent> {

	@Override
	public void handleMessage(Robot robot, DpadUpEvent message) {
		robot.keyPress(KeyEvent.VK_UP);
		robot.keyRelease(KeyEvent.VK_UP);
	}
}
