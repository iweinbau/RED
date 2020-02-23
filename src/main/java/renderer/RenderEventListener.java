package renderer;

import film.FrameBuffer;
import film.Tile;

/**
 *
 * Listener interface for all objects that which to listen to events produced by the renderer
 *
 */
public interface RenderEventListener {
    void finished(Tile tile);
    void notifyBufferChange(FrameBuffer buffer);
    void notifyStartRender();
    void notifyStopRender();

}
