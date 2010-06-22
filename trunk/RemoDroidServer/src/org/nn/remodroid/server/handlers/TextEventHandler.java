package org.nn.remodroid.server.handlers;

import java.awt.AWTKeyStroke;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.nn.remodroid.messages.TextEvent;

public class TextEventHandler implements MessageHandler<TextEvent> {

	@Override
	public void handleMessage(Robot robot, TextEvent message) {
		System.out.println("-> TextMessage: " + message.getText());
		for (int i = 0; i < message.getText().length(); ++i) {
			char character = message.getText().charAt(i);
			boolean pressShift = 'A' <= character && character <= 'Z';
			character = Character.toUpperCase(character);
			
			int keyCode = AWTKeyStroke.getAWTKeyStroke(character, 0).getKeyCode();
			System.out.println("-> TextMessage: " + character + " " + keyCode);
			
			if (pressShift) {
				robot.keyPress(KeyEvent.VK_SHIFT);
			}
			
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
			
			if (pressShift) {
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
		}
	}
}
