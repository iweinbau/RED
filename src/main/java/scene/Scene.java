package scene;

import core.Constants;
import core.HitRecord;
import core.Ray;
import geometry.Geometry;
import light.Light;
import math.RGBSpectrum;

import java.text.Format;
import java.util.Collections;
import java.util.LinkedList;

public class Scene {

    public static final double MAX_SCENE_DIST = Constants.kLarge;

    LinkedList<Geometry> objects = new LinkedList<>();
    LinkedList<Light> lights = new LinkedList<>();

    public Scene() {}

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
                hitRecord.setIntersection(tmpRecord);
            }
            hitRecord.intersectionTests += tmpRecord.intersectionTests;
        }
        return hitRecord;
    }

    public boolean shadowTraceRay(Ray ray) {
        for (Geometry object: objects) {
            HitRecord tmpRecord = new HitRecord();
            if(object.intersect(ray,tmpRecord) && tmpRecord.getDistance() < ray.getMaxDistance() ) {
                if(tmpRecord.getGeometry().getMaterial().isShadowCaster()){
                    return false;
                }
            }
        }
        return true;
    }
}
