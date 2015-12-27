package com.game.puntem;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GameObject {
	
	private static int nextId = 1;
	
	private int id;
	private float[] rotation;
	private float[] translation;
	private float[] scale;
	private float[] modelMatrix;
	private float[] modelMatrixInverse;
	private boolean hasChanged;
	private Model model;
	
	
	public int getId(){
		return id;
	}
	public float[] getTranslation(){
		return translation;
	}
	public float[] getScale(){
		return scale;
	}
	public float[] getRotation(){
		return rotation;
	}
	public Model getModel(){
		return model;
	}
	
	// get model matrix.
	// if rotation, translation or scale have changed -> recalculate matrix and then return it
	// if they haven't changed, just return last calculation
	public float[] getModelMatrix(){
		if (hasChanged){
			recalcModelMatrix();
		}
		
		return modelMatrix; 
	}
	
	public float[] getModelMatrixInverse(){
		if (hasChanged){
			recalcModelMatrix();
		}
		
		return modelMatrixInverse; 
	}
	
	private void recalcModelMatrix()
	{
		// start with identity = nothing happens
		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.setIdentityM(modelMatrixInverse, 0);		
		
		
		// if object has been moved -> calculate translation matrix
		if ( translation[0] != 0 || translation[1] != 0 || translation[2] != 0 ){
			Matrix.translateM(modelMatrix, 0, translation[0], translation[1], translation[2]);
		}
		

		// if object has been rotated -> calculate rotation matrix
		if ( rotation[0] != 0 ){
			Matrix.rotateM(modelMatrix, 0, rotation[0], 1, 0, 0);
		}
		if ( rotation[1] != 0 ){
			Matrix.rotateM(modelMatrix, 0, rotation[1], 0, 1, 0);
		}
		if ( rotation[2] != 0 ){
			Matrix.rotateM(modelMatrix, 0, rotation[2], 0, 0, 1);
		}
		

		// if object has been scaled -> calculate scalation matrix
		if ( scale[0] != 1 || scale[1] != 1 || scale[2] != 1 ){
			Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2]);
		}
		
		
		
		
		

		Matrix.invertM(modelMatrixInverse, 0, modelMatrix, 0);
		
		// matrix is "updated" again and so we can say that nothing has changed (until rotation, scale or translation will change again)
		hasChanged = false;
	}
	
	
	public GameObject(Model model)
	{ 
		this.id = nextId++;
		this.model = model;
		
		modelMatrixInverse = new float[16];
		modelMatrix = new float[16];
		rotation = new float[3];
		translation = new float[3];
		scale = new float[3];

		// init rotation with 0 degrees, translation with 0-units moved and scale with factor 1
		rotation[0] = 0.0f; rotation[1] = 0.0f; rotation[2] = 0.0f;
		translation[0] = 0.0f; translation[1] = 0.0f; translation[2] = 0.0f;
		scale[0] = 1.0f; scale[1] = 1.0f; scale[2] = 1.0f;
		
		hasChanged = true;
	}
	
	public void translate(float x, float y, float z){
		
		translation[0] += x;
		translation[1] += y;
		translation[2] += z;
		hasChanged = true;
	}	
	public void rotateX(float angle){
		rotation[0] += angle;
		hasChanged = true;
	}
	public void rotateY(float angle){
		rotation[1] += angle;
		hasChanged = true;
	}
	public void rotateZ(float angle){
		rotation[2] += angle;
		hasChanged = true;
	}
	public void scale(float factor){
		scale(factor,factor,factor);
	}
	public void scale(float factorX, float factorY, float factorZ){
		translation[0] += factorX;
		translation[1] += factorY;
		translation[2] += factorZ;
		hasChanged = true;
	}

	public void draw(Shader shader, float[] matrixVP){
		float[] matrixMVP = new float[16];
		Matrix.multiplyMM(matrixMVP, 0, matrixVP, 0, getModelMatrix(), 0);

		if (shader.mObjectID >= 0){
			
			float x = ((float)(( id >> 0 ) & 0xff)) / 255.0f;
			float y = ((float)(( id >> 8 ) & 0xff)) / 255.0f;
			float z = ((float)(( id >> 16 ) & 0xff)) / 255.0f;
			
			GLES20.glUniform4f(shader.mObjectID, x, y, z, 1.0f);
		}
		
		model.draw(shader, matrixMVP);
		
	}
	

	public Ray getPickRay(float[] v, float[] matrixView) {

		float[] mMV = new float[16];
		float[] mInvMV = new float[16];	
		
		Matrix.multiplyMM(mMV, 0, matrixView, 0, getModelMatrix(), 0);
		Matrix.invertM(mInvMV, 0, mMV, 0);


		float[] vDir = Math.transformVertex4(v, mInvMV); vDir[3] = 1.0f;

		/*
		float[] vDir = new float[3];
 
		vDir[0] = v[0] * mInvMV[0*4 + 0] + v[1] * mInvMV[1*4 + 0] + v[2] * mInvMV[2*4 + 0];
		vDir[1] = v[0] * mInvMV[0*4 + 1] + v[1] * mInvMV[1*4 + 1] + v[2] * mInvMV[2*4 + 1];
		vDir[2] = v[0] * mInvMV[0*4 + 2] + v[1] * mInvMV[1*4 + 2] + v[2] * mInvMV[2*4 + 2];*/
		

		float[] vOrig = new float[3];
		vOrig[0] = mInvMV[3*4 + 0];
		vOrig[1] = mInvMV[3*4 + 1];
		vOrig[2] = mInvMV[3*4 + 2];
		
		
		Ray r = new Ray(vOrig, vDir);
		
		return r;
	}
	
	public boolean intersect(float[] v, float[] matrixView,  float[] result){
		Ray ray = getPickRay(v, matrixView);
		return model.intersect(ray, result);
	}
}
