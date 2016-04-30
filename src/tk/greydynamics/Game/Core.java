package tk.greydynamics.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.ImageIOImageData;

import tk.greydynamics.Entity.Entity;
import tk.greydynamics.Entity.Entities.ObjectEntity;
import tk.greydynamics.Entity.Layer.EntityLayer;
import tk.greydynamics.Event.EventHandler;
import tk.greydynamics.JavaFX.JavaFXHandler;
import tk.greydynamics.Mod.ModTools;
import tk.greydynamics.Model.RawModel;
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
	
	public static String PATH_DATA = "/Data/"; 
	public static String PATH_UPDATE = "/Update/"; 
	public static String PATH_PATCH = "/Patch/"; 
	public static String PATH_UPDATE_PATCH = FileHandler.normalizePath(PATH_UPDATE+PATH_PATCH);
	
	public static String EDITOR_PATH_TEMP = "temp/"; 
	public static String EDITOR_PATH_GAMEDATA = "games/"; 
	
	public static Random random = new Random();
	
	public static void main(String[] args){			
		String argLine = ""; 
		for (String s : args){
			argLine+=s+" "; 
		}
		if (argLine.equals("")){ 
			argLine = "[No-Arguments]"; 
		}
		System.out.println("Starting with Arguments: "+argLine); 
				
		/*Initialize Variables*/
		runnables = new ArrayList<Runnable>();
		runnablesQ = new ArrayList<Runnable>();
		isExecutingRunnables = false;
		//sharedObjs = null;
		gamePath = null;
		gameVersion = null;
		checkVersion();
		
		currentDir = FileHandler.normalizePath(Paths.get("").toAbsolutePath().toString()); 
		keepAlive = true;
		runEditor = false;
		
		FileHandler.cleanFolder("temp"); 
		FileHandler.cleanFolder("output"); 
		
		TICK_RATE = 20;
				
		DISPLAY_WIDTH = 1280; DISPLAY_HEIGHT = 720;
		DISPLAY_RATE = 60;
		
		zNear = 0.001f;
		zFar = 2500f;
		FOV = 60f;
		
		jfxHandler = new JavaFXHandler();
		license();
		
		eventHandler = new EventHandler();
		game = new Game();
		
		if (buildVersion.contains("NEW VERSION")){ 
			jfxHandler.getDialogBuilder().showInfo("Info", 
					"Make sure to run the latest version!\n"+ 
						"http://greydynamics.github.io/Frostbite3_Editor/"); 
		}
		
		jfxHandler.getDialogBuilder().showAsk("Do you accept the license terms?", "Do you accept the license terms?\n\n"
				+ "If you don't agree, the application will close automaticly.", null, new Runnable() {
					@Override
				public void run() {
					System.exit(133992);
				}
		});
		modTools = new ModTools();
		
		
//		jfxHandler.getMainWindow().createEventGraphWindow(Core.getGame().getResourceHandler().getEBXHandler().loadFile(FileHandler.readFile("__DOCUMENTATION__/eventgraph/pf_pipe_box_04.ebx")), true, true, false);
		
		
		//jfxHandler.getMainWindow().createImagePreviewWindow(null, null, new ResourceLink(), "test");
				
		/*Let's loop until user's exit request was initialized*/		
		while (keepAlive){
			/* Wait for starting editor.
			 * JavaFX is running on a different Thread,
			 * so we can slow this one down in the mean time.
			 */
			System.out.print(""); 
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
		            Display.setTitle("MapEditor / Template Viewer! (PLS IGNORE TERRAIN.."); 
		            Display.setResizable(true);
		            Display.create();
		            Display.setIcon(new ByteBuffer[] {
		            	new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon/16.png")), false, false, null), 
		                new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon/32.png")), false, false, null) 
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
				game.getEntityHandler().getGizmoHandler().init(game.getModelHandler());
				render = new Render(game);
				inputHandler = new InputHandler();
				
				
				EntityLayer debugLayer = new EntityLayer("debugLayer", null);
				float[] pos = new float[]{
						-1.000000f, -1.000000f, 1.000000f,
						-1.000000f, 1.000000f, 1.000000f,
						-1.000000f, -1.000000f, -1.000000f,
						-1.000000f, 1.000000f, -1.000000f,
						1.000000f, -1.000000f, 1.000000f,
						1.000000f, 1.000000f, 1.000000f,
						1.000000f, -1.000000f, -1.000000f,
						1.000000f, 1.000000f, -1.000000f};
				
				float[] normal = new float[]{
						-1.000000f, -1.000000f, 1.000000f, 0.0f,//working
						-1.000000f, 1.000000f, 1.000000f, 0.0f,
						-1.000000f, -1.000000f, -1.000000f, 0.0f,
						-1.000000f, 1.000000f, -1.000000f, 0.0f,
						1.000000f, -1.000000f, 1.000000f, 0.0f,
						1.000000f, 1.000000f, 1.000000f, 0.0f,
						1.000000f, -1.000000f, -1.000000f, 0.0f,
						1.000000f, 1.000000f, -1.000000f, 0.0f};
				
				int[] in = new int[]{
						4, 3, 1,
						8, 7, 3,
						6, 5, 7,
						2, 1, 5, 
						3, 7, 5, 
						8, 4, 2, 
						2, 4, 1, 
						4, 8, 3, 
						8, 6, 7, 
						6, 2, 5, 
						1, 3, 5, 
						6, 8, 2};
				for (int i=0; i<in.length; i++){
					in[i] = in[i]-1;
				}
				RawModel[] rawModels = new RawModel[]{game.getModelHandler().addRawModel(GL11.GL_TRIANGLES, "debugModel", pos, new float[pos.length/3*2], normal, in)};
				Entity debugEntity = new ObjectEntity(debugLayer, "debugEntity", null, null, rawModels, null, null);
				debugLayer.getEntities().add(debugEntity);
//				game.getEntityHandler().getLayers().add(debugLayer);
				
				
				
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
		System.out.println("Thanks for using!\n" 
				 + "If u have noticed any Bugs, make sure to report them\n" 
				 + "directly on Github to @GreyDynamics." 
				 + "\n\n" 
				 + "Have a good one, Bye!"); 

		System.exit(0);
	}
	private static void license() {
		Scanner in = new Scanner(System.in);
		if (!new File("LICENSE").exists()){
			System.err.println("No License file exists!");
			System.out.println("\n\nPress Enter to exit application.");
			System.exit(404);
		}
//		ArrayList<String> licenseLines = FileHandler.readTextFile("LICENSE");
		String command = "notepad LICENSE";
		try {
			Process child = Runtime.getRuntime().exec(command);
			child.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		String newVersion = ""; 
		try{
			URL url = new URL("https://raw.githubusercontent.com/GreyDynamics/FrostBite3_Editor/develop/version"); 
			URLConnection ec = url.openConnection();
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                ec.getInputStream(), "UTF-8")); 
	        String inputLine;
	        StringBuilder a = new StringBuilder();
	        while ((inputLine = in.readLine()) != null)
	            a.append(inputLine);
	        in.close();

	        newVersion += a.toString();
	        newVersion = newVersion.split("\\|")[0]; 
		}catch(Exception e){
			System.err.println("Could not get version info from GitHub..."); 
		}
		try{
			FileReader fr = new FileReader("version"); 
			
			BufferedReader br = new BufferedReader(fr);
			buildVersion = br.readLine();
			br.close();
			fr.close();
		}catch (Exception e){
			buildVersion = "n/a"; 
			System.err.println("NO VERSION FILE FOUND!"); 
		}
		System.out.println("Version: "+buildVersion); 
		if (buildVersion.contains("|")){ 
			isDEBUG = true;
			System.err.println("RUNNING IN DEBUG MODE!"); 
			String[] versionArgs = buildVersion.split("\\|"); 
			gamePath = versionArgs[1];
			buildVersion = versionArgs[0]+" DEBUG MODE! "; 
		}else if (!buildVersion.equalsIgnoreCase(newVersion) && !newVersion.equalsIgnoreCase("")){ 
			buildVersion += " | [NEW VERSION ADVILABLE]"; 
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
				String[] parts = path.split("/"); 
				if (parts[parts.length-1].endsWith("/")){ 
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
	public static InputHandler getInputHandler() {
		return inputHandler;
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
