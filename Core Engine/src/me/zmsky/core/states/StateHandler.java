package me.zmsky.core.states;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import me.zmsky.core.utils.InputHandler;

public class StateHandler {
	
	private List<GameState> states;
	private GameState currentState;
	private InputHandler handler;
	
	public StateHandler(){
		states = new ArrayList<GameState>();
		handler = new InputHandler();
	}
	public void registerState(GameState state){
		states.add(state);
		handler.setCurrentListener(state);
		
		if(currentState == null)
			currentState = state;
	}
	public void enterState(Enum<?> stateID){
		if(currentState != null)
			currentState.LeftState();
		
		for(GameState s : states){
			if(s.getStateID().equals(stateID)){
				currentState = s;
				currentState.EnterState();
				handler.setCurrentListener(currentState);
				
				break;
			}
		}
	}
	public void updateStates(double delta){
		if(currentState != null)
			currentState.Update(delta);
	}
	public void renderStates(Graphics2D g){
		if(currentState != null)
			currentState.Render(g);
	}
	public void renderGui(Graphics2D g){
		if(currentState != null)
			currentState.RenderGui(g);
	}
	public void notifyExit(){
		for(GameState state : states){
			state.GameClosing();
		}
	}
	public InputHandler getInputHandler(){ return handler; }
}
