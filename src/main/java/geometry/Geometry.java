package geometry;

import core.HitRecord;
import core.Ray;
import material.Material;
import math.ITransform3D;
import math.Point3D;
import math.Transform3D;
import math.Vector3D;

public abstract class Geometry implements ITransform3D {

    /**
     * Transformation of this geometry.
     */
    final Transform3D transform;

    /**
     * Material of this geometry.
     */
    final Material material;

    /**
     * Base constructor
     * @param transform transformation of this object.
     * @param material material of this object.
     */
    public Geometry(Transform3D transform, Material material) {
        this.transform = transform;
        this.material = material;
    }

    /**
     * Base constructor
     * @param transform transformation of this object.
     */
    public Geometry(Transform3D transform) {
        this.transform = transform;
        this.material = null;
    }

    /**
     * Get the objects material.
     * @return Material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     *  Check for ray object intersection.
     * @param ray the ray to check against.
     * @param hitRecord hit record object for returning hit information.
     * @return true if the ray intersect this object, false otherwise.
     */
    public abstract boolean intersect(Ray ray,HitRecord hitRecord);

    /**
     * Returns the bounding box of this geometric object.
     * @return BBox
     */
    public abstract BBox boundingBox();


    @Override
    public void translate(Point3D p) {
        transform.translate(p);
    }

    @Override
    public void scale(double s) {
        transform.scale(s);
    }

    @Override
    public void scale(Vector3D s) {
        transform.scale(s);
    }

    @Override
    public void rotateX(double angle) {
        transform.rotateX(angle);
    }

    @Override
    public void rotateY(double angle) {
        transform.rotateY(angle);
    }

    @Override
    public void rotateZ(double angel) {
        transform.rotateZ(angel);
    }

    @Override
    public void rotate(Vector3D angle) {
        transform.rotate(angle);
    }

    @Override
    public void align(Vector3D v1, Vector3D v2) {
        transform.align(v1,v2);
    }

    @Override
    public void rotateTo(Vector3D v) {
        transform.rotateTo(v);
    }
}
