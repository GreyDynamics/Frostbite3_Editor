#version 400 core
in vec2 pass_texCoord;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform float isHighlighted;
uniform vec3 heighlightedColor;

void main(void){
	out_Color = texture(textureSampler, pass_texCoord) * vec4(1-(isHighlighted*heighlightedColor.x), 1-(isHighlighted*heighlightedColor.y), 1-(isHighlighted*heighlightedColor.z), 1);
}