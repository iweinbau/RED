package material;

import bxrdf.LambertianReflection;
import math.RGBSpectrum;
import pathnode.ScatterNode;
import textures.Color;
import textures.Constant;
import textures.Texture;

/**
 * Matte materials, which only have lambertian reflection.
 */
public class Matte extends Material {

    /**
     * Base color of material.
     */
    Texture<RGBSpectrum> cd;

    /**
     * Diffuse factor of material.
     */
    Texture<Double> kd;

    /**
     * Construct new Matte material with given color and factor.
     * @param cd
     * @param kd
     */
    public Matte(Texture<RGBSpectrum> cd, Constant kd) {
        this.cd = cd;
        this.kd = kd;
    }

    /**
     * Calculate BRDF at scatter node.
     * @param scatterNode
     */
    @Override
    public void calculateBRDF(ScatterNode scatterNode) {
        scatterNode.setBxRDF(new LambertianReflection(cd.evaluate(scatterNode).scale(kd.evaluate(scatterNode))));
    }
}
