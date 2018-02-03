package me.zmsky.core.shaders;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface Shader {
	public void Render(BufferedImage buffer, Graphics2D g);	
	public void Update(double delta);
}
