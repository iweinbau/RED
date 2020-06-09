package scene;

import camera.PerspectiveCamera;
import geometry.BVH;
import geometry.Quad;
import geometry.Sphere;
import geometry.TriangleMesh;
import light.AreaLight;
import light.EnvironmentLight;
import material.Emission;
import material.Glass;
import material.Matte;
import math.*;
import textures.Color;
import textures.Constant;

public class Cave extends SceneBuilder {
    @Override
    public void buildScene() {
        scene = new Scene();

        Transform3D objT;
        Transform2D T;
        Transform3D lightT;

        TriangleMesh mesh = factory.getTriangleMesh("cube_opening.obj");
        objT = new Transform3D();
        BVH bvh = new BVH(objT,mesh,
                new Matte(new Color(new RGBSpectrum(1.)),new Constant(1)));
        bvh.buildAccelerationStructure();

        scene.addGeometry(bvh);

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1));
        lightT.rotateY(45);
        lightT.rotateX(-170);
        lightT.translate(new Point3D(2,4,2));
        Emission emit = new Emission(new RGBSpectrum(1.),4);
        Quad lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1));
        lightT.rotateY(0);
        lightT.rotateX(-180);
        lightT.translate(new Point3D(0,4,0));
        emit = new Emission(new RGBSpectrum(1.),2);
        lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));

    }

    @Override
    public void buildCamera(int width, int height) {
        camera = new PerspectiveCamera(
                new Point3D(0,0.5,0.5),
                new Point3D(0,0,0),width,height,90);
    }
}
