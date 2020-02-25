package core;

import math.Point3D;
import math.Vector3D;
import textures.Constant;

/*
 * Class representing a ray starting from a certain point in a specified direction.
 * all points along ray are described as p = o + t * d;
 */
public class Ray {
    /**
     * Max ray distance used for clipping intersection that exceed this value.
     */
    private double maxDistance;

    /**
     * Ray starting point
     */
    private Point3D origin;

    /**
     * Direction of ray.
     */
    private Vector3D direction;

    /**
     * Construct new ray with infinite length.
     *
     * @param origin the starting point of the ray.
     * @param direction the direction of the ray.
     */
    public Ray(Point3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction;
        this. maxDistance = Double.POSITIVE_INFINITY;
    }

    /**
     * Construct new ray with specified max distance.
     * @param origin the starting point of the ray.
     * @param direction the direction of the ray.
     * @param maxDistance max ray distance.
     */
    public Ray(Point3D origin, Vector3D direction, double maxDistance) {
        this.origin = origin;
        this.direction = direction;
        this. maxDistance = maxDistance;
    }

    /**
     * Construct new ray, copy constructor.
     * @param ray the ray to make a copy of
     */
    public Ray(Ray ray) {
        this.origin = ray.origin;
        this.direction = ray.direction;
        this. maxDistance = ray.maxDistance;
    }

    /**
     *
     * Update the max ray distance to the new value specified by maxDistance
     * @param maxDistance the new max ray distance.
     */
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     *
     * Get the max ray distance.
     * @return double
     */
    public double getMaxDistance() {
        return maxDistance;
    }

    /**
     *
     * Get the ray origin.
     * @return Point3D
     */
    public Point3D getOrigin() {
        return origin;
    }

    /**
     * Get ray direction
     * @return Vector3D
     */
    public Vector3D getDirection() {
        return direction;
    }

    /**
     *
     * Get point along ray at distance t;
     * @param t the distance along the ray.
     * @return Point3D
     * @throws IllegalArgumentException if t is smaller then 0;
     */
    public Point3D getPointAlongRay(double t) throws IllegalArgumentException{
        if( t < 0)
            throw new IllegalArgumentException("Argument should be larger then 0");
        if(t < Constants.kEps)
            return this.origin;
        return origin.add(direction.scale(t));
    }
}
