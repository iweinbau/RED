package pathnode;

import core.Ray;
import geometry.Geometry;
import material.Emission;
import math.*;
import scene.Scene;

/**
 * Node at a the surface of geometry.
 */
public class SurfaceNode extends ScatterNode {

    Geometry geometry;

    public SurfaceNode(Point3D position, Point3D localPoint, Point2D uv, Vector3D wo, Normal normal, Geometry geometry, PathNode parent) {
        super(position, localPoint, uv, wo, normal, parent);
        this.geometry = geometry;
        this.geometry.getMaterial().calculateBRDF(this);
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample) {
        // bxRDF is null this node is on a light surface.
        if (bxRDF == null)
            return null;
        Vector3D wi = bxRDF.sample_wi(wo,normal,sample);
        Ray ray = new Ray(this.position,wi);
        ScatterNode next = trace(scene,ray);
        next.parent = this;
        this.successor = next;
        // expand node and update of next node, throughput: next.throughput = this.scatter;
        next.throughput = this.scatter_f(wi);
        return next;
    }

    @Override
    public double pdf(Vector3D wi) {
        if(bxRDF == null)
            return 1;
        return bxRDF.pdf(wo,wi,normal);
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        // If bxRDF is null this is a node on the light surface and has a zero contribution.
        if(bxRDF == null)
            return RGBSpectrum.BLACK;
        return bxRDF.f(this.wo,wi,this.normal)
                .scale(this.normal.maxDot(wi)/this.bxRDF.pdf(wo,wi,normal))
                .multiply(throughput);
    }

    @Override
    public RGBSpectrum scatter_f(Vector3D wi) {
        // If bxRDF is null this is a node on the light surface and has a zero contribution.
        if(bxRDF == null)
            return RGBSpectrum.BLACK;
        return bxRDF.sample_f(this.wo,wi,this.normal)
                .scale(this.normal.maxDot(wi)/this.bxRDF.pdf(wo,wi,normal))
                .multiply(throughput);
    }

    @Override
    public RGBSpectrum Le() {
        if (geometry.getMaterial() instanceof Emission)
            return geometry.getMaterial().Le(position,normal,wo).multiply(this.throughput);
        return RGBSpectrum.BLACK;
    }

    @Override
    public boolean isSurfaceNode() {
        return true;
    }
}
