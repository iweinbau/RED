package renderer;

import camera.Camera;
import film.FrameBuffer;
import film.Tile;
import integrator.Integrator;
import math.Point2D;
import math.RGBSpectrum;
import pathnode.EyeNode;
import sampler.Random;
import sampler.Sampler;
import sampler.Stratified;
import sampler.Uniform;
import scene.Scene;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Renderer implements RenderEventInterface {

    Integrator integrator;

    Scene scene;

    Camera camera;

    boolean shouldStop;

    Collection<RenderEventListener> renderEventListeners = new LinkedList<>();

    int samplesPerPixel;

    public Renderer(int samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    public void setSamplesPerPixel(int samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    public void render() {
        if( this.camera == null)
            throw new IllegalStateException("No Camera available!");
        if( this.scene == null)
            throw new IllegalStateException("No Scene available!");
        if (this.integrator == null)
            throw new IllegalStateException("No Integrator available!");

        shouldStop = false;


        final ExecutorService service = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        for (Tile tile: this.camera.getVp().subdivide(64,64)) {

            // create a thread which renders the specific tile
            Thread thread = new Thread( () -> {
                Sampler sampler = new Stratified(samplesPerPixel);
                outerLoop:
                for (int height = tile.yStart, i = 0; height < tile.yEnd; height++, i++) {
                    for (int width = tile.xStart,j =0; width < tile.xEnd; width++, j++) {
                            if (shouldStop) {
                                break outerLoop;
                            }

                            // render pixel height,width
                            EyeNode eye = new EyeNode(this.camera, width, height);

                            // Once a path has been calculated we have to calculate radiance along it.
                            // eye radiance is the final value of the pixel.
                            RGBSpectrum L = integrator.computeRadiance(eye, scene,
                                    sampler);

                            //TODO: test if L is valid!
                            this.camera.getVp().addColor(height, width, L,samplesPerPixel);
                    }
                }

                for (RenderEventListener listener: renderEventListeners) {
                    listener.finished(tile);
                }

            });

            //TODO: fix exception for multithreading
            Future future = service.submit(thread);
//            try {
//                future.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
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

    public FrameBuffer render(Tile tile) {
        FrameBuffer frameBuffer = new FrameBuffer(tile.getWidth(),tile.getHeight());

        final ExecutorService service = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        for (Tile t: tile.subdivide(64,64)) {

            // create a thread which renders the specific tile
            Thread thread = new Thread( () -> {
                Sampler sampler = new Stratified(samplesPerPixel);
                outerLoop:
                for (int height = t.yStart, i = 0; height < t.yEnd; height++, i++) {
                    for (int width = t.xStart,j =0; width < t.xEnd; width++, j++) {
                        if (shouldStop) {
                            break outerLoop;
                        }

                        // render pixel height,width
                        EyeNode eye = new EyeNode(this.camera, width, height);

                        // Once a path has been calculated we have to calculate radiance along it.
                        // eye radiance is the final value of the pixel.
                        RGBSpectrum L = integrator.computeRadiance(eye, scene,
                                sampler);

                        frameBuffer.addPixel(i, j, L, samplesPerPixel);
                    }
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

        return frameBuffer;
    }

    public void addRenderEventListener(RenderEventListener listener) {
        this.renderEventListeners.add(listener);
    }

    @Override
    public void startRender() {
        clearBuffers();
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStartRender();
        }

        this.render();
    }

    @Override
    public void clearBuffers() {
        this.camera.clear();
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyBufferChange(camera.getRenderBuffer());
        }
    }

    @Override
    public void stopRender() {
        shouldStop = true;
        try {
            camera.bufferToImage("output.png",1.0,2.2);
            camera.normalBufferToImage("normalBuffer.png");
            camera.depthBufferToImage("depthBuffer.png");
            camera.intersectionBufferToImage("intersection.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStopRender();
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
