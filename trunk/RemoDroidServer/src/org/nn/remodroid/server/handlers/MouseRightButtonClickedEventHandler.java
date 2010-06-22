package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.InputEvent;

import org.nn.remodroid.messages.MouseRightButtonClickedEvent;

public class MouseRightButtonClickedEventHandler implements MessageHandler<MouseRightButtonClickedEvent> {

	@Override
	public void handleMessage(Robot robot, MouseRightButtonClickedEvent message) {
		System.out.println(getClass().getSimpleName());
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
	}
}
