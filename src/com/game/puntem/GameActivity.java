package com.game.puntem;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameActivity extends Activity
						  implements OnTouchListener /*implements GLSurfaceView.Renderer, OnTouchListener, 
SensorEventListener*/
{	
	private TriangleRenderer mRenderer;
	private GLSurfaceView glSurface;
//	private int width, height;
//	private GameListener listener;

	DisplayMetrics displayMetrics = new DisplayMetrics();
	
	
	float[] acceleration = new float[3];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	
//    	final DisplayMetrics displayMetrics = new DisplayMetrics();
//    	getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//    	density = displayMetrics.density;
        
        glSurface = new GLSurfaceView(this);
        /*glSurface.setEGLConfigChooser(new EGLConfigChooser() {
        	   	
        })*/
        //setContentView(R.layout.activity_game);
        
        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
     
        if (supportsEs2)
        {
        	
            // Request an OpenGL ES 2.0 compatible context.
            glSurface.setEGLContextClientVersion(2);
//            glSurface.setRenderer(this);
            
        	getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            
//            mRenderer = new TriangleRenderer(this);
            
            // Set the renderer to our demo renderer, defined below.
        	
        	glSurface.getHolder().setFormat(PixelFormat.RGBA_8888);
        	glSurface.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
            glSurface.setRenderer(mRenderer = new TriangleRenderer(this.getAssets(), this.getResources(), displayMetrics)); //new TriangleRenderer(this.getAssets(), this.getResources()) //mRenderer, displayMetrics.density
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }       
        
//        glSurface.setRenderer(this);
        
        setContentView(glSurface);
        glSurface.setOnTouchListener(this);
//        
//        glSurface.setOnTouchListener(this);
//        
//        SensorManager manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//        if(manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0)
//        {
//        	Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
//        	manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
//        }
    }
    

//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		// nothing to do at the moment about this
//		
//	}
//
//	public void onSensorChanged(SensorEvent event) {
//		System.arraycopy(event.values, 0, acceleration, 0, 3);
//		
//	}
//
    /*
    private float[] pixelToScreenSpace(float[] pos)
    {
    	float[] newPos = new float[2];
    	
    	//2 funktionen zur änderungen von pixel auf screen space...koordinatenursprungspunkt des screens ist ab diesen zeitpunkt 0,0 in der Mitte des screens.
    	newPos[0] = 2.0f * (pos[0] / ((float)displayMetrics.widthPixels))- 1.0f;
    	newPos[1] = -( 2.0f * (pos[1] / ((float)displayMetrics.heightPixels)) -1.0f);
    	
		return newPos;
    }*/

	public boolean onTouch(View view, MotionEvent event)
	{		
		if(event != null)
		{		
			if(event.getAction() == MotionEvent.ACTION_DOWN && glSurface != null && mRenderer != null )
			{
				/*
				float[] newPosInPixel = new float[2]; 
				newPosInPixel[0] = event.getX(); 
				newPosInPixel[1] = event.getY();
				float[] newPosInScreenSpace = pixelToScreenSpace(newPosInPixel);
				*/
				
				//saves a touch in a list.
				mRenderer.registerTouch((int)event.getX(), (int)event.getY());
			}
			
			return true;
		}
		else
		{
			return super.onTouchEvent(event);
		}
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		glSurface.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		glSurface.onResume();
	}
}
