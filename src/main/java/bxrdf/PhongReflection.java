package bxrdf;

import math.Normal;
import math.RGBSpectrum;
import math.Vector3D;

public class PhongReflection extends BxRDF {

    RGBSpectrum ks;
    double e;

    public PhongReflection(RGBSpectrum cs, double e) {
        super(BxrdfType.BRDF_SPECULAR.getFlag());
        this.ks = cs;
        this.e = e;
    }

    @Override
    public RGBSpectrum rho() {
        return ks;
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        Vector3D R = wi.reflect(normal);
        return ks.scale(Math.pow(Math.max(0,R.dot(wo)),e));
    }
}
