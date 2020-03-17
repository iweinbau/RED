package textures.texturemap;

import math.Point2D;
import math.Transform2D;
import pathnode.ScatterNode;

public class UVMap extends TextureMap<Point2D> {

    public UVMap(Transform2D transform) {
        super(transform);
    }

    @Override
    public Point2D map(ScatterNode scatterNode) {
        return transform.localToGlobal(scatterNode.getUv());
    }
}
