package org.nn.remodroid.server.handlers;

import java.awt.Robot;

import org.nn.remodroid.messages.VScrollEvent;

public class VScrollEventHandler implements MessageHandler<VScrollEvent> {

	@Override
	public void handleMessage(Robot robot, VScrollEvent message) {
		robot.mouseWheel(message.getRate());
	}
}
