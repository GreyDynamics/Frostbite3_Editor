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
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.opengl.ImageIOImageData;

import tk.greydynamics.Event.EventHandler;
import tk.greydynamics.JavaFX.JavaFXHandler;
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
		
		zNear = 1f;
		zFar = 2500f;
		FOV = 60f;
		
		jfxHandler = new JavaFXHandler();
		eventHandler = new EventHandler();
		game = new Game();
		if (buildVersion.contains("NEW VERSION")){
			jfxHandler.getDialogBuilder().showInfo("Info",
					"Make sure to run the latest version!\n"+
						"http://greydynamics.github.io/Frostbite3_Editor/");
		}
		jfxHandler.getDialogBuilder().showWarning("WARNING",
				"This project is in development!\n"
				+ "\nA lot of functions are missing or bugged.\n"
				+ "There is no support given at this time!\n\n"
				+ "PS: This tool can cause the killing of your kittens.\n", null);
		modTools = new ModTools();
		
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
		            Display.setDisplayMode(new DisplayMode(400, 10));
		            Display.setTitle("PLEASE IGNORE/MINIMIZE THIS WINDOW!");
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
				game.getGuis().add(new GuiTexture(game.getModelHandler().getLoader().getCrosshairID(), new Vector2f(0.0f, 0.0f), new Vector2f(0.15f, 0.15f)));
				render = new Render(game);	
				inputHandler = new InputHandler();
				
				
				
				
				
				
				
				/*
				MeshChunkLoader msl = game.getResourceHandler().getMeshChunkLoader();
				msl.loadFile(FileHandler.readFile("C:\\Program Files (x86)\\Origin Games\\Battlefield 4\\single_dump_fs_xp7\\bundles\\res\\xp7\\levels\\mp_valley\\objects\\props\\signs\\sign_02_mesh 6d14a9804ba3fcbd 6002000080000000340000007000c000.mesh"), Core.getGame().getCurrentBundle());
				RawModel[] rawModels = new RawModel[msl.getSubMeshCount()];
				for (int submesh=0; submesh<msl.getSubMeshCount();submesh++){
					RawModel model = game.getModelHandler().addRawModel(GL11.GL_TRIANGLES, msl.getName()+submesh, msl.getVertexPositions(submesh), msl.getUVCoords(submesh), msl.getIndices(submesh));
					
					int textureID = game.getModelHandler().getLoader().getNotFoundID();
					rawModels[submesh] = model;
				}
				Entity en = new ObjectEntity("test", null, null, rawModels, null);
				EntityLayer layer = new EntityLayer("testlayer");
				layer.getEntities().add(en);
				game.getEntityHandler().getLayers().add(layer);
				*/
				
				
				
				
				
				
				
				/*EntityLayer testLayer = new EntityLayer("test");
				Entity parentEnt = new ObjectEntity("test", null, null, null);
				stackEntityTest(0, 15, parentEnt);//15 means ~65.000!!!
				testLayer.getEntities().add(parentEnt);
				game.getEntityHandler().getLayers().add(testLayer);*/
								
				while(!Display.isCloseRequested() && keepAlive){
					currentTime = (int) (System.currentTimeMillis()%1000/(1000/TICK_RATE));
					if (currentTime != oldTime){
						oldTime = currentTime;
						currentTick++;
						
						//update at rate
						game.update();
						if (currentTick%(TICK_RATE/4)==0){//0.25xTICK_RATE
							game.lowRateUpdate();
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
				game.getModelHandler().getLoader().cleanUp(); //CleanUp GPU-Memory!
				game.getShaderHandler().cleanUpAll();
			}
		}
		System.out.println("Thanks for using!\n"
				 + "If u have noticed any Bugs, make sure to report them\n"
				 + "directly on Github to @GreyDynamics."
				 + "\n\n"
				 + "Have a good one, Bye!");

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
