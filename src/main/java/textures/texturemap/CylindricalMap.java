package textures.texturemap;

import core.Constants;
import math.Point2D;
import math.Transform2D;
import math.Transform3D;
import math.Vector3D;
import pathnode.ScatterNode;

public class CylindricalMap extends TextureMap<Point2D> {

    Transform3D worldToTexture;

    public CylindricalMap(Transform2D transform, Transform3D worldToTexture) {
        super(transform);
        this.worldToTexture = worldToTexture;
    }

    @Override
    public Point2D map(ScatterNode scatterNode) {
        Vector3D vec = worldToTexture.globalToLocal(scatterNode.getLocalPoint()).toVector()
                .subtract(new Vector3D(0));
        return transform.localToGlobal(new Point2D(
                 Phi(vec) * 0.5 * Constants.invPI,
                vec.getY()));
    }

    double Phi(Vector3D p) {
        double phi = Math.atan2(p.getZ(),p.getX());
        if(phi < 0)
            phi += 2 * Constants.PI;
        return phi;
    }
}
