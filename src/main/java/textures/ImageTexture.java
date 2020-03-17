package textures;

import pathnode.ScatterNode;

public abstract class ImageTexture<T> extends Texture<T> {

    @Override
    public abstract T evaluate(ScatterNode scatterNode);

}
