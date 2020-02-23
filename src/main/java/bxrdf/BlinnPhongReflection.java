package bxrdf;

import math.Normal;
import math.RGBSpectrum;
import math.Vector3D;

public class BlinnPhongReflection extends BxRDF {

    RGBSpectrum ks;
    double e;

    public BlinnPhongReflection(RGBSpectrum ks, double e) {
        super(BxrdfType.BRDF_SPECULAR.getFlag());
        this.ks = ks;
        this.e = e;
    }
    @Override
    public RGBSpectrum rho() {
        return RGBSpectrum.BLACK;
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        Vector3D halfway = wi.add(wo);
        halfway = halfway.normalize();

        return ks.scale(Math.pow(Math.max(0,normal.dot(halfway)),e));
    }
}
