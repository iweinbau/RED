import geometry.BVH;
import geometry.Plane;
import geometry.Sphere;
import material.Matte;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import org.junit.Assert;
import org.junit.Test;
import textures.Color;
import textures.Constant;

public class BVHTest {

    @Test
    public void BVHTest() {
        BVH bvh = new BVH(new Transform());
        Transform objT = new Transform();
        objT.translate(new Point3D(1,0,0));
        Sphere s = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
        bvh.addGeometry(s);
        objT = new Transform();
        objT.translate(new Point3D(-1,0,0));
        s = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1)));
        bvh.addGeometry(s);
        objT = new Transform();
        objT.translate(new Point3D(2,0,0));
        s = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1)));
        bvh.addGeometry(s);

        bvh.buildAccelerationStructure();
    }
}
