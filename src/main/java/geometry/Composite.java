package geometry;

import core.HitRecord;
import core.Ray;
import core.SurfaceSample;
import material.Material;
import math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A container of different Geometric objects.
 */
public class Composite extends Geometry {

    /**
     * All children
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
            addGeometry( new Triangle(indices[i], indices[i+1], indices[i+2], mesh, transform, material));
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
            hitRecord.intersectionTests += tmpRecord.intersectionTests;
        }
        return hit;
    }

    @Override
    public BBox boundingBox() {
        if (composites.size() <=0 )
            throw new IllegalStateException("Cannot create bounding box for empty composite ");
        BBox boundingBox = composites.get(0).boundingBox();
        for (int i = 1; i<composites.size(); i++) {
            boundingBox = boundingBox.union(composites.get(i).boundingBox());
        }
        return boundingBox;
    }
}
