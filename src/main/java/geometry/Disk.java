package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import core.SurfaceSample;
import material.Material;
import math.*;
import sampler.Sampler;
import textures.Constant;

public class Disk extends Geometry implements Primitive {

    private final Point3D originPoint = new Point3D(0);

    private final Normal normal = new Normal(0,1,0);

    private final double radius  = 1;

    private Normal normalT;

    public Disk(Transform transform, Material material) {
        super(transform, material);
        normalT = transform.localToGlobal(normal).normalize().toNormal();
    }

    public Disk(Transform transform) {
        super(transform);
        normalT = transform.localToGlobal(normal).normalize().toNormal();
    }

    boolean isOnDisk(Point3D point) {
        return originPoint.subtract(point).length() < radius ;
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

        if (isOnDisk(localPoint)) {
            Point3D globalPoint = ray.getPointAlongRay(t);
            Normal n = transform.localToGlobal(normal);
            hitRecord.setIntersection(ray.getDirection().neg(),this,localPoint,globalPoint,n,t);
            return true;
        }

        return false;
    }

    @Override
    public BBox boundingBox() {
        Point3D p1 = originPoint.subtract(radius,radius,radius).toPoint();
        Point3D p2 = originPoint.add(radius,radius,radius);
        return new BBox(transform.localToGlobal(p1),transform.localToGlobal(p2));
    }

    @Override
    public double getArea() {
        return Constants.PI * radius * radius;
    }

    @Override
    public double pdf(SurfaceSample sample) {
        return Constants.invPI * radius * radius;
    }

    @Override
    public SurfaceSample sample(Point2D sample) {
        Point2D pointOnUnitDisk = Sampler.samplePointOnUnitDisk(sample);
        return new SurfaceSample(transform.localToGlobal(
                new Point3D(pointOnUnitDisk.getX(),0,pointOnUnitDisk.getY())),
                normalT);
    }

    @Override
    public RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi) {
        return material.Le(point,normal,wi);
    }
}
