package textures;

import math.RGBSpectrum;
import pathnode.ScatterNode;

public abstract class ScaleTexture<T> extends Texture<T> {
    Texture<Double> texture1;
    Texture<T> texture2;

    public abstract T evaluate(ScatterNode scatterNode);

    //return texture2.evaluate(scatterNode).scale(texture1.evaluate(scatterNode));

}
