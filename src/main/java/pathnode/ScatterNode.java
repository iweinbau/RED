package pathnode;

import bxrdf.BxRDF;
import math.*;

/**
 *
 * t can either be on a surface or disappearing into the background.
 *
 */
public abstract class ScatterNode extends PathNode {

    /**
     * surface normal at position, null if it is a background node.
     */
    Normal normal;

    Point2D uv;

    /**
     * Bxrdf at surface, null if it is a background node
     */
    BxRDF bxRDF;

    public ScatterNode(Point3D position, Point3D localPoint, Point2D uv, Vector3D wo, Normal normal,PathNode parent) {
        super(position, localPoint, wo, parent);
        this.normal = normal;
        this.uv = uv;
    }

    public Point2D getUv() {
        return this.uv;
    }

    public abstract RGBSpectrum Le();

    public abstract double pdf(Vector3D wi);

    public abstract boolean isSurfaceNode();

    public void setBxRDF(BxRDF bxRDF) {
        this.bxRDF = bxRDF;
    }
}
