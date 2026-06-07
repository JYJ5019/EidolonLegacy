#version 120

uniform vec4 ColorModulator;

varying vec4 vertexColor;

void main() {
    vec4 color = vertexColor * ColorModulator;
    if (color.a <= 0.001) {
        discard;
    }
    gl_FragColor = color;
}
