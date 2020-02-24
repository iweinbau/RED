import camera.PerspectiveCamera;
import geometry.Box;
import geometry.Plane;
import geometry.Sphere;
import geometry.Triangle;
import gui.ProgressReporter;
import integrator.DirectLightIntegrator;
import integrator.Integrator;
import light.DirectionalLight;
import light.PointLight;
import material.Matte;
import material.Phong;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import math.Vector3D;
import org.junit.Test;
import renderer.Renderer;
import sampler.Sampler;
import scene.Scene;
import textures.Color;
import textures.Constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TracerTest {

    int MAX_OBJECTS = 600;
    int NUM_TRIALS = 3;
    int width = 800;
    int height = 800;

    @Test
    public void sphereObjectTest() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("measurements_sphere.txt", false));

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,2),
                new Point3D(0f),width,height,90);

        Scene scene = new Scene();

        Transform lightT;
        lightT = new Transform();
        lightT.translate(new Point3D(2,1,2));
        scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

        double volume = 0.1 / MAX_OBJECTS;
        double radius = Math.pow(0.75 * volume * 3.1415, 1./3.);

        Random rand = new Random();

        Integrator integrator = new DirectLightIntegrator(new Sampler());
        Renderer renderer = new Renderer();

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);

        int prevInt = 0;
        for (int i = 0; i <= MAX_OBJECTS; i+=50) {
            writer.write(String.format("%d ",i));
            System.out.println(String.format("%d ",i));
            for (int j = 0; j < i-prevInt; j++) {
                Transform Tobj = new Transform();
                Tobj.scale(radius);
                Tobj.translate(new Point3D(
                        1 - 2 * rand.nextDouble(),
                        1 - 2 * rand.nextDouble(),
                        1 - 2 * rand.nextDouble()));
                Matte mat = new Matte(new Color(new RGBSpectrum(
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble())),new Constant(1));

                scene.addGeometry(new Sphere(Tobj,mat));
            }

            prevInt = i;

            for(int trial =0 ; trial< NUM_TRIALS ; trial++) {

                renderer.startRender();

                writer.write(String.format("%f8 ",reporter.getFinishedTime()));
            }

            writer.newLine();
        }

        writer.close();
    }

    @Test
    public void boxObjectTest() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("measurements_box.txt", false));

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,0,3),
                new Point3D(0f),width,height,90);

        Scene scene = new Scene();

        Transform lightT;
        lightT = new Transform();
        lightT.translate(new Point3D(2,1,2));
        scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

        double volume = 0.1 / MAX_OBJECTS;
        double radius = Math.pow(0.75 * volume * 3.1415, 1./3.);

        Random rand = new Random();

        Integrator integrator = new DirectLightIntegrator(new Sampler());
        Renderer renderer = new Renderer();

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);

        int prevInt = 0;
        for (int i = 0; i <= MAX_OBJECTS; i+=50) {
            writer.write(String.format("%d ",i));
            System.out.println(String.format("%d ",i));
            for (int j = 0; j < i-prevInt; j++) {
                Transform Tobj = new Transform();
                Tobj.scale(radius);
                Tobj.rotate(new Vector3D(
                        90 * rand.nextDouble(),
                        90 * rand.nextDouble(),
                        90 * rand.nextDouble()));
                Tobj.translate(new Point3D(
                        1 - 2 * rand.nextDouble(),
                        1 - 2 * rand.nextDouble(),
                        1 - 2 * rand.nextDouble()));
                Matte mat = new Matte(new Color(new RGBSpectrum(
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble())),new Constant(1));

                scene.addGeometry(new Box(Tobj,mat));
            }

            prevInt = i;

            for(int trial =0 ; trial< NUM_TRIALS ; trial++) {

                renderer.startRender();

                writer.write(String.format("%f8 ",reporter.getFinishedTime()));
            }

            writer.newLine();
        }

        writer.close();
    }

    @Test
    public void LightTest() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("measurements_lights.txt", false));

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,2),
                new Point3D(0f),width,height,90);

        Scene scene = new Scene();

        double volume = 0.1 / MAX_OBJECTS;
        double radius = Math.pow(0.75 * volume * 3.1415, 1./3.);

        Random rand = new Random();

        Integrator integrator = new DirectLightIntegrator(new Sampler());
        Renderer renderer = new Renderer();

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);

        for (int i = 0; i < 10; i++) {
            Transform Tobj = new Transform();
            Tobj.scale(radius);
            Tobj.translate(new Point3D(
                    1 - 2 * rand.nextDouble(),
                    1 - 2 * rand.nextDouble(),
                    1 - 2 * rand.nextDouble()));
            Matte mat = new Matte(new Color(new RGBSpectrum(
                    rand.nextDouble(),
                    rand.nextDouble(),
                    rand.nextDouble())),new Constant(1));

            scene.addGeometry(new Sphere(Tobj,mat));
        }

        int prevInt = 0;
        for (int i = 0; i <= MAX_OBJECTS; i+=50) {
            writer.write(String.format("%d ",i));
            System.out.println(String.format("%d ",i));
            for (int j = 0; j < i-prevInt; j++) {
                Transform lightT = new Transform();
                lightT.translate(new Point3D(
                                1 - 2 * rand.nextDouble(),
                                1 - 2 * rand.nextDouble(),
                                1 - 2 * rand.nextDouble()));
                scene.addLight(new PointLight(new RGBSpectrum(1),lightT));
            }

            prevInt = i;

            for(int trial =0 ; trial< NUM_TRIALS ; trial++) {

                renderer.startRender();

                writer.write(String.format("%f8 ",reporter.getFinishedTime()));
            }

            writer.newLine();
        }

        writer.close();
    }
}
