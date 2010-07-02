package org.nn.remodroid.server;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.nn.remodroid.messages.CloseCurrentAppEvent;
import org.nn.remodroid.messages.DpadCenterEvent;
import org.nn.remodroid.messages.DpadDownEvent;
import org.nn.remodroid.messages.DpadLeftEvent;
import org.nn.remodroid.messages.DpadRightEvent;
import org.nn.remodroid.messages.DpadUpEvent;
import org.nn.remodroid.messages.FindServerRequestEvent;
import org.nn.remodroid.messages.FindServerResponseEvent;
import org.nn.remodroid.messages.HScrollEvent;
import org.nn.remodroid.messages.KeyDelPressedEvent;
import org.nn.remodroid.messages.KeyPressedEvent;
import org.nn.remodroid.messages.KeyVolumeDownEvent;
import org.nn.remodroid.messages.KeyVolumeUpEvent;
import org.nn.remodroid.messages.MouseLeftButtonClickedEvent;
import org.nn.remodroid.messages.MouseMoveEvent;
import org.nn.remodroid.messages.MouseRightButtonClickedEvent;
import org.nn.remodroid.messages.RemoteMessage;
import org.nn.remodroid.messages.TextEvent;
import org.nn.remodroid.messages.VScrollEvent;
import org.nn.remodroid.messages.ZoomEvent;
import org.nn.remodroid.server.handlers.CloseCurrentAppEventHandler;
import org.nn.remodroid.server.handlers.DpadCenterEventHandler;
import org.nn.remodroid.server.handlers.DpadDownEventHandler;
import org.nn.remodroid.server.handlers.DpadLeftEventHandler;
import org.nn.remodroid.server.handlers.DpadRightEventHandler;
import org.nn.remodroid.server.handlers.DpadUpEventHandler;
import org.nn.remodroid.server.handlers.HScrollEventHandler;
import org.nn.remodroid.server.handlers.KeyDelPressedEventHandler;
import org.nn.remodroid.server.handlers.KeyPressedEventHandler;
import org.nn.remodroid.server.handlers.KeyVolumeDownEventHandler;
import org.nn.remodroid.server.handlers.KeyVolumeUpEventHandler;
import org.nn.remodroid.server.handlers.MessageHandler;
import org.nn.remodroid.server.handlers.MouseLeftButtonClickedEventHandler;
import org.nn.remodroid.server.handlers.MouseMoveEventHandler;
import org.nn.remodroid.server.handlers.MouseRightButtonClickedEventHandler;
import org.nn.remodroid.server.handlers.TextEventHandler;
import org.nn.remodroid.server.handlers.VScrollEventHandler;
import org.nn.remodroid.server.handlers.ZoomEventHandler;

public class RemoDroidServer extends Thread {

	public static final int SERVER_PORT = 9011;
	public static final int BUFFER_SIZE = 5000;
	
	private Map<Class<? extends RemoteMessage>, MessageHandler<? extends RemoteMessage>> handlers = 
		new HashMap<Class<? extends RemoteMessage>, MessageHandler<? extends RemoteMessage>>();
	
	private RemoDroidServer() {
		handlers.put(MouseMoveEvent.class, new MouseMoveEventHandler());
		handlers.put(MouseLeftButtonClickedEvent.class, new MouseLeftButtonClickedEventHandler());
		handlers.put(MouseRightButtonClickedEvent.class, new MouseRightButtonClickedEventHandler());
		handlers.put(KeyPressedEvent.class, new KeyPressedEventHandler());
		handlers.put(KeyDelPressedEvent.class, new KeyDelPressedEventHandler());
		handlers.put(TextEvent.class, new TextEventHandler());
		handlers.put(KeyVolumeUpEvent.class, new KeyVolumeUpEventHandler());
		handlers.put(KeyVolumeDownEvent.class, new KeyVolumeDownEventHandler());
		handlers.put(VScrollEvent.class, new VScrollEventHandler());
		handlers.put(HScrollEvent.class, new HScrollEventHandler());
		handlers.put(CloseCurrentAppEvent.class, new CloseCurrentAppEventHandler());
		handlers.put(ZoomEvent.class, new ZoomEventHandler());
		handlers.put(DpadCenterEvent.class, new DpadCenterEventHandler());
		handlers.put(DpadUpEvent.class, new DpadUpEventHandler());
		handlers.put(DpadDownEvent.class, new DpadDownEventHandler());
		handlers.put(DpadLeftEvent.class, new DpadLeftEventHandler());
		handlers.put(DpadRightEvent.class, new DpadRightEventHandler());
	}
	
	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];
		DatagramSocket socket = null;
		
		try {
			Robot robot = new Robot();
			socket = new DatagramSocket(SERVER_PORT);
			
			ConnectionHelper.sendMessage(new FindServerResponseEvent(), 
					InetAddress.getByName("255.255.255.255"), RemoDroidServer.SERVER_PORT);
			
			while (true) {
				ObjectInputStream is = null;
				try {
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
					is = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
					RemoteMessage message = (RemoteMessage) is.readObject();
					System.out.println("# Message received: " + message.getClass().getSimpleName());
					
					if (message instanceof FindServerRequestEvent) {
						sendFindServerReply(packet.getAddress());
					} else {
						MessageHandler<RemoteMessage> handler = cast(handlers.get(message.getClass()));
						if (handler != null) {
							handler.handleMessage(robot, message);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (AWTException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	private void sendFindServerReply(InetAddress address) {
		ConnectionHelper.sendMessage(new FindServerResponseEvent(), address, SERVER_PORT);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}
	
	public static void main(String[] args) {
		new RemoDroidServer().start();
	}
}
