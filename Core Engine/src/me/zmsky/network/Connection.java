package me.zmsky.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection implements Runnable{
	
	//The current socket used for receiving/sending info.
	private Socket client;
	//Our current InputStream used to read objects.
	private ObjectInputStream in;
	//Our current OutputStream used to send objects.
	private ObjectOutputStream out;
	
	//The network listeners currently listening for info.
	private List<NetworkListener> listeners;
	
	//The connection's unique ID
	private int ID;
	
	/** Empty Constructor.
	 * 
	 */
	public Connection(){
		listeners = new ArrayList<NetworkListener>();
	}
	/** Default constructor that creates the input/output streams.
	 * 
	 * @param client The current active socket.
	 * @param ID The unique ID of this connection.
	 * @throws Exception May throw an exception if in/out streams cannot be created.
	 */
	public Connection(Socket client, int ID) throws Exception{
		this.ID = ID;
		this.client = client;
		
		listeners = new ArrayList<NetworkListener>();
		
		out = new ObjectOutputStream(new BufferedOutputStream(this.client.getOutputStream()));
		out.flush();
		in = new ObjectInputStream(new BufferedInputStream(this.client.getInputStream()));
	}
	/** Runnable method that will be listening to inbound objects, and notifying the
	 * current NetworkListener on its Received(Connection c, Object o); method.
	 * 
	 * If a problem occurs, or the connection is closed prematurely, the current NetworkListener
	 * will be notified on its Disconnected(Connection); method.
	 */
	@Override
	public void run(){
		try{
			for(NetworkListener l : listeners)
				l.Connected(this);
			
			while(!client.isClosed()){
				Object read = in.readObject();
				
				for(NetworkListener l : listeners)
					l.Received(this, read);
			}
		}catch(Exception e){}
		
		for(NetworkListener l : listeners)
			l.Disconnected(this);
	}
	/** Sets the socket this connection will be wrapping.
	 * 
	 * @param client The desired socket to be set.
	 */
	protected void setSocket(Socket client){ 
		this.client = client; 
		
		try{
			in = new ObjectInputStream(new BufferedInputStream(this.client.getInputStream()));
			out = new ObjectOutputStream(new BufferedOutputStream(this.client.getOutputStream()));
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/** Sends an object upstream to the client.
	 * 
	 * @param o The object that will be sent upstream.
	 */
	public void sendObject(Object o){
		try{
			out.writeObject(o);
			out.flush();
		}catch(Exception e){ }
	}
	/** Closes the current connection.
	 */
	public void closeConnection(){
		try{
			client.close();
		}catch(Exception e){}
	}
	/** Adds a new NetworkListener that will listen to info received from the client.
	 * 
	 * @param listener The desired listener that will be listening.
	 */
	public void addListener(NetworkListener listener){
		listeners.add(listener);
	}
	/** Starts the client.
	 * 
	 */
	protected void start(){
		new Thread(this).start();
	}
	/** Gets the unique ID of this connection.
	 * 
	 * @return This connection's current ID.
	 */
	public int getID(){ return ID; }
	/** Gets the raw socket from this connection.
	 * 
	 * @return The active socket.
	 */
	public Socket getSocket(){ return client; }
}
