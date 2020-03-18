package camera;

import core.Sample;
import film.ViewPlane;
import math.Point3D;
import math.Vector3D;
import pathnode.EyeNode;

public class PerspectiveCamera extends Camera{

    double lensRadius;

    double focalDistance;

    /**
     *
     * Construct new Perspective camera with a viewPlane distance of l.
     *
     * @param eye the position of the camera
     * @param lookAtPoint The point to look at.
     * @param horizontalResolution The horizontal image resolution.
     * @param verticalResolution The vertical image resolution.
     * @param FOV the horizontal FOV in degree.
     * @param viewPlaneDist The distance of the viewPlane.
     */
    public PerspectiveCamera(Point3D eye,
                             Point3D lookAtPoint,
                             int horizontalResolution,
                             int verticalResolution,
                             double FOV,
                             double viewPlaneDist) {

        super(eye, lookAtPoint,new ViewPlane(horizontalResolution,verticalResolution,FOV,viewPlaneDist));

        this.w = eye.subtract(lookAtPoint).normalize();
        this.u = this.up.cross(this.w).normalize();
        this.v = this.w.cross(this.u);

    }

    /**
     *
     * Construct new Perspective camera with a viewPlane distance of 1.
     *
     * @param eye the position of the camera
     * @param lookAtPoint The point to look at.
     * @param horizontalResolution The horizontal image resolution.
     * @param verticalResolution The vertical image resolution.
     * @param FOV the horizontal FOV in degree.
     */
    public PerspectiveCamera(Point3D eye,
                             Point3D lookAtPoint,
                             int horizontalResolution,
                             int verticalResolution,
                             double FOV) {
        this(eye, lookAtPoint,horizontalResolution,verticalResolution,FOV,1f);
    }

    /**
     * Generate a new view direction.
     * @param sample on the view plane.
     * @return Direction from the eye to the sample point on the view plane.
     */
    public Vector3D viewDirection(Sample sample){
        if(!vp.isInside(sample))
            throw new IllegalArgumentException("The given sample is outside the view plane");
        double u = (sample.getX()-vp.getHorizontalRes() *0.5f)*vp.getSx();
        double v = (sample.getY()-vp.getVerticalRes() *0.5f)*vp.getSy();

        return this.u.scale(u).add(this.v.scale(v)).add(this.w.scale(-vp.getDistance())).normalize();
    }
}
