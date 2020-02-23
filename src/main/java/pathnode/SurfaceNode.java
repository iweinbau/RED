package pathnode;

import geometry.Geometry;
import math.*;
import scene.Scene;

public class SurfaceNode extends ScatterNode {

    Geometry geometry;

    public SurfaceNode(Point3D position, Vector3D wo, Normal normal, Geometry geometry, PathNode parent) {
        super(position,wo,normal,parent);
        this.geometry = geometry;
        this.geometry.getMaterial().calculateBRDF(this);
    }

    @Override
    public ScatterNode expand(Scene scene) {
        // TODO: expand node and update throughput: beta *= this.throughput * this.scatter;
        return null;
    }

    @Override
    public ScatterNode trace(Scene scene, Vector3D direction) {
        return null;
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        return bxRDF.f(this.wo,wi,this.normal)
                .scale(this.normal.maxDot(wi)/this.bxRDF.pdf(this.wo,wi,normal))
                .multiply(throughput);
    }

    @Override
    public boolean isSurfaceNode() {
        return true;
    }
}
