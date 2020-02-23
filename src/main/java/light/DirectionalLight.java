package light;

import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import math.Vector3D;
import pathnode.ScatterNode;

public class DirectionalLight extends Light {

    Vector3D wi;

    public DirectionalLight(RGBSpectrum I, Vector3D direction, Transform transform) {
        super(I,transform);
        this.wi = direction.normalize().neg();
    }

    @Override
    public Vector3D sample_wi(ScatterNode interaction) {
        return this.wi;
    }

    @Override
    public double Li_pdf() {
        return 1;
    }

    @Override
    public RGBSpectrum Li() {
        return I;
    }

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        return Li().scale(1/this.Li_pdf());
    }

    @Override
    public double distanceTo(Point3D p) {
        return 1;
    }

    @Override
    public Visibility getVisibility(Point3D hitPoint) {
        return new Visibility(hitPoint,wi);
    }

}
