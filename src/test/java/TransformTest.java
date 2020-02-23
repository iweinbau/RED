import math.Point3D;
import math.Transform;
import math.Vector3D;
import org.junit.Test;
import org.junit.Assert;


public class TransformTest {

    @Test
    public void TranslateTest() {
        Transform transform = new Transform();
        // Translate to 0,2,0
        transform.translate(new Point3D(0,2,0));

        // Test if 0,0,0 in local space results in 0,2,0 in global space.
        Point3D global  = transform.localToGlobal(new Point3D(0));
        Assert.assertEquals(new Point3D(0,2,0),global);

        // Test if 0,2,0 in global space results in 0,0,0 local space.
        Point3D local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(0,0,0),local);

        transform.translate(new Point3D(1,0,0));
        global  = transform.localToGlobal(new Point3D(0));
        Assert.assertEquals(new Point3D(1,2,0),global);

        local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(0,0,0),local);

        transform.translate(new Point3D(-1,-2,0));
        global  = transform.localToGlobal(new Point3D(0));
        Assert.assertEquals(new Point3D(0,0,0),global);

    }

    @Test
    public void ScaleTest() {
        Transform transform = new Transform();

        //scale factor 2
        transform.scale(2);

        // Test if 0,0,0 in local space results in 0,2,0 in global space.
        Point3D global  = transform.localToGlobal(new Point3D(1));
        Assert.assertEquals(new Point3D(2,2,2),global);

        // Test if 0,2,0 in global space results in 0,0,0 local space.
        Point3D local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(1,1,1),local);

    }

    @Test
    public void RotateTest() {
        Transform transform = new Transform();
        transform.rotateX(90);

        Point3D global  = transform.localToGlobal(new Point3D(0,1,0));
        Assert.assertEquals(new Point3D(0,0,1),global);

        Point3D local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(0,1,0),local);

        transform = new Transform();
        transform.rotateY(90);

        global  = transform.localToGlobal(new Point3D(1,0,0));
        Assert.assertEquals(new Point3D(0,0,-1),global);

        local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(1,0,0),local);

        transform = new Transform();
        transform.rotateZ(90);

        global  = transform.localToGlobal(new Point3D(1,0,0));
        Assert.assertEquals(new Point3D(0,1,0),global);

        local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(1,0,0),local);

        transform = new Transform();
        transform.rotate(new Vector3D(90,90,90));

        global  = transform.localToGlobal(new Point3D(1,0,0));
        Assert.assertEquals(new Point3D(0,0,1),global);

        local = transform.globalToLocal(global);
        Assert.assertEquals(new Point3D(1,0,0),local);

    }


}
