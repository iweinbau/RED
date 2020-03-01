package pathnode;

import bxrdf.BxRDF;
import math.Normal;
import math.Point3D;
import math.RGBSpectrum;
import math.Vector3D;

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

    /**
     * Bxrdf at surface, null if it is a background node
     */
    BxRDF bxRDF;

    public ScatterNode(Point3D position, Vector3D wo, Normal normal,PathNode parent) {
        super(position,wo,parent);
        this.normal = normal;
    }

    public abstract RGBSpectrum Le();

    public abstract double pdf(Vector3D wi);

    public abstract boolean isSurfaceNode();

    public void setBxRDF(BxRDF bxRDF) {
        this.bxRDF = bxRDF;
    }
}
