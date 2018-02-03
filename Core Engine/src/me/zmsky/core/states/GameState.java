package me.zmsky.core.states;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

import me.zmsky.core.utils.InputListener;

public abstract class GameState implements InputListener{
	
	public void RenderGui(Graphics2D g){
		
	}
	public void Render(Graphics2D g){
		
	}
	public void Update(double delta){
		
	}
	public void EnterState(){
		
	}
	public void LeftState(){
		
	}
	public void GameClosing(){
		
	}
	@Override
	public void onMouseMove(int button, int x, int y, boolean Drag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheelMove(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onKeyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseClick(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseRelease(int button, int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
	public abstract Enum<?> getStateID();
}
