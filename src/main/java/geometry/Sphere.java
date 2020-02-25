package geometry;

import core.*;
import material.Material;
import math.Normal;
import math.Point3D;
import math.Transform;
import math.Vector3D;

public class Sphere  extends Geometry{

    /**
     * Construct new Sphere object
     * @param transform
     * @param material
     */
    public Sphere(Transform transform, Material material) {
        super(transform, material);
    }

    /**
     * Construct new sphere object.
     * @param transform
     */
    public Sphere(Transform transform) {
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
        Ray localRay = transform.globalToLocal(ray);


        Vector3D origin = localRay.getOrigin().toVector();

        double a = localRay.getDirection().dot(localRay.getDirection());
        double b = 2.0 * (localRay.getDirection().dot(origin));
        double c = origin.dot(origin) - 1.0;

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
            Normal normal =  globalHit.subtract(transform.localToGlobal(new Point3D(0))).normalize().toNormal();
            hitRecord.setIntersection(ray.getDirection().neg(), this, localHit, globalHit, normal,t);
            return true;

        }
        else
            return false;
    }
}
