package integrator;

import light.Light;
import math.RGBSpectrum;
import math.Vector3D;
import pathnode.EyeNode;
import pathnode.ScatterNode;
import sampler.Sampler;
import scene.Scene;

/**
 * Class for making an approximation of the render equation
 */
public abstract class Integrator {

    /**
     *
     * Construct new Integrator class.
     */
    public Integrator() {}

    /**
     *
     * @param eyeNode starting node in path.
     * @param scene scene to trace rays in.
     * @return RGBSpectrum computed radiance.
     */
    public abstract RGBSpectrum computeRadiance(EyeNode eyeNode, Scene scene, Sampler sampler);


    // TODO: add other way of sampling lights eg. sampleOneLight or sampleAllLights
    // By first picking a light using some distribution and after this sampling from the picked light.
    // the sample_pdf of of this will be P(k) * P(p|k) where k represent the light and p the point on light k.
    public RGBSpectrum directLights(ScatterNode scatterNode,Scene scene, Sampler sampler) {
        RGBSpectrum L = RGBSpectrum.BLACK;

        for (Light light:scene.getLights()) {
            Vector3D wi = light.sample_wi(scatterNode,sampler.sample2D());

            if(!light.getVisibility(scatterNode.getPosition()).isVisible(scene))
                continue;
            else {
                L = L.add(
                        scatterNode.scatterLight(wi)
                        .multiply(light.scatter(wi.neg()))
                        .scale(1./light.distanceFactor(scatterNode.getPosition())));
            }
        }
        return L;
    }

}
