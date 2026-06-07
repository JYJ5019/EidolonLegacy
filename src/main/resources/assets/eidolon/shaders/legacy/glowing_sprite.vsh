#version 120

varying vec2 texCoord0;
varying vec4 vertexColor;

void main() {
    gl_Position = ftransform();
    texCoord0 = gl_MultiTexCoord0.xy;
    vertexColor = gl_Color;
}
