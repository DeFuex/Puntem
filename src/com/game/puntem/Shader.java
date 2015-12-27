package com.game.puntem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.R.string;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

public class Shader 
{
	Resources resources;
	AssetManager assets;
	String _name = "";
	int _programId;
	int _vertexShaderId = -1;
	int _fragmentShaderId = -1;
	boolean _isLoaded = false;
	

	
	/** This will be used to pass in the transformation matrix. */
	public int mMVPMatrixHandle;
	public int mTexHandle;
	/** This will be used to pass in model position information. */
	public int mPositionHandle;
	/** This will be used to pass in model color information. */
	public int mColorHandle;	
	public int mTexCoordHandle;
	public int mObjectID;
	
	
	
	
	
	
	private static final String TAG = "Shader.java";
	
	public Shader( AssetManager assets, Resources resources )
	{
		this.resources = resources;
		this.assets = assets;
	}

	
	boolean load(String name, int vertexShaderFileId, int fragmentShaderFileId)
	{
		_name = name;

		if ( _isLoaded ) return true;
		
		Log.i(TAG, "loading shader '" + _name + "'");
		
		//Loading Shader file
	    if ( ( _vertexShaderId = loadfile(vertexShaderFileId, "VertexShader", GLES20.GL_VERTEX_SHADER)) == 0 )
			return false;

		if ( ( _fragmentShaderId =  loadfile(fragmentShaderFileId, "FragmentShader", GLES20.GL_FRAGMENT_SHADER)) == 0 )
		{
			GLES20.glDeleteShader(_vertexShaderId);
			return false;
		}
		
		Log.i(TAG, "\t-linking");
		 //link parsed and compiled shaders with an id and attach(vertex&fragment) them together
	    _programId = GLES20.glCreateProgram();
	    GLES20.glAttachShader(_programId, _vertexShaderId);
	    GLES20.glAttachShader(_programId, _fragmentShaderId);
	    GLES20.glLinkProgram(_programId);
	 
	    // Check the program
	    //int infoLogLength;
	    final int[] result = new int[1];
	    GLES20.glGetProgramiv(_programId, GLES20.GL_LINK_STATUS, result, 0);
		
	    if (result[0] == 0)
		{
			Log.e(TAG, "\t\terror: Linking-error");
			Log.e(TAG, GLES20.glGetProgramInfoLog(_programId));
//			GLES20.glGetProgramiv(_programId, GL_INFO_LOG_LENGTH, &infoLogLength);
//			vector<char> errorMessage(infoLogLength);
//			glGetProgramInfoLog(_programId, infoLogLength, NULL, &errorMessage[0]);
//			cerr << &errorMessage[0] << endl;

			GLES20.glDeleteShader(_vertexShaderId);
			GLES20.glDeleteShader(_fragmentShaderId);
			GLES20.glDeleteProgram(_programId);

			return false;
		}
 

        mMVPMatrixHandle = GLES20.glGetUniformLocation(_programId, "u_MVPMatrix");
        mTexHandle = GLES20.glGetUniformLocation(_programId, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(_programId, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(_programId, "a_Color");
        mTexCoordHandle = GLES20.glGetAttribLocation(_programId, "a_TexCoord");
        mObjectID = GLES20.glGetUniformLocation(_programId, "u_ObjectID");
	    
		_isLoaded = true;
		return true;
	}
	
//	  public static String getStackTrace(Throwable aThrowable) {
//		    final Writer result = new StringWriter();
//		    final PrintWriter printWriter = new PrintWriter(result);
//		    aThrowable.printStackTrace(printWriter);
//		    return result.toString();
//		  }

	
	
	//parse shader files and compile them + binding them to a shader id	
	int loadfile(int fileId, String shaderName, int shaderPart)
	{
		Log.i(TAG, "\t-loading '" + this._name + "'");
		
		
	    String thisline = "";
	    String shaderCode = "";
		
		Log.i(TAG, "\t\t-read from file");
				
		try 
		{
			Log.i(TAG, "sie sir, sind ein bastard");
			InputStream stream = resources.openRawResource(fileId);
			
			if (stream == null) throw new Exception("Could not find resource");
			BufferedReader instream = new BufferedReader(new InputStreamReader(stream));
			
		    //highscore += in.readInt();
		    while ((thisline = instream.readLine()) != null)
		    {
		    	shaderCode += thisline + "\n";
		    }
		    instream.close();
		} 
		catch (Exception e) 
		{
			Log.e(TAG, "\t\t\terror: File not found(" + e.getMessage() + ")");
//			Log.e(TAG, getStackTrace(e));
		    e.printStackTrace();
		    return 0;
		}

		//Creating id for the given shaderpart - vertex or fragment 
		int id = GLES20.glCreateShader(shaderPart); //GLuint eigentlich und shaderPart ein GLenum

		Log.i(TAG, "\t\t-compile");
	    
		final int[] compileResult = new int[1]; //instead of int compileResult = GLES20.GL_FALSE;
	    //int infoLogLength;
	    String shaderCodePtr = shaderCode.toString(); //sollte ein char sein?
	    
	    //combine the created shaderpart id with the parsed content of the shader
	    GLES20.glShaderSource(id , shaderCodePtr);
	    
	    //compile the shaderCode content
	    GLES20.glCompileShader(id);
	    GLES20.glGetShaderiv (id, GLES20.GL_COMPILE_STATUS, compileResult, 0);
	    
	    //instead of compileResult != GL_TRUE
	    if ( compileResult[0] == 0 )
	    {
			Log.e(TAG, "\t\t\terror: Compile-error - Could not compile shader " + shaderName + ":");
			Log.e(TAG, GLES20.glGetShaderInfoLog(id));
			GLES20.glDeleteShader(id);
			
			//statt:
//			GLES20.glGetShaderiv(id, GL_INFO_LOG_LENGTH, infoLogLength);
//			vector<char> errorMessage(infoLogLength);
//			glGetShaderInfoLog(id, infoLogLength, NULL, &errorMessage[0]);
//			cerr << &errorMessage[0] << endl;
			return 0;
	    }
	    
	    return id;
	}
	
	boolean checkLoaded()
	{	
		if (!_isLoaded)
		{
			Log.e(TAG, "error: Trying to access shader '" + _name + "', but it is not loaded yet.");
			return false;
		}

		return true;
	}
	void use()
	{
		if (!checkLoaded()) return;
		GLES20.glUseProgram(_programId);
	}
	void unuse()
	{
		if (!checkLoaded()) return;
		GLES20.glUseProgram(0);
	}

}
