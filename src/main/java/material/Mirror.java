package material;

import bxrdf.SpecularReflection;
import bxrdf.fresnel.NoFresnel;
import math.RGBSpectrum;
import pathnode.ScatterNode;
import textures.Texture;

public class Mirror extends Material {

    private Texture<RGBSpectrum> ks;

    public Mirror(Texture<RGBSpectrum> ks) {
        this.ks = ks;
    }

    @Override
    public void calculateBRDF(ScatterNode scatterNode) {
        scatterNode.setBxRDF(new SpecularReflection(ks.evaluate(scatterNode),new NoFresnel()));
    }
}
