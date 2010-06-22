package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.DpadLeftEvent;

public class DpadLeftEventHandler implements MessageHandler<DpadLeftEvent> {

	@Override
	public void handleMessage(Robot robot, DpadLeftEvent message) {
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_LEFT);
	}
}
