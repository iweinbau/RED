package light;

import core.Ray;
import math.*;
import pathnode.ScatterNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import textures.Texture;

public class EnvironmentLight extends Light {

    Texture<RGBSpectrum> I;

    public EnvironmentLight(RGBSpectrum I, Transform transform) {
        super(I, transform);
    }

    @Override
    public Vector3D sample_wi(ScatterNode scatterNode) {
        throw new NotImplementedException();
    }

    @Override
    public double Li_pdf() {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum Li() {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        throw new NotImplementedException();
    }

    @Override
    public double distanceTo(Point3D p) {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum Le(Ray ray) {
        // TODO: calculate uv coordinates for the mapping
        return I.evaluate(new Point2D(0));
    }

    @Override
    public Visibility getVisibility(Point3D hitPoint) {
        throw new NotImplementedException();
    }
}
