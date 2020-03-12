package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import material.Material;
import math.*;

/**
 * Class representing Axis aligned box
 */
public class Box extends Geometry{

    /**
     * Box minimum point
     */
    private final Point3D p1 = new Point3D(-1, -1, -1);
    /**
     * Box maximum point
     */
    private final Point3D p2 = new Point3D(1, 1, 1);

    /**
     * Construct new Box with a given transformation and material.
     * @param transform
     * @param material
     */
    public Box(Transform3D transform, Material material) {
        super(transform, material);
    }

    /**
     * Construct new Box with a given transformation and material.
     * @param transform
     */
    public Box(Transform3D transform) {
        super(transform);
    }

    @Override
    public BBox boundingBox() {
        return new BBox(transform.localToGlobal(p1),transform.localToGlobal(p2));
    }

    /**
     *
     * Ray box intersection.
     * @param ray the ray to check against.
     * @param hitRecord hit record object for returning hit information.
     * @return true if the ray intersect this box, false otherwise.
     */
    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        //hitRecord.intersectionTests += 1;
        Ray localRay = transform.globalToLocal(ray);

        double ox = localRay.getOrigin().getX();
        double oy = localRay.getOrigin().getY();
        double oz = localRay.getOrigin().getZ();

        double tmin,tymin,tzmin;
        double tmax,tymax,tzmax;

        Normal normal;

        double dx = 1.0 / localRay.getDirection().getX();
        if (dx >= 0) {
            tmin = (p1.getX() - ox) * dx;
            tmax = (p2.getX() - ox) * dx;
        }
        else {
            tmin = (p2.getX() - ox) * dx;
            tmax = (p1.getX() - ox) * dx;
        }

        double dy = 1.0 / localRay.getDirection().getY();
        if (dy >= 0) {
            tymin = (p1.getY() - oy) * dy;
            tymax = (p2.getY() - oy) * dy;
        }
        else {
            tymin = (p2.getY() - oy) * dy;
            tymax = (p1.getY() - oy) * dy;
        }

        if ( (tmin > tymax) || (tymin > tmax) )
            return false;

        if (tymin > tmin) {
            tmin = tymin;
            normal = new Normal(0,-dy,0).normalize().toNormal();
        } else {
            normal = new Normal(-dx,0,0).normalize().toNormal();
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        double dz = 1.0 / localRay.getDirection().getZ();
        if (dz >= 0) {
            tzmin = (p1.getZ() - oz) * dz;
            tzmax = (p2.getZ() - oz) * dz;
        }
        else {
            tzmin = (p2.getZ() - oz) * dz;
            tzmax = (p1.getZ() - oz) * dz;
        }

        if ( (tmin > tzmax) || (tzmin > tmax) )
            return false;

        if (tzmin > tmin) {
            tmin = tzmin;
            normal = new Normal(0,0,-dz).normalize().toNormal();
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        if (tmin < tmax && tmax > Constants.kEps) {
            if ( tmin < Constants.kEps) {
                tmin = tmax;
            }
            Point3D localHit = localRay.getPointAlongRay(tmin);
            Point3D globalHit = ray.getPointAlongRay(tmin);
            hitRecord.setIntersection(ray.getDirection().neg(), this, localHit, globalHit,new Point2D(),
                    transform.localToGlobal(normal),tmin);
            return true;

        }else {
            return false;
        }
    }

}
