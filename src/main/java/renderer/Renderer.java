package renderer;

import camera.Camera;
import film.Tile;
import integrator.Integrator;
import math.RGBSpectrum;
import pathnode.EyeNode;
import scene.Scene;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Renderer implements RenderEventInterface {

    Integrator integrator;

    Scene scene;

    Camera camera;

    boolean shouldStop;
    Collection<RenderEventListener> renderEventListeners = new LinkedList<>();

    public Renderer() {

    }

    public void render() {
        if( this.camera == null)
            throw new IllegalStateException("No Camera available!");
        if( this.scene == null)
            throw new IllegalStateException("No Scene available!");
        if (this.integrator == null)
            throw new IllegalStateException("No Integrator available!");

        boolean shouldStop = false;


        final ExecutorService service = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        for (Tile tile: this.camera.getVp().subdivide(64,64)) {

            // create a thread which renders the specific tile
            Thread thread = new Thread( () -> {
                outerloop:
                for (int height = tile.yStart; height < tile.yEnd; height++) {
                    for (int width = tile.xStart; width < tile.xEnd; width++) {

                        if(shouldStop) {
                            break outerloop;
                        }
                        // render pixel height,width
                        EyeNode eye = new EyeNode(this.camera, width, height);

                        // Once a path has been calculated we have to calculate radiance along it.
                        // eye radiance is the final value of the pixel.
                        RGBSpectrum L = integrator.computeRadiance(eye,scene);

                        //TODO: test if L is valid!
                        this.camera.getVp().addColor(height, width, L);

                    }
                }

                for (RenderEventListener listener: renderEventListeners) {
                    listener.finished(tile);
                }

            });

            service.submit(thread);
        }

        // execute the threads
        service.shutdown();

        // wait until the threads have finished
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopRender();
    }

    public void addRenderEventListener(RenderEventListener listener) {
        this.renderEventListeners.add(listener);
    }

    @Override
    public void startRender() {
        this.camera.clear();
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyBufferChange(camera.getRenderBuffer());
        }
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStartRender();
        }
        this.render();
    }

    @Override
    public void stopRender() {
        this.shouldStop = true;
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStopRender();
        }
        try {
            camera.bufferToImage("output.png",1.0,2.2);
            camera.normalBufferToImage("normalBuffer.png");
            camera.depthBufferToImage("depthBuffer.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyBufferChange(camera.getRenderBuffer());
        }
    }

    public void setIntegrator(Integrator integrator) {
        this.integrator = integrator;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
