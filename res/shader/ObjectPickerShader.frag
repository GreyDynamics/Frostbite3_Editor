#version 400 core

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform vec3 pickerColor;

void main(void){
	out_Color = vec4(pickerColor.x, pickerColor.y, pickerColor.z, 1);
}