package me.zmsky.core.shaders;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ShaderHandler {
	private List<Shader> shaders;	
	private BufferedImage shaderBuffer;
		
	public ShaderHandler(int bufferWidth, int bufferHeight){
		shaders = new ArrayList<>();
		shaderBuffer = new BufferedImage(bufferWidth, bufferHeight,BufferedImage.TYPE_INT_ARGB);
	}
	public void Update(double delta){
		synchronized(shaders){
			for(Shader s : shaders){
				s.Update(delta);
			}
		}
	}
	public void Render(BufferedImage buffer, Graphics2D g){
		shaderBuffer = buffer;
		
		synchronized(shaders){
			for(Shader s : shaders){
				s.Render(shaderBuffer, (Graphics2D) shaderBuffer.getGraphics());
			}
		}
		
		buffer = shaderBuffer;
	}
	public void addShader(Shader s){
		synchronized(shaders){
			shaders.add(s);
		}
	}
	public void removeShader(Shader s){
		synchronized(shaders){
			shaders.remove(s);
		}
	}
}
