#version 400 core
in vec2 pass_texCoord;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform float isHighlighted;
uniform vec3 heighlightedColor;
uniform float isPicker;
uniform vec3 pickerColor;

void main(void){
	if(isPicker>0.0) {
		out_Color = vec4(pickerColor.x, pickerColor.y, pickerColor.z, 1);
	} else {
		out_Color = texture(textureSampler, pass_texCoord) * vec4(1-(isHighlighted*heighlightedColor.x), 1-(isHighlighted*heighlightedColor.y), 1-(isHighlighted*heighlightedColor.z), 1);
	}
	
}