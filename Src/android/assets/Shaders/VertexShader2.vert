attribute vec4 a_color;
attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_worldView;
uniform vec3 u_lightColor;
uniform vec2 u_lightPos;

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec3 v_lightColor;
varying vec2 v_lightPos;
varying vec2 v_position;


void main() {
    v_color = a_color;
    v_lightPos = u_lightPos;
    v_lightColor = u_lightColor;
    v_position.xy = a_position.xy;
    v_texCoords = a_texCoord0;
    gl_Position = u_worldView * a_position;
}