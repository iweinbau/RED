package textures.texturemap;

import core.Constants;
import math.*;
import pathnode.ScatterNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SphericalMap extends TextureMap<Point2D> {

    Transform3D worldToTexture;

    public SphericalMap(Transform2D transform, Transform3D worldToTexture) {
        super(transform);
        this.worldToTexture = worldToTexture;
    }

    @Override
    public Point2D map(ScatterNode scatterNode) {
        Vector3D vec = worldToTexture.globalToLocal(scatterNode.getLocalPoint()).toVector()
                .subtract(new Vector3D(0)).normalize();
        double u = Phi(vec) * 0.5 * Constants.invPI;
        double v = Theta(vec) * Constants.invPI;
        return transform.localToGlobal(new Point2D(u,v));
    }

    double Theta(Vector3D p) {
        return Math.acos(Math.min(1,Math.max(p.getY(),-1)));
    }

    double Phi(Vector3D p) {
        double phi = Math.atan2(p.getZ(),p.getX());
        if(phi < 0)
            phi += 2 * Constants.PI;
        return phi;
    }
}
