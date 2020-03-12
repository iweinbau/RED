package textures;

import math.RGBSpectrum;
import pathnode.ScatterNode;

public class ScaleTexture extends Texture<RGBSpectrum> {
    Texture<Double> texture1;
    Texture<RGBSpectrum> texture2;

    @Override
    public RGBSpectrum evaluate(ScatterNode scatterNode) {
        return texture2.evaluate(scatterNode).scale(texture1.evaluate(scatterNode));
    }
}
