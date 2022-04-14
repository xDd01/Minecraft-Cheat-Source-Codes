#version 130

uniform float radius;
uniform vec2 resolution;
uniform vec4 in_color;
uniform vec2 p1;
uniform vec2 p2;

float roundedBoxSDF(vec2 CenterPosition, vec2 Size, float Radius) {
    return length(max(abs(CenterPosition)-Size+Radius, 0.0))-Radius;
}

void main() {
    vec2 point1 = p1;
    vec2 point2 = p2;
    point1 /= vec2(2.);
    point1 += vec2(.5);
    point2 /= vec2(2.);
    point2 += vec2(.5);

    vec2 diff = abs(point2 - point1);
    float width = (resolution.x / resolution.y) * (diff.x / diff.y);

    vec2 uv = (gl_FragCoord.xy / resolution - point1) / (point2 - point1) - .5;
    uv = vec2(uv.x * width, uv.y);

    float len = length(diff);
    float distance = roundedBoxSDF(uv * resolution.x * len, vec2(.5 * width, .5) * resolution.x * len, radius * resolution.x * len);
    gl_FragColor = vec4(1., 1., 1., clamp(1.-distance, 0., 1.))*in_color;
}