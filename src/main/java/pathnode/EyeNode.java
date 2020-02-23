package pathnode;

import camera.Camera;
import core.Ray;
import core.Sample;
import math.RGBSpectrum;
import math.Vector3D;
import scene.Scene;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * EyeNode represent the first node in a path.
 * This is actual the node at the camera position.
 *
 */

public class EyeNode extends PathNode {

    Camera camera;
    // Pixel position of the eyeNode;
    int width; int height;

    public EyeNode(Camera camera,int width, int height) {
        super(camera.getPosition(),null,null);
        this.camera = camera;
        this.width = width;
        this.height = height;
    }

    public Sample sample() {
        return new Sample(width +0.5, height +0.5);
    }

    @Override
    public ScatterNode expand(Scene scene) {
        this.successor = trace(scene,camera.viewDirection(sample()));
        this.successor.throughput = new RGBSpectrum(1);
        return this.successor;
    }

    @Override
    public ScatterNode trace(Scene scene, Vector3D direction) {
        Ray ray = new Ray(camera.getPosition(),direction);
        ScatterNode scatterNode = scene.traceRay(ray).toScatterNode(this);
        camera.getVp().addNormal(height, width,scatterNode.normal);
        camera.getVp().addDepth(height, width,this.distanceTo(scatterNode));
        return scatterNode;
    }

    @Override
    public RGBSpectrum scatter(Vector3D direction) {
        throw new NotImplementedException();
    }
}
