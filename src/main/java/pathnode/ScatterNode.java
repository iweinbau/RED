package pathnode;

import bxrdf.BxRDF;
import math.Normal;
import math.Point3D;
import math.Vector3D;

/**
 *
 * Path node type other than eye nodes. It can either be on a surface or disappearing into the background.
 *
 */
public abstract class ScatterNode extends PathNode {

    /**
     *
     */
    Normal normal;

    /**
     *
     */
    BxRDF bxRDF;

    public ScatterNode(Point3D position, Vector3D wo, Normal normal,PathNode parent) {
        super(position,wo,parent);
        this.normal = normal;
    }

    public abstract boolean isSurfaceNode();

    public void setBxRDF(BxRDF bxRDF) {
        this.bxRDF = bxRDF;
    }
}
