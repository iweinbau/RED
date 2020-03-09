package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import math.Normal;
import math.Point2D;
import math.Point3D;
import math.Vector3D;
import textures.Constant;

public class BBox {

    private Point3D pMin;

    private Point3D pMax;

    public BBox(Point3D p1) {
        this.pMin = p1;
        this.pMax = p1;
    }

    public BBox(Point3D p1, Point3D p2) {
        this.pMin = new Point3D(Math.min(p1.getX(),p2.getX()),
                Math.min(p1.getY(),p2.getY()),
                Math.min(p1.getZ(),p2.getZ()));
        this.pMax = new Point3D(Math.max(p1.getX(),p2.getX()),
                Math.max(p1.getY(),p2.getY()),
                Math.max(p1.getZ(),p2.getZ()));
    }

    public BBox() {
        this.pMax = new Point3D(-Constants.kLarge, -Constants.kLarge, -Constants.kLarge);
        this.pMin = new Point3D(Constants.kLarge, Constants.kLarge, Constants.kLarge);

    }

    /**
     *
     * Returns a new bounding box that's the union of this bounding box with a given point.
     * @param p a point for which we want the union.
     * @return a bounding box encapsulating this BBox and p.
     */
    public BBox union(Point3D p) {
        return new BBox(new Point3D(Math.min(pMin.getX(),p.getX()),
                Math.min(pMin.getY(),p.getY()),
                Math.min(pMin.getZ(),p.getZ())),
                new Point3D(Math.max(pMax.getX(),p.getX()),
                        Math.max(pMax.getY(),p.getY()),
                        Math.max(pMax.getZ(),p.getZ())));

    }

    public BBox union(BBox other) {
        return new BBox(new Point3D(Math.min(pMin.getX(),other.pMin.getX()),
                Math.min(pMin.getY(),other.pMin.getY()),
                Math.min(pMin.getZ(),other.pMin.getZ())),
                new Point3D(Math.max(pMax.getX(),other.pMax.getX()),
                        Math.max(pMax.getY(),other.pMax.getY()),
                        Math.max(pMax.getZ(),other.pMax.getZ())));

    }

    public Vector3D diagonal() {
        return pMax.subtract(pMin);
    }

    public Point3D center() {
        return pMin.add(pMax).scale(0.5);
    }
    /**
     *
     * Returns the index of the axis which has the longest extent.
     *
     * @return 0 for the x axis. 1 for the y axis and 2 for the z axis.
     */
    int maximumExent() {
        Vector3D d = this.diagonal();
        if (d.getX() > d.getY() && d.getX() > d.getZ())
            return 0;
        else if (d.getY() > d.getZ())
            return 1;
        else
            return 2;
    }

    public Point3D getpMin() {
        return pMin;
    }

    public Point3D getpMax() {
        return pMax;
    }

    /**
     * Check if a point p is within this bounding box.
     * @param p
     * @return
     */
    public boolean inside(Point3D p){
        return ((p.getX() >= pMin.getX() && p.getX() <= pMax.getX()) && (p.getY() >= pMin.getY() && p.getY() <= pMax.getY()) && (p.getZ() >= pMin.getZ() && p.getZ() <= pMax.getZ()));
    }

    /**
     *
     * Ray box intersection.
     * @param ray the ray to check against.
     * @return true if the ray intersect this box, false otherwise.
     */
    public boolean intersect(Ray ray, double t) {
        double ox = ray.getOrigin().getX();
        double oy = ray.getOrigin().getY();
        double oz = ray.getOrigin().getZ();

        double tmin,tymin,tzmin;
        double tmax,tymax,tzmax;

        double dx = 1.0 / ray.getDirection().getX();
        if (dx >= 0) {
            tmin = (pMin.getX() - ox) * dx;
            tmax = (pMax.getX() - ox) * dx;
        }
        else {
            tmin = (pMax.getX() - ox) * dx;
            tmax = (pMin.getX() - ox) * dx;
        }

        double dy = 1.0 / ray.getDirection().getY();
        if (dy >= 0) {
            tymin = (pMin.getY() - oy) * dy;
            tymax = (pMax.getY() - oy) * dy;
        }
        else {
            tymin = (pMax.getY() - oy) * dy;
            tymax = (pMin.getY() - oy) * dy;
        }

        if ( (tmin > tymax) || (tymin > tmax) )
            return false;

        if (tymin > tmin) {
            tmin = tymin;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        double dz = 1.0 / ray.getDirection().getZ();
        if (dz >= 0) {
            tzmin = (pMin.getZ() - oz) * dz;
            tzmax = (pMax.getZ() - oz) * dz;
        }
        else {
            tzmin = (pMax.getZ() - oz) * dz;
            tzmax = (pMin.getZ() - oz) * dz;
        }

        if ( (tmin > tzmax) || (tzmin > tmax) )
            return false;

        if (tzmin > tmin) {
            tmin = tzmin;
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        return tmin < t && tmax > Constants.kEps;
    }


}
