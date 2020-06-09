package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import material.Material;
import math.*;

public class Cylinder extends Geometry {

    private final double yMax = 1;
    private final double yMin = -1;

    public Cylinder(Transform3D transform, Material material) {
        super(transform, material);
    }

    public Cylinder(Transform3D transform) {
        super(transform);
    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        Ray localRay = transform.globalToLocal(ray);

        Vector3D rayDir = localRay.getDirection();
        Point3D rayOrigin = localRay.getOrigin();

        double a = rayDir.getX() * rayDir.getX() + rayDir.getZ() * rayDir.getZ();
        double b = 2 * (rayDir.getX() * rayOrigin.getX() + rayDir.getZ() * rayOrigin.getZ());
        double c = rayOrigin.getX() * rayOrigin.getX() + rayOrigin.getZ() * rayOrigin.getZ() - 1;

        double d = b * b - 4.0 * a * c;

        if (d < 0)
            return false;

        double dr = Math.sqrt(d);

        // numerically solve the equation a*t^2 + b * t + c = 0
        double q = -0.5 * (b < 0 ? (b - dr) : (b + dr));

        double t0 = q / a;
        double t1 = c / q;

        if(t0 >= Constants.kEps || t1 >= Constants.kEps) {
            double t = t1;
            Point3D localHit;
            if (t > Constants.kEps) {
                localHit = localRay.getPointAlongRay(t);
                // Testing height
                if (localHit.getY() > 1 || localHit.getY() < -1){
                    t = t0;
                    if (t < Constants.kEps)
                        return false;
                    localHit = localRay.getPointAlongRay(t);
                    if (localHit.getY() > 1 || localHit.getY() < -1){
                        return false;
                    }
                }
            } else {
                t = t0;
                if (t < Constants.kEps)
                    return false;
                localHit = localRay.getPointAlongRay(t);
                if (localHit.getY() > 1 || localHit.getY() < -1){
                    return false;
                }
            }

            Point3D globalHit = ray.getPointAlongRay(t);
            Normal normal =  new Normal(localHit.getX(),0,localHit.getZ()).normalize().toNormal();
            normal = (normal.dot(rayDir.neg()) < 0.f) ? normal.neg().toNormal() : normal;

            double phi = Math.atan2(localHit.getZ(),localHit.getX());
            if(phi < 0)
                phi += 2*Constants.PI;
            double u = phi * 0.5 * Constants.invPI;
            double v = (localHit.getZ() - yMin) / (yMax - yMin);
            Point2D uv = new Point2D(u,v);

            hitRecord.setIntersection(ray.getDirection().neg(), this, localHit, globalHit, uv,
                    transform.localToGlobal(normal).normalize().toNormal(),t);
            return true;
        }

        return false;
    }

    @Override
    public BBox boundingBox() {
        return null;
    }
}
