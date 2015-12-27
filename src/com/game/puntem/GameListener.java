package com.game.puntem;

import javax.microedition.khronos.opengles.GL10;

public interface GameListener 
{
	public void setup(GameActivity activity, GL10 gl);
	public void mainLoopIteration(GameActivity activity, GL10 gl);
}