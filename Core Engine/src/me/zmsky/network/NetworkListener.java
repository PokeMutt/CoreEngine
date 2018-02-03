package me.zmsky.network;

public interface NetworkListener {
	public void Connected(Connection c);
	public void Disconnected(Connection c);
	public void Received(Connection c, Object o);
}
