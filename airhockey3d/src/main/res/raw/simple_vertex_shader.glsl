

attribute vec4 a_Position;
attribute vec4 a_Color;
varying vec4 v_Color;

uniform mat4 u_Matrix;

void main() {
    //定义顶点坐标颜色
    v_Color = a_Color;
    gl_PointSize = 10.0;
    //通过矩阵变换
    gl_Position = u_Matrix * a_Position;
}
