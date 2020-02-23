package textures;

import math.Point2D;

public abstract class Texture<T> {
    public abstract T evaluate();
    public abstract T evaluate(Point2D uv);
}