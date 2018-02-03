package me.zmsky.core.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;

public abstract class CommonTools {
	public static Font loadFont(String path){
		try{
			InputStream i = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			Font font = Font.createFont(Font.TRUETYPE_FONT, i);
			
			return font;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	public static Color getDarkerColor(Color c, int iterations){
		c = c.darker();
		
		if(iterations != 0)
			return getDarkerColor(c, iterations-1);
		else
			return c;
	}
    public static Rectangle2D getStringBounds(Graphics2D g, Font f, String content)
	{
    	return f.createGlyphVector(g.getFontMetrics().getFontRenderContext(), content).getVisualBounds();
	}
}
