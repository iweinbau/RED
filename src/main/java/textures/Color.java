package textures;

import math.Point2D;
import math.RGBSpectrum;

public class Color extends Texture<RGBSpectrum> {

    RGBSpectrum color;

    public Color(RGBSpectrum color) {
        this.color = color;
    }

    @Override
    public RGBSpectrum evaluate() {
        return color;
    }

    @Override
    public RGBSpectrum evaluate(Point2D uv) {
        return color;
    }
}
