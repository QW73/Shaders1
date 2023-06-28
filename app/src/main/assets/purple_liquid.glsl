precision mediump float;

uniform float iTime;
uniform vec2 iResolution;
uniform vec4 iMouse;

const float arrow_density = 4.5;
const float arrow_length = 0.45;

const int iterationTime1 = 20;
const int iterationTime2 = 20;
const int vector_field_mode = 0;
const float scale = 8.0;

const float velocity_x = 0.1;
const float velocity_y = 0.2;

const float mode_2_speed = 2.5;
const float mode_1_detail = 200.0;
const float mode_1_twist = 50.0;

const int isArraw = 1;

const vec3 luma = vec3(0.2126, 0.7152, 0.0722);

float f(vec2 p) {
    return sin(p.x + sin(p.y + iTime * velocity_x)) * sin(p.y * p.x * 0.1 + iTime * velocity_y);
}

struct Field {
    vec2 vel;
    vec2 pos;
};

Field field(vec2 p, int mode) {
    Field field;
    field.vel = vec2(0.0, 0.0);// значения по умолчанию
    field.pos = vec2(0.0, 0.0);// значения по умолчанию
    if (mode == 0) {
        vec2 ep = vec2(0.05, 0.0);
        vec2 rz = vec2(0.0);
        // centered grid sampling
        for (int i = 0; i < iterationTime1; i++) {
            float t0 = f(p);
            float t1 = f(p + ep.xy);
            float t2 = f(p + ep.yx);
            vec2 g = vec2((t1 - t0), (t2 - t0)) / ep.xx;
            vec2 t = vec2(-g.y, g.x);
            // need update 'p' for next iteration, but give it some change.
            p += (mode_1_twist * 0.01) * t + g * (1.0 / mode_1_detail);
            p.x = p.x + sin(iTime * mode_2_speed / 10.0) / 10.0;
            p.y = p.y + cos(iTime * mode_2_speed / 10.0) / 10.0;
            rz = g;
        }
        field.vel = rz;
        return field;
    }

    if (mode == 1) {
        vec2 ep = vec2(0.05, 0.0);
        vec2 rz = vec2(0.0);
        // centered grid sampling
        for (int i = 0; i < iterationTime1; i++) {
            float t0 = f(p);
            float t1 = f(p + ep.xy);
            float t2 = f(p + ep.yx);
            vec2 g = vec2((t1 - t0), (t2 - t0)) / ep.xx;
            vec2 t = vec2(-g.y, g.x);
            // need update 'p' for next iteration, but give it some change.
            p += (mode_1_twist * 0.01) * t + g * (1.0 / mode_1_detail);
            p.x = p.x + sin(iTime * mode_2_speed / 10.0) / 10.0;
            p.y = p.y + cos(iTime * mode_2_speed / 10.0) / 10.0;
            rz = g;
        }

        field.vel = rz;
        // add curved effect into curved mesh
        for (int i = 1; i < iterationTime2; i++) {
            // try comment these 2 lines, will give more edge effect
            p.x += 0.3 / float(i) * sin(float(i) * 3.0 * p.y + iTime * mode_2_speed) + 0.5;
            p.y += 0.3 / float(i) * cos(float(i) * 3.0 * p.x + iTime * mode_2_speed) + 0.5;
        }
        field.pos = p;
        return field;
    }

    return field;
}

float segm(vec2 p, vec2 a, vec2 b) { // from iq
    vec2 pa = p - a;
    vec2 ba = b - a;
    float h = clamp(dot(pa, ba) / dot(ba, ba), 0.0, 1.0);
    return length(pa - ba * h) * 20.0 * arrow_density;
}

float fieldviz(vec2 p, int mode) {
    vec2 ip = floor(p * arrow_density) / arrow_density + vec2(0.5 / arrow_density);
    vec2 t = field(ip, mode).vel;
    float m = min(0.1, pow(length(t), 0.5) * (arrow_length / arrow_density));
    vec2 b = normalize(t) * m;
    float rz = segm(p, ip, ip + b);
    vec2 prp = vec2(-b.y, b.x);
    rz = min(rz, segm(p, ip + b, ip + b * 0.65 + prp * 0.3));
    return clamp(min(rz, segm(p, ip + b, ip + b * 0.65 - prp * 0.3)), 0.0, 1.0);
}

vec3 getRGB(in Field fld, in int mode) {
    if (mode == 0) {
        vec2 p = fld.vel;
        vec3 origCol = vec3(p * 0.5 + 0.5, 1.5);
        return origCol;
    }

    if (mode == 1) {
        vec2 p = fld.pos;
        float r = cos(p.x + p.y + 1.0) * 0.5 + 0.5;
        float g = sin(p.x + p.y + 1.0) * 0.5 + 0.5;
        float b = (sin(p.x + p.y) + cos(p.x + p.y)) * 0.3 + 0.5;
        vec3 col = sin(vec3(-0.3, 0.1, 0.5) + p.x - p.y) * 0.65 + 0.35;
        return vec3(r, g, b);
    }

    return vec3(0.0, 0.0, 0.0);
}

void main() {
    vec2 fragCoord = gl_FragCoord.xy;
    vec2 p = fragCoord / iResolution.xy - vec2(0.5);
    p.x *= iResolution.x / iResolution.y;
    p *= scale;

    vec2 uv = fragCoord / iResolution.xy;
    vec3 col;
    float fviz;

    int vector_mode = 0;
    Field fld = field(p, vector_mode);
    col = getRGB(fld, vector_mode) * 0.85;
    gl_FragColor = vec4(col, 1.0);
}
