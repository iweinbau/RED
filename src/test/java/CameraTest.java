import core.Sample;
import camera.PerspectiveCamera;
import core.Ray;
import math.Point3D;
import math.Vector3D;
import org.junit.Assert;
import org.junit.Test;
import pathnode.EyeNode;

public class CameraTest {

    @Test
    public void RayGen() throws Exception {
        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,1),
                new Point3D(0),10,10,90);

    }
}
