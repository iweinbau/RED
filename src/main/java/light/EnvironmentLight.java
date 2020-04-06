package light;

import core.Constants;
import core.Ray;
import math.*;
import pathnode.ScatterNode;
import sampler.Sampler;

public class EnvironmentLight extends Light {


    private Vector3D sampleDirection;

    /**
     * Construct new light with given power.
     *
     * @param I         the light power.
     */
    public EnvironmentLight(RGBSpectrum I) {
        super(I, new Transform3D());
    }

    @Override
    public RGBSpectrum Le(Ray ray) {
        return I;
    }

    @Override
    public Vector3D sample_wi(ScatterNode scatterNode, Point2D sample) {
        Point3D samplePoint = Sampler.samplePointOnUnitSphere(sample).scale(Constants.kLarge);
        Vector3D sampleDirection = Sampler.samplePointOnUnitSphere(sample).scale(Constants.kLarge)
                .subtract(scatterNode.getPosition()).normalize();
        this.sampleDirection = sampleDirection;
        return sampleDirection;
    }

    @Override
    public double Li_pdf() {
        return 4*Constants.PI;
    }

    @Override
    public RGBSpectrum Li(Vector3D wi) {
        return I;
    }

    @Override
    public RGBSpectrum power() {
        return I.scale(Constants.PI * Constants.kLarge *  Constants.kLarge);
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        return I.scale(1/Li_pdf());
    }

    @Override
    public double distanceFactor(Point3D p) {
        return 1;
    }

    @Override
    public Visibility getVisibility(Point3D hitPoint) {
        return new Visibility(hitPoint,sampleDirection);
    }
}
