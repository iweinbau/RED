package textures.texturemap;

import math.Point2D;
import math.Transform2D;
import math.Transform3D;
import math.Vector3D;
import pathnode.ScatterNode;

public class PlanarMap extends TextureMap<Point2D> {

    private  static Vector3D u = new Vector3D(1,0,0);
    private  static Vector3D v = new Vector3D(0,1,0);

    Transform3D worldToTexture;

    public PlanarMap(Transform2D transform, Transform3D worldToTexture) {
        super(transform);
        this.worldToTexture = worldToTexture;
    }

    @Override
    public Point2D map(ScatterNode scatterNode) {
        Vector3D vec = worldToTexture.globalToLocal(scatterNode.getLocalPoint().toVector());
        return transform.localToGlobal(new Point2D( vec.dot(u), vec.dot(v)));
    }
}
