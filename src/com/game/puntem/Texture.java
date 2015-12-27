package com.game.puntem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;



public class Texture 
{
	Resources resources;
	AssetManager assets;
	boolean _isLoaded = false;
	String name;
	int iError;
	
	final int[] _textureID = new int[1];
	int _slot = -1;
	
	private static final String TAG = "Texture.java";
	

	public Texture( AssetManager assets, Resources resources )
	{
		this.resources = resources;
		this.assets = assets;
	}
	/*
	private static ByteBuffer extract(Bitmap bmp)
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();
		// Convert ARGB -> RGBA
		for (int y = bmp.getHeight() - 1; y > -1; y--)
		{
	
			for (int x = 0; x < bmp.getWidth(); x++)
			{
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
				int alpha = ((pix >> 24) & 0xFF);
				int red = ((pix >> 16) & 0xFF);
				int green = ((pix >> 8) & 0xFF);
				int blue = ((pix) & 0xFF);
			
				// Make up alpha for interesting effect
			
				//ib.put(red << 24 | green << 16 | blue << 8 | ((red + blue + green) / 3));
			ib.put(red << 24 | green << 16 | blue << 8 | alpha);
			}
		}
		bb.position(0);
		return bb;
	}
*/
	
	
	boolean loadTexture(String name, int resourceId)
	{
		this.name = name;
		if ( _isLoaded )
		{
			return true;
		}
		Log.i(TAG, "loading texture '" + name + "'");
		
		try
		{
			if(_textureID[0] == 0)
			{
				
				Bitmap bmp = BitmapFactory.decodeResource(resources, resourceId);
				//ByteBuffer bb = extract(bmp);
//				int width = bmp.getWidth();
//				int height = bmp.getHeight();
				
				
			
				//generating and linking(preparing space for a texture) id for binding a texture
				GLES20.glGenTextures(1, _textureID, 0);

				
				if(_textureID[0] == 0)
				{
					throw new RuntimeException("Error creating texture");
				}
				
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureID[0]);
				if ( (iError = GLES20.glGetError()) != 0 )
				{
					Log.e(TAG, "\t\t\terror: " + Integer.toString(iError) + " after glBindTexture");
				}

				
				
				//Load the bitmap into the "to be" bound texture.        
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
				if ( (iError = GLES20.glGetError()) != 0 )
				{
					Log.e(TAG, "\t\t\terror: " + Integer.toString(iError) + " after texImage2D");
				}
				
		    	    
				//normally with glfwLoadTexture2d you can actually load the given texture(in this case the parameter name)
				//glfwLoadTexture2D(name, 0);
				//in this case we use a Bitmap to get our texture resource and have context for name instead of String
		 
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
				if ( (iError = GLES20.glGetError()) != 0 )
				{
					Log.e(TAG, "\t\t\terror: " + Integer.toString(iError) + " after glTexParameteri");
				}
				GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
				if ( (iError = GLES20.glGetError()) != 0 )
				{
					Log.e(TAG, "\t\t\terror: " + Integer.toString(iError) + " after glGenerateMipmap");
				}
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
				if ( (iError = GLES20.glGetError()) != 0 )
				{
					Log.e(TAG, "\t\t\terror: " + Integer.toString(iError) + " after glBindTexture");
				}
			}
			
			if(_textureID[0] == 0)
			{
				throw new RuntimeException("Error loading texture");
			}
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "\t\t\terror: " + e.getMessage());
//			Log.e(TAG, Shader.getStackTrace(e));
		    e.printStackTrace();
		    return false;
		}

			
		_isLoaded = true;
		return true;
	}

	void use(int slot)
	{
		if (!checkLoaded())
		{
			return;
		}

		_slot = slot;

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + slot);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureID[0]);
	}
	void unuse()
	{
		if (!checkLoaded()) return;

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + _slot);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		
		_slot = -1;
	}

	boolean checkLoaded()
	{	
		if (!_isLoaded)
		{
			Log.e(TAG, "error: Trying to access texture '" + "-ein Bastard ist er-" + name + "', but it is not loaded yet.");
			return false;
		}

		return true;
	}
	
	
	
}
