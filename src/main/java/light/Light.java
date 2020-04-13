package light;

import core.Ray;
import math.*;
import pathnode.ScatterNode;

public abstract class Light {

    /**
     * Light source power
     */
    RGBSpectrum I;

    /**
     * Light transformation.
     */
    Transform3D transform;

    /**
     * Construct new light with given power.
     * @param I the light power.
     * @param transform transformation of the light source.
     */
    public Light(RGBSpectrum I, Transform3D transform) {
        this.I = I;
        this.transform = transform;
    }

    /**
     *
     * Sample incoming direction on light source
     *
     * @param scatterNode reference node in path.
     * @return Vector3D
     */
    public abstract Vector3D sample_wi(ScatterNode scatterNode, Point2D sample);

    /**
     * Get probability of sampling a certain direction on the light source.
     * @return double
     */
    public abstract double Li_pdf();

    /**
     *
     * Returns self emitted radiance. Used for area lights.
     *
     * @param ray
     * @return
     */
    public RGBSpectrum Le (Ray ray){
        return RGBSpectrum.BLACK;
    }

    /**
     * Radiance along direction.
     * @return RGBSpectrum
     */
    public abstract RGBSpectrum Li();

    public abstract RGBSpectrum power();

    /**
     * Scatter light in a given direction
     * @param wi the direction in which to scatterLight light.
     * @return RGBSpectrum fraction of light scatterLight into wi.
     */
    public abstract RGBSpectrum scatter(Vector3D wi);

    /**
     * Distance from from p to light.
     * @param p
     * @return
     */
    public abstract double distanceFactor(Point3D p);

    /**
     * Get visibility function for this light for a given surface point.
     * @param hitPoint
     * @return
     */
    public abstract Visibility getVisibility(Point3D hitPoint);
}
