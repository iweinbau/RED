package film;

import core.Sample;
import math.Normal;
import math.RGBSpectrum;
import math.Vector3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewPlane {
    /**
     * Horizontal resolution in pixels
     */
    int horizontalRes;

    /**
     * Vertical resolution in pixels.
     */
    int verticalRes;

    /**
     * AspectRation horizontal/vertical.
     */
    double aspectRatio;

    /**
     * With of the view plane.
     */
    double width;

    /**
     * Height of the view plane.
     */
    double height;

    double sx; //pixel size in horizontal direction;
    double sy; //pixel size in vertical direction;
    double d; // view plane distance from camera;

    /**
     * Max distance used to normalize values in depth buffer.
     */
    double maxDepth;

    /**
     *
     * Main buffer for the final render.
     */
    FrameBuffer buffer;

    /**
     * Normal buffer.
     */
    FrameBuffer normalBuffer;

    /**
     * Depth buffer.
     */
    FrameBuffer depthBuffer;

    /**
     * Constructor
     * @param horizontalRes pixels in x direction.
     * @param verticalRes pixels in y direction.
     * @param FOV horizontal field of view;
     * @param d view plane distance with respect to the camera eye point.
     */
    public ViewPlane(int horizontalRes, int verticalRes,double FOV,double d) {
        this.horizontalRes = horizontalRes;
        this.verticalRes = verticalRes;
        this.aspectRatio = horizontalRes / verticalRes;
        this.d = d;

        this.width = (2.0f * Math.tan(0.5f * Math.toRadians(FOV))) * d;
        this.height = this.width / this.aspectRatio;

        this.sx = width / horizontalRes;
        this.sy = height / verticalRes;

        this.buffer = new FrameBuffer(horizontalRes,verticalRes);
        this.depthBuffer = new FrameBuffer(horizontalRes,verticalRes);
        this.normalBuffer = new FrameBuffer(horizontalRes,verticalRes);

    }

    public ViewPlane(int horizontalResolution, int verticalResolution, double viewPlaneDist) {
    }

    /**
     * Add a RGBSpectrum to a pixel on the viewPlane
     * @param height the height index of the pixel.
     * @param width the width index of the pixel.
     * @param color the RGBSpectrum of the pixel.
     */
    public void addColor(int height, int width, RGBSpectrum color){
        buffer.addPixel(height,width,color);
    }

    /**
     *
     * Add depth value to depth buffer.
     *
     * @param height the height index of the pixel.
     * @param width the width index of the pixel.
     * @param depth the depth of the first intersection.
     */
    public void addDepth(int height, int width, double depth) {
        if( maxDepth < depth)
            maxDepth = depth;
        depthBuffer.addPixel(height,width,new RGBSpectrum(depth));
    }

    public void addNormal(int height, int width, Normal normal) {
        if(normal == null)
            normalBuffer.addPixel(height, width, RGBSpectrum.BLACK);
        else
            normalBuffer.addPixel(height, width, new RGBSpectrum(normal.scale(1./2.).add(new Vector3D(1./2.))));
    }

    /**
     *
     * Subdivide framebuffer into tiles.
     *
     * @param prefWidth preferred tile with
     * @param prefHeight preferred tile height
     * @return Collection of tiles.
     * @throws IllegalArgumentException
     */
    public Collection<Tile> subdivide(int prefWidth, int prefHeight) throws IllegalArgumentException{
        if (prefWidth <= 0)
            throw new IllegalArgumentException(
                    "the width of a tile must be larger than zero!");
        if (prefHeight <= 0)
            throw new IllegalArgumentException(
                    "the height of a tile must be larger than zero!");

        final List<Tile> tiles = new ArrayList<>();
        for (int y = 0; y < this.verticalRes; y += prefHeight) {
            for (int x = 0; x < this.horizontalRes; x += prefWidth) {
                int xEnd = Math.min(this.horizontalRes, x + prefWidth);
                int yEnd = Math.min(this.verticalRes, y + prefHeight);
                tiles.add(new Tile(x, y, xEnd, yEnd));
            }
        }
        return tiles;
    }

    /**
     *
     * Check if sample is within the view plane
     * @param sample
     * @return true if the sample is within the view plane, false otherwise.
     */
    public boolean isInside(Sample sample) {
        if(sample.getX() < 0 || sample.getX() > horizontalRes + 1)
            return false;
        if(sample.getY() < 0 || sample.getY() > verticalRes + 1)
            return false;
        return true;
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
    public void bufferToImage(String fileName,double sensitivity, double gamma) throws IOException {
        this.buffer.writeBufferToImage(fileName, sensitivity, gamma);
    }

    /**
     *
     * Write depth buffer to png image file.
     *
     * @param fileName Name of the output file.
     * @throws IOException when error occurred during execution.
     */
    public void depthBufferToImage(String fileName) throws IOException {
        this.depthBuffer.setBufferWeight(maxDepth);
        this.depthBuffer.writeBufferToImage(fileName,1, 1);
    }

    /**
     *
     * Write normal buffer to png image file.
     *
     * @param fileName Name of the output file.
     * @throws IOException when error occurred during execution.
     */
    public void normalBufferToImage(String fileName) throws IOException {
        this.normalBuffer.writeBufferToImage(fileName, 1, 1);
    }

    /**
     *
     * Pixels in x direction.
     * @return int
     */
    public int getHorizontalRes() {
        return horizontalRes;
    }

    /**
     *
     * Pixels in y direction.
     * @return int
     */
    public int getVerticalRes() {
        return verticalRes;
    }

    /**
     *
     * pixel width;
     * @return double
     */
    public double getSx() {
        return sx;
    }

    /**
     *
     * pixel height.
     * @return double
     */
    public double getSy() {
        return sy;
    }

    /**
     * View plane distance
     * @return double
     */
    public double getDistance() {
        return d;
    }


    /**
     * Returns the render buffer to a BufferedImage
     * @param sensitivity the image sensitivity (brightness)
     * @param gamma the gamma correction exponent.
     * @return An BufferImage object.
     */
    public BufferedImage toBufferedImage(double sensitivity, double gamma) {
        return this.buffer.toBufferedImage(sensitivity, gamma);
    }

    /**
     * Returns the render buffer to a BufferedImage
     * @return An FrameBuffer object.
     */
    public FrameBuffer getRenderBuffer() {
        return this.buffer;
    }

    /**
     * Clear all buffers.
     */
    public void clear() {
        this.buffer.clear();
        this.depthBuffer.clear();
        this.normalBuffer.clear();
    }
}
