package pathnode;

import geometry.Geometry;
import material.Emission;
import math.*;
import scene.Scene;

/**
 * Node at a the surface of geometry.
 */
public class SurfaceNode extends ScatterNode {

    Geometry geometry;

    public SurfaceNode(Point3D position, Point2D uv, Vector3D wo, Normal normal, Geometry geometry, PathNode parent) {
        super(position,uv,wo,normal,parent);
        this.geometry = geometry;
        this.geometry.getMaterial().calculateBRDF(this);
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample) {
        // TODO: expand node and update of next node, throughput: beta *= this.throughput * this.scatter;
        return null;
    }

    @Override
    public ScatterNode trace(Scene scene, Vector3D direction) {
        return null;
    }

    @Override
    public double pdf(Vector3D wi) {
        if(bxRDF == null)
            return 1;
        return bxRDF.pdf(wo,wi,normal);
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        if(bxRDF == null)
            return RGBSpectrum.BLACK;
        return bxRDF.f(this.wo,wi,this.normal)
                .scale(this.normal.maxDot(wi))
                .multiply(throughput);
    }

    @Override
    public RGBSpectrum Le() {
        if (geometry.getMaterial() instanceof Emission)
            return geometry.getMaterial().Le(position,normal,wo);
        return RGBSpectrum.BLACK;
    }

    @Override
    public boolean isSurfaceNode() {
        return true;
    }
}
