package org.nn.remodroid.server.handlers;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.CloseCurrentAppEvent;

public class CloseCurrentAppEventHandler implements MessageHandler<CloseCurrentAppEvent> {

	@Override
	public void handleMessage(Robot robot, CloseCurrentAppEvent message) {
//		robot.keyPress(KeyEvent.VK_ALT);
//		robot.keyPress(KeyEvent.VK_F4);
//		robot.keyRelease(KeyEvent.VK_F4);
//		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_D);
		robot.keyRelease(KeyEvent.VK_D);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}
}
