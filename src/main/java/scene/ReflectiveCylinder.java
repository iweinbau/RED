package scene;

import camera.PerspectiveCamera;
import geometry.Box;
import geometry.Cylinder;
import geometry.Plane;
import geometry.Quad;
import light.AreaLight;
import material.Emission;
import material.Matte;
import material.Mirror;
import math.*;
import textures.Color;
import textures.Constant;

public class ReflectiveCylinder extends SceneBuilder {
    @Override
    public void buildScene() {
        scene = new Scene();

        Transform3D objT = new Transform3D();
        Transform2D T = new Transform2D();
        Transform3D lightT;

        // BOTTOM white floor
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(102,128,152)),new Constant(1))));

        // TOP white roof
        objT = new Transform3D();
        objT.rotateX(180);
        objT.translate(new Point3D(0,5,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(102,128,152)),new Constant(1))));

        // BACK white wall
        objT = new Transform3D();
        objT.rotateX(90);
        objT.translate(new Point3D(0,0,-3));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(102,128,152)),new Constant(1))));

        // Front white wall
        objT = new Transform3D();
        objT.rotateX(90);
        objT.rotateY(180);
        objT.translate(new Point3D(0,0,5));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(102,128,152)),new Constant(1))));

        // RIGHT green wall
        objT = new Transform3D();
        objT.rotateZ(90);
        objT.translate(new Point3D(3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(102,128,152)),new Constant(1))));

        // LEFT red wall
        objT = new Transform3D();
        objT.rotateZ(-90);
        objT.translate(new Point3D(-3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(102,128,152)),new Constant(1))));

        objT = new Transform3D();
        objT.scale(new Vector3D(2,0.4,2));
        objT.translate(new Point3D(0,0.4,0.5));
        Cylinder cylinder = new Cylinder(objT,new Mirror(
                new Color(new RGBSpectrum(1.,0,0))));
        scene.addGeometry(cylinder);

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1.));
        lightT.rotateX(-120);
        lightT.translate(new Point3D(0,3,3));
        Emission emit = new Emission(new RGBSpectrum(1.),4);
        Quad lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));
    }

    @Override
    public void buildCamera(int width, int height) {
        camera = new PerspectiveCamera(
                new Point3D(-2,4,2),
                new Point3D(0,0,0),width,height,90);
    }
}
