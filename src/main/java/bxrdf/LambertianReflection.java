package bxrdf;

import core.Constants;
import math.Normal;
import math.RGBSpectrum;
import math.Vector3D;

public class LambertianReflection extends BxRDF {

    /**
     * color factor for lambertian reflection
     */
    RGBSpectrum cd;

    public LambertianReflection(RGBSpectrum cd) {
        super(BxrdfType.BSDF_REFLECTION.getFlag() | BxrdfType.BSDF_DIFFUSE.getFlag());
        this.cd = cd;
    }

    /**
     *
     * Returns lambertian reflectance model.
     *
     * @param wo Normalized outgoing direction.
     * @param wi Normalized incoming direction.
     * @param normal surface normal.
     * @return Returns cd / PI;
     */
    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        return cd.scale(Constants.invPI);
    }

}
