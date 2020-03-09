package camera;

import core.Sample;
import film.FrameBuffer;
import film.ViewPlane;
import core.Ray;
import math.Point3D;
import math.Vector3D;
import pathnode.EyeNode;

import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class Camera {

    /**
     * Camera eye position.
     */
    Point3D eye;

    /**
     * Point to look at.
     */
    Point3D lookAtPoint;

    /**
     * Camera view plane.
     */
    final ViewPlane vp;

    /**
     * Camera up vector.
     */
    final static Vector3D up = new Vector3D(0f,1f,0f);

    /**
     * Orthonormal basis for local camera axis.
     */
    Vector3D u; // points to the right
    Vector3D v; // points to the up direction;
    Vector3D w; // points to the view direction;

    /**
     *
     * Base constructor.
     *
     * @param eye the camera eye position.
     * @param lookAtPoint the point to look at.
     * @param vp The camera vp.
     */
    public Camera(Point3D eye, Point3D lookAtPoint,ViewPlane vp) {
        this.eye = eye;
        this.lookAtPoint = lookAtPoint;
        this.vp = vp;
    }

    /**
     * Get the current view plane of the camera.
     * @return the ViewPlane of this camera.
     */
    public ViewPlane getVp() {
        return vp;
    }

    /**
     *
     * Write render buffer to png image file.
     *
     * @param fileName Name of the output file.
     * @param sensitivity the image sensitivity (brightness)
     * @param gamma the gamma correction exponent.
     * @throws IOException when error occurred during execution.
     */
    public void bufferToImage(String fileName, double sensitivity, double gamma) throws IOException {
        this.vp.bufferToImage(fileName, sensitivity, gamma);
    }

    /**
     * Returns the render buffer to a BufferedImage
     * @param sensitivity the image sensitivity (brightness)
     * @param gamma the gamma correction exponent.
     * @return An BufferImage object.
     */
    public BufferedImage toBufferedImage(double sensitivity, double gamma) {
        return this.getVp().toBufferedImage(sensitivity,gamma);
    }

    /**
     * Write depth buffer to png image file.
     *
     * @param fileName Name of the output file.
     * @throws IOException when error occurred during execution.
     */
    public void depthBufferToImage(String fileName) throws IOException {
        this.vp.depthBufferToImage(fileName);
    }

    /**
     * Write normal buffer to png image file.
     *
     * @param fileName Name of the output file.
     * @throws IOException when error occurred during execution.
     */
    public void normalBufferToImage(String fileName) throws IOException {
        this.vp.normalBufferToImage(fileName);
    }

    public void intersectionBufferToImage(String fileName) throws IOException {
        this.vp.intersectionBufferToImage(fileName);
    }

    /**
     *
     * Get the render buffer of the ViewPlane.
     *
     * @return FrameBuffer object.
     */
    public FrameBuffer getRenderBuffer() {
        return this.vp.getRenderBuffer();
    }

    /**
     *
     * Generate a new view direction.
     * @param sample on the view plane.
     * @return Vector3D
     */
    public  abstract Vector3D viewDirection(Sample sample);

    /**
     *
     * Returns the camera eye position.
     * @return Vector3D
     */
    public Point3D getPosition() {
        return this.eye;
    }

    /**
     *
     * Clear all FrameBuffers.
     */
    public void clear() {
        this.vp.clear();
    }

}
