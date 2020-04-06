package core;

import math.Normal;
import math.Point3D;

/**
 * Class for representing a sample on a geometric surface.
 */
public class SurfaceSample {

    /**
     * Sampled point on a surface.
     */
    private final Point3D sampledPoint;

    /**
     * surface normal at the sampled point.
     */
    private final Normal sampledNormal;

    /**
     * Construct new surface sample
     * @param sampledPoint point at the surface
     * @param sampledNormal normal at the surface.
     */
    public SurfaceSample(Point3D sampledPoint, Normal sampledNormal) {
        this.sampledPoint = sampledPoint;
        this.sampledNormal = sampledNormal;
    }

    /**
     * returns the sampled point.
     * @return Point3D
     */
    public Point3D getSampledPoint() {
        return sampledPoint;
    }

    /**
     * returns the surface normal at the sampled point.
     * @return Normal
     */
    public Normal getSampledNormal() {
        return sampledNormal;
    }
}
