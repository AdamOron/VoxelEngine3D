package renderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;

public class DisplayManager
{
	private static final int WIDTH = 1920, HEIGHT = 1080;
	private static final int FPS_CAP = 240;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static void createDisplay()
	{
		ContextAttribs attribs = new ContextAttribs(3,2)
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(), attribs);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);
		
		lastFrameTime = getCurrentTime();
		
		Mouse.setGrabbed(true);
//		try {
//			Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
//		} catch (LWJGLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void updateDisplay()
	{	
		Display.sync(FPS_CAP);
		Display.update();
		
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		
		lastFrameTime = getCurrentTime();
		
		Display.setTitle((int)getFrameRate()+"");
	}
	
	public static void closeDisplay()
	{
		Display.destroy();
	}
	
	private static long getCurrentTime()
	{
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
	public static float getFrameTimeSeconds()
	{
		return delta;
	}
	
	public static float getFrameRate()
	{
		return 1 / delta;
	}
}
