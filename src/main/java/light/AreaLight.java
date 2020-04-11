package light;

import core.Constants;
import core.Ray;
import core.SurfaceSample;
import geometry.Primitive;
import material.Emission;
import math.*;
import pathnode.ScatterNode;

public class AreaLight extends Light {

    Primitive primitive;

    double area;

    private SurfaceSample sample;

    /**
     * Construct new light with given radiance.
     *
     * @param primitive The primitive shape of the area light source.
     * @param emission  Emission material
     */
    public AreaLight(Primitive primitive, Emission emission) {
        super(emission.getI(), new Transform3D());
        this.primitive = primitive;
        this.area = primitive.getArea();
    }

    @Override
    public Vector3D sample_wi(ScatterNode scatterNode, Point2D sample) {
        this.sample = this.primitive.sample(sample);
        return this.sample.getSampledPoint().subtract(scatterNode.getPosition()).normalize();
    }

    @Override
    public double Li_pdf() {
        return primitive.pdf(sample);
    }

    @Override
    public RGBSpectrum Li(Vector3D wi) {
        return I;
    }

    @Override
    public RGBSpectrum Le(Ray ray) {
        return super.Le(ray);
    }

    @Override
    public RGBSpectrum power() {
        return I.scale(area * Constants.PI);
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        RGBSpectrum value = Li(wi).scale(this.sample.getSampledNormal().absDot(wi)/this.Li_pdf());
        return value;
    }

    @Override
    public double distanceFactor(Point3D p) {
        return p.subtract(sample.getSampledPoint()).dot(p.subtract(sample.getSampledPoint()));
    }

    @Override
    public Visibility getVisibility(Point3D hitPoint) {
        return new Visibility(hitPoint,sample.getSampledPoint());
    }
}
