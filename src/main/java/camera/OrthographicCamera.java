package camera;

import core.Sample;
import core.Ray;
import film.ViewPlane;
import math.Point3D;
import math.Vector3D;
import pathnode.EyeNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class OrthographicCamera extends Camera {

    /**
     * @param eye
     * @param lookAtPoint
     */
    public OrthographicCamera(Point3D eye, Point3D lookAtPoint,
                              int horizontalResolution,
                              int verticalResolution,
                              double viewPlaneDist) {
        super(eye, lookAtPoint,new ViewPlane(horizontalResolution,verticalResolution,viewPlaneDist));
    }

    @Override
    public Vector3D viewDirection(Sample sample) {
        throw new NotImplementedException();
    }
}
