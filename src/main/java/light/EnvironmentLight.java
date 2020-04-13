package light;

import core.Constants;
import core.Ray;
import math.*;
import pathnode.ScatterNode;
import sampler.Sampler;

public class EnvironmentLight extends Light {


    private Vector3D sampleDirection;
    private double cosThetaI;

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
        Vector3D w = scatterNode.getNormal();
        Vector3D v = new Vector3D(0.0034, 1, 0.0071).cross(w);
        v = v.normalize();
        Vector3D u = v.cross(w);

        Point3D p = Sampler.samplePointOnHemisphere(sample);

        this.sampleDirection = (u.scale(p.getX()).add(v .scale(p.getY())).add(w.scale(p.getZ()))).normalize();
        this.cosThetaI = w.dot(sampleDirection);

        return sampleDirection;
    }

    @Override
    public double Li_pdf() {
        return cosThetaI * Constants.invPI;
    }

    @Override
    public RGBSpectrum Li() {
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
