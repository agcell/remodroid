package org.nn.remodroid.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nn.remodroid.messages.FindServerRequestEvent;
import org.nn.remodroid.messages.FindServerResponseEvent;
import org.nn.remodroid.messages.RemoteMessage;
import org.nn.remodroid.server.RemoDroidServer;
import org.nn.remodroid.client.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectServerActivity extends Activity implements Runnable {

	private static final String CLASSTAG = SelectServerActivity.class.getSimpleName();
	
	
	
	private Thread thread = null;
	
	private ListView myListView;
	private final List<String> servers = Collections.synchronizedList(new ArrayList<String>());
	private ArrayAdapter<String> serversAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("************** onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		myListView = (ListView) findViewById(R.id.myListView);
        serversAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, servers);
        myListView.setAdapter(serversAdapter);
        
        if (thread == null) {
	        thread = new Thread(this);
	        thread.start();
        }
        
        
	}
	
	@Override
	protected void onPause() {
		System.out.println("************** onPause");
		if (thread != null) {
			try {
				thread.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				thread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		thread = null;
		super.onPause();
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[RemoDroidServer.BUFFER_SIZE];
		DatagramSocket socket = null;
		
		try {
			sendMessage(new FindServerRequestEvent(), 
					InetAddress.getByName("255.255.255.255"), RemoDroidServer.SERVER_PORT);
			
			socket = new DatagramSocket(RemoDroidServer.SERVER_PORT);
			socket.setSoTimeout(1500);
			while (true) {
				ObjectInputStream is = null;
				try {
					final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					socket.receive(packet);
					if (packet.getLength() > 0) {
						is = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
						RemoteMessage message = (RemoteMessage) is.readObject();
						if (message instanceof FindServerResponseEvent) {
							Log.d(CLASSTAG, "# Message received: " + message.getClass().getSimpleName() + " " 
									+ packet.getAddress().getHostName());
							addServer(packet.getAddress().getHostName());
						}
					}
				} catch (SocketTimeoutException e) {
					System.out.println("************** SocketTimeoutException");
					break;
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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
	
	private void addServer(final String serverName) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				serversAdapter.add(serverName);
			}
		});
	}
	
	public static void sendMessage(RemoteMessage message, InetAddress address, int serverPort) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
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
