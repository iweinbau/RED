import camera.PerspectiveCamera;
import geometry.*;
import gui.ProgressReporter;
import gui.SceneTab;
import integrator.DirectLightIntegrator;
import integrator.Integrator;
import light.PointLight;
import material.Matte;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform3D;
import math.Vector3D;
import org.junit.Test;
import parser.MeshFactory;
import renderer.Renderer;
import scene.Scene;
import textures.Color;
import textures.Constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class BVHTest {


    private final static int width = 400;
    private final static int height = 400;

    private final static int NUM_TRIALS = 10;

    private final static int MAX_OBJECTS = 10000;

    @Test
    public void BVHTest(){

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,2,3),
                new Point3D(0,0,0),width,height,90);

        Scene scene = new Scene();


        MeshFactory factory = new MeshFactory();
        TriangleMesh mesh = factory.getTriangleMesh("sphere.obj");
        BVH bvh = new BVH(new Transform3D(),mesh,
                new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
        bvh.buildAccelerationStructure();

        scene.addGeometry(bvh);


        Integrator integrator = new DirectLightIntegrator();
        Renderer renderer = new Renderer(10);

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);

        renderer.startRender();

    }


    @Test
    public void BVHMeasurement() throws IOException {

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,2,2),
                new Point3D(0,1,0f),width,height,90);

        Scene scene = new Scene();

        List<String> testFiles = new ArrayList<>();
        testFiles.add("sphere.obj");
        testFiles.add("monkey.obj");
        testFiles.add("bunny.obj");
        testFiles.add("armadillo.obj");

        Integrator integrator = new DirectLightIntegrator();
        Renderer renderer = new Renderer(1);

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);


        /**
         *
         *  TEST BUILD TIMES
         *
         */
        BufferedWriter writerBuilder = new BufferedWriter(new FileWriter("./Measurements/Milestone2/measurements_build_times.txt", false));
        BufferedWriter writerRenderBVH = new BufferedWriter(new FileWriter("./Measurements/Milestone2/measurements_render_bvh_timings.txt", false));
        BufferedWriter writerRender = new BufferedWriter(new FileWriter("./Measurements/Milestone2/measurements_render_timings.txt", false));


        for (String object:testFiles) {
            writerBuilder.write(String.format("%s ",object.substring(0,object.length()-4)));
            writerRender.write(String.format("%s ",object.substring(0,object.length()-4)));
            writerRenderBVH.write(String.format("%s ",object.substring(0,object.length()-4)));
            System.out.println(object);
            for (int i=0; i< NUM_TRIALS; i++) {

                scene = new Scene();
                renderer.setScene(scene);

                MeshFactory factory = new MeshFactory();
                TriangleMesh mesh = factory.getTriangleMesh(object);
                Transform3D objT = new Transform3D();
                BVH comp = new BVH(objT,mesh,
                        new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));

                reporter.start();
                comp.buildAccelerationStructure();
                reporter.done();

                writerBuilder.write(String.format(Locale.US,"%f8 ",reporter.getFinishedTime()));
                scene.addGeometry(comp);

                System.out.println("Start render with BVH");
                renderer.startRender();
                writerRenderBVH.write(String.format(Locale.US,"%f8 ",reporter.getFinishedTime()));

                scene = new Scene();
                //renderer.setScene(scene);

                Composite composite = new Composite(objT,mesh,
                        new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
                scene.addGeometry(composite);

                if(!object.equals("armadillo.obj")) {
                    System.out.println("Start render without BVH");
                    renderer.startRender();
                    writerRender.write(String.format(Locale.US, "%f8 ", reporter.getFinishedTime()));
                    System.out.println(reporter.getFinishedTime());
                } else {
                    writerRender.write(String.format(Locale.US, "%f8 ", 0.0));
                }

            }
            writerBuilder.newLine();
            writerRender.newLine();
            writerRenderBVH.newLine();

        }
        writerBuilder.close();
        writerRender.close();
        writerRenderBVH.close();
    }


    private final int MAX_LEAF = 10;

    @Test
    public void BVHMeasurementMaxDepth() throws IOException {

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,2,2),
                new Point3D(0,1,0f),width,height,90);

        Scene scene = new Scene();

        List<String> testFiles = new ArrayList<>();
        testFiles.add("sphere.obj");
        testFiles.add("monkey.obj");
        testFiles.add("bunny.obj");
        testFiles.add("armadillo.obj");

        Integrator integrator = new DirectLightIntegrator();
        Renderer renderer = new Renderer(1);

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);


        /**
         *
         *  TEST BUILD TIMES
         *
         */

        for (String object:testFiles) {
            String name = object.substring(0,object.length()-4);
            BufferedWriter writerBuilder = new BufferedWriter(new FileWriter(String.format("./Measurements/Milestone2/measurements_build_times_%s_depth.txt",name), false));
            BufferedWriter writerRenderBVH = new BufferedWriter(new FileWriter(String.format("./Measurements/Milestone2/measurements_render_bvh_timings_%s_depth.txt",name), false));


            System.out.println(object);
            for(int numLeaf = 1; numLeaf <= MAX_LEAF; numLeaf++) {
                writerBuilder.write(String.format(Locale.US,"%d ",numLeaf));
                writerRenderBVH.write(String.format(Locale.US,"%d ",numLeaf));
                for (int i=0; i< NUM_TRIALS; i++) {
                    scene = new Scene();
                    renderer.setScene(scene);

                    Transform3D lightT = new Transform3D();
                    lightT.translate(new Point3D(0,1,3));
                    scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

                    MeshFactory factory = new MeshFactory();
                    TriangleMesh mesh = factory.getTriangleMesh(object);
                    Transform3D objT = new Transform3D();
                    BVH comp = new BVH(objT,mesh,
                            new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)),numLeaf);

                    reporter.start();
                    comp.buildAccelerationStructure();
                    reporter.done();

                    writerBuilder.write(String.format(Locale.US,"%f8 ",reporter.getFinishedTime()));
                    scene.addGeometry(comp);

                    System.out.println("Start render with BVH");
                    renderer.startRender();
                    writerRenderBVH.write(String.format(Locale.US,"%f8 ",reporter.getFinishedTime()));
                }
                writerBuilder.newLine();
                writerRenderBVH.newLine();
            }

            writerBuilder.close();
            writerRenderBVH.close();
        }
    }


    @Test
    public void sphereObjectTest() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("./Measurements/Milestone2/measurements_sphere_test.txt", false));

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,2),
                new Point3D(0f),width,height,90);

        Scene scene = new Scene();

        BVH bvh = new BVH(new Transform3D());
        scene.addGeometry(bvh);

        Transform3D lightT;
        lightT = new Transform3D();
        lightT.translate(new Point3D(2,1,2));
        scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

        double volume = 0.1 / MAX_OBJECTS;
        double radius = Math.pow(0.75 * volume * 3.1415, 1./3.);

        Random rand = new Random();

        Integrator integrator = new DirectLightIntegrator();
        Renderer renderer = new Renderer(1);

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);

        int prevInt = 0;
        for (int i = 0; i <= MAX_OBJECTS; i+=50) {
            writer.write(String.format("%d ",i));
            System.out.println(String.format("%d ",i));
            for (int j = 0; j < i-prevInt; j++) {
                Transform3D Tobj = new Transform3D();
                Tobj.scale(radius);
                Tobj.translate(new Point3D(
                        1 - 2 * rand.nextDouble(),
                        1 - 2 * rand.nextDouble(),
                        1 - 2 * rand.nextDouble()));
                Matte mat = new Matte(new Color(new RGBSpectrum(
                        rand.nextDouble(),
                        rand.nextDouble(),
                        rand.nextDouble())),new Constant(1));

                bvh.addGeometry(new Sphere(Tobj,mat));
            }

            reporter.start();
            bvh.buildAccelerationStructure();
            reporter.done();
            double buildTime = reporter.getFinishedTime();


            prevInt = i;

            for(int trial =0 ; trial< NUM_TRIALS ; trial++) {

                renderer.startRender();

                writer.write(String.format(Locale.US,"%f8 ", buildTime + reporter.getFinishedTime()));
            }

            writer.newLine();
        }

        writer.close();
    }

    @Test
    public void boxObjectTest() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("./Measurements/Milestone2/measurements_box_test.txt", false));

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,0,3),
                new Point3D(0f),width,height,90);

        Scene scene = new Scene();

        Transform3D lightT;
        lightT = new Transform3D();
        lightT.translate(new Point3D(2,1,2));
        scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

        BVH bvh = new BVH(new Transform3D());
        scene.addGeometry(bvh);

        double volume = 0.1 / MAX_OBJECTS;
        double radius = Math.pow(0.75 * volume * 3.1415, 1./3.);

        Random rand = new Random();

        Integrator integrator = new DirectLightIntegrator();
        Renderer renderer = new Renderer(1);

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);

        renderer.addRenderEventListener(reporter);

        int prevInt = 0;
        for (int i = 0; i <= MAX_OBJECTS; i+=50) {
            writer.write(String.format("%d ",i));
            System.out.println(String.format("%d ",i));
            for (int j = 0; j < i-prevInt; j++) {
                Transform3D Tobj = new Transform3D();
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

                bvh.addGeometry(new Box(Tobj,mat));
            }
            reporter.start();
            bvh.buildAccelerationStructure();
            reporter.done();
            double buildTime = reporter.getFinishedTime();

            prevInt = i;

            for(int trial =0 ; trial< NUM_TRIALS ; trial++) {

                renderer.startRender();

                writer.write(String.format(Locale.US,"%f8 ",buildTime + reporter.getFinishedTime()));
            }

            writer.newLine();
        }

        writer.close();
    }

}
