package bxrdf;

import core.Constants;
import math.*;
import sampler.Sampler;

/**
 *
 *  Base class for all Bidirectional reflectance distributions.
 *
 */
public abstract class BxRDF{

    /**
     *
     * BxRDF flag, indicate which type of bxrdf it represent.
     *
     */
    final long flag;

    /**
     *
     * Constructor
     * @param flag bxrdf type.
     */
    public BxRDF(long flag) {
        this.flag = flag;
    }

    /**
     *
     * Returns true if the BxRDF is of the provided types, false otherwise.
     * @param flag the bxrdf type to check against.
     * @return true if this bxrdf is of the given type, returns false otherwise.
     */
    public boolean isOfType(long flag) {
        return (this.flag & flag) == this.flag;
    }


    public abstract RGBSpectrum rho();

    /**
     *
     * Used for sampling direct light contributions
     *
     * @param wo Normalized outgoing direction.
     * @param wi Normalized incoming direction.
     * @param normal surface normal.
     * @return Returns bidirectional reflectance from fr(wi -> wo).
     */
    public abstract RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal);

    /**
     *
     * Used for sampling indirect light contributions.
     * @param wo Normalized outgoing direction.
     * @param wi Normalized incoming direction.
     * @param normal surface normal.
     * @return Returns bidirectional reflectance from fr(wi -> wo).
     */
    public RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal) { return f(wo,wi,normal);
    }

    /**
     *
     * Sample a random incoming direction in the hemisphere given by the normal.
     *
     * @param wo Normalized outgoing direction
     * @param normal normal direction
     * @param sample sample point
     * @return Returns sampled incoming direction wi.
     */
    public Vector3D sample_wi(Vector3D wo, Normal normal, Point2D sample) {
        Vector3D v = new Vector3D(0.0034, 1, 0.0071).cross(normal);
        v = v.normalize();
        Vector3D u = v.cross(normal);

        Point3D p = Sampler.samplePointOnHemisphere(sample);

        Vector3D wi = u.scale(p.getX()).add(v .scale(p.getY())).add(normal.scale(p.getZ())).normalize();
        return wi;
    }

    /**
     *
     * Gives the probability density function of the outgoing and incoming direction with respect to the given normal.
     *
     * @param wo Normalized outgoing direction.
     * @param wi Normalized incoming direction.
     * @param normal surface normal.
     * @return probability density function.
     */
    public double pdf(Vector3D wo, Vector3D wi, Normal normal) {
        return normal.absDot(wi) * Constants.invPI;
    }
}
