import camera.PerspectiveCamera;
import geometry.*;
import gui.ProgressReporter;
import integrator.DirectLightIntegrator;
import integrator.Integrator;
import material.Matte;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform3D;
import org.junit.Test;
import parser.MeshFactory;
import renderer.Renderer;
import scene.Scene;
import textures.Color;
import textures.Constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BVHTest {


    private final static int width = 100;
    private final static int height = 100;

    private final static int NUM_TRIALS = 3;
    @Test
    public void BVHTest(){

        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,2,2),
                new Point3D(0f),width,height,90);

        Scene scene = new Scene();


        MeshFactory factory = new MeshFactory();
        TriangleMesh mesh = factory.getTriangleMesh("monkey.obj");
        BVH bvh = new BVH(new Transform3D(),mesh,
                new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
        bvh.buildAccelerationStructure();


        Integrator integrator = new DirectLightIntegrator();
        Renderer renderer = new Renderer(1);

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
                renderer.setScene(scene);

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

}
