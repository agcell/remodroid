package org.nn.remodroid.client;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import org.nn.remodroid.server.ConnectionHelper;
import org.nn.remodroid.server.RemoDroidServer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectServerActivity extends Activity implements Runnable {

	private static final String CLASSTAG = SelectServerActivity.class.getSimpleName();
	
	public static final String SERVER_ADDRESS = "server_address";
	
	private Thread thread = null;
	
	private InetAddress serverAddress;
	
	private ListView myListView;
	private final List<ServerInfo> servers = Collections.synchronizedList(new ArrayList<ServerInfo>());
	private ArrayAdapter<ServerInfo> serversAdapter;
	
	private boolean stopThread = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("************** onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		myListView = (ListView) findViewById(R.id.myListView);
        serversAdapter = new ArrayAdapter<ServerInfo>(this, android.R.layout.simple_list_item_1, servers);
        myListView.setAdapter(serversAdapter);
        
        myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArrayAdapter<ServerInfo> serversAdapter = cast(parent.getAdapter());
				ServerInfo serverInfo = serversAdapter.getItem(position);
				Intent intent = new Intent(SelectServerActivity.this, RemoDroidActivity.class);
				intent.putExtra(SERVER_ADDRESS, serverInfo.getAddress());
				startActivity(intent);
			}
		});
        
        if (thread == null) {
	        thread = new Thread(this);
	        thread.start();
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		if (thread == null) {
	        thread = new Thread(this);
	        thread.start();
        }
	}
	
	@Override
	protected void onPause() {
		System.out.println("************** onPause");
		stopThread = true;
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
		serversAdapter.clear();
		super.onPause();
	}
	
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[RemoDroidServer.BUFFER_SIZE];
		DatagramSocket socket = null;
		
		stopThread = false;
		
		try {
			socket = new DatagramSocket(RemoDroidServer.SERVER_PORT);
			socket.setSoTimeout(1500);
			
			boolean messageSent = false;
			while (true) {
				if (stopThread) {
					break;
				}
				
				if (!messageSent) {
					ConnectionHelper.sendMessage(new FindServerRequestEvent(), 
							InetAddress.getByName("255.255.255.255"), RemoDroidServer.SERVER_PORT);
					messageSent = true;
				}
				
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
							addServer(packet.getAddress().getHostName(), packet.getAddress());
						}
					}
				} catch (SocketTimeoutException e) {
					System.out.println("************** SocketTimeoutException");
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
	
	private void addServer(final String serverName, final InetAddress address) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				serversAdapter.add(new ServerInfo(serverName, address));
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}
	
	private static class ServerInfo {
		
		private String name;
		
		private InetAddress address;
		
		ServerInfo(String name, InetAddress address) {
			this.name = name;
			this.address = address;
		}
		
		InetAddress getAddress() {
			return address;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
