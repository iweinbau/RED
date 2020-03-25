import camera.PerspectiveCamera;
import film.Pixel;
import geometry.Plane;
import geometry.Quad;
import geometry.Sphere;
import gui.ProgressReporter;
import integrator.DirectLightIntegrator;
import integrator.Integrator;
import light.AreaLight;
import material.Emission;
import material.Matte;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform3D;
import math.Vector3D;
import org.junit.Assert;
import org.junit.Test;
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

public class ConvergenceTest {

    private final static int width = 200;
    private final static int height = 200;

    private final static int NUM_TRIALS = 3;

    private final static int MAX_SAMPLES = 10;

    @Test
    public void testScene() {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,false);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,5),
                new Point3D(0,0,0f),width,height,90);

        /**
         *
         * Construct a new scene
         *
         */
        Scene scene = new Scene();

        Transform3D objT = new Transform3D();
        objT.translate(new Point3D(0,1,0));
        Sphere s = new Sphere(objT, new Matte(
                new Color(new RGBSpectrum(1,0,0)),
                new Constant(1)));
        objT = new Transform3D();
        Plane p = new Plane(objT, new Matte(
                new Color(new RGBSpectrum(1)),
                new Constant(1)));

        Transform3D lightT = new Transform3D();
        lightT.scale(1);
        lightT.rotateZ(-120);
        lightT.translate(new Point3D(-3,3,0));
        Quad q = new Quad(lightT,new Emission(new RGBSpectrum(1)));
        AreaLight light = new AreaLight(q,new Emission(new RGBSpectrum(1)));

        scene.addGeometry(q);
        scene.addGeometry(s);
        scene.addGeometry(p);
        scene.addLight(light);


        /**
         * construct new integrator
         */
        Integrator integrator = new DirectLightIntegrator();


        /**
         * construct new renderer
         */
        Renderer renderer = new Renderer(5);

        renderer.setIntegrator(integrator);
        renderer.setCamera(camera);
        renderer.setScene(scene);
        renderer.addRenderEventListener(reporter);

        renderer.startRender();

        Pixel pixelUmbra = camera.getRenderBuffer().getPixel(100,120);
        Pixel pixelPenumbra = camera.getRenderBuffer().getPixel(100,160);
        Pixel pixelLight = camera.getRenderBuffer().getPixel(100,70);

        System.out.println(String.format("Pixel umbra: %f8",pixelUmbra.getSpectrum().getX()));
        System.out.println(String.format("Pixel penumbra: %f8",pixelPenumbra.getSpectrum().getX()));
        System.out.println(String.format("Pixel light: %f8",pixelLight.getSpectrum().getX()));

    }

    private static final double AREA_SCALE = 2.;

    @Test
    public void AreaConvergence() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,5),
                new Point3D(0,0,0f),width,height,90);

        /**
         * construct new integrator
         */
        Integrator integrator = new DirectLightIntegrator();


        /**
         *
         *  Create output files
         *
         */
        BufferedWriter writerUmbra = new BufferedWriter(new FileWriter("./Measurements/Milestone2/convergence/umbra_area.txt", false));
        BufferedWriter writerPenUmbra= new BufferedWriter(new FileWriter("./Measurements/Milestone2/convergence/penumbra_area.txt", false));
        BufferedWriter writerLight = new BufferedWriter(new FileWriter("./Measurements/Milestone2/convergence/light_area.txt", false));

        for (double areaScaleFactor = 0.5; areaScaleFactor <= AREA_SCALE; areaScaleFactor+= 0.1) {
            double area = areaScaleFactor * 4;
            System.out.println(String.format("Area: %f8",area));

            /**
             * construct new renderer
             */
            Renderer renderer = new Renderer(2000);

            /**
             *
             * Construct a new scene
             *
             */
            Scene scene = new Scene();

            Transform3D objT = new Transform3D();
            objT.translate(new Point3D(0,1,0));
            Sphere s = new Sphere(objT, new Matte(
                    new Color(new RGBSpectrum(1,0,0)),
                    new Constant(1)));
            objT = new Transform3D();
            Plane p = new Plane(objT, new Matte(
                    new Color(new RGBSpectrum(1)),
                    new Constant(1)));

            Transform3D lightT = new Transform3D();
            lightT.scale(areaScaleFactor);
            lightT.rotateZ(-120);
            lightT.translate(new Point3D(-3,3,0));
            Quad q = new Quad(lightT,new Emission(new RGBSpectrum(1)));
            AreaLight light = new AreaLight(q,new Emission(new RGBSpectrum(1)));

            scene.addGeometry(q);
            scene.addGeometry(s);
            scene.addGeometry(p);
            scene.addLight(light);

            renderer.setIntegrator(integrator);
            renderer.setCamera(camera);
            renderer.setScene(scene);
            renderer.addRenderEventListener(reporter);

            writerUmbra.write(String.format(Locale.US,"%f5 ", area));
            writerPenUmbra.write(String.format(Locale.US,"%f5 ", area));
            writerLight.write(String.format(Locale.US,"%f5 ", area));

            /**
             *
             * Converged pixel values.
             *
             */
            renderer.startRender();

            Pixel pixelUmbra = camera.getRenderBuffer().getPixel(100,120);
            Pixel pixelPenumbra = camera.getRenderBuffer().getPixel(100,160);
            Pixel pixelLight = camera.getRenderBuffer().getPixel(100,70);

            /**
             *
             * Write output to file.
             *
             */
            writerUmbra.write(String.format(Locale.US,"%f8 ",pixelUmbra.getSpectrum().getX()));
            writerPenUmbra.write(String.format(Locale.US,"%f8 ",pixelPenumbra.getSpectrum().getX()));
            writerLight.write(String.format(Locale.US,"%f8 ",pixelLight.getSpectrum().getX()));

            renderer.setSamplesPerPixel(100);

            for (int i = 0; i < NUM_TRIALS; i++) {

                renderer.startRender();

                pixelUmbra = camera.getRenderBuffer().getPixel(100,120);
                pixelPenumbra = camera.getRenderBuffer().getPixel(100,160);
                pixelLight = camera.getRenderBuffer().getPixel(100,70);

                /**
                 *
                 * Write output to file.
                 *
                 */
                writerUmbra.write(String.format(Locale.US,"%f8 ",pixelUmbra.getSpectrum().getX()));
                writerPenUmbra.write(String.format(Locale.US,"%f8 ",pixelPenumbra.getSpectrum().getX()));
                writerLight.write(String.format(Locale.US,"%f8 ",pixelLight.getSpectrum().getX()));

            }

            /**
             * New line
             */
            writerUmbra.newLine();
            writerPenUmbra.newLine();
            writerLight.newLine();

        }

        writerUmbra.close();
        writerPenUmbra.close();
        writerLight.close();
    }

    @Test
    public void Convergence() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        PerspectiveCamera camera = new PerspectiveCamera(
                new Point3D(0,3,5),
                new Point3D(0,0,0f),width,height,90);

        /**
         *
         * Construct a new scene
         *
         */
        Scene scene = new Scene();

        Transform3D objT = new Transform3D();
        objT.translate(new Point3D(0,1,0));
        Sphere s = new Sphere(objT, new Matte(
                new Color(new RGBSpectrum(1,0,0)),
                new Constant(1)));
        objT = new Transform3D();
        Plane p = new Plane(objT, new Matte(
                new Color(new RGBSpectrum(1)),
                new Constant(1)));

        Transform3D lightT = new Transform3D();
        lightT.scale(new Vector3D(1,1,1));
        lightT.rotateZ(-120);
        lightT.translate(new Point3D(-3,3,0));
        Quad q = new Quad(lightT,new Emission(new RGBSpectrum(1)));
        AreaLight light = new AreaLight(q,new Emission(new RGBSpectrum(1)));

        scene.addGeometry(q);
        scene.addGeometry(s);
        scene.addGeometry(p);
        scene.addLight(light);

        /**
         * construct new integrator
         */
        Integrator integrator = new DirectLightIntegrator();


        /**
         *
         *  Create output files
         *
         */
        BufferedWriter writerUmbra = new BufferedWriter(new FileWriter("./Measurements/Milestone2/convergence/umbra.txt", true));
        BufferedWriter writerPenUmbra= new BufferedWriter(new FileWriter("./Measurements/Milestone2/convergence/penumbra.txt", true));
        BufferedWriter writerLight = new BufferedWriter(new FileWriter("./Measurements/Milestone2/convergence/light.txt", true));

        for (int samples = 1001; samples < MAX_SAMPLES; samples+= 20) {
            System.out.println(String.format("Number of samples: %d",samples));
            /**
             * construct new renderer
             */
            Renderer renderer = new Renderer(samples);

            renderer.setIntegrator(integrator);
            renderer.setCamera(camera);
            renderer.setScene(scene);
            renderer.addRenderEventListener(reporter);

            writerUmbra.write(String.format("%d ", samples));
            writerPenUmbra.write(String.format("%d ", samples));
            writerLight.write(String.format("%d ", samples));

            for (int i = 0; i < NUM_TRIALS; i++) {

                renderer.startRender();

                Pixel pixelUmbra = camera.getRenderBuffer().getPixel(100,120);
                Pixel pixelPenumbra = camera.getRenderBuffer().getPixel(100,160);
                Pixel pixelLight = camera.getRenderBuffer().getPixel(100,70);

                /**
                 *
                 * Write output to file.
                 *
                 */
                writerUmbra.write(String.format(Locale.US,"%f8 ",pixelUmbra.getSpectrum().getX()));
                writerPenUmbra.write(String.format(Locale.US,"%f8 ",pixelPenumbra.getSpectrum().getX()));
                writerLight.write(String.format(Locale.US,"%f8 ",pixelLight.getSpectrum().getX()));

            }

            /**
             * New line
             */
            writerUmbra.newLine();
            writerPenUmbra.newLine();
            writerLight.newLine();

        }

        writerUmbra.close();
        writerPenUmbra.close();
        writerLight.close();
    }
}
