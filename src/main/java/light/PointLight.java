package light;

import core.Constants;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import math.Vector3D;
import pathnode.ScatterNode;
import textures.Constant;

public class PointLight extends Light{

    /**
     * Light position in global space.
     */
    Point3D lightPosition;

    /**
     * Construct new PointLight with given irradiance.
     * @param I
     * @param transform
     */
    public PointLight(RGBSpectrum I,Transform transform) {
        super(I,transform);
        this.I = I;
        lightPosition = transform.localToGlobal(new Point3D(0));
    }

    /**
     *
     * Calculate the contribution from a sampling direction.
     *
     * @return
     */
    @Override
    public RGBSpectrum Li() {
        return I.scale(4 * Constants.invPI);
    }

    /**
     *
     * @param wi the direction in which to scatter light.
     * @return RGBSpectrum
     */
    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        return Li().scale(1/this.Li_pdf());
    }

    /**
     *
     * @param p
     * @return double
     */
    @Override
    public double distanceTo(Point3D p) {
        return p.subtract(lightPosition).length();
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
    public Vector3D sample_wi(ScatterNode scatterNode) {
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
