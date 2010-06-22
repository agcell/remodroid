package org.nn.remodroid.server.handlers;

import java.awt.Robot;

import org.nn.remodroid.messages.RemoteMessage;

public interface MessageHandler<T extends RemoteMessage> {

	void handleMessage(Robot robot, T message);
}
