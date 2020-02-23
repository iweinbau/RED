package core;

import geometry.Geometry;
import math.Normal;
import math.Point3D;
import math.Vector3D;
import pathnode.BackgroundNode;
import pathnode.PathNode;
import pathnode.ScatterNode;
import pathnode.SurfaceNode;

/**
 *
 * Class for representing surface hits.
 */
public class HitRecord {

    /**
     * distance of the hit
     */
    double distance;

    /**
     * Hit point in the local coordinates if the interacted object
     */
    Point3D localHit;

    /**
     * Hit point in global space.
     */
    Point3D globalHit;

    /**
     * Hit direction.
     */
    Vector3D direction;

    /**
     * Surface normal on hit point.
     */
    Normal normal;

    /**
     * Interacted geometry.
     */
    Geometry geometry;

    /**
     * flag for when we hit something.
     */
    boolean hit = false;

    /**
     *
     * get hit distance
     * @return double
     */
    public double getDistance() {
        return distance;
    }

    /**
     *
     * Update hit record with new intersection.
     *
     * @param direction direction
     * @param object geometry
     * @param localHit local hit point
     * @param globalHit global hit point
     * @param normal surface normal
     * @param t hit distance.
     */
    public void setIntersection(Vector3D direction, Geometry object, Point3D localHit,
                               Point3D globalHit, Normal normal, double t) {
        this.distance = t;
        this.localHit = localHit;
        this.globalHit = globalHit;
        this.normal = normal;
        this.geometry = object;
        this.direction = direction;
        this.hit = true;
    }

    /**
     *
     * Update hit record with new hit record.
     *
     * @param record the new updated record.
     */
    public void setIntersection(HitRecord record) {
        this.distance = record.distance;
        this.localHit = record.localHit;
        this.globalHit = record.globalHit;
        this.normal = record.normal;
        this.geometry = record.geometry;
        this.direction = record.direction;
        this.hit = record.hit;
    }

    /**
     *
     * Transform hit record the a new scatter node.
     * If no hit occurred it returns a BackgroundNode else it returns a SurfaceNode.
     * @param parent origin node.
     * @return new ScatterNode.
     */
    public ScatterNode toScatterNode(PathNode parent) {
        if(!hit)
            return new BackgroundNode(direction,parent);
        else {
            return new SurfaceNode(globalHit,direction,normal,geometry,parent);
        }
    }
}
