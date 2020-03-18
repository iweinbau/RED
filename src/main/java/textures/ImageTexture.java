package textures;

import math.Point2D;
import parser.Image;
import pathnode.ScatterNode;
import textures.texturemap.TextureMap;

public abstract class ImageTexture<T> extends Texture<T> {

    TextureMap<Point2D> mapping;
    Image image;

    public ImageTexture(TextureMap mapping,Image image) {
        this.mapping = mapping;
        this.image = image;
    }

    @Override
    public abstract T evaluate(ScatterNode scatterNode);

}
