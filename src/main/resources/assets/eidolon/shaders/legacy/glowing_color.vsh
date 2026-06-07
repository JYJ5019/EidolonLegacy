#version 120

varying vec4 vertexColor;

void main() {
    gl_Position = ftransform();
    vertexColor = gl_Color;
}
