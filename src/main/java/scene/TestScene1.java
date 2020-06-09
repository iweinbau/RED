package scene;

import camera.PerspectiveCamera;
import geometry.Box;
import geometry.Plane;
import geometry.Quad;
import geometry.Sphere;
import light.AreaLight;
import material.Emission;
import material.Matte;
import math.*;
import textures.Color;
import textures.Constant;

public class TestScene1 extends SceneBuilder{

    @Override
    public void buildScene() {
        scene = new Scene();

        Transform3D objT = new Transform3D();
        Transform2D T = new Transform2D();
        Transform3D lightT;

        // BOTTOM white floor
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));

        // TOP white roof
        objT = new Transform3D();
        objT.rotateX(180);
        objT.translate(new Point3D(0,5,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));

        // BACK white wall
        objT = new Transform3D();
        objT.rotateX(90);
        objT.translate(new Point3D(0,0,-2));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));

        // Front white wall
        objT = new Transform3D();
        objT.rotateX(90);
        objT.rotateY(180);
        objT.translate(new Point3D(0,0,5));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1.)),new Constant(1))));

        // RIGHT green wall
        objT = new Transform3D();
        objT.rotateZ(90);
        objT.translate(new Point3D(3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(0,1.,0)),new Constant(1))));

        // LEFT red wall
        objT = new Transform3D();
        objT.rotateZ(-90);
        objT.translate(new Point3D(-3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1.,0,0)),new Constant(1))));

        // DIFFUSE CUBE
        objT= new Transform3D();
        objT.scale(new Vector3D(1,1.5,1));
        objT.rotateY(-20);
        objT.translate(new Point3D(-1,1.5,-1));
        scene.addGeometry(new Box(objT,new Matte(new Color(new RGBSpectrum(1.)),new Constant(1))));

        // DIFFUSE SPHERE
        objT= new Transform3D();
        objT.scale(0.8);
        objT.translate(new Point3D(1,0.8,0.5));
        scene.addGeometry(new Sphere(objT,new Matte(new Color(new RGBSpectrum(1.)),new Constant(1))));

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1));
        lightT.rotateX(180);
        lightT.translate(new Point3D(0,4.9999,1));
        Emission emit = new Emission(new RGBSpectrum(1.),4);
        Quad lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));
    }

    @Override
    public void buildCamera(int width, int height) {
        camera = new PerspectiveCamera(
                new Point3D(0,2,3.5),
                new Point3D(0,2,0),width,height,90);
    }
}
