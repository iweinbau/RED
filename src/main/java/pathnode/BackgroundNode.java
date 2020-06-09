package pathnode;

import bxrdf.BxrdfType;
import core.Ray;
import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.List;

/**
 * Node for paths disappearing into the background.
 */
public class BackgroundNode extends ScatterNode {

    public BackgroundNode(Vector3D wo, PathNode parent) {
        super(null, null, null,wo,null,parent);
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample, double branch) {
        throw new NotImplementedException();
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample) {
        throw new NotImplementedException();
    }

    @Override
    public ScatterNode trace(Scene scene, Ray ray) {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum scatterLight(Vector3D direction) {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum Le() {
        return RGBSpectrum.BLACK;
    }

    @Override
    public boolean isSurfaceNode() {
        return false;
    }

    @Override
    public RGBSpectrum scatter_f(Vector3D wi) {
        throw new NotImplementedException();
    }

}
