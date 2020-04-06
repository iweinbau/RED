package integrator;

import light.Light;
import math.Point2D;
import math.RGBSpectrum;
import pathnode.EyeNode;
import pathnode.ScatterNode;
import sampler.Sampler;
import scene.Scene;

public class PathTracer extends Integrator {

    private final int MAX_DEPTH;

    public PathTracer(int maxDepth) {
        this.MAX_DEPTH = maxDepth;
    }

    public PathTracer() {
        this.MAX_DEPTH = 12;
    }

    /**
     *
     *
     * Compute radiance along path starting from an eyeNode in a given scene.
     * @param eyeNode starting node in path.
     * @param scene scene to trace rays in.
     * @return RGBSpectrum computed radiance.
     */
    @Override
    public RGBSpectrum computeRadiance(EyeNode eyeNode, Scene scene, Sampler sampler) {
        RGBSpectrum L = RGBSpectrum.BLACK;

        sampler.startNewPixel();
        Point2D sample;
        while ( (sample = sampler.nextPixelSample()) != null) {
            ScatterNode scatterNode = eyeNode.expand(scene, sample);
            int depth = 0;
            while (depth++ < MAX_DEPTH) {
                // Add area light contribution.
                L = L.add(scatterNode.Le());

                // Check for surface node
                if (scatterNode.isSurfaceNode()) {
                    // Add direct light contribution
                    L = L.add(directLights(scatterNode, scene, sampler));
                    // Expand path to new direction
                    scatterNode = scatterNode.expand(scene, sampler.sample2D());

                    // If next path node is null stop tracing.
                    if(scatterNode == null)
                        break;
                }else {
                    for (Light light : scene.getLights()) {
                        L = L.add(light.Le(scatterNode.rayFormParent()).multiply(scatterNode.getThroughput()));
                    }
                    break;
                }
            }
        }
        return L;
    }
}
