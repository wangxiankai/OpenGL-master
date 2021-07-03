

attribute vec4 a_Position;
attribute vec4 a_Color;
varying vec4 v_Color;

void main() {
    //定义顶点坐标颜色
    v_Color = a_Color;

    gl_PointSize = 10.0;
    gl_Position = a_Position;
}
