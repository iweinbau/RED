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
            Queue<ScatterNode> nodeQueue = new LinkedList<>();
            nodeQueue.add(eyeNode.expand(scene, sample));
            while (!nodeQueue.isEmpty()) {
                ScatterNode scatterNode = nodeQueue.poll();

                // Check for surface node
                if (scatterNode.isSurfaceNode()) {

                    // Add area light contribution.
                    L = L.add(scatterNode.Le());

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
                    for (Light light : scene.getLights()) {
                        L = L.add(light.Le(scatterNode.rayFormParent()).multiply(scatterNode.getThroughput()));
                    }
                }
            }
        }
        return L;
    }

    @Override
    public RGBSpectrum computeRadiance(EyeNode eyeNode, Scene scene, Sampler sampler,int depth) {
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

                    // Add area light contribution.
                    if( scatterNode.getDepth() == depth)
                        L = L.add(scatterNode.Le());

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
                    if( scatterNode.getDepth() == depth) {
                        for (Light light : scene.getLights()) {
                            L = L.add(light.Le(scatterNode.rayFormParent()).multiply(scatterNode.getThroughput()));
                        }
                    }
                }
            }
        }
        return L;
    }

}
