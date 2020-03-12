package math;

import core.Ray;

public class Transform2D implements ITransform2D {
    Matrix3 T = Matrix3.IDENTITY;
    Matrix3 inverseT = Matrix3.IDENTITY;

    public Transform2D() {

    }

    public Matrix3 getTransformation() {
        return T;
    }

    public Matrix3 getInverseTransformation() {
        return inverseT;
    }

    @Override
    public void translate(Point2D p) {
        Matrix3 transformation = new Matrix3(
                1,	0,	p.x,
                0,	1,	p.y,
                0,	0,	1);

        Matrix3 inverse = new Matrix3(
                1,	0,	-p.x,
                0,	1,	-p.y,
                0,	0,	1);

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void scale(double s) {
        Matrix3 transformation = new Matrix3(
                s,	0,	0,
                0,	s,	0,
                0,	0,	1);

        Matrix3 inverse = new Matrix3(
                1/s,	0,	0,
                0,	1/s,	0,
                0,	0,	1);
        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void scale(Vector2D s) {
        Matrix3 transformation = new Matrix3(
                s.x, 0, 0,
                0,	s.y, 0,
                0,	0, 1);

        Matrix3 inverse = new Matrix3(
                1/s.x,	0,	0,
                0,	1/s.y,	0,
                0,	0, 1);

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void rotate(double angle) {
        double rad =  Math.toRadians(angle);
        double sin =  Math.sin(rad);
        double cos =  Math.cos(rad);

        Matrix3 transformation = new Matrix3(
                cos,	-sin,	0,
                sin,	cos,	0,
                0,	0,	1);
        Matrix3 inverse = new Matrix3(
                cos,	sin,	0,
                -sin,	cos,	0,
                0,	0,	1);

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    public Point2D localToGlobal(Point2D p){
        return T.multiply(p);
    }

    public Point2D globalToLocal(Point2D p) {
        return inverseT.multiply(p);
    }

    public Vector2D localToGlobal(Vector2D v){
        return T.multiply(v);
    }

    public Vector2D globalToLocal(Vector2D v) {
        return inverseT.multiply(v);
    }

}
