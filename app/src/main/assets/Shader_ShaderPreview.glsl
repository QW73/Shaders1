#version 300 es

precision mediump float;


uniform float u_time; 
//uniform vec4 uColor;
//uniform vec4 uBackgroundColor;

in vec2 textureCoord;
out vec4 fragColor;

void main() {
    vec3 color = vec3(0.);
    color = vec3(textureCoord.x, textureCoord.y, abs(sin(u_time)));
    
    fragColor = vec4(color, 1.0);
    //fragColor = vec4(abs(sin(uColor.x)), abs(cos(uBackgroundColor.x)), abs(sin(u_time)), 1.0);
}

