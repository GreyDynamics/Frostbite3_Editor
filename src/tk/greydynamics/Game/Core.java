package tk.greydynamics.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.ImageIOImageData;

import tk.greydynamics.Messages;
import tk.greydynamics.Event.EventHandler;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.Maths.Matrices;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Render.Render;
import tk.greydynamics.Render.Gui.GuiTexture;
import tk.greydynamics.Resource.FileHandler;

public class Core {
	/*Main Components*/
	private static Game game;
	private static Render render;
	private static EventHandler eventHandler;
	private static InputHandler inputHandler;
	private static ModTools modTools;
	private static JavaFXHandler jfxHandler;
		
	/*Defaults for Screen Configuration.*/
	public static int DISPLAY_WIDTH;
	public static int DISPLAY_HEIGHT;
	public static int DISPLAY_RATE;
	public static float zNear;
	public static float zFar;
	public static float FOV;
	
	/*Boring stuff to make Ticks possible.*/
	public static int TICK_RATE;
	public static int currentTick = -1;
	public static int currentTime = 0;
	public static int oldTime = -1;
	
	/*Active session*/
	public static String gamePath;
	public static String gameName;
	public static boolean keepAlive;
	public static boolean runEditor;
	public static boolean isDEBUG;
	public static String currentDir;
	public static String gameVersion;
	
	/*You w00t mate ?*/
	public static String buildVersion;
		
	/*Support for Cross-Threading*/
	private static ArrayList<Runnable> runnables;
	private static ArrayList<Runnable> runnablesQ;
	private static boolean isExecutingRunnables;	
	
	public static String PATH_DATA = "/Data/"; //$NON-NLS-1$
	public static String PATH_UPDATE = "/Update/"; //$NON-NLS-1$
	public static String PATH_PATCH = "/Patch/"; //$NON-NLS-1$
	public static String PATH_UPDATE_PATCH = FileHandler.normalizePath(PATH_UPDATE+PATH_PATCH);
	
	public static String EDITOR_PATH_TEMP = "temp/"; //$NON-NLS-1$
	public static String EDITOR_PATH_GAMEDATA = "games/"; //$NON-NLS-1$
	
	public static Random random = new Random();
	
	public static void main(String[] args){
		
//		Vector3f up = 		new Vector3f(1.0f, 0.0f, 0.0f);
//		Vector3f forward =  new Vector3f(0.0f, -1.0f, 0.0f);
//		Vector3f right = 	new Vector3f(0.0f, 0.0f, 1.0f);
//		
//		System.out.println("Scale X: "+up.length());
//		System.out.println("Scale Y: "+forward.length());
//		System.out.println("Scale Z: "+right.length());
//		System.exit(0);		
		
		
		String argLine = ""; //$NON-NLS-1$
		for (String s : args){
			argLine+=s+" "; //$NON-NLS-1$
		}
		if (argLine.equals("")){ //$NON-NLS-1$
			argLine = Messages.getString("Core.8"); //$NON-NLS-1$
		}
		System.out.println(Messages.getString("Core.9")+argLine); //$NON-NLS-1$
				
		/*Initialize Variables*/
		runnables = new ArrayList<Runnable>();
		runnablesQ = new ArrayList<Runnable>();
		isExecutingRunnables = false;
		//sharedObjs = null;
		gamePath = null;
		gameVersion = null;
		checkVersion();
		
		currentDir = FileHandler.normalizePath(Paths.get("").toAbsolutePath().toString()); //$NON-NLS-1$
		keepAlive = true;
		runEditor = false;
		
		FileHandler.cleanFolder("temp"); //$NON-NLS-1$
		FileHandler.cleanFolder("output"); //$NON-NLS-1$
		
		TICK_RATE = 20;
				
		DISPLAY_WIDTH = 1280; DISPLAY_HEIGHT = 720;
		DISPLAY_RATE = 60;
		
		zNear = 1f;
		zFar = 2500f;
		FOV = 60f;
		
		jfxHandler = new JavaFXHandler();
		eventHandler = new EventHandler();
		game = new Game();
		if (buildVersion.contains(Messages.getString("Core.13"))){ //$NON-NLS-1$
			jfxHandler.getDialogBuilder().showInfo(Messages.getString("Core.14"), //$NON-NLS-1$
					Messages.getString("Core.15")+ //$NON-NLS-1$
						"http://greydynamics.github.io/Frostbite3_Editor/"); //$NON-NLS-1$
		}
		jfxHandler.getDialogBuilder().showWarning(Messages.getString("Core.0"), //$NON-NLS-1$
				Messages.getString("Core.18") //$NON-NLS-1$
				+ Messages.getString("Core.19") //$NON-NLS-1$
				+ Messages.getString("Core.20") //$NON-NLS-1$
				+ Messages.getString("Core.21"), null); //$NON-NLS-1$
		modTools = new ModTools();
		
		//jfxHandler.getMainWindow().createImagePreviewWindow(null, null, new ResourceLink(), "test");
				
		/*Let's loop until user's exit request was initialized*/		
		while (keepAlive){
			/* Wait for starting editor.
			 * JavaFX is running on a different Thread,
			 * so we can slow this one down in the mean time.
			 */
			System.out.print(""); //$NON-NLS-1$
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			/*Mod was selected -> Run Editor*/
			if (runEditor){
				jfxHandler.getMainWindow().toggleLeftVisibility();
				
				jfxHandler.getMainWindow().toggleModLoaderVisibility();
				
				try {
		            Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
		            Display.setTitle(Messages.getString("Core.23")); //$NON-NLS-1$
		            Display.setResizable(true);
		            Display.create();
		            Display.setIcon(new ByteBuffer[] {
		            	new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon/16.png")), false, false, null), //$NON-NLS-1$
		                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon/32.png")), false, false, null) //$NON-NLS-1$
		            });
		            Mouse.setClipMouseCoordinatesToWindow(true);
		            //Mouse.setGrabbed(true);
		        } catch (Exception e) {
		            e.printStackTrace();
		            Display.destroy();
		            System.exit(1);
		        }
				game.getModelHandler().getLoader().init();
				game.getShaderHandler().init();
				game.buildExplorerTree();
				game.getGuis().add(new GuiTexture(game.getModelHandler().getLoader().getCrosshairID(), new Vector2f(0.0375f, -0.0375f), new Vector2f(0.15f, 0.15f)));
				render = new Render(game);
				inputHandler = new InputHandler();
				
				//Debug - PickingFrameBufferImage
				game.getGuis().add(new GuiTexture(render.getFrameBufferHandler().getPickingTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.5f, -0.3f)));
				
								
				while(!Display.isCloseRequested() && keepAlive && runEditor){
					currentTime = (int) (System.currentTimeMillis()%1000/(1000/TICK_RATE));
					if (currentTime != oldTime){
						oldTime = currentTime;
						currentTick++;
						
						//update at rate
						game.update();
						game.getModelHandler().addLifeTick();
						if (currentTick%(TICK_RATE/4)==0){//0.25xTICK_RATE
							game.lowRateUpdate();
						}
						if (currentTick%(TICK_RATE*10)==0){//Every 10 Seconds
							game.getModelHandler().cleanUnused();
						}
						eventHandler.listen();
					}
					//update instantly
					inputHandler.listen();
					render.update();
					
					/*Proccess all runnables*/
					isExecutingRunnables = true;
					for (Runnable runna : runnables){
						runna.run();
					}
					runnables.clear();
					isExecutingRunnables = false;
					for (Runnable runnaQ : runnablesQ){
						runnables.add(runnaQ);
					}
					runnablesQ.clear();
					/*End of Runnable section*/
					
					
					
				}
				//CleanUp GPU-Memory!
				game.getModelHandler().getLoader().cleanUp();
				game.getShaderHandler().cleanUpAll();
				render.getFrameBufferHandler().cleanUp();
				runEditor = false;
				keepAlive = false;
			}
		}
		System.out.println(Messages.getString("Core.26") //$NON-NLS-1$
				 + Messages.getString("Core.27") //$NON-NLS-1$
				 + Messages.getString("Core.28") //$NON-NLS-1$
				 + "\n\n" //$NON-NLS-1$
				 + Messages.getString("Core.30")); //$NON-NLS-1$

		System.exit(0);
	}
	public static void runOnMainThread(Runnable run){
		if (isExecutingRunnables){
			runnablesQ.add(run);
		}else{
			runnables.add(run);
		}
	}
	
	public static Game getGame(){
		return game;
	}
	
	public static Render getRender(){
		return render;
	}
	
	public static EventHandler getEventHander(){
		return eventHandler;
	}

	public static JavaFXHandler getJavaFXHandler() {
		return jfxHandler;
	}

	public static ModTools getModTools() {
		return modTools;
	}
	/*public static Object[] getSharedObjs() {
		return sharedObjs;
	}
	public static void setSharedObjs(Object[] sharedObjs) {
		Core.sharedObjs = sharedObjs;
	}*/
	
	public static void checkVersion(){
		/*VERSION CHECK*/
		String newVersion = ""; //$NON-NLS-1$
		try{
			URL url = new URL("https://raw.githubusercontent.com/GreyDynamics/FrostBite3_Editor/develop/version"); //$NON-NLS-1$
			URLConnection ec = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                ec.getInputStream(), "UTF-8")); //$NON-NLS-1$
	        String inputLine;
	        StringBuilder a = new StringBuilder();
	        while ((inputLine = in.readLine()) != null)
	            a.append(inputLine);
	        in.close();

	        newVersion += a.toString();
	        newVersion = newVersion.split("\\|")[0]; //$NON-NLS-1$
		}catch(Exception e){
			System.err.println(Messages.getString("Core.35")); //$NON-NLS-1$
		}
		try{
			FileReader fr = new FileReader(Messages.getString("Core.36")); //$NON-NLS-1$
			
			BufferedReader br = new BufferedReader(fr);
			buildVersion = br.readLine();
			br.close();
			fr.close();
		}catch (Exception e){
			buildVersion = Messages.getString("Core.37"); //$NON-NLS-1$
			System.err.println(Messages.getString("Core.38")); //$NON-NLS-1$
		}
		System.out.println(Messages.getString("Core.39")+buildVersion); //$NON-NLS-1$
		if (buildVersion.contains("|")){ //$NON-NLS-1$
			isDEBUG = true;
			System.err.println(Messages.getString("Core.41")); //$NON-NLS-1$
			String[] versionArgs = buildVersion.split("\\|"); //$NON-NLS-1$
			gamePath = versionArgs[1];
			buildVersion = versionArgs[0]+Messages.getString("Core.43"); //$NON-NLS-1$
		}else if (!buildVersion.equalsIgnoreCase(newVersion) && !newVersion.equalsIgnoreCase("")){ //$NON-NLS-1$
			buildVersion += Messages.getString("Core.45"); //$NON-NLS-1$
		}
		/*END OF VERSION CHECK*/
	}
	public static void keepAlive(boolean b) {
		keepAlive = b;
	}
	
	public static boolean setGamePath(String path){
		Core.gamePath = path;
		if (path!=null){
			try{
				String[] parts = path.split("/"); //$NON-NLS-1$
				if (parts[parts.length-1].endsWith("/")){ //$NON-NLS-1$
					Core.gameName = parts[parts.length-2];
				}else{
					Core.gameName = parts[parts.length-1];
				}
				return true;
			}catch(IndexOutOfBoundsException e){
				Core.gameName = null;
			}
		}else{
			Core.gameName = null;
		}
		return false;
	}
	
	/*public static void stackEntityTest(int i, int max, Entity parent){
		for (int ie = 0; ie<15;ie++){
			if (i<=max){
				i++;
				Entity e = new ObjectEntity("test"+i, null, parent, null);
				e.setPosition(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
				e.setRotation(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()));
				stackEntityTest(i, max, e);
				parent.getChildrens().add(e);
			}else{
				break;
			}
		}
	}*/
}
