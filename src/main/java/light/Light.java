package light;

import core.Ray;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import math.Vector3D;
import pathnode.ScatterNode;

public abstract class Light {

    RGBSpectrum I;
    Transform transform;

    public Light(RGBSpectrum I,Transform transform) {
        this.I = I;
        this.transform = transform;
    }

    public abstract Vector3D sample_wi(ScatterNode interaction);

    public abstract double Li_pdf();

    /**
     *
     * Returns self emitted radiance.
     *
     * @param ray
     * @return
     */
    public RGBSpectrum Le (Ray ray){
        return RGBSpectrum.BLACK;
    }

    public abstract RGBSpectrum Li();

    public abstract RGBSpectrum scatter(Vector3D wi);

    public abstract double distanceTo(Point3D p);

    public abstract Visibility getVisibility(Point3D hitPoint);
}
