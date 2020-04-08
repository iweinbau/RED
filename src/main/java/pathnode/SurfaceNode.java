package pathnode;

import core.Ray;
import geometry.Geometry;
import material.Emission;
import math.*;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        ScatterNode scatterNode = trace(scene,ray);
        successors.add(scatterNode);

        scatterNode.parent = this;
        // expand node and update of next node, throughput: next.throughput = this.scatter;
        scatterNode.throughput = this.scatter_f(wi);

        return scatterNode;
    }

    @Override
    public double pdf(Vector3D wi) {
        if(bxRDF == null)
            return 1;
        return bxRDF.sample_pdf(wo,wi,normal);
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        // If bxRDF is null this is a node on the light surface and has a zero contribution.
        if( bxRDF == null) {
            return RGBSpectrum.BLACK;
        }
        return bxRDF.f(this.wo,wi,this.normal)
                .scale(this.normal.maxDot(wi)/this.bxRDF.sample_pdf(wo,wi,normal))
                .multiply(throughput);
    }

    @Override
    public RGBSpectrum scatter_f(Vector3D wi) {
        // If bxRDF is null this is a node on the light surface and has a zero contribution.
        return bxRDF.sample_f(this.wo,wi,this.normal)
                .scale(this.normal.maxDot(wi)/this.bxRDF.sample_pdf(this.wo,wi,this.normal))
                .multiply(throughput);
    }

    @Override
    public RGBSpectrum Le() {
        return geometry.getMaterial().Le(position,normal,wo).multiply(this.throughput);
    }

    @Override
    public boolean isSurfaceNode() {
        return true;
    }
}
