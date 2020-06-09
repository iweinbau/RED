package scene;

import camera.Camera;
import parser.MeshFactory;
import parser.TextureFactory;

public abstract class SceneBuilder {

    Scene scene;
    Camera camera;

    final static MeshFactory factory = new MeshFactory();

    final static TextureFactory textureFactory = new TextureFactory();

    public abstract void buildScene();

    public abstract void buildCamera(int width, int height);

    public Camera getCamera() {
        if (camera == null) {
            throw new IllegalStateException("Build Camera first.");
        }
        return camera;
    }

    public Scene getScene() {
        if (camera == null) {
            throw new IllegalStateException("Build Scene first.");
        }
        return scene;
    }
}
