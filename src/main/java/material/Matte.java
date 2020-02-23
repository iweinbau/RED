package material;

import bxrdf.LambertianReflection;
import math.RGBSpectrum;
import pathnode.ScatterNode;
import textures.Color;
import textures.Constant;
import textures.Texture;

public class Matte extends Material {

    Texture<RGBSpectrum> cd;
    Texture<Double> kd;

    public Matte(Color cd, Constant kd) {
        this.cd = cd;
        this.kd = kd;
    }

    @Override
    public void calculateBRDF(ScatterNode scatterNode) {
        scatterNode.setBxRDF(new LambertianReflection(cd.evaluate().scale(kd.evaluate())));
    }
}
