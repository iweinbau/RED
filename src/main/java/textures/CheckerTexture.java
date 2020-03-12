package textures;

import math.Point2D;
import math.RGBSpectrum;
import math.Transform2D;
import pathnode.ScatterNode;
import textures.texturemap.TextureMap;

public class CheckerTexture extends Texture<RGBSpectrum> {

    Texture<RGBSpectrum> texture1;
    Texture<RGBSpectrum> texture2;

    TextureMap mapping;

    public CheckerTexture(TextureMap mapping, Texture<RGBSpectrum> texture1, Texture<RGBSpectrum> texture2) {
        this.texture1 = texture1;
        this.texture2 = texture2;
        this.mapping = mapping;
    }

    @Override
    public RGBSpectrum evaluate(ScatterNode scatterNode) {
        Point2D t = mapping.map(scatterNode);
        if ( (int)(Math.floor(t.getX()) + Math.floor(t.getY())) % 2 == 0)
            return texture1.evaluate(scatterNode);
        else
            return texture2.evaluate(scatterNode);
    }
}
