package pathnode;

import core.Ray;
import math.Point2D;
import math.Point3D;
import math.RGBSpectrum;
import math.Vector3D;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * PathNode represent node in a path.
 */
public abstract class PathNode{

    static final double MAX_DEPTH = 5;

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
     *
     */
    RGBSpectrum throughput;

    /**
     *
     * Parent node, null if no parent exist.
     */
    PathNode parent;

    /**
     *
     * Successor node, next node in path. null if no successor exist.
     */
    ScatterNode successor;

    /**
     *
     *
     */
    public PathNode(Point3D position, Vector3D wo, PathNode parent) {
        this.position = position;
        this.wo = wo;
        this.parent = parent;
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

    public abstract ScatterNode trace(Scene scene,Vector3D direction);

    public abstract RGBSpectrum scatter(Vector3D direction);

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
        throw new NotImplementedException();
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
}
