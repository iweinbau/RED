package pathnode;

import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Node for paths disappearing into the background.
 */
public class BackgroundNode extends ScatterNode {

    public BackgroundNode(Vector3D wo, PathNode parent) {
        super(null, null,wo,null,parent);
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample) {
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
    public RGBSpectrum Le() {
        return RGBSpectrum.BLACK;
    }

    @Override
    public double pdf(Vector3D wi) {
        return 1;
    }

    @Override
    public boolean isSurfaceNode() {
        return false;
    }
}
