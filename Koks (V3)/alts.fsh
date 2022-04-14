

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

vec2 pModMirror2(inout vec2 p, vec2 size) {
    vec2 halfsize = size*0.5;
    vec2 c = floor((p + halfsize)/size);
    p = mod(p + halfsize, size) - halfsize;
    p *= mod(c,vec2(2.))*2. - vec2(1.);
    return c;
}
float pModMirror1(inout float p, float size) {
    float halfsize = size*0.5;
    float c = floor((p + halfsize)/size);
    p = mod(p + halfsize,size) - halfsize;
    p *= mod(c, 2.0)*2. - 1.;
    return c;
}


float wave(vec2 p) {
    float v = sin(p.x*0.7 + sin(p.y*2.2) + sin(p.y * .43));
    return v*v;
}

const mat2 rot = mat2(0.5, 0.86, -0.86, 0.5);

float map(vec2 p,float t)
{
    float v = abs(sin(p.x+p.y*1.4))*0.1;
    v += wave(p);
    p.y += t;
    p *= rot;
    v += wave(p);
    p.y += t * .17;	//0.17
    p *= rot;
    v += wave(p);
    v+=pow(abs(sin(p.x+v)),2.0);

    v = abs(1.5 - v);
    return v;
}

void main(void) {
    vec2 uv = (gl_FragCoord.xy * 2.0 - resolution.xy) / min(resolution.x, resolution.y);
    float scale =4.0;
    float speed = .8;
    vec3 col = vec3(.9, 0.53, 0.86);
    uv.y += sin(fract(time*0.1+uv.x)*6.28)*0.05;	// wibble
    //uv.xy += time*0.25;				// scroll
    //pModMirror2(uv.xy,vec2(2.0));		// mxy
    //pModMirror1(uv.x,2.0);			// mx
    vec2 p = uv*scale;
    //p.y += dot(p,p)*.018;
    p.y+= 1.0/p.y*p.y;
    float t = time*speed;
    float v = map(p,t);
    v = smoothstep(-3.5,3.5,v);


    //col *= vec3(1.0-sin(abs(uv.x)));	// loop blend


    gl_FragColor = vec4(col*v*v, 1);
}
