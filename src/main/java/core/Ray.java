package core;

import math.Point3D;
import math.Vector3D;

public class Ray {
    private double maxDistance;
    private Point3D origin;
    private Vector3D direction;

    public Ray(Point3D origin, Vector3D direction) {
        this.origin = origin;
        this.direction = direction;
        this. maxDistance = Double.POSITIVE_INFINITY;
    }

    public Ray(Point3D origin, Vector3D direction, double maxDistance) {
        this.origin = origin;
        this.direction = direction;
        this. maxDistance = maxDistance;
    }

    public Ray(Ray ray) {
        this.origin = ray.origin;
        this.direction = ray.direction;
        this. maxDistance = ray.maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public Point3D getOrigin() {
        return origin;
    }

    public Vector3D getDirection() {
        return direction;
    }

    public Point3D getPointAlongRay(double t) {
        return origin.add(direction.scale(t));
    }
}
