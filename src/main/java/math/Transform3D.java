package math;

import core.Ray;

public class Transform3D implements ITransform3D {
    Matrix4 T = Matrix4.IDENTITY;
    Matrix4 inverseT = Matrix4.IDENTITY;

    public Transform3D() {

    }

    public Matrix4 getTransformation() {
        return T;
    }

    public Matrix4 getInverseTransformation() {
        return inverseT;
    }

    @Override
    public void translate(Point3D p) {
        Matrix4 transformation = new Matrix4(	1,	0,	0,	p.x,
                0,	1,	0,	p.y,
                0,	0,	1,	p.z,
                0,	0,	0,	1);

        Matrix4 inverse = new Matrix4(1,	0,	0,	-p.x,
                0,	1,	0,	-p.y,
                0,	0,	1,	-p.z,
                0,	0,	0,	1);

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void scale(double s) {
        Matrix4 transformation = new Matrix4(	s,	0,	0, 0,
                0,	s,	0,	0,
                0,	0,	s,	0,
                0,	0,	0,	1);

        Matrix4 inverse = new Matrix4(1/s,	0,	0, 0,
                0,	1/s,	0,	0,
                0,	0,	1/s,	0,
                0,	0,	0,	1);

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void scale(Vector3D s) {
        Matrix4 transformation = new Matrix4(	s.x,	0,	0, 0,
                0,	s.y,	0,	0,
                0,	0,	s.z,	0,
                0,	0,	0,	1);

        Matrix4 inverse = new Matrix4(1/s.x,	0,	0, 0,
                0,	1/s.y,	0,	0,
                0,	0,	1/s.z,	0,
                0,	0,	0,	1);

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void rotateX(double angle) {
        double rad = (double) Math.toRadians(angle);
        double sin = (double) Math.sin(rad);
        double cos = (double) Math.cos(rad);

        Matrix4 transformation = new Matrix4(	1,	0,	0, 0,
                0,	cos,	-sin,	0,
                0,	sin,	cos,	0,
                0,	0,	0,	1);

        Matrix4 inverse = transformation.transpose();

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void rotateY(double angle) {
        double rad = (double) Math.toRadians(angle);
        double sin = (double) Math.sin(rad);
        double cos = (double) Math.cos(rad);

        Matrix4 transformation = new Matrix4(
        cos,	0,	sin, 0,
                0,	1,	0,	0,
                -sin,	0,	cos,	0,
                0,	0,	0,	1);

        Matrix4 inverse = transformation.transpose();

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void rotateZ(double angle) {
        double rad =  Math.toRadians(angle);
        double sin =  Math.sin(rad);
        double cos =  Math.cos(rad);

        Matrix4 transformation = new Matrix4(
        cos,	-sin,	0, 0,
                sin,	cos,	0,	0,
                0,	0,	1,	0,
                0,	0,	0,	1);

        Matrix4 inverse = transformation.transpose();

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void rotate(Vector3D angle) {
        this.rotateZ(angle.z);
        this.rotateY(angle.y);
        this.rotateX(angle.x);
    }

    @Override
    public void align(Vector3D v1, Vector3D v2) {
        double dot = v1.normalize().dot(v2.normalize());
        double crossNorm = v1.normalize().cross(v2.normalize()).length();
        Matrix4 transformation = new Matrix4(
                dot,	crossNorm,	0, 0,
                -crossNorm,	dot,	0,	0,
                0,	0,	1,	0,
                0,	0,	0,	1);

        Matrix4 inverse = transformation.transpose();

        T = transformation.multiply(T);
        inverseT = inverseT.multiply(inverse);
    }

    @Override
    public void rotateTo(Vector3D v) {
        this.align(new Vector3D(0,1,0),v);
    }

    public Point3D localToGlobal(Point3D p){
        return T.multiply(p);
    }

    public Point3D globalToLocal(Point3D p) {
        return inverseT.multiply(p);
    }

    public Vector3D localToGlobal(Vector3D v){
        return T.multiply(v);
    }

    public Vector3D globalToLocal(Vector3D v) {
        return inverseT.multiply(v);
    }

    public Normal localToGlobal(Normal n){
        return inverseT.transpose().multiply(n);
    }

    public Normal globalToLocal(Normal n) {
        return T.transpose().multiply(n);
    }

    public Ray localToGlobal(Ray ray){
        Point3D p = T.multiply(ray.getOrigin());
        Vector3D dir = T.multiply(ray.getDirection());
        return new Ray(p,dir);
    }

    public Ray globalToLocal(Ray ray) {
        Point3D p = inverseT.multiply(ray.getOrigin());
        Vector3D dir = inverseT.multiply(ray.getDirection());
        return new Ray(p,dir,ray.getMaxDistance());
    }
}
