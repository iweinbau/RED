package bxrdf;

import bxrdf.fresnel.FresnelDielectric;
import math.Normal;
import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;

public class SpecularTransmission extends BxRDF {

    RGBSpectrum cTransmit;

    FresnelDielectric fresnel;


    // Index of refraction above and under the surface
    double etaAbove;
    double etaBelow;

    public SpecularTransmission(RGBSpectrum cTransmit, double etaAbove, double etaBelow) {
        super(BxrdfType.BSDF_TRANSMISSION.getFlag() | BxrdfType.BSDF_SPECULAR.getFlag());
        this.etaAbove = etaAbove;
        this.etaBelow = etaBelow;
        this.cTransmit = cTransmit;
        this.fresnel = new FresnelDielectric(etaAbove,etaBelow);
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        return RGBSpectrum.BLACK;
    }

    @Override
    public RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal) {
        return  cTransmit.scale((1 - fresnel.eval(normal.dot(wi))/normal.absDot(wi)));
    }

    @Override
    public Vector3D sample_wi(Vector3D wo, Normal normal, Point2D u) {
        // 1. find the incoming and transmission index. Based of the direction.
        boolean entering = normal.dot(wo) > 0;
        double etaI = entering ? etaAbove : etaBelow;
        double etaT = entering ? etaBelow : etaAbove;

        // 2. Make sure the normal is in same direction as wo.
        Normal n =normal.dot(wo) < 0.f ? normal.neg().toNormal(): normal;

        // 3. refract incoming direction using snell's law.
        return wo.refract(n,etaI/etaT);
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
