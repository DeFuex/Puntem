precision mediump float;

varying vec4 color;
varying vec2 texCoord;

uniform sampler2D u_Texture;

void main()
{
	vec4 colorOfTexturePixel = texture2D(u_Texture, texCoord);
	gl_FragColor = color * colorOfTexturePixel;
}