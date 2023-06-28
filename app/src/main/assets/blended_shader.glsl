precision mediump float;

uniform float iTime;
uniform vec2 iResolution;
uniform vec4 iMouse;

uniform sampler2D texture1;
uniform sampler2D texture2;

varying vec2 texCoord;

void main() {
    vec4 color1 = texture2D(texture1, texCoord);
    vec4 color2 = texture2D(texture2, texCoord);
    gl_FragColor = mix(color1, color2, 0.5);
}
