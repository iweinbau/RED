package geometry;

import core.*;
import material.Material;
import math.*;
import sampler.Sampler;

public class Triangle extends Geometry implements Primitive {

    /**
     * Triangle points in local space.
     */
    Point3D p0,p1,p2;

    /**
     * Triangle normal in local space.
     */
    Normal n0,n1,n2;

    Point2D uv0,uv1,uv2;

    /**
     * reference to triangle mesh.
     */
    TriangleMesh mesh;

    /**
     * Construct new triangle from triangle mesh.
     * @param p0 index first point.
     * @param p1 index second point.
     * @param p2 index third point
     * @param mesh reference to the Triangle mesh
     * @param transform object transform.
     * @param material object material.
     */
    public Triangle(int p0, int p1, int p2, TriangleMesh mesh, Transform3D transform, Material material) {
        super(transform, material);
        this.p0 = mesh.getVertices(p0);
        this.p1 = mesh.getVertices(p1);
        this.p2 = mesh.getVertices(p2);
        this.n0 = mesh.getNormals(p0);
        this.n1 = mesh.getNormals(p1);
        this.n2 = mesh.getNormals(p2);
        this.uv0 = mesh.getUv(p0);
        this.uv1 = mesh.getUv(p1);
        this.uv2 = mesh.getUv(p2);
        this.mesh = mesh;
    }

    /**
     * Construct new triangle object
     * @param p0 First point in local space
     * @param p1 Second point in local space
     * @param p2 Third point in local space
     * @param transform triangle transformation.
     * @param material material transformation.
     */
    public Triangle(Point3D p0, Point3D p1, Point3D p2, Transform3D transform, Material material) {
        super(transform, material);
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n0 = p1.subtract(p0).cross(p2.subtract(p0)).normalize().toNormal();
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

        Vector3D origin = localRay.getOrigin().toVector();

        Matrix3 A = new Matrix3(p0.subtract(p1),p0.subtract(p2),localRay.getDirection());

        double beta = new Matrix3(p0.subtract(origin.toPoint()),
                                    p0.subtract(p2),
                                    localRay.getDirection()).det() / A.det();
        if( beta < 0 || beta > 1)
            return false;

        double gamma = new Matrix3(p0.subtract(p1),
                                    p0.subtract(origin.toPoint()),
                                    localRay.getDirection()).det() / A.det();
        if( gamma < 0 || gamma > 1 - beta)
            return false;

        double t = new Matrix3(p0.subtract(p1),p0.subtract(p2),p0.subtract(origin.toPoint())).det() / A.det();

        if(t < Constants.kEps)
            return false;

        Normal normal;
        Point2D uv;
        if(mesh != null) {
            normal = n0.scale(1 - gamma - beta).add(n1.scale(beta)).add(n2.scale(gamma)).normalize().toNormal();
            if (this.uv0 != null) {
                uv = uv0.scale(1 - gamma - beta).add(uv1.scale(beta)).add(uv2.scale(gamma));
            } else {
                uv = new Point2D();
            }
        }
        else {
            normal = n0;
            uv = new Point2D();
        }

        // we intersect this triangle
        Point3D localHit = p0.add(p1.subtract(p0).scale(beta)).add(p2.subtract(p0).scale(gamma));
        Point3D globalHit = transform.localToGlobal(localHit);

        hitRecord.setIntersection(ray.getDirection().neg(),
                this,localHit,globalHit,uv,transform.localToGlobal(normal).normalize().toNormal(),t);

        return true;
    }

    @Override
    public BBox boundingBox() {
        return new BBox(transform.localToGlobal(p0),transform.localToGlobal(p1))
                .union(transform.localToGlobal(p2));
    }

    @Override
    public double getArea() {
        return transform.localToGlobal(p1).subtract(transform.localToGlobal(p0))
                .dot(transform.localToGlobal(p2).subtract(transform.localToGlobal(p0)))/2.;
    }

    @Override
    public SurfaceSample sample(Point2D sample) {
        Point2D b = Sampler.uniformSampleTriangle(sample);
        Point3D sampledPoint = p0.scale(1-b.getX()-b.getY()).add(p1.scale(b.getX())).add(p2.scale(b.getY()));
        Normal normal;
        if(mesh != null)
            normal = n0.scale(1-b.getX()-b.getY())
                    .add(n1.scale(b.getX()))
                    .add(n2.scale(b.getY())).normalize().toNormal();
        else
            normal = n0;
        return new SurfaceSample(transform.localToGlobal(sampledPoint),
                transform.localToGlobal(normal).normalize().toNormal());
    }

    @Override
    public RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi) {
        return material.Le(point,normal,wi);
    }

    @Override
    public double pdf(SurfaceSample sample) {
        return 1./this.getArea();
    }
}
