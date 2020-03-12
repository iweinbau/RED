package textures.texturemap;

import math.Point2D;
import math.Transform2D;
import pathnode.ScatterNode;

public abstract class TextureMap {
    Transform2D transform;
    public TextureMap(Transform2D transform) {
        this.transform = transform;
    }
    public abstract Point2D map(ScatterNode scatterNode);
}
