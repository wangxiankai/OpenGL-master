#extension GL_OES_EGL_image_external : require

precision mediump float;
varying vec4 v_Color;

void main() {
    //颜色设置
    gl_FragColor = v_Color;
}
