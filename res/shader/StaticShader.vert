#version 400 core
in vec3 position;
in vec2 texCoord;

out vec2 pass_texCoord;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position, 1.0);
	pass_texCoord = vec2(texCoord);
}