#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords; // 픽셀의 좌표
varying vec2 v_lightPos;
varying vec3 v_lightColor;
varying vec2 v_position;

uniform sampler2D u_texture;


void main(void) {
    int dropOffDist = 300;
    float dist = 0;
    dist = pow(abs(v_lightPos.x - v_position.x), 2);
    dist += pow(abs(v_lightPos.y - v_position.y), 2);
    dist = sqrt(dist);

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords) * min(.75f, (dropOffDist / max(1,dist))); // 거리에 따라 색상 변경
    gl_FragColor = (gl_FragColor + vec4(v_lightColor, 0))/2; // 주변색상 조금 더 연하게 변경
}