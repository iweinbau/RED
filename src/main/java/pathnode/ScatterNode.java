package pathnode;

import bxrdf.BxRDF;
import bxrdf.BxrdfType;
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

    public ScatterNode(Point3D position, Point3D localPoint, Point2D uv, Vector3D wo, Normal normal,PathNode parent) {
        super(position, localPoint, wo, parent);
        this.normal = normal;
        this.uv = uv;
    }

    public Point2D getUv() {
        return this.uv;
    }

    public abstract RGBSpectrum scatterLight(Vector3D direction);

    public abstract RGBSpectrum scatter_f(Vector3D wi);

    public abstract RGBSpectrum Le();

    public abstract boolean isSurfaceNode();

    public void setBxRDF(BxRDF bxRDF) {
        this.BSRDF.addBxRDF(bxRDF);
    }

    public boolean isSpecularBounce() {
        if (this.parent.BSRDF.numComponents() <= 0 || this.parent.BSRDF.sampledBRDF == null)
            return false;
        return (this.parent.BSRDF.sampledBRDF.flag & BxrdfType.BSDF_SPECULAR.getFlag()) != 0;
    }

    public Normal getNormal() {
        return this.normal;
    }
}
