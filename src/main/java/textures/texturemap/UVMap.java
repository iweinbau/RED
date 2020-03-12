package textures.texturemap;

import math.Point2D;
import math.Transform2D;
import math.Transform3D;
import pathnode.ScatterNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UVMap extends TextureMap{

    public UVMap(Transform2D transform) {
        super(transform);
    }

    @Override
    public Point2D map(ScatterNode scatterNode) {
        return transform.localToGlobal(scatterNode.getUv());
    }
}
