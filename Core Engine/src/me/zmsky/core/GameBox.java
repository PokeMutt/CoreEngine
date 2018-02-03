package me.zmsky.core;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import me.zmsky.core.shaders.ShaderHandler;
import me.zmsky.core.states.StateHandler;
import me.zmsky.core.utils.CommonTools;
import me.zmsky.core.utils.CrashReportScreen;
import me.zmsky.core.utils.StackTraceUtils;

public abstract class GameBox {
	
	private static GameLoop loop;
	
	public static void InitGame(String gameTitle, int windowWidth, int windowHeight){
		loop = new GameLoop(gameTitle, windowWidth, windowHeight);
	}
	public static void setTickSpeed(double TicksPerSecond){ loop.setTickSpeed(TicksPerSecond); }
	public static AlphaComposite getDefaultComposite(){ return loop.getDefaultComposite(); }
	public static int getTicksPerSecond(){ return loop.getTicksPerSecond(); }
	public static Font getDefaultFont(){ return loop.getDefaultFont(); }
	public static StateHandler getStateHandler(){ return loop.getStateHandler(); }
	public static ShaderHandler getShaderHandler(){ return loop.getShaderHandler(); }
	public static int getFramesPerSecond(){ return loop.getFramePerSecond(); }
	public static JFrame getWindowFrame(){ return loop.window; }
	public static int getWindowHeight(){ return loop.getHeight(); }
	public static int getWindowWidth(){ return loop.getWidth(); }
	public static void StartGame(){ loop.start(); }
	public static void StopGame(){ loop.stop(); }
	
	
	private static class GameLoop implements Runnable, UncaughtExceptionHandler{
		private JFrame window;
		private Canvas canvas;
		private BufferStrategy strategy;
		
		private boolean Running;
		private boolean Error;
		
		private Font defaultFont;	
		private BufferedImage Buffer;	//For double buffering
		private StateHandler stateHandler;
		private CrashReportScreen crashScreen;
		private ShaderHandler shaderHandler;
		private AlphaComposite composite;
		
		private static double TickSpeed = 1000000000.0 / 60.0;
		private static int FPS,TPS;
		
		public GameLoop(String title, int width, int height){
			stateHandler = new StateHandler();
			crashScreen = new CrashReportScreen();
			shaderHandler = new ShaderHandler(width, height);
			
			defaultFont = CommonTools.loadFont("Font/MF.ttf");
			
			window = new JFrame();
			
			canvas = new Canvas();
			canvas.setSize(width, height);
			canvas.setIgnoreRepaint(true);
			canvas.setBackground(Color.black);
			
			window.add(canvas);
			window.pack();
			
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
			
			canvas.addMouseListener(stateHandler.getInputHandler());
			canvas.addMouseMotionListener(stateHandler.getInputHandler());
			canvas.addMouseWheelListener(stateHandler.getInputHandler());
			canvas.addKeyListener(stateHandler.getInputHandler());
			
			window.setTitle(title);
			window.setSize(width, height);
			window.addComponentListener(new ComponentListener() {
			    public void componentResized(ComponentEvent e) {
			        createBuffer();
			    }
				public void componentHidden(ComponentEvent arg0) {}
				public void componentMoved(ComponentEvent arg0) {}
				public void componentShown(ComponentEvent arg0) {}
			});
			window.addWindowListener(new WindowListener() {
				public void windowOpened(WindowEvent e) {}
				public void windowIconified(WindowEvent e) {}
				public void windowDeiconified(WindowEvent e) {}
				public void windowDeactivated(WindowEvent e) {}
				public void windowClosing(WindowEvent e) { stop(); }
				public void windowClosed(WindowEvent e) {}
				public void windowActivated(WindowEvent arg0) {}
			});
			
			window.setLocationRelativeTo(null);
			
			createBuffer();
			
			Thread.setDefaultUncaughtExceptionHandler(this);
		}
		public void run(){
			try{
				long Current, SnapB, SnapA = System.currentTimeMillis(), Last = System.nanoTime(),Timer = System.currentTimeMillis();
				int fps = 0, tps = 0;
				double Delta = 0;
				
				while(Running){
					SnapB = System.currentTimeMillis();
					
					Current = System.nanoTime();
					Delta+= (Current - Last) / TickSpeed;
					Last = Current;
					
					while(Delta >= 1){
						Update(Delta);
						tps++;
						Delta--;
					}
					
					if(SnapB >= SnapA+(1000/60)){
						SnapA = SnapB;
						
						Render();
						fps++;
						
						if(System.currentTimeMillis() - Timer >= 1000){
							TPS = tps;
							FPS = fps;
							
							fps = 0;
							tps = 0;
							
							Timer+= 1000;
						}
					}
					else
						continue;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			stateHandler.notifyExit();
			
			if(!Error)
				System.exit(0);
		}
		public void start(){
			Running = true;
			window.setVisible(true);
			
			new Thread(this).start();
		}
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace(); 
			Running = false;
			Error = true;
			
			window.dispose();
			
			crashScreen.setError("Unexpected Exception in thread " + t.getName() + ": " + "\n" + StackTraceUtils.getStackTrace(e));
			new Thread(crashScreen).run();
		}
		public void Render(){
			Graphics2D g = (Graphics2D) Buffer.getGraphics();
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			stateHandler.renderStates(g);
			shaderHandler.Render(Buffer, g);
			stateHandler.renderGui(g);
			
			g.dispose();
			
			g = (Graphics2D) strategy.getDrawGraphics();
			composite = (AlphaComposite) g.getComposite();
			
			g.drawImage(Buffer, 0, 0, null);
			g.dispose();
			strategy.show();
		}
		private void Update(double Delta){ 
			stateHandler.updateStates(Delta); 
			shaderHandler.Update(Delta); 
		}
		private void createBuffer(){
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
			Buffer = gc.createCompatibleImage(window.getWidth(), window.getHeight(), Transparency.TRANSLUCENT);
			Buffer.setAccelerationPriority(1);
			
			//Buffer = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_ARGB); 
			composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER);
		}
		public AlphaComposite getDefaultComposite(){ return composite; }
		public void stop(){ Running = false; }
		public int getTicksPerSecond(){ return TPS; }
		public int getFramePerSecond(){ return FPS; }
		public Font getDefaultFont(){ return defaultFont; }
		public int getWidth(){ return canvas.getWidth(); }
		public int getHeight(){ return canvas.getHeight(); }
		public StateHandler getStateHandler(){ return stateHandler; }
		public ShaderHandler getShaderHandler(){ return shaderHandler; }
		public void setTickSpeed(double TicksPerSecond){ TickSpeed = 100000000.0 / TicksPerSecond; }
	}
}
