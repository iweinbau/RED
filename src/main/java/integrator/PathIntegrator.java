package integrator;

import light.Light;
import math.Point2D;
import math.RGBSpectrum;
import pathnode.EyeNode;
import pathnode.ScatterNode;
import sampler.Sampler;
import scene.Scene;

public class PathIntegrator extends Integrator {

    private final int MAX_DEPTH;

    private final int BRANCH_FACTOR;

    public PathIntegrator(int maxDepth, int branchFactor) {
        this.MAX_DEPTH = maxDepth;
        this.BRANCH_FACTOR = branchFactor;
    }


    public PathIntegrator() {
        MAX_DEPTH = 14;
        BRANCH_FACTOR = 1;
    }

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

                    // Add area light contribution.
                    L = L.add(scatterNode.Le());

                    // Expand path to new direction
                    scatterNode = scatterNode.expand(scene,sampler.sample2D());
                    if(scatterNode == null) {
                        break;
                    }
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
