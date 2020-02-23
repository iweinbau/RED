package geometry;

import core.Constants;
import core.HitRecord;
import core.Ray;
import material.Material;
import math.*;

public class Triangle extends Geometry {

    Point3D p0,p1,p2;
    Normal n0,n1,n2;
    TriangleMesh mesh;

    public Triangle(int p0, int p1, int p2, TriangleMesh mesh, Transform transform, Material material) {
        super(transform, material);
        this.p0 = mesh.getVertices(p0);
        this.p1 = mesh.getVertices(p1);
        this.p2 = mesh.getVertices(p2);
        this.n0 = mesh.getNormals(p0);
        this.n1 = mesh.getNormals(p1);
        this.n2 = mesh.getNormals(p2);
        this.mesh = mesh;
    }

    public Triangle(Point3D p0, Point3D p1, Point3D p2, Transform transform, Material material) {
        super(transform, material);
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.n0 = p1.subtract(p0).cross(p2.subtract(p0)).normalize().toNormal();
    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {

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

        // we intersect this triangle
        Point3D localHit = p0.add(p1.subtract(p0).scale(beta)).add(p2.subtract(p0).scale(gamma));
        Point3D globalHit = transform.localToGlobal(localHit);

        Normal normal;
        if(mesh != null)
            normal = n0.scale(1 - gamma - beta).add(n1.scale(beta)).add(n2.scale(gamma)).normalize().toNormal();
        else
            normal = n0;

        hitRecord.setIntersection(ray.getDirection().neg(),
                this,localHit,globalHit,transform.localToGlobal(normal).normalize().toNormal(),t);

        return true;
    }
}
