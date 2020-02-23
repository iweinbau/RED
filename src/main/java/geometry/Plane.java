package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import material.Material;
import math.Normal;
import math.Point3D;
import math.Transform;

public class Plane extends Geometry {

    /**
     * Point on the plane
     */
    Point3D planePoint;

    /**
     * Plane normal.
     */
    Normal planeNormal;

    public Plane(Transform transform, Material material) {
        super(transform, material);
        this.planeNormal = transform.localToGlobal(new Normal(0,1,0)).normalize().toNormal();
        this.planePoint = transform.localToGlobal(new Point3D(0,0,0));
    }

    public Plane(Transform transform) {
        super(transform);
    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        Ray localRay = transform.globalToLocal(ray);
        double t = planePoint.subtract(ray.getOrigin()).dot(planeNormal) / ray.getDirection().dot(planeNormal);
        if(t > Constants.kEps) {
            Point3D localPoint = localRay.getPointAlongRay(t);
            Point3D globalHitPoint = ray.getPointAlongRay(t);
            hitRecord.setIntersection(ray.getDirection().neg(),this,localPoint,globalHitPoint,planeNormal,t);
            return true;
        }
        return false;
    }
}
