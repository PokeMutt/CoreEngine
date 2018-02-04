package me.zmsky.resources;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.soap.Node;

public abstract class ImageCenter {
	private static Random random = new Random();
	private static String FilePath;
	
	private static Map<String, BufferedImage> cachedImages = new HashMap<String, BufferedImage>();
	
	public static BufferedImage ChangeImageColor(BufferedImage image, int Tolerance, Color Mask, Color Replace) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage NewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics painter = NewImage.getGraphics();
        
        painter.drawImage(image, 0, 0, null);
        
       	Color mask = new Color(Mask.getRGB(),true);
        Color replace = new Color(Replace.getRGB(),true);
        
        WritableRaster raster = NewImage.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null); 
                int r = pixels[0];
                int g = pixels[1];
                int b = pixels[2];
                
                if((r >= mask.getRed() - Tolerance) && r <= (mask.getRed() + Tolerance))
                	pixels[0] = replace.getRed();
                
                if((g >= mask.getGreen() - Tolerance) && g <= (mask.getGreen() + Tolerance))
                	pixels[1] = replace.getGreen();
                
                if((b >= mask.getBlue() - Tolerance) && b <= (mask.getBlue() + Tolerance))
                	pixels[2] = replace.getBlue();
                
                raster.setPixel(xx, yy, pixels);
            }
        }
        
        return NewImage;
    }
	public static BufferedImage getScaledImage(BufferedImage image, double Scale){
		Image scaled = image.getScaledInstance((int) (image.getWidth() * Scale), (int) (image.getHeight()* Scale), Image.SCALE_SMOOTH);
		BufferedImage newimage = new BufferedImage(scaled.getWidth(null),scaled.getHeight(null),image.getType());
		Graphics g = newimage.getGraphics();
		
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		
		return newimage;
	}
	public static BufferedImage FillColor(BufferedImage image, Color mask){
		BufferedImage newimage = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
		Graphics2D g = (Graphics2D) newimage.getGraphics();
		
		for(int y = 0; y < image.getHeight(); y++){
			for(int x = 0; x < image.getWidth(); x++){
				int pixel = image.getRGB(x, y);
				int alpha = (pixel >> 24) & 0xff;

				if(alpha > 0){
					g.setColor(mask);
					g.fillRect(x, y, 1, 1);
				}
			}
		}
		g.dispose();
		
		return newimage;
	}
	public static BufferedImage getImage(String name){
		BufferedImage sprite;
		
		name = FilePath + name;
		sprite = getCachedImage(name);
		
		if(sprite == null){
			try{ 
				InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
				sprite = ImageIO.read(stream); 
				cachedImages.put(name, sprite);
				
				stream.close();
			}
			catch (Exception a) { 
				a.printStackTrace();
				
				sprite = new BufferedImage(250,200,BufferedImage.TYPE_INT_ARGB);
				Graphics graphics = sprite.getGraphics(); graphics.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
				graphics.fillRect(0, 0, sprite.getWidth(), sprite.getHeight());
			}
		}
		
		return sprite;
	}
	public static List<BufferedImage> extractFrames(String path) throws IOException {
		List<BufferedImage> Animation = new ArrayList<BufferedImage>();
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
		ImageInputStream in = ImageIO.createImageInputStream(stream);
		reader.setInput(in);
		
		for (int i = 0, count = reader.getNumImages(true); i < count; i++)
		{
		    BufferedImage image = reader.read(i);
		    
		    boolean Empty = true;
		    for(int y = 0; y < image.getHeight(); y++){
		    	for(int x = 0; x < image.getWidth(); x++){
		    		int pixel = image.getRGB(x, y);
		    		
		    		if(!((pixel>>24) == 0x00)){
		    			Empty = false;
		    			break;
		    		}
		    	}
		    }
		    
		    if(!Empty)
		    	Animation.add(image);
		}
		
		stream.close();
		in.close();
		
	    return Animation;
	}
    public static BufferedImage getNoiseImage(int width, int height){
        Random rand = new Random();
        BufferedImage imgg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) imgg.getRaster().getDataBuffer()).getData();
         
        for(int i = 0; i < pixels.length;i++){
            pixels[i] = rand.nextInt();
        }
         
        return imgg;
    }
    public static BufferedImage RotateImage(BufferedImage image,double Degrees){
    	BufferedImage i = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = (Graphics2D) i.getGraphics();
    	
    	g.rotate(Math.toRadians(Degrees));
    	g.drawImage(image, null, 0, 0);
    	
    	return i;
    }
	public static BufferedImage FlipImageHorizontally(BufferedImage image){
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-image.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}
	public static BufferedImage FlipImageVertically(BufferedImage image){
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -image.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);
		return image;
	}
	private static BufferedImage getCachedImage(String name){ return cachedImages.get(name); }
    public static void setImageCenterDefaultPath(String Path){ FilePath = Path; }
}
