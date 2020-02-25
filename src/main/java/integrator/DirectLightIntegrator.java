package integrator;

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
     * @param sampler a sampler class to get samples from
     */
    public DirectLightIntegrator(Sampler sampler) {
        super(sampler);
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
    public RGBSpectrum computeRadiance(EyeNode eyeNode, Scene scene) {
        RGBSpectrum L = RGBSpectrum.BLACK;
        ScatterNode scatterNode = eyeNode.expand(scene);

        //TODO: add own light contribution eg. the source term
        if(scatterNode.isSurfaceNode()) {
            L = L.add(directLights(scatterNode,scene));
        }
        return L;
    }

}
