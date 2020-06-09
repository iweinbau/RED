package pathnode;

import bxrdf.BxRDF;
import bxrdf.BxRDFContainer;
import core.HitRecord;
import core.Ray;
import math.Point2D;
import math.Point3D;
import math.RGBSpectrum;
import math.Vector3D;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * PathNode represent node in a path.
 */
public abstract class PathNode{

    /**
     * Path depth of this node
     */
    int depth;

    /**
     * Outgoing direction of the node
     */
    Vector3D wo;

    /**
     *  Position in the world
     */
    Point3D position;

    /**
     *  Local position.
     */

    Point3D localPosition;

    /**
     *
     */
    RGBSpectrum throughput;

    double branch_factor = 1;

    /**
     *
     * Parent node, null if no parent exist.
     */
    PathNode parent;

    /**
     *
     * Successor node, next node in path. null if no successor exist.
     */
    List<ScatterNode> successors;

    /**
     * Bxrdf at surface, null if it is a background node
     */
    BxRDFContainer BSRDF;

    /**
     *
     *
     */
    public PathNode(Point3D position, Point3D localPoint, Vector3D wo, PathNode parent) {
        this.position = position;
        this.localPosition = localPoint;
        this.wo = wo;
        this.parent = parent;
        this.successors = new ArrayList<>();
        this.BSRDF = new BxRDFContainer();
        if(parent != null) {
            this.depth = parent.depth + 1;
        }
        else {
            this.depth = 0;
        }
    }

    /**
     *
     * Expand path with a new node.
     *
     */
    public abstract ScatterNode expand(Scene scene, Point2D sample);

    /**
     *
     * Expand path with a new node.
     *
     */
    public abstract ScatterNode expand(Scene scene, Point2D sample,double branch);

    public ScatterNode trace(Scene scene,Ray ray) {
        HitRecord hitRecord = scene.traceRay(ray);
        ScatterNode scatterNode = hitRecord.toScatterNode(this);
        return scatterNode;
    }

    /**
     *
     *
     * Returns normalized direction to an other path node. If other is a background node eg. position is null.
     * the other -wo direction is returned.
     *
     * If this is an eye node eg. no interaction other -wo is returned.
     *
     * @param other node to point to.
     * @return normalised direction.
     */
    public Vector3D directionTo(PathNode other) {
        return other.position.subtract(this.position).normalize();
    }

    /**
     *
     * Construct ray from this node to the other node.
     *
     * @param other node to point to.
     * @return new Ray with origin this and direction this.directionTo(other).
     */
    public Ray rayTo(PathNode other){
        throw new NotImplementedException();
    }

    public Ray rayFormParent() {
        return new Ray(this.position,wo);
    }

    public Point3D getPosition() {
        return this.position;
    }

    protected double distanceTo(PathNode other) {
        if(other.position == null)
            return 0;
        return other.position.subtract(this.position).length();
    }

    public Point3D getLocalPoint() {
        return localPosition;
    }

    public RGBSpectrum getThroughput() {
        return throughput;
    }

    public int getDepth() {
        return this.depth;
    }

}
