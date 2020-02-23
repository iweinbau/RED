package light;

import core.Constants;
import core.Ray;
import math.Point3D;
import math.Vector3D;
import scene.Scene;

public class Visibility {

    Point3D p0;
    Vector3D dir;
    double distance;

    public Visibility(Point3D p0, Point3D p1) {
        this.p0 = p0;
        this.dir = p1.subtract(p0).normalize();
        this.distance = p1.subtract(p0).length();
    }

    public Visibility(Point3D p0, Vector3D dir) {
        this.p0 = p0;
        this.dir = dir;
        this.distance = Double.POSITIVE_INFINITY;
    }

    public boolean isVisible(Scene scene){
        Ray visibilityRay = new Ray(p0.add(dir.scale(Constants.kEps)),dir,distance);
        return scene.shadowTraceRay(visibilityRay);
    }
}
