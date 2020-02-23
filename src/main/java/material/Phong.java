package material;

import math.RGBSpectrum;
import pathnode.ScatterNode;
import textures.Texture;

public class Phong extends Material {

    Texture<RGBSpectrum> cd;
    Texture<Double> kd;
    Texture<RGBSpectrum> cs;
    Texture<Double> ks;
    Texture<Double> roughness;

    PhongModel phongModel = PhongModel.PHONG;

    public Phong(Texture<RGBSpectrum> cd, Texture<Double> kd,
                 Texture<RGBSpectrum> cs, Texture<Double> ks,
                 Texture<Double> roughness,PhongModel model) {
        this.cd = cd;
        this.kd = kd;
        this.cs = cs;
        this.ks = ks;
        this.roughness = roughness;
        this.phongModel = model;
    }

    public Phong(Texture<RGBSpectrum> cd, Texture<Double> kd,
                 Texture<RGBSpectrum> cs, Texture<Double> ks,
                 Texture<Double> roughness) {
        this.cd = cd;
        this.kd = kd;
        this.cs = cs;
        this.ks = ks;
        this.roughness = roughness;
    }

    @Override
    public void calculateBRDF(ScatterNode scatterNode) {
        // TODO:
    }

    public enum PhongModel{
        BLINN_PHONG,
        PHONG
    }
}
