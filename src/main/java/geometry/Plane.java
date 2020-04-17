package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import material.Material;
import math.*;

import java.awt.geom.Arc2D;

public class Plane extends Geometry {

    /**
     * Point on the plane
     */
    Point3D planePoint;

    /**
     * Plane normal.
     */
    Normal planeNormal;

    /**
     * Construct new Plane object from a given transformation.
     * @param transform Plane transformation object.
     * @param material Plane material.
     */
    public Plane(Transform3D transform, Material material) {
        super(transform, material);
        this.planeNormal = transform.localToGlobal(new Normal(0,1,0)).normalize().toNormal();
        this.planePoint = transform.localToGlobal(new Point3D(0,0,0));
    }

    /**
     * Construct new Plane without any material.
     * @param transform
     */
    public Plane(Transform3D transform) {
        super(transform);
    }

    /**
     *
     * @param ray the ray to check against.
     * @param hitRecord hit record object for returning hit information.
     * @return
     */
    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        hitRecord.intersectionTests += 1;

        Ray localRay = transform.globalToLocal(ray);
        double t = (planePoint.subtract(ray.getOrigin())).dot(planeNormal) / ray.getDirection().dot(planeNormal);
        if(t > Constants.kEps) {
            Point3D localPoint = localRay.getPointAlongRay(t);
            Point3D globalHitPoint = ray.getPointAlongRay(t);
            Point2D uv = new Point2D(localPoint.getX() - Math.floor(localPoint.getX()),
                    localPoint.getZ() - Math.floor(localPoint.getZ()));
            Normal n = planeNormal;
            if( n.dot(ray.getDirection().neg()) < 0){
                n = n.neg().toNormal();
            }
            hitRecord.setIntersection(ray.getDirection().neg(),this,localPoint,globalHitPoint,uv,n,t);
            return true;
        }
        return false;
    }

    @Override
    public BBox boundingBox() {
        return null;
    }
}
