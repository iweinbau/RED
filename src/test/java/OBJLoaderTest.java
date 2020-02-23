import geometry.TriangleMesh;
import org.junit.Assert;
import org.junit.Test;
import parser.MeshFactory;

public class OBJLoaderTest {

    @Test
    public void OBJLoaderFileTest(){
        MeshFactory factory = new MeshFactory();
        TriangleMesh mesh = factory.getTriangleMesh("flat.obj");
    }
}
