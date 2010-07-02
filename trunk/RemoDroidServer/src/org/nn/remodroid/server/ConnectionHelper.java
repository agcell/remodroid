package org.nn.remodroid.server;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.nn.remodroid.messages.RemoteMessage;

public class ConnectionHelper {
	
	public static void sendMessage(RemoteMessage message, InetAddress address, int serverPort) {
		sendMessage(message, address, serverPort, false);
	}
	
	public static void sendMessage(RemoteMessage message, InetAddress address, int serverPort, boolean broadcast) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			ObjectOutputStream os = null;
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
				os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
				os.writeObject(message);
				os.flush();
		      
				byte[] buffer = byteStream.toByteArray();
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	} 
}
