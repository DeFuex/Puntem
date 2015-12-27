uniform mat4 u_MVPMatrix;


attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_TexCoord;


varying vec4 color;
varying vec2 texCoord;


void main()
{
	color = a_Color;
	texCoord = a_TexCoord;
	gl_Position = u_MVPMatrix * a_Position;
}