package integrator;

import light.Light;
import math.Point2D;
import math.RGBSpectrum;
import pathnode.EyeNode;
import pathnode.ScatterNode;
import sampler.Sampler;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

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
            Queue<ScatterNode> nodesToExpand = new LinkedList<>();
            nodesToExpand.add(eyeNode.expand(scene, sample));
            while (!nodesToExpand.isEmpty()) {

                ScatterNode scatterNode = nodesToExpand.poll();

                // Check for surface node
                if (scatterNode.isSurfaceNode()) {

                    // Add area light contribution.
                    L = L.add(scatterNode.Le());

                    // Expand path to new direction
                    if(scatterNode.getDepth() < MAX_DEPTH) {
                        for (int i = 0; i < BRANCH_FACTOR; i++) {
                            ScatterNode next = scatterNode.expand(scene, sampler.sample2D());
                            if (next == null) {
                                break;
                            }
                            nodesToExpand.add(next);
                        }
                    }
                }else {
                    for (Light light : scene.getLights()) {
                        L = L.add(light.Le(scatterNode.rayFormParent()).multiply(scatterNode.getThroughput()));
                    }
                }
            }
        }
        return L;
    }
}
