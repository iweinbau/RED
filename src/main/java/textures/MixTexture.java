package textures;

import math.RGBSpectrum;
import math.Triple;
import pathnode.ScatterNode;

public abstract class MixTexture<T> extends Texture<T> {
    Texture<T> texture1;
    Texture<T> texture2;
    Texture<Double> amount;

    public abstract T evaluate(ScatterNode scatterNode);
//        double a = Math.min(0,Math.max(amount.evaluate(scatterNode),1));
//        return texture1.evaluate(scatterNode).scale(1-a).add(texture2.evaluate(scatterNode).scale(a));
}
