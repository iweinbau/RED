package textures;

import math.RGBSpectrum;
import pathnode.ScatterNode;

public class Color extends Texture<RGBSpectrum> {

    RGBSpectrum color;

    public Color(RGBSpectrum color) {
        this.color = color;
    }

    @Override
    public RGBSpectrum evaluate(ScatterNode scatterNode) {
        return color;
    }
}
