package com.game.puntem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;

public class Model 
{
	/** Store our model data in a float buffer. */
	private FloatBuffer mTriangle1Vertices;
	
	private float[] data;
	
	

	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;
	/** How many elements per vertex. */
	//Ab wann beginnt der nächste vertex. setzt offset of die position des nächsten vertex im dreieck. also wieder auf 0 für position
	private final int mStrideBytes = (3+4+2) * mBytesPerFloat;	
	/** Offset of the position data. */
	//index wo die positionsdaten im floatarray(triangle1VerticesData) beginnen.
	private final int mPositionOffset = 0;
	/** Size of the position data in elements. */
	//Größe, also wieviel vertices die position des dreiecks hat
	private final int mPositionDataSize = 3;
	/** Offset of the color data. */
	//index wo die farbdaten im floatarray(triangle1VerticesData) beginnen.
	private final int mColorOffset = 3;
	/** Size of the color data in elements. */
	//Größe, also wieviel farbpositionen das dreieck hat.
	private final int mColorDataSize = 4;	
	/** Offset of the color data. */
	//index wo die texturdaten im floatarray(triangle1VerticesData) beginnen.
	private final int mTexCoordOffset = 7;
	/** Gibt an wie viele texturcoordinaten (eine texturcoordinate = 1 float) im float-array PRO vertex enthalten sind.
	* In unserem fall haben wir zwei texturcoordinaten(u, v) pro vertex.
	*/
	private final int mTexCoordDataSize = 2;
	


	
	
	public Model()
	{
	}
	
	public void setModelData(float[] data)
	{
		this.data = data;
		
		// Initialize the buffers. = VBO vertexbufferobject....is created
		mTriangle1Vertices = ByteBuffer.allocateDirect(data.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		//VBO vertexbufferobject....is filled with the data from triangle1VerticesData
		mTriangle1Vertices.put(data).position(0);
	}
	
	public void draw(Shader shader, float[] matrixMVP)
	{	
		// Pass in the position information
		mTriangle1Vertices.position(mPositionOffset);
		GLES20.glVertexAttribPointer(shader.mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
         mStrideBytes, mTriangle1Vertices);                
        GLES20.glEnableVertexAttribArray(shader.mPositionHandle);
        
        // Pass in the color information
        mTriangle1Vertices.position(mColorOffset);
        GLES20.glVertexAttribPointer(shader.mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
         mStrideBytes, mTriangle1Vertices);
        GLES20.glEnableVertexAttribArray(shader.mColorHandle);
        
        // Pass in the color information
        mTriangle1Vertices.position(mTexCoordOffset);
        GLES20.glVertexAttribPointer(shader.mTexCoordHandle, mTexCoordDataSize, GLES20.GL_FLOAT, false,
         mStrideBytes, mTriangle1Vertices);
        GLES20.glEnableVertexAttribArray(shader.mTexCoordHandle);

        GLES20.glUniformMatrix4fv(shader.mMVPMatrixHandle, 1, false, matrixMVP, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, data.length * mBytesPerFloat / mStrideBytes );
	}
	
	
	//verfies every single triangle, if it got hit or not!
	public boolean intersect(Ray ray, float[] result)
	{
		List<float[]> list = new ArrayList<float[]>();

		float[] v0 = new float[4];
		float[] v1 = new float[4];
		float[] v2 = new float[4];
		float[] temp = new float[3];
		
		for( int i = 0; i < data.length * mBytesPerFloat / (mStrideBytes*3) ; i ++)
		{
			Math.copyVector3(v0, data, ((i * 3 + 0) * mStrideBytes / mBytesPerFloat)); v0[3] = 1;
			Math.copyVector3(v1, data, ((i * 3 + 1) * mStrideBytes / mBytesPerFloat)); v1[3] = 1;
			Math.copyVector3(v2, data, ((i * 3 + 2) * mStrideBytes / mBytesPerFloat)); v2[3] = 1;
			
			if ( Math.intersectTriangle(ray, v0, v1, v2, temp ) )
			{
				list.add(temp.clone());
			}
		}
		
		if ( list.isEmpty())
			return false;
				
		// TODO: DO NOT TAKE FIRST, BUT NEAREST
		Math.copyVector3(result, list.get(0));
		
		return true;
	}
	
	public static Model createTriangle()
	{
		// This triangle is red, green, and blue.
		final float[] data = 
		{
			// X, Y, Z,
			// R, G, B, A
			-0.5f, -0.25f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			-0.5f*0.5f + 0.5f, -0.25f*0.5f + 0.5f,

			0.5f, -0.25f, 0.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.5f*0.5f + 0.5f, -0.25f*0.5f + 0.5f,

			0.0f, 0.559016994f, 0.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f*0.5f + 0.5f, 0.559016994f*0.5f + 0.5f
		};
		
		Model m = new Model();
		m.setModelData(data);
		return m;
	}

	public static Model createQuad() {
		final float[] data = 
		{
			// X, Y, Z,
			// R, G, B, A
			-0.5f, -0.5f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 0.0f,

			0.5f, -0.5f, 0.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f,

			-0.5f, 0.5f, 0.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f,

			0.5f, -0.5f, 0.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			1.0f, 0.0f,

			-0.5f, 0.5f, 0.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 1.0f,

			0.5f, 0.5f, 0.0f,
			0.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f
		};
		
		Model m = new Model();
		m.setModelData(data);
		return m;
	}
}
