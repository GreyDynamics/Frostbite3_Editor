#version 400 core
in vec3 position;
in vec2 texCoord;
in vec4 normal;

out vec2 pass_texCoord;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 pass_normal;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;

void main(void){
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	pass_texCoord = vec2(texCoord);
	
	surfaceNormal = (transformationMatrix * vec4(normal.xyz, 0.0)).xyz;
	toLightVector = lightPosition - worldPosition.xyz;
	
	pass_normal = normal.xyz;
}