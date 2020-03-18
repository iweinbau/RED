package geometry;

import core.*;
import material.Material;
import math.*;
import sampler.Sampler;
import textures.Constant;

public class Sphere extends Geometry implements Primitive{

    private double radius = 1.;

    /**
     * Construct new Sphere object
     * @param transform
     * @param material
     */
    public Sphere(Transform3D transform, Material material) {
        super(transform, material);
    }

    /**
     * Construct new Sphere object
     * @param transform
     * @param radius
     * @param material
     */
    public Sphere(Transform3D transform, double radius, Material material) {
        super(transform, material);
        this.radius = radius;
    }

    /**
     * Construct new sphere object.
     * @param transform
     */
    public Sphere(Transform3D transform) {
        super(transform);
    }

    /**
     *
     * @param ray the ray to check against.
     * @param hitRecord hit record object for returning hit information.
     * @return
     */
    @Override
    public boolean intersect(Ray ray,HitRecord hitRecord) {
        hitRecord.intersectionTests += 1;

        Ray localRay = transform.globalToLocal(ray);

        Vector3D origin = localRay.getOrigin().toVector();

        double a = localRay.getDirection().dot(localRay.getDirection());
        double b = 2.0 * (localRay.getDirection().dot(origin));
        double c = origin.dot(origin) - radius * radius;

        double d = b * b - 4.0 * a * c;

        if (d < 0)
            return false;
        double dr = Math.sqrt(d);

        // numerically solve the equation a*t^2 + b * t + c = 0
        double q = -0.5 * (b < 0 ? (b - dr) : (b + dr));

        double t0 = q / a;
        double t1 = c / q;

        if(t0 >= Constants.kEps || t1 >= Constants.kEps) {
            double t = Math.min(t0,t1);
            if (t < Constants.kEps) {
                t = Math.max(t0,t1);
            }
            // we hit
            Point3D localHit = localRay.getPointAlongRay(t);
            Point3D globalHit = ray.getPointAlongRay(t);
            double phi = Math.atan2(localHit.getZ(),localHit.getX());
            if(phi < 0)
                phi += 2*Constants.PI;
            double theta = Math.acos(Math.min(1,Math.max(localHit.getY()/radius,-1)));
            double u = phi * 0.5 * Constants.invPI;
            double v = theta * Constants.invPI;
            Point2D uv = new Point2D(u,v);
            Normal normal =  globalHit.subtract(transform.localToGlobal(new Point3D(0))).normalize().toNormal();
            hitRecord.setIntersection(ray.getDirection().neg(), this, localHit, globalHit, uv, normal,t);
            return true;

        }
        else
            return false;
    }

    @Override
    public BBox boundingBox() {
        return new BBox(transform.localToGlobal(new Point3D(-radius,-radius,-radius)),
                transform.localToGlobal(new Point3D(radius,radius,radius)));
    }

    @Override
    public double getArea() {
        return 4 * Constants.PI * radius * radius;
    }

    @Override
    public SurfaceSample sample(Point2D sample) {
        Point3D sampledPoint = Sampler.samplePointOnUnitSphere(sample).scale(radius);
        return new SurfaceSample(transform.localToGlobal(sampledPoint),
                transform.localToGlobal(sampledPoint.toVector().normalize().toNormal()));
    }

    @Override
    public RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi) {
        return material.Le(point,normal,wi);
    }

    @Override
    public double pdf(SurfaceSample sample) {
        return 4 * Constants.invPI * radius * radius;
    }
}
