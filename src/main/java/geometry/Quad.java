package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import core.SurfaceSample;
import material.Material;
import math.*;

public class Quad extends Geometry implements Primitive{

    private final Point3D originPoint = new Point3D(0);

    private final Normal normal = new Normal(0,1,0);

    private final Vector3D u = new Vector3D(1,0,0);

    private final Vector3D v = new Vector3D(0,0,-1);

    private final double length = 1;

    private Normal normalT;

    private double area;

    public Quad(Transform3D transform, Material material) {
        super(transform, material);
        this.normalT = this.transform.localToGlobal(normal).normalize().toNormal();
        area = 4 * transform.localToGlobal(u).length() * transform.localToGlobal(v).length();
    }

    public Quad(Transform3D transform) {
        super(transform);
        this.normalT = transform.localToGlobal(normal).normalize().toNormal();
    }

    boolean isOnQuad(Point3D point) {
        Vector3D vectorFromCenter = originPoint.subtract(point);

        if ( Math.abs(u.dot(vectorFromCenter)) > length)
            return false;
        if ( Math.abs(v.dot(vectorFromCenter)) > length)
            return false;

        return true;

    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        hitRecord.intersectionTests += 1;

        Ray localRay = transform.globalToLocal(ray);
        double t = originPoint.subtract(localRay.getOrigin()).dot(normal) / localRay.getDirection().dot(normal);

        if(t < Constants.kEps){
            return false;
        }

        Point3D localPoint = localRay.getPointAlongRay(t);

        if (isOnQuad(localPoint)) {
            Point3D globalPoint = ray.getPointAlongRay(t);
            double u = 0.5 + 0.5 * localPoint.getX();
            double v = 0.5 + 0.5 * localPoint.getZ();
            Point2D uv = new Point2D(u,v);
            hitRecord.setIntersection(ray.getDirection().neg(),this,localPoint,globalPoint,uv,normalT,t);
            return true;
        }

        return false;
    }

    @Override
    public BBox boundingBox() {
        return new BBox(transform.localToGlobal(new Point3D(-length,0,-length)),
                transform.localToGlobal(new Point3D(length,0,length)));
    }

    @Override
    public double getArea() {
        return area;
    }

    @Override
    public double pdf(SurfaceSample sample) {
        return 1. / area;
    }

    @Override
    public SurfaceSample sample(Point2D sample) {
        Point3D pointOnPlane = u.scale(2 * sample.getX() - 1).add(v.scale(2 * sample.getY() -1)).toPoint();
        return new SurfaceSample(transform.localToGlobal(pointOnPlane),normalT);
    }

    @Override
    public RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi) {
        return material.Le(point,normal,wi);
    }
}
