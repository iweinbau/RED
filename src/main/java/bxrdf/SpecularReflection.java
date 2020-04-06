package bxrdf;

import math.Normal;
import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;

public class SpecularReflection extends BxRDF {

    RGBSpectrum cReflect;

    public SpecularReflection(RGBSpectrum cReflect) {
        super(BxrdfType.BRDF_REFLECTION.getFlag() | BxrdfType.BRDF_SPECULAR.getFlag());
        this.cReflect = cReflect;
    }

    @Override
    public RGBSpectrum rho() {
        return cReflect;
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        return RGBSpectrum.BLACK;
    }

    @Override
    public RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal) {
        return  cReflect.scale(1/normal.dot(wi));
    }

    @Override
    public Vector3D sample_wi(Vector3D wo, Normal normal, Point2D u) {
        return wo.reflect(normal);
    }

    /**
     *
     *
     * @param wo outgoing direction
     * @param wi incoming direction (the reflection vector)
     * @param normal the normal vector.
     * @return the pdf
     */
    @Override
    public double pdf(Vector3D wo, Vector3D wi, Normal normal) {
        return 1;
    }
}
