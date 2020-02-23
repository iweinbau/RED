import core.HitRecord;
import core.Ray;
import geometry.Box;
import geometry.Sphere;
import math.Point3D;
import math.Transform;
import math.Vector3D;
import org.junit.Assert;
import org.junit.Test;

public class GeometryTest {
    @Test
    public void SphereTest() {
        Sphere s = new Sphere(new Transform());

        Assert.assertTrue(s.intersect(new Ray(new Point3D(0,0,3),new Vector3D(0,0,-1)),
                new HitRecord()));

        Assert.assertTrue(s.intersect(new Ray(new Point3D(0f,0,0),
                new Vector3D(3,0,-1)),new HitRecord()));

        Assert.assertFalse(s.intersect(new Ray(new Point3D(0,0,3),
                new Vector3D(3,0,-1)),new HitRecord()));

        Transform t = new Transform();
        t.scale(4);
        s = new Sphere(new Transform());
        Assert.assertTrue(s.intersect(new Ray(new Point3D(0,0,3),
                new Vector3D(0,0,-1)),new HitRecord()));

        Assert.assertTrue(s.intersect(new Ray(new Point3D(0.2,0,0),
                new Vector3D(3,0,1)),new HitRecord()));

        Assert.assertFalse(s.intersect(new Ray(new Point3D(0,0,3),
                new Vector3D(3,0,-1)),new HitRecord()));
    }

    @Test
    public void BoxTest(){
        Box b = new Box(new Transform());

        Assert.assertTrue(b.intersect(new Ray(new Point3D(0,0,3),new Vector3D(0,0,-1)),
                new HitRecord()));

        Assert.assertTrue(b.intersect(new Ray(new Point3D(0f,0,0),
                new Vector3D(3,0,-1)),new HitRecord()));

        Assert.assertFalse(b.intersect(new Ray(new Point3D(0,0,3),
                new Vector3D(3,0,-1)),new HitRecord()));

        Transform t = new Transform();
        t.scale(4);
        b = new Box(new Transform());
        Assert.assertTrue(b.intersect(new Ray(new Point3D(0,0,3),
                new Vector3D(0,0,-1)),new HitRecord()));

        Assert.assertTrue(b.intersect(new Ray(new Point3D(0.2,0,0),
                new Vector3D(3,0,1)),new HitRecord()));

        Assert.assertFalse(b.intersect(new Ray(new Point3D(0,0,3),
                new Vector3D(3,0,-1)),new HitRecord()));
    }
}
