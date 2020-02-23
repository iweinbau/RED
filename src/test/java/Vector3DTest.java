import math.Vector3D;
import org.junit.Assert;
import org.junit.Test;

public class Vector3DTest {

    @Test
    public void CrossTest() throws Exception {
        Vector3D v1 = new Vector3D(0,1,0);
        Vector3D v2 = new Vector3D(0,0,-1);

        Vector3D cross = v1.cross(v2);

        Assert.assertEquals(new Vector3D(-1,0,0),cross);
    }
}
