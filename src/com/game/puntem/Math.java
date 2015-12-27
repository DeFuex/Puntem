package com.game.puntem;

public class Math {	

	public static final float PI = 3.14159265f;
	//makes it possible to copy one part(offset + size) of an array to another array(dest[]) 
	public static void copyArray(float[] dest, float[] src, int size, int offsetSrc)
	{
		for ( int i = 0 ; i < size ; i ++)
			dest[i] = src[i + offsetSrc];
	}
	public static void copyArray(float[] dest, float[] src, int size)
	{
		for ( int i = 0 ; i < size ; i ++)
			dest[i] = src[i];
	}
	//copys one vector with 3 elements (x,y,z) to another vector (array).
	public static void copyVector3(float[] dest, float[] src)
	{
		copyArray(dest, src, 3);
	}
	public static void copyVector3(float[] dest, float[] src, int offset)
	{
		copyArray(dest, src, 3, offset);
	}
	public static void copyVector4(float[] dest, float[] src)
	{
		copyArray(dest, src, 4);
	}
	public static void copyVector4(float[] dest, float[] src, int offset)
	{
		copyArray(dest, src, 4, offset);
	}
	//copys one matrix (16 elements of an array) into another matrix
	public static void copyMatrix(float[] dest, float[] src)
	{
		copyArray(dest, src, 16);
	}
	
	//transform vertices from one space to another. when you want to transform vertices from the world space to the screen space, you need view projection matrix and the necessary world vector coordinates.
	public static float[] transformVertex4(float[] v, float[] m)
	{
		float[] vNew = new float[4];
		for (int i = 0 ; i < 4 ; i ++){
			vNew[i] = v[0] * m[0 + 4*i] +
					  v[1] * m[1 + 4*i] + 
					  v[2] * m[2 + 4*i] + 
					  v[3] * m[3 + 4*i];
		}
		
		vNew[0] /= vNew[3];
		vNew[1] /= vNew[3];
		vNew[2] /= vNew[3];
		vNew[3] /= vNew[3];
		
		return vNew;
	}
	
	//dot product: cos(alpha) * | v1 - v2 | where alpha is the angle between v1 and v2
	public static float dot3(float[] v1, float[] v2){
		return v1[0] * v2[0] + v1[1]* v2[1] + v1[2] * v2[2]; 
	}
	
	//cross product: http://en.wikipedia.org/wiki/Cross_product
	public static float[] cross3( float[] a, float[] b){
		float[] result = new float[3];
		
		result[0]=a[1]*b[2]-a[2]*b[1];
		result[1]=a[2]*b[0]-a[0]*b[2];
		result[2]=a[0]*b[1]-a[1]*b[0];
		
		return result;
	}

	
	// sub(v1,v2) = v1 - v2 where dimension (=3 or 4) is the dimension (3-component = x,y,z  or 4-component = x,y,z,w) of the vectors
	public static float[] sub(float[] v1, float[] v2, int dimension)
	{
		float[] result = new float[dimension];
		
		for(int i = 0 ; i < dimension; i ++){
			result[i] = v1[i] - v2[i];
		}
		
		return result;
	}
	public static float[] sub3(float[] v1, float[] v2)
	{
		return sub(v1,v2,3);
	}
	public static float[] sub4(float[] v1, float[] v2)
	{
		return sub(v1,v2,4);
	}
	
	// a triangle is defined by 3 points (v0,v1,v2)
	// this function tests if the ray hits the triangle. if so TRUE is returned, otherwise false.
	// if the ray hits the triangle, the hitpoint is saved to result.
	public static boolean intersectTriangle(Ray ray, float[] v0, float[] v1, float[] v2, float[] result)
	{
		float[] edge1 = Math.sub3(v1, v0);
		float[] edge2 = Math.sub3(v2, v0);
		
		float[] pvec = Math.cross3(ray.getDirection(), edge2);
		float det = Math.dot3(edge1, pvec);
		
		float[] tvec;

	    if( det > 0 )
	    {
	    	tvec = Math.sub3(ray.getPositon(), v0);
	    }
	    else
	    {
	    	tvec = Math.sub3(v0,ray.getPositon());
	        det = -det;
	    }

	    if( det < 0.0001f )
	        return false;

	    // Calculate U parameter and test bounds
	    float u = Math.dot3(tvec,pvec);
	    if( u < 0.0f || u > det )
	        return false;

	    // Prepare to test V parameter
	    float[] qvec = Math.cross3(tvec, edge1);

	    float v = Math.dot3(ray.getDirection(), qvec);
	    if( v < 0.0f || u + v > det )
	        return false;

	    // Calculate t, scale parameters, ray intersects triangle
	    float t = Math.dot3(edge2, qvec) / det;
	    
	    result[0] = ray.getPositon()[0] + ray.getDirection()[0] * t;
	    result[1] = ray.getPositon()[1] + ray.getDirection()[1] * t;
	    result[2] = ray.getPositon()[2] + ray.getDirection()[2] * t;
	    
		return true;
	}
}
