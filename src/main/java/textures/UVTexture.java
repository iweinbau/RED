package textures;

import math.Point2D;
import math.RGBSpectrum;
import pathnode.ScatterNode;
import textures.texturemap.TextureMap;

public class UVTexture extends Texture<RGBSpectrum> {

    TextureMap mapping;

    public UVTexture(TextureMap mapping) {
        this.mapping = mapping;
    }

    @Override
    public RGBSpectrum evaluate(ScatterNode scatterNode) {
        Point2D t = mapping.map(scatterNode);
        return new RGBSpectrum(t.getX()-Math.floor(t.getX()),t.getY()-Math.floor(t.getY()),0);
    }
}
