package textures;

import pathnode.ScatterNode;

public abstract class Texture<T> {
    public abstract T evaluate(ScatterNode scatterNode);
}