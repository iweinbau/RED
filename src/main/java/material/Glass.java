package material;

import bxrdf.SpecularReflection;
import bxrdf.SpecularTransmission;
import bxrdf.fresnel.FresnelDielectric;
import bxrdf.fresnel.NoFresnel;
import math.RGBSpectrum;
import pathnode.ScatterNode;
import textures.Texture;

import java.io.IOException;

public class Glass extends Material {

    private Texture<RGBSpectrum> reflection;
    private Texture<RGBSpectrum> transmission;
    private Texture<Double> IOR;

    public Glass(Texture<RGBSpectrum> reflection, Texture<RGBSpectrum> transmission, Texture<Double> IOR) {
        this.reflection = reflection;
        this.transmission = transmission;
        this.IOR = IOR;
    }

    @Override
    public void calculateBRDF(ScatterNode scatterNode) {
        double etaI = IOR.evaluate(scatterNode);
        scatterNode.setBxRDF(
                new SpecularReflection(reflection.evaluate(scatterNode),
                        new FresnelDielectric(1.0, etaI)));
        scatterNode.setBxRDF(
                new SpecularTransmission(transmission.evaluate(scatterNode),1.0,etaI));
    }
}
