package com.game.puntem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.os.Bundle;

import javax.microedition.khronos.opengles.GL10;

import com.game.puntem.GameActivity;
import com.game.puntem.GameListener;

public class TriangleTest {
//extends GameActivity implements GameListener {
	
//	private FloatBuffer vertices;
//	private FloatBuffer colors;
//	
//	public void onCreate(Bundle savedInstance)
//	{
//		super.onCreate(savedInstance);
//		setGameListener(this);
//	}
//	
//	public void setup(GameActivity activity, GL10 gl) 
//	{
//		ByteBuffer buffer = ByteBuffer.allocateDirect(3*3*4);
//		buffer.order(ByteOrder.nativeOrder());
//		vertices = buffer.asFloatBuffer();
//		
//		vertices.put(-0.5f);
//		vertices.put(-0.5f);
//		vertices.put(0);
//		
//		vertices.put(0.5f);
//		vertices.put(-0.5f);
//		vertices.put(0);
//		
//		vertices.put(0);
//		vertices.put(0.5f);
//		vertices.put(0);
//		
//		vertices.rewind();
//		
//		buffer = ByteBuffer.allocateDirect(3*4*4);
//		buffer.order(ByteOrder.nativeOrder());
//		colors = buffer.asFloatBuffer();
//		
//		colors.put(1);
//		colors.put(0);
//		colors.put(0);
//		colors.put(1);
//		
//		colors.put(0);
//		colors.put(1);
//		colors.put(0);
//		colors.put(1);
//		
//		colors.put(0);
//		colors.put(0);
//		colors.put(1);
//		colors.put(1);
//		
//		colors.rewind();
//	}
//
//	public void mainLoopIteration(GameActivity activity, GL10 gl) 
//	{
//		gl.glViewport(0, 0, activity.getViewportWidth(), activity.getViewportHeight());
//		
//		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//		
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
//		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
//		gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);
//		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
//	}

}
