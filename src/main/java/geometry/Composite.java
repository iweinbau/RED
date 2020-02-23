package geometry;

import core.HitRecord;
import core.Ray;
import material.Material;
import math.Transform;

import java.util.ArrayList;
import java.util.List;

public class Composite extends Geometry {

    List<Geometry> composites = new ArrayList<>();

    public Composite(Transform transform, Material material) {
        super(transform, material);
    }

    public Composite(Transform transform, TriangleMesh mesh, Material material) {
        super(transform, material);
        int [] indices = mesh.getIndices();
        for (int i = 0; i < indices.length; i+=3) {
            addGeometry(new Triangle(indices[i], indices[i+1], indices[i+2], mesh, transform, material));
        }
    }

    public void addGeometry(Geometry geometry) {
        this.composites.add(geometry);
    }

    @Override
    public boolean intersect(Ray ray, HitRecord hitRecord) {
        boolean hit = false;
        Ray copyRay = new Ray(ray);
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
