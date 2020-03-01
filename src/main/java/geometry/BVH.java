package geometry;

import material.Material;
import math.Transform;

public class BVH extends Composite {

    public BVH(Transform transform) {
        super(transform);
    }

    public BVH(Transform transform, TriangleMesh mesh, Material material) {
        super(transform, mesh, material);
    }
}
