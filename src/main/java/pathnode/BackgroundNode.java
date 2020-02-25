package pathnode;

import math.RGBSpectrum;
import math.Vector3D;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Node for paths disappearing into the background.
 */
public class BackgroundNode extends ScatterNode {

    public BackgroundNode(Vector3D wo, PathNode parent) {
        super(null,wo,null,parent);
    }

    @Override
    public ScatterNode expand(Scene scene) {
        throw new NotImplementedException();
    }

    @Override
    public ScatterNode trace(Scene scene, Vector3D direction) {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum scatter(Vector3D direction) {
        throw new NotImplementedException();
    }

    @Override
    public boolean isSurfaceNode() {
        return false;
    }
}
