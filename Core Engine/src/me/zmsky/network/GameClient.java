package me.zmsky.network;

import java.net.Socket;

public class GameClient{
	//The server we will be connecting to.
	private Connection server;
	
	/** Empty constructor.
	 * 
	 */
	public GameClient(){
		server = new Connection();
	}
	/** Starts listening for info with the already specified IP and Port.
	 * 
	 * @return If a valid connection was made with the host.
	 */
	public boolean connect(String IP, int Port){
		try{
			Socket serverSocket = new Socket(IP, Port);
			server.setSocket(serverSocket);

			server.start();
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	/** Closes connection with the server.
	 */
	public void close(){
		server.closeConnection();
	}
	/** Sends an object to the server.
	 * 
	 * @param o The object that will be sent to the server.
	 */
	public void sendObject(Object o){
		server.sendObject(o);
	}
	/** Adds a listener to the list of NetworkListeners.
	 * 
	 * @param listener The listener to be added.
	 */
	public void addNetworkListener(NetworkListener listener){ server.addListener(listener); }
}
