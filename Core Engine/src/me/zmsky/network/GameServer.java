package me.zmsky.network;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class GameServer implements Runnable, NetworkListener{
	
	//The ServerSocket that will be receiving new connections constantly.
	private ServerSocket server;
	//Just a wrapper for our NetworkListener in charge of reading objects from all clients/checking to
	//see if one disconnects or connects.
	private ServerListener listener;
	//A list containing all our current, live connections.
	private List<Connection> connections;	
	//A list of NetworkListeners hearing for any information passed by the clients.
	private List<NetworkListener> listeners;
	
	//Indicates if the server is currently running.
	private boolean Running;
	//The current IP of the server.
	private String IP;
	//The current Port of the server.
	private int Port;
	
	/** The default constructor for the GameServer.
	 * 
	 * @param IP The IP on which the server will be running on.
	 * @param Port The Port on which the server will be running on.
	 */
	public GameServer(String IP, int Port){
		connections = new ArrayList<Connection>();
		listeners = new ArrayList<NetworkListener>();
		
		listener = new ServerListener();
		
		this.IP = IP;
		this.Port = Port;
	}
	/** This runnable method is constantly listening to new connections. 
	 * The thread is locked until a new connection is received, as it waits on the line
	 * server.accept();
	 * 
	 * Once a new connection is inbound, all the current network listeners will have their
	 * Connected(Connection c); method called, and the connection will be added to the current list
	 * of active connections.
	 */
	@Override
	public void run(){
		if(!Running){
			Running = true;
			
			try{
				while(Running){
					Socket newClient = server.accept();
					
					synchronized(connections){
						Connection newConnection = new Connection(newClient, connections.size());
						newConnection.addListener(listener);
						newConnection.addListener(this);
						newConnection.start();
						
						for(NetworkListener listener : listeners){
							listener.Connected(newConnection);
						}
						
						connections.add(newConnection);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/** Disconnects all the active connections by closing their socket.
	 * The list of connections will be cleared after the method is called.
	 */
	public void disconnectAll(){
		synchronized(connections){
			for(Connection c : connections){
				try{
					c.closeConnection();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			connections.clear();
		}
	}
	/** Sends an object to all the active connections.
	 * 
	 * @param o The object that will be sent.
	 */
	public void sendAll(Object o){
		synchronized(connections){
			for(Connection c : connections){
				c.sendObject(o);
			}
		}
	}
	/** Sends an object to all the active connections, except a specific one.
	 * 
	 * @param o The object that will be sent.
	 */
	public void sendAll(Object o, Connection exclude){
		synchronized(connections){
			for(Connection c : connections){
				if(c.getID() != exclude.getID())
					c.sendObject(o);
			}
		}
	}
	/** Starts the server with the specified IP and Port.
	 * If the server doesn't start correctly, this method will return false.
	 * Otherwise, this method will return true.
	 * 
	 * @return If the server has been started successfully.
	 */
	public boolean start(){
		try{
			server = new ServerSocket();
			server.bind(new InetSocketAddress(IP, Port));
			
			new Thread(this).start();
			return true;
		}catch(Exception e){}
		
		return false;
	}
	/** Gets a connection from the list of active connections by a given ID.
	 * 
	 * @param ID The ID of the desired connection.
	 * @return The connection with the specified ID.
	 */
	public Connection getConnectionByID(int ID){
		synchronized(connections){
			for(Connection c : connections)
				if(c.getID() == ID)
					return c;
		}
		
		return null;
	}
	/** Stops the server, disconnecting all the active connections and closing the
	 * server socket.
	 */
	public void stop(){
		try{
			Running = false;
			disconnectAll();
			server.close();
		}catch(Exception e){}
	}
	/** Returns the list of active connections.
	 * 
	 * @return The list of active connections.
	 */
	public List<Connection> getConnections(){ return connections; }
	/** Adds a new NetworkListener that will be watching out for incoming traffic.
	 * 
	 * @param listener The desired listener to be added.
	 */
	public void addNetworkListener(NetworkListener listener){ listeners.add(listener); }
	
	
	/** This class is only a wrapper for listening to all of our current connections.
	 * We will only be redirecting all the objects received by a client to all the other ones,
	 * let alone if they are disconnected.
	 * @author ZmSky
	 *
	 */
	private class ServerListener implements NetworkListener{
		@Override
		public void Disconnected(Connection c) {
			synchronized(connections){
				for(NetworkListener listener : listeners){
					listener.Disconnected(c);
				}
				
				connections.remove(c);
			}
		}
		@Override
		public void Received(Connection c, Object o) {
			synchronized(connections){
				for(NetworkListener listener : listeners){
					listener.Received(c, o);
				}
			}
			
			sendAll(o, c);
		}
		public void Connected(Connection c) {}
	}
}
