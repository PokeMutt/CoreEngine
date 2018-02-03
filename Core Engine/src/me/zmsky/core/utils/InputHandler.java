package me.zmsky.core.utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
	//Current object that needs is receiving input.
	private InputListener CurrentListener;
	
	public void mouseReleased(MouseEvent e) {
		if(CurrentListener == null) return;
		CurrentListener.onMouseRelease(e.getButton(), e.getX(), e.getY());
	}
	public void mousePressed(MouseEvent e) {
		if(CurrentListener == null) return;
		CurrentListener.onMouseClick(e.getButton(), e.getX(), e.getY());
	}
	public void mouseDragged(MouseEvent e) {
		if(CurrentListener == null)
			return; 
		
		CurrentListener.onMouseMove(e.getButton(), e.getX(), e.getY(), true);
	}
	public void mouseMoved(MouseEvent e) {
		if(CurrentListener == null)
			return;
		
		CurrentListener.onMouseMove(e.getButton(), e.getX(), e.getY(), false);
	}
	public void keyPressed(KeyEvent e) { if(CurrentListener == null) return; CurrentListener.onKeyPressed(e); }
	public void keyReleased(KeyEvent e) { if(CurrentListener == null) return; CurrentListener.onKeyReleased(e); }	
	public void mouseWheelMoved(MouseWheelEvent e) { if(CurrentListener == null) return; CurrentListener.onMouseWheelMove(e); }
	public void keyTyped(KeyEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void setCurrentListener(InputListener listener){ CurrentListener = listener; }
	public void mouseClicked(MouseEvent e) {}

}
