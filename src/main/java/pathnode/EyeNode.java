package pathnode;

import camera.Camera;
import core.HitRecord;
import core.Ray;
import core.Sample;
import math.Point2D;
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
        super(camera.getPosition(), camera.getPosition(),null,null);
        this.camera = camera;
        this.width = width;
        this.height = height;
    }

    public Sample sample(Point2D sample) {
        return new Sample(width + sample.getX(), height + sample.getY());
    }

    @Override
    public ScatterNode expand(Scene scene, Point2D sample) {
        this.successor = trace(scene,camera.viewDirection(sample(sample)));
        this.successor.throughput = new RGBSpectrum(1);
        return this.successor;
    }

    @Override
    public ScatterNode trace(Scene scene, Vector3D direction) {
        Ray ray = new Ray(camera.getPosition(),direction);
        HitRecord hitRecord = scene.traceRay(ray);
        ScatterNode scatterNode = hitRecord.toScatterNode(this);
        camera.getVp().addNormal(height, width,scatterNode.normal);
        camera.getVp().addIntersection(height,width,hitRecord.intersectionTests);
        camera.getVp().addDepth(height, width,this.distanceTo(scatterNode));
        return scatterNode;
    }

    @Override
    public RGBSpectrum scatter(Vector3D direction) {
        throw new NotImplementedException();
    }
}
