package geometry;

import core.HitRecord;
import core.Ray;
import material.Material;
import math.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * A container of different Geometric objects.
 */
public class Composite extends Geometry {

    /**
     * All chile objects.
     */
    List<Geometry> composites = new ArrayList<>();

    /**
     * Construct new composite.
     * @param transform transform of the root object.
     */
    public Composite(Transform transform) {
        super(transform);
    }

    /**
     * Construct new composite object from a triangle mesh.
     * @param transform transform of the root object.
     * @param mesh triangle mesh
     * @param material material
     */
    public Composite(Transform transform, TriangleMesh mesh, Material material) {
        super(transform, material);
        int [] indices = mesh.getIndices();
        for (int i = 0; i < indices.length; i+=3) {
            addGeometry(new Triangle(indices[i], indices[i+1], indices[i+2], mesh, new Transform(), material));
        }
    }

    public void addGeometry(Geometry geometry) {
        this.composites.add(geometry);
    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        boolean hit = false;
        Ray copyRay = transform.globalToLocal(ray);
        for (Geometry object: this.composites) {
            HitRecord tmpRecord = new HitRecord();
            if(object.intersect(copyRay,tmpRecord) &&  tmpRecord.getDistance() < copyRay.getMaxDistance() ){
                copyRay.setMaxDistance(tmpRecord.getDistance());
                hitRecord.setIntersection(tmpRecord);
                hit = true;
            }
        }
        return hit;
    }
}
