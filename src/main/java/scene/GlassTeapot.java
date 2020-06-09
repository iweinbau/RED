package scene;

import camera.PerspectiveCamera;
import geometry.BVH;
import geometry.Plane;
import geometry.Quad;
import geometry.TriangleMesh;
import light.AreaLight;
import material.Emission;
import material.Glass;
import material.Matte;
import math.*;
import parser.MeshFactory;
import textures.Color;
import textures.Constant;

public class GlassTeapot extends SceneBuilder {
    @Override
    public void buildScene() {
        scene = new Scene();

        Transform3D objT;
        Transform2D T;
        Transform3D lightT;

        TriangleMesh mesh = factory.getTriangleMesh("teapot.obj");
        objT = new Transform3D();
        objT.scale(1);
        objT.rotateY(180);
        objT.translate(new Point3D(-.5,0,0));
        BVH bvh = new BVH(objT,mesh,
                new Glass(new Color(new RGBSpectrum(1.,0,0)),
                        new Color(new RGBSpectrum(1.,0,0)),new Constant(1.5)));
        bvh.buildAccelerationStructure();

        scene.addGeometry(bvh);

        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1.)),new Constant(0.6))));

        objT = new Transform3D();
        objT.rotateZ(90);
        objT.translate(new Point3D(3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1.)),new Constant(0.6))));

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1.));
        lightT.rotateZ(-90);
        lightT.translate(new Point3D(-3,0.5,0));
        Emission emit = new Emission(new RGBSpectrum(1.),5);
        Quad lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));
    }

    @Override
    public void buildCamera(int width, int height) {
        Point3D origin = new Point3D(0,1,2.5);
        Point3D destination = new Point3D(0.5,0.5,0);
        camera = new PerspectiveCamera(
                origin,
                destination,width,height,90);

    }
}
