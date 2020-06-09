package pathnode;

import bxrdf.BxrdfType;
import core.Constants;
import core.Ray;
import geometry.BVH;
import geometry.Geometry;
import geometry.Triangle;
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
        if (BSRDF.numComponents() == 0)
            return null;
        Vector3D wi = BSRDF.sample_wi(wo,normal,sample);
        if (wi == null)
            return null;

        Point3D pos = this.position.add(wi.scale(Constants.kEps));

        Ray ray = new Ray(pos,wi);

        ScatterNode scatterNode = trace(scene,ray);
        // expand node and update of next node, throughput: next.throughput = this.scatterLight;
        scatterNode.throughput = this.scatter_f(wi);

        if(scatterNode.throughput.isZero())
            return null;

        successors.add(scatterNode);

        scatterNode.parent = this;

        return scatterNode;
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample, double branch) {
        // bxRDF is null this node is on a light surface.
        if (BSRDF.numComponents() == 0)
            return null;
        Vector3D wi = BSRDF.sample_wi(wo,normal,sample);
        if (wi == null)
            return null;

        Point3D pos = this.position.add(wi.scale(Constants.kEps));

        Ray ray = new Ray(pos,wi);

        ScatterNode scatterNode = trace(scene,ray);
        // expand node and update of next node, throughput: next.throughput = this.scatterLight;
        scatterNode.throughput = this.scatter_f(wi).scale(1./branch);

        if(scatterNode.throughput.isZero())
            return null;

        successors.add(scatterNode);

        scatterNode.parent = this;

        return scatterNode;
    }

    @Override
    public RGBSpectrum scatterLight(Vector3D wi) {
        // If bxRDF is null this is a node on the light surface and has a zero contribution.
        if( BSRDF.numComponents() == 0) {
            return RGBSpectrum.BLACK;
        }
        return throughput.multiply(BSRDF.f(this.wo,wi,this.normal)
                .scale(this.normal.absDot(wi)));
    }

    @Override
    public RGBSpectrum scatter_f(Vector3D wi) {
        // If bxRDF is null this is a node on the light surface and has a zero contribution.
        if( BSRDF.numComponents() == 0) {
            return RGBSpectrum.BLACK;
        }
        RGBSpectrum f = BSRDF.sample_f(wo,wi,this.normal);
        double pdf = BSRDF.pdf(wo,wi,normal);
        return throughput.multiply(f).scale(this.normal.absDot(wi)/(pdf));
    }

    @Override
    public RGBSpectrum Le() {
        return this.throughput.multiply(geometry.getMaterial().Le(position,normal,wo));
    }

    @Override
    public boolean isSurfaceNode() {
        return true;
    }
}
