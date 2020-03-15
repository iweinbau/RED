package textures;

import pathnode.ScatterNode;

public abstract class ImageTexture<T> extends Texture<T> {

    @Override
    public T evaluate(ScatterNode scatterNode) {
        return null;
    }
}
