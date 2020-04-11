package material;

import math.Normal;
import math.Point3D;
import math.RGBSpectrum;
import math.Vector3D;
import pathnode.ScatterNode;
import textures.Texture;

public class Emission extends Material {

    RGBSpectrum I;
    double factor;

    public Emission(RGBSpectrum I,double factor) {
        this.I = I;
        this.factor = factor;
    }

    public Emission(RGBSpectrum I) {
        this.I = I;
        this.factor = 1;
    }


    public RGBSpectrum getI() {
        return I.scale(factor);
    }

    @Override
    public void calculateBRDF(ScatterNode scatterNode) {

    }

    @Override
    public RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi) {
        return normal.dot(wi) > 0 ?  getI() : new RGBSpectrum(0);
    }

    @Override
    public boolean isShadowCaster() {
        return false;
    }
}
