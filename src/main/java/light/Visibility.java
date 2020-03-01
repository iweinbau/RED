package light;

import core.Constants;
import core.Ray;
import math.Point3D;
import math.Vector3D;
import scene.Scene;

/**
 * Class for testing visibility of 2 points in a scene.
 */
public class Visibility {

    /**
     * first point
     */
    Point3D p0;

    /**
     * direction to second point.
     */
    Vector3D dir;

    /**
     * Distance to second point.
     */
    double distance;

    /**
     * Construct new visibility between 2 points.
     * @param p0 first point.
     * @param p1 second point.
     */
    public Visibility(Point3D p0, Point3D p1) {
        this.p0 = p0;
        this.dir = p1.subtract(p0).normalize();
        this.distance = p1.subtract(p0).length() - Constants.kEps;
    }

    /**
     * Construct new visibility function for a point and a direction.
     * @param p0 point
     * @param dir visibility direction
     */
    public Visibility(Point3D p0, Vector3D dir) {
        this.p0 = p0;
        this.dir = dir;
        this.distance = Double.POSITIVE_INFINITY;
    }

    /**
     * Chech the visibility for a given scene.
     * @param scene the scene where the visibility has to be evaluated.
     * @return true it's visible in scene, false otherwise.
     */
    public boolean isVisible(Scene scene){
        Ray visibilityRay = new Ray(p0.add(dir.scale(Constants.kEps)),dir,distance);
        return scene.shadowTraceRay(visibilityRay);
    }
}
