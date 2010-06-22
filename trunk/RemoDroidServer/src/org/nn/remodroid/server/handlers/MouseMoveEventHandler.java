package org.nn.remodroid.server.handlers;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

import org.nn.remodroid.messages.MouseMoveEvent;

public class MouseMoveEventHandler implements MessageHandler<MouseMoveEvent> {

	@Override
	public void handleMessage(Robot robot, MouseMoveEvent message) {
		PointerInfo info = MouseInfo.getPointerInfo();
		Point p = info.getLocation();
		robot.mouseMove((int) p.getX() + message.getXOffset(), (int) p.getY() + message.getYOffset());
	}
}
