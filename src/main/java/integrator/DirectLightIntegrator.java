package integrator;

import light.Light;
import math.Point2D;
import math.RGBSpectrum;
import pathnode.EyeNode;
import pathnode.ScatterNode;
import sampler.Sampler;
import scene.Scene;

/**
 *
 * Class for direct light estimation.
 */
public class DirectLightIntegrator extends Integrator {

    /**
     * Construct new DirectLightIntegrator
     */
    public DirectLightIntegrator() {}

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

            L = L.add(scatterNode.Le());

            if (scatterNode.isSurfaceNode()) {
                L = L.add(directLights(scatterNode, scene, sampler));
            } else {
                for (Light light : scene.getLights()) {
                    L = L.add(light.Le(scatterNode.rayFormParent()));
                }
            }
        }
        return L;
    }

    @Override
    public RGBSpectrum computeRadiance(EyeNode eyeNode, Scene scene, Sampler sampler, int depth) {
        return RGBSpectrum.BLACK;
    }
}
