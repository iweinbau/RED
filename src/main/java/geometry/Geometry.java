package geometry;

import core.HitRecord;
import core.Ray;
import material.Material;
import math.ITransform;
import math.Point3D;
import math.Transform;
import math.Vector3D;

public abstract class Geometry implements ITransform {

    /**
     * Transformation of this geometry.
     */
    final Transform transform;

    /**
     * Material of this geometry.
     */
    final Material material;

    /**
     * Base constructor
     * @param transform transformation of this object.
     * @param material material of this object.
     */
    public Geometry(Transform transform, Material material) {
        this.transform = transform;
        this.material = material;
    }

    /**
     * Base constructor
     * @param transform transformation of this object.
     */
    public Geometry(Transform transform) {
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
}
