package com.game.puntem;

public class Ray {

	private float[] position;
	private float[] direction;
	
	public Ray(float[] position, float[] direction){
		this.position = position;
		this.direction = direction;
	}
	
	public float[] getPositon(){ return this.position; }
	public float[] getDirection(){ return this.direction; }
}
