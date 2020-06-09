package scene;

import camera.PerspectiveCamera;
import geometry.Plane;
import geometry.Quad;
import geometry.Sphere;
import light.AreaLight;
import light.EnvironmentLight;
import material.Emission;
import material.Matte;
import math.*;
import textures.Color;
import textures.Constant;

public class TestScene2 extends SceneBuilder {
    @Override
    public void buildScene() {
        scene = new Scene();

        Transform3D objT = new Transform3D();
        Transform2D T = new Transform2D();
        Transform3D lightT;

        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1.)),new Constant(1))));


        objT = new Transform3D();
        objT.translate(new Point3D(0,1,0));
        scene.addGeometry(new Sphere(objT,new Matte(new Color(new RGBSpectrum(0,1.,0)),new Constant(1))));

        objT = new Transform3D();
        objT.translate(new Point3D(-1.5,1,0));
        scene.addGeometry(new Sphere(objT,new Matte(new Color(new RGBSpectrum(1.,0,0)),new Constant(1))));

        objT = new Transform3D();
        objT.translate(new Point3D(1.5,1,0));
        scene.addGeometry(new Sphere(objT,new Matte(new Color(new RGBSpectrum(0,0,1.0)),new Constant(1))));

        lightT  = new Transform3D();
        lightT.rotateX(120);
        lightT.translate(new Point3D(0,2,-3));
        Emission emit = new Emission(new RGBSpectrum(1.),4);
        Quad lObjq = new Quad(lightT, emit);

        //scene.addLight(new EnvironmentLight(new RGBSpectrum(0.4)));

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));
    }

    @Override
    public void buildCamera(int width, int height) {
        camera = new PerspectiveCamera(new Point3D(-3.,3,4),
                new Point3D(0,0.5,0),width,height,90);
    }
}
