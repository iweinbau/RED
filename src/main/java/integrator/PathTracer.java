package integrator;

import light.Light;
import math.Point2D;
import math.RGBSpectrum;
import pathnode.EyeNode;
import pathnode.ScatterNode;
import sampler.Sampler;
import scene.Scene;

import java.util.LinkedList;
import java.util.Queue;

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
            Queue<ScatterNode> nodeQueue = new LinkedList<>();
            nodeQueue.add(eyeNode.expand(scene, sample));
            while (!nodeQueue.isEmpty()) {
                ScatterNode scatterNode = nodeQueue.poll();

                // Check for surface node
                if (scatterNode.isSurfaceNode()) {

                    if (scatterNode.getDepth() == 1 || scatterNode.isFromSpecularBounce()) {
                        // Add area light contribution.
                        L = L.add(scatterNode.Le());
                    }

                    // Add direct light contributions
                    L = L.add(directLights(scatterNode, scene, sampler));

                    if (scatterNode.getDepth() > MAX_DEPTH)
                        continue;

                    for (int branch = 0; branch < BRANCH_FACTOR; branch++) {
                        // Expand path to new directions
                        ScatterNode nodeToAdd = scatterNode.expand(scene, sampler.sample2D(),BRANCH_FACTOR);

                        if (nodeToAdd == null) {
                            break;
                        }

                        nodeQueue.add(nodeToAdd);
                    }

                } else {
                    if (scatterNode.getDepth() == 1 || scatterNode.isFromSpecularBounce()) {
                        for (Light light : scene.getLights()) {
                            L = L.add(light.Le(scatterNode.rayFormParent())
                                    .multiply(scatterNode.getThroughput()));
                        }
                    }
                }
            }
        }
        return L;
    }

    @Override
    public RGBSpectrum computeRadiance(EyeNode eyeNode, Scene scene, Sampler sampler, int depth) {
        RGBSpectrum L = RGBSpectrum.BLACK;

        sampler.startNewPixel();
        Point2D sample;
        while ( (sample = sampler.nextPixelSample()) != null) {
            Queue<ScatterNode> nodeQueue = new LinkedList<>();
            nodeQueue.add(eyeNode.expand(scene, sample));
            while (!nodeQueue.isEmpty()) {
                ScatterNode scatterNode = nodeQueue.poll();

                // Check for surface node
                if (scatterNode.isSurfaceNode()) {

                    if (scatterNode.getDepth() == depth) {
                        if (scatterNode.getDepth() == 1 || scatterNode.isFromSpecularBounce()) {
                            // Add area light contribution.
                            L = L.add(scatterNode.Le());
                        }

                        // Add direct light contributions
                        L = L.add(directLights(scatterNode, scene, sampler));
                    }

                    if (scatterNode.getDepth() > Math.min(MAX_DEPTH,depth))
                        continue;

                    for (int branch = 0; branch < BRANCH_FACTOR; branch++) {
                        // Expand path to new directions
                        ScatterNode nodeToAdd = scatterNode.expand(scene, sampler.sample2D(),BRANCH_FACTOR);

                        if (nodeToAdd == null) {
                            break;
                        }

                        nodeQueue.add(nodeToAdd);
                    }

                } else {
                    if(scatterNode.getDepth() == depth) {
                        if (scatterNode.getDepth() == 1 || scatterNode.isFromSpecularBounce()) {
                            for (Light light : scene.getLights()) {
                                L = L.add(light.Le(scatterNode.rayFormParent())
                                        .multiply(scatterNode.getThroughput()));
                            }
                        }
                    }
                }
            }
        }
        return L;
    }
}
