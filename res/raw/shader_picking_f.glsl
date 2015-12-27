precision mediump float;

varying vec4 color;
varying vec2 texCoord;

uniform vec4 u_ObjectID;

void main()
{
	gl_FragColor = u_ObjectID;
}