package bxrdf;

import bxrdf.fresnel.Fresnel;
import math.Normal;
import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;

public class SpecularReflection extends BxRDF {

    RGBSpectrum cReflect;
    Fresnel fresnel;

    public SpecularReflection(RGBSpectrum cReflect, Fresnel fresnel) {
        super(BxrdfType.BSDF_REFLECTION.getFlag() | BxrdfType.BSDF_SPECULAR.getFlag());
        this.cReflect = cReflect;
        this.fresnel = fresnel;
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        return RGBSpectrum.BLACK;
    }

    @Override
    public RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal) {
        return  cReflect.scale(fresnel.eval(normal.dot(wi))/normal.absDot(wi));
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
     * @return the sample_pdf cos(wi) to remove the cos dependency in LTE.
     */
    @Override
    public double sample_pdf(Vector3D wo, Vector3D wi, Normal normal) {
        return 1;
    }

    @Override
    public double pdf(Vector3D wo, Vector3D wi, Normal normal) {
        return 0;
    }
}
