#version 400 core
in vec2 pass_texCoord;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 pass_normal;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform float isHighlighted;
uniform vec3 highlightedColor;
uniform float isNormal;

void main(void){
	if (isNormal > 0.0){
		vec3 unitpassnormal = (normalize(pass_normal)+1)/2;
		vec3 unitNormal = (normalize(surfaceNormal)+1)/2;
		vec3 unitLightVector = (normalize(toLightVector)+1)/2;
		
		out_Color = vec4(unitpassnormal, 1.0);
	}else{
		vec3 unitNormal = normalize(surfaceNormal);
		vec3 unitLightVector = normalize(toLightVector);
		
		float dot1 = dot(unitNormal, unitLightVector);
		float brightness = max(dot1, 0.5);
		vec3 diffuse = brightness * vec3(0.95, 0.95, 0.95);
		//vec4 color = vec4(diffuse, 1.0) * texture(textureSampler, pass_texCoord);
		vec4 color = texture(textureSampler, pass_texCoord);
		if (isHighlighted > 0.0){
			color = color * vec4(highlightedColor, 1.0);
		}
		
		out_Color = color;
	}
}