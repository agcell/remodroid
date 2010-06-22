package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.InputEvent;

import org.nn.remodroid.messages.MouseLeftButtonClickedEvent;

public class MouseLeftButtonClickedEventHandler implements MessageHandler<MouseLeftButtonClickedEvent>{

	@Override
	public void handleMessage(Robot robot, MouseLeftButtonClickedEvent message) {
		System.out.println(getClass().getSimpleName());
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
}
