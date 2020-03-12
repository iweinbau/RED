package textures;

import math.RGBSpectrum;
import pathnode.ScatterNode;

public class MixTexture extends Texture<RGBSpectrum> {
    Texture<RGBSpectrum> texture1;
    Texture<RGBSpectrum> texture2;
    Texture<Double> amount;

    @Override
    public RGBSpectrum evaluate(ScatterNode scatterNode) {
        double a = Math.min(0,Math.max(amount.evaluate(scatterNode),1));
        return texture1.evaluate(scatterNode).scale(1-a).add(texture2.evaluate(scatterNode).scale(a));
    }
}
