package tk.greydynamics.Render.FrameBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Vector2f;


public class FrameBufferHandler {
	//Basecode from ThinMatrix (Water Shader);
	private int pickingFrameBuffer = -1;
	private int pickingTexture;
	private int pickingDepthBuffer;
	private Vector2f pickingSize;
	private FloatBuffer rgbaPickingBuffer;

	public FrameBufferHandler(){
		initialisePickingFrameBuffer(Display.getWidth(), Display.getHeight());
	}

	public void bindPickingFrameBuffer() {
		bindFrameBuffer(pickingFrameBuffer, this.pickingSize);
	} 

	public void unbindCurrentFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public int getPickingFrameBuffer() {
		return pickingFrameBuffer;
	}

	public int getPickingTexture() {
		return pickingTexture;
	}

	public int getPickingDepthBuffer() {
		return pickingDepthBuffer;
	}
	public void cleanUp() {//call when closing the game
		cleanUpPickingFrameBuffer();
	}

	private void initialisePickingFrameBuffer(int width, int height) {
		if (pickingDepthBuffer==-1){
			cleanUpPickingFrameBuffer();
		}
		pickingFrameBuffer = createFrameBuffer();
		pickingTexture = createTextureAttachment(width, height);
		pickingDepthBuffer = createDepthBufferAttachment(width, height);
		pickingSize = new Vector2f(width, height);
		rgbaPickingBuffer = BufferUtils.createFloatBuffer(
				(int) (1 *
						1 *
				4)
			);
		unbindCurrentFrameBuffer();
	}

	private void cleanUpPickingFrameBuffer(){
		GL30.glDeleteFramebuffers(pickingFrameBuffer);
		GL11.glDeleteTextures(pickingTexture);
		GL30.glDeleteRenderbuffers(pickingDepthBuffer);
	}
	private void bindFrameBuffer(int frameBuffer, Vector2f size){
		bindFrameBuffer(frameBuffer, (int) size.x, (int) size.y);
	}

	private void bindFrameBuffer(int frameBuffer, int width, int height){
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);//To make sure the texture isn't bound
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	private int createFrameBuffer() {
		int frameBuffer = GL30.glGenFramebuffers();
		//generate name for frame buffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		//create the framebuffer
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		//indicate that we will always render to color attachment 0
		return frameBuffer;
	}

	private int createTextureAttachment(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
				0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
				texture, 0);
		return texture;
	}

	//    private int createDepthTextureAttachment(int width, int height){
	//        int texture = GL11.glGenTextures();
	//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	//        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height,
	//                0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
	//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	//        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
	//                texture, 0);
	//        return texture;
	//    }

	private int createDepthBufferAttachment(int width, int height) {
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width,
				height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
				GL30.GL_RENDERBUFFER, depthBuffer);
		return depthBuffer;
	}

	public Vector2f getPickingSize() {
		return pickingSize;
	}

	public FloatBuffer getRGBAPickingBuffer() {
		return rgbaPickingBuffer;
	}
}
