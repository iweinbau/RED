package textures.texturemap;

import math.Point2D;
import math.Transform2D;
import pathnode.ScatterNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class PlanarMap extends TextureMap {
    public PlanarMap(Transform2D transform) {
        super(transform);
    }

    @Override
    public Point2D map(ScatterNode scatterNode) {
        throw new NotImplementedException();
    }
}
