package light;

import core.Constants;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import math.Vector3D;
import pathnode.ScatterNode;
import textures.Constant;

public class PointLight extends Light{

    Point3D lightPosition;

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

    @Override
    public RGBSpectrum scatter(Vector3D wi) {
        return Li().scale(1/this.Li_pdf());
    }

    @Override
    public double distanceTo(Point3D p) {
        return p.subtract(lightPosition).length();
    }

    @Override
    public Visibility getVisibility(Point3D hitPoint) {
        return new Visibility(hitPoint,lightPosition);
    }

    /**
     *
     * @return
     */
    @Override
    public Vector3D sample_wi(ScatterNode scatterNode) {
        return lightPosition.subtract(scatterNode.getPosition()).normalize();
    }

    /**
     *
     * @return
     */
    @Override
    public double Li_pdf() {
        return 1;
    }
}
