package textures;

import math.RGBSpectrum;
import parser.Image;
import pathnode.ScatterNode;
import textures.texturemap.TextureMap;

public class RGBTexture extends ImageTexture<RGBSpectrum> {

    public RGBTexture(TextureMap mapping, Image image) {
        super(mapping, image);
    }

    @Override
    public RGBSpectrum evaluate(ScatterNode scatterNode) {
        return image.lookUp(mapping.map(scatterNode));
    }
}
