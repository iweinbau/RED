package light;

import core.Constants;
import math.*;
import pathnode.ScatterNode;

public class PointLight extends Light{

    /**
     * Light position in global space.
     */
    Point3D lightPosition;

    /**
     * Construct new PointLight with given power.
     * @param I
     * @param transform
     */
    public PointLight(RGBSpectrum I, Transform3D transform) {
        super(I,transform);
        lightPosition = transform.localToGlobal(new Point3D(0));
    }

    /**
     *
     * Calculate the contribution from a sampling direction.
     *
     * @return
     */
    @Override
    public RGBSpectrum Li(Vector3D wi) {
        return I;
    }

    @Override
    public RGBSpectrum power() {
        return I.scale(4 * Constants.PI);
    }

    /**
     *
     * @param wi the direction in which to scatter light.
     * @return RGBSpectrum
     */
    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        return Li(wi);
    }

    /**
     *
     * @param p
     * @return double
     */
    @Override
    public double distanceFactor(Point3D p) {
        return p.subtract(lightPosition).dot(p.subtract(lightPosition));
    }

    /**
     *
     * @param hitPoint
     * @return Visibility
     */
    @Override
    public Visibility getVisibility(Point3D hitPoint) {
        return new Visibility(hitPoint,lightPosition);
    }

    /**
     *
     * returns direction from reference node to light position.
     * @param scatterNode reference node in path.
     * @return Vector3D
     */
    @Override
    public Vector3D sample_wi(ScatterNode scatterNode, Point2D sample) {
        return lightPosition.subtract(scatterNode.getPosition()).normalize();
    }

    /**
     *
     * @return double
     */
    @Override
    public double Li_pdf() {
        return 1;
    }
}
