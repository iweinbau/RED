package distributeRenderer;

import film.Pixel;
import java.io.Serializable;

public class RenderResponse implements Serializable {

    /**
     * The horizontal starting coordinate of this tile (inclusive).
     */
    public final int xStart;

    /**
     * The vertical starting coordinate of this tile (inclusive).
     */
    public final int yStart;

    /**
     * The horizontal end coordinate of this tile (exclusive).
     */
    public final int xEnd;

    /**
     * The vertical end coordinate of this tile (exclusive).
     */
    public final int yEnd;

    /**
     *
     * List of all pixel that a slave has rendered.
     *
     */
    Pixel[] frameBuffer;

    public RenderResponse(int xStart, int yStart, int xEnd, int yEnd,Pixel[] frameBuffer)
            throws IllegalArgumentException {
        if (xStart > xEnd)
            throw new IllegalArgumentException("the minimum x coordinate is "
                    + "larger than the maximum x coordinate!");
        if (yStart > yEnd)
            throw new IllegalArgumentException("the minimum y coordinate is "
                    + "larger than the maximum y coordinate!");
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xEnd;
        this.yEnd = yEnd;

        this.frameBuffer = frameBuffer;

    }
}
