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
    private final int BRANCH_FACTOR;

    public PathTracer(int maxDepth, int branchFactor) {
        this.MAX_DEPTH = maxDepth;
        this.BRANCH_FACTOR = branchFactor;
    }

    public PathTracer() {
        this.MAX_DEPTH = 12;
        this.BRANCH_FACTOR = 1;
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

            for (int depth = 0; depth < MAX_DEPTH; depth++) {

                // Check for surface node
                if (scatterNode.isSurfaceNode()) {

                    if (depth == 0 || scatterNode.isSpecularBounce()) {
                        // Add area light contribution.
                        L = L.add(scatterNode.Le());
                    }

                    // Add direct light contributions
                    L = L.add(directLights(scatterNode, scene, sampler));

                    // Expand path to new direction
                    scatterNode = scatterNode.expand(scene, sampler.sample2D());

                    if (scatterNode == null) {
                        break;
                    }

                } else {
                    if (depth == 0 || scatterNode.isSpecularBounce()) {
                        for (Light light : scene.getLights()) {
                            L = L.add(light.Le(scatterNode.rayFormParent()).multiply(scatterNode.getThroughput()));
                        }
                    }
                    break;
                }
            }
        }
        return L;
    }
}
