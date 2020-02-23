package scene;

import core.HitRecord;
import core.Ray;
import geometry.Geometry;
import light.Light;
import math.RGBSpectrum;

import java.util.Collections;
import java.util.LinkedList;

public class Scene {
    public static final double MAX_SCENE_DIST = Double.POSITIVE_INFINITY;

    LinkedList<Geometry> objects = new LinkedList<>();
    LinkedList<Light> lights = new LinkedList<>();

    public Scene() {
    }

    public void addGeometry(Geometry... objects) {

        Collections.addAll(this.objects, objects);
    }
    public void addLight(Light... lights) {

        Collections.addAll(this.lights,lights);
    }

    public LinkedList<Geometry> getObjects() {
        return objects;
    }

    public LinkedList<Light> getLights() {
        return lights;
    }

    public HitRecord traceRay(Ray ray) {
        HitRecord hitRecord = new HitRecord();
        for (Geometry object: objects) {
            HitRecord tmpRecord = new HitRecord();
            if(object.intersect(ray,tmpRecord) &&  tmpRecord.getDistance() < ray.getMaxDistance() ){
                ray.setMaxDistance(tmpRecord.getDistance());
                hitRecord = tmpRecord;
            }
        }
        return hitRecord;
    }

    public boolean shadowTraceRay(Ray ray) {
        for (Geometry object: objects) {
            HitRecord tmpRecord = new HitRecord();
            if(object.intersect(ray,tmpRecord) && tmpRecord.getDistance() < ray.getMaxDistance() ){
                return false;
            }
        }
        return true;
    }

    public RGBSpectrum environmentLight(Ray ray) {
        RGBSpectrum L = RGBSpectrum.BLACK;
        for (Light light: lights) {
            L = L.add(light.Le(ray));
        }
        return L;
    }
}
