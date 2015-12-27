package com.game.puntem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;


/**
* This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
* renderers -- the static class GLES20 is used instead.
*/
public class TriangleRenderer implements GLSurfaceView.Renderer
{
	private AssetManager assets;
	private Resources resources;
	private Shader shader;
	private Shader shaderPicking;
	private Texture tex;
	DisplayMetrics displayMetrics = new DisplayMetrics();
	
	
	
	
	private Queue<int[]> touches;
	
	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	private float[] mViewMatrix = new float[16];

	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	private float[] mProjectionMatrix = new float[16];

	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mVPMatrix = new float[16];
	private float[] mInvVPMatrix = new float[16];
	private float[] mInvVMatrix = new float[16];

	int[] fboId = new int[1];
	int[] textureId = new int[1];
	int[] textureDepthId = new int[1];
	
	
	
	private float gameTime;
	private float gameTimeElapsed; //zeit die seit dem letzten frame vergangen ist.
	
	private float trianglesPerSecond;
	

	private int lastTriangleIndex;
	private float lastTriangleBirth;
	
	private Map<String, GameObject> gameObjects;
	private Map<String, Model> gameModels;
	
	
	
	

	/**
	 * Initialize the model data.
	 */
	public TriangleRenderer(AssetManager assets, Resources resources, DisplayMetrics displayMetrics) //AssetManager assets, Resources resources //final Context activityContext
	{	
		this.displayMetrics = displayMetrics;
		this.assets = assets;
		this.resources = resources;
		
		this.gameObjects = new HashMap<String, GameObject>();
		this.gameModels = new HashMap<String,Model>();
		
		this.touches = new ConcurrentLinkedQueue<int[]>();
		
		this.lastTriangleBirth = 0;
		this.lastTriangleIndex = 0;
		this.trianglesPerSecond = 1.0f;
		
		setupModels();
		setupGameObjects();
	}
	

	// executed at startup
	private void setupModels()
	{
		gameModels.clear();
		
		gameModels.put("triangle", Model.createTriangle());
		gameModels.put("quad", Model.createQuad());
	}	
	// executed at startup
	private void setupGameObjects()
	{		
		gameObjects.clear();
	}
	
	
	
	
	
	
	// executed once per frame
	private void updateGameObjects()
	{
		//lastTriangleBirth saves the time where the last triangle spawned. gameTime saves the current system time. when the difference is bigger than one second then a new triangle is created.
		if ((gameTime - lastTriangleBirth) > (1.0f / trianglesPerSecond))
		{
			spawnTriangle();
		}
		
		//goes through every gameobject and rotates it.
		for ( GameObject go : gameObjects.values())
		{
			go.rotateZ(gameTimeElapsed * 180.0f);
		}
	}
	
	static Random rand = new Random();
	
	//creates a new triangle
	private void spawnTriangle() 
	{
		//creates a new gameobject with triangle as model and spawns it somewhere.
		GameObject t = new GameObject(gameModels.get("triangle"));
		
		t.translate((rand.nextFloat() - 0.5f) * 4.0f, 
					(rand.nextFloat() - 0.5f) * 4.0f, 
					-rand.nextFloat() * 6.0f);	
		t.scale(1.5f);
		
		//shows the actual triangle by putting it into the gameobjects list + gives it a name triangle1, triangle2 etc..
		gameObjects.put("Triangle " + Integer.toString(lastTriangleIndex), t);
		
		lastTriangleIndex++;
		lastTriangleBirth = gameTime;
		trianglesPerSecond += 0.05;		
	}


	// executed once per frame (at the begin)
	private void updateTime()
	{
        long timeInMilliseconds = SystemClock.uptimeMillis();
        float timeInSeconds = (float)(((double)timeInMilliseconds) / 1000.0);
        
        gameTimeElapsed = gameTime - timeInSeconds;
        gameTime = timeInSeconds;
	}
	
	
	private void updateInput()
	{
		List<int[]> list = new ArrayList<int[]>();
		
		while( !touches.isEmpty()){
			list.add(touches.poll()); //poll reads one element of the queue(touches) and deletes it from the queue.
		}
		
		//iterating through the list and executing the onTouched function for each touch on the screen...doesnt matter if an object is touched or not.
		for (int[] position : list){
			onTouched(position[0], position[1]);
		}
	}


	private void recalcViewProjectionMatrix()
	{
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		Matrix.invertM(mInvVPMatrix, 0, mVPMatrix, 0); 
		Matrix.invertM(mInvVMatrix, 0, mViewMatrix, 0); 		
	}
	
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
	{

		// Position the eye behind the origin.Tells me the position where my eye is.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = 1.5f;

		// We are looking toward the distance. tells me the position of my object im looking at.
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
		recalcViewProjectionMatrix();
		
		tex = new Texture(assets, resources);
		tex.loadTexture("tex2", R.raw.texture_2);
		
		
		shader = new Shader(assets, resources);
		shader.load("shining", R.raw.shader_shining_v, R.raw.shader_shining_f);
		shader.use();

		shaderPicking = new Shader(assets, resources);
		shaderPicking.load("picking", R.raw.shader_picking_v, R.raw.shader_picking_f);
		shaderPicking.use();
		
		 //shader._programId instead of programHandle

	}	
	public void onSurfaceChanged(GL10 glUnused, int width, int height)
	{
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		//Blickfeld: perspektivische sichtweise. + CLIPPING (left, right, bottom, top, near, far,) 
		//near und far sagen mir wie weit oder nah ein objekt sein darf bis es nicht mehr angezeigt wird.
		//left right, wo ist der linke oder rechte bildschirmrand
		//bottom top, wo ist der untere oder obere bildschirmrand
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);		
		recalcViewProjectionMatrix();
	}	

	public void onDrawFrame(GL10 glUnused)
	{
		updateTime();	
		
		//Delete of color from every pixel on the screen display. with 0 0 0 0 = color black he just clears with black. with gles.20.gl_depth_buffer... everything in the depth and color buffer gets deleted.
		GLES20.glClearColor(0, 0, 0, 0);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);	

		shaderPicking.use();
        for (GameObject go : gameObjects.values()){
        	go.draw(shaderPicking, mVPMatrix);
        }
		
		shaderPicking.unuse();
		updateInput();
		
		updateGameObjects();
		
		
		

		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);	

		shader.use();
		tex.use(0);
        GLES20.glUniform1i(shader.mTexHandle, 0);	        
        
        for (GameObject go : gameObjects.values()){
        	go.draw(shader, mVPMatrix);
        }
        
		tex.unuse();
		shader.unuse();
	}	
	public void onTouched(int mouseX, int mouseY) {
		/*
		float[] v = new float[4];
		v[0] = mouseX / mProjectionMatrix[0 + 4*0];
		v[1] = mouseY / mProjectionMatrix[1 + 4*1];
		v[2] = 1.0f;
		v[3] = 1.0f;

		String currentIntersectionKey = null; //key of the object that last got hit by the "finger" 
		GameObject currentIntersection = null; //object that last got hit by the "finger"
		float[] currentIntersectionPoint = new float[3]; // point where the object last got hit by the "finger"
		float[] temp = new float[3];
		
		//iterating through all gameobjects with the key
		for (String goKey : gameObjects.keySet()){
			GameObject go = gameObjects.get(goKey);
			
			//method that calculates if an object got hit or not!
			if ( go.intersect(v, mViewMatrix, temp) ) {
				
				if ( currentIntersection == null) {
					currentIntersectionKey = goKey;
					currentIntersection = go;
					Math.copyVector3(currentIntersectionPoint, temp);
				}
				else if (currentIntersectionPoint[2] > temp[2]) {
					currentIntersectionKey = goKey;
					currentIntersection = go;
					Math.copyVector3(currentIntersectionPoint, temp);
				}				
			}
		}
		
		*/
		//in this bytebuffer we will get our pixels.
		ByteBuffer buffer = ByteBuffer.allocateDirect(4);
		buffer.order(ByteOrder.nativeOrder());
		buffer.position(0);
		
		//used to read the dimensions of the screen display //x, y, width, height
	    int[] results = new int[4];
	    GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, results, 0);
	    
	    //gets a pixel with 1 x 1 (width x height) for x and y (1 and 1) //RGBA = how textures get saved on the screen //unsigned byte = gets saved into the buffer //over all: getting color values for 1 pixel
		GLES20.glReadPixels(mouseX, results[3]-mouseY, 1,1, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
		
		//gets first, second and third color values (bytes)
		int x = buffer.get(0);
		int y = buffer.get(1);
		int z = buffer.get(2);
		
		//shifting bytes to get an id for object..nice
		int id = x + (y << 8) + (z << 16); // + (w << 24);
		
		//compares before generated id with the corresponding object for this id and calls any function for stuff that this object could do....for example...delete -> gameObjects.remove(goKey);
		if ( id != 0)
		{
			for (String goKey : gameObjects.keySet()){
				GameObject go = gameObjects.get(goKey);
				
				if ( id == go.getId() ) {
					onGameObjectTouched(goKey, go);
					break;
				}
					
			}
		}
	}
	
	static float abs(float x){ if (x < 0) return -x; return x; }
	
	public void onGameObjectTouched(String goKey, GameObject go) {
		gameObjects.remove(goKey);
	}

	
	//the actual save function to save x and y coordinates of a recognized toch to a list which is proceeded later (in the rendering thread = onRender)
	public void registerTouch(int x, int y) {
		
		int[] position = new int[2]; position[0] = x; position[1] = y;
		touches.add(position);		
	}


}
