package integrator;

import core.Sample;
import light.Light;
import math.Point2D;
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
    // the pdf of of this will be P(k) * P(p|k) where k represent the light and p the point on light k.
    public RGBSpectrum directLights(ScatterNode scatterNode,Scene scene, Sampler sampler) {
        RGBSpectrum L = RGBSpectrum.BLACK;

        for (Light light:scene.getLights()) {
            Vector3D wi = light.sample_wi(scatterNode,sampler.sample2D());

            if(!light.getVisibility(scatterNode.getPosition()).isVisible(scene))
                continue;
            else {
                //TODO: take number of samples into account
                //TODO: sample on area light surface.
                //TODO: extract pdf from the scatter call for both scatter and light
                L = L.add(
                        scatterNode.scatter(wi)
                        .multiply(light.scatter(wi.neg()))
                        .scale(1./(scatterNode.pdf(wi) * light.Li_pdf() * light.distanceFactor(scatterNode.getPosition()))));
            }
        }
        return L;
    }

}
