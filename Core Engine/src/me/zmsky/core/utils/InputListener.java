package me.zmsky.core.utils;

import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

public interface InputListener {
	public void onMouseMove(int button, int x, int y, boolean Drag);
	public void onMouseWheelMove(MouseWheelEvent e);
	public void onKeyPressed(KeyEvent e);
	public void onKeyReleased(KeyEvent e);
	public void onMouseClick(int button, int x, int y);
	public void onMouseRelease(int button, int x, int y);
}
