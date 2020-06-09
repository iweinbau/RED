import camera.PerspectiveCamera;
import film.Pixel;
import geometry.Plane;
import geometry.Quad;
import geometry.Sphere;
import gui.ProgressReporter;
import integrator.DirectLightIntegrator;
import integrator.Integrator;
import integrator.PathIntegrator;
import integrator.PathTracer;
import light.AreaLight;
import material.Emission;
import material.Matte;
import math.*;
import org.junit.Test;
import renderer.Renderer;
import scene.*;
import textures.Color;
import textures.Constant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class ConvergenceTest {

    private final static int width = 400;
    private final static int height = 400;

    private final static int NUM_TRIALS = 3;

    private final static int MAX_SAMPLES = 1024;
    private final static int MAX_DEPTH = 20;
    private final static int MAX_BRANCH = 64;


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

    @Test
    public void ConvergenceDirect() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);


        /**
         * construct new integrator
         */
        Integrator integrator = new DirectLightIntegrator();


        for (int samples = 2; samples <= MAX_SAMPLES; samples*=2) {
            System.out.println(String.format("Number of Samples: %d",samples));
            Renderer renderer = new Renderer(samples);

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(integrator);
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.render();

            builder.getCamera()
                    .bufferToImage(String.format("./output_scene_1/direct/%d_direct.png",samples),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_scene_1/direct/%d_direct",samples));
        }
    }

    @Test
    public void ConvergencePathIntegrator() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        /**
         * construct new integrator
         */
        Integrator integrator = new PathIntegrator();


        for (int samples = 2; samples <= MAX_SAMPLES; samples*=2) {
            System.out.println(String.format("Number of Samples: %d",samples));
            Renderer renderer = new Renderer(samples);

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(integrator);
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.render();

            builder.getCamera()
                    .bufferToImage(String.format("./output_scene_1/pathIntegrator/%d_indirect.png",samples),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_scene_1/pathIntegrator/%d_indirect",samples));
        }
    }

    @Test
    public void ConvergencePathTracer() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        /**
         * construct new integrator
         */
        Integrator integrator = new PathTracer();


        for (int samples = 2; samples <= MAX_SAMPLES; samples*=2) {
            System.out.println(String.format("Number of Samples: %d",samples));
            Renderer renderer = new Renderer(samples);

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(integrator);
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.render();

            builder.getCamera()
                    .bufferToImage(String.format("./output_scene_1/pathTracer/%d_indirect.png",samples),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_scene_1/pathTracer/%d_indirect",samples));
        }
    }


    @Test
    public void ConvergenceDirectIncreasingLight() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);


        /**
         * construct new integrator
         */
        Integrator integrator = new DirectLightIntegrator();

        SceneBuilder builder = new TestScene1();
        builder.buildScene();
        builder.buildCamera(width,height);

        int samples = 128;

        System.out.println(String.format("Number of Samples: %d",samples));
        Renderer renderer = new Renderer(samples);

        renderer.setIntegrator(integrator);
        renderer.setCamera(builder.getCamera());
        renderer.setScene(builder.getScene());
        renderer.addRenderEventListener(reporter);

        renderer.render();

        builder.getCamera()
                .bufferToImage(String.format("./output_test_2/direct/%d_%d_direct.png",samples,1),1.0,2.2);
        builder.getCamera().rawBufferToFile(String.format("./output_test_2/direct/%d_%d_direct",samples,1));

    }

    @Test
    public void ConvergencePathIntegratorIncreasingLight() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);


        /**
         * construct new integrator
         */
        Integrator integrator = new PathIntegrator();

        SceneBuilder builder = new TestScene1();
        builder.buildScene();
        builder.buildCamera(width,height);

        int samples = 128;

        System.out.println(String.format("Number of Samples: %d",samples));
        Renderer renderer = new Renderer(samples);

        renderer.setIntegrator(integrator);
        renderer.setCamera(builder.getCamera());
        renderer.setScene(builder.getScene());
        renderer.addRenderEventListener(reporter);

        renderer.render();

        builder.getCamera()
                .bufferToImage(String.format("./output_test_2/pathIntegrator/%d_%d_indirect.png",samples,3),1.0,2.2);
        builder.getCamera().rawBufferToFile(String.format("./output_test_2/pathIntegrator/%d_%d_indirect",samples,3));

    }

    @Test
    public void ConvergencePathTracerIncreasingLight() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);


        /**
         * construct new integrator
         */
        Integrator integrator = new PathTracer();

        SceneBuilder builder = new TestScene1();
        builder.buildScene();
        builder.buildCamera(width,height);

        int samples = 128;

        System.out.println(String.format("Number of Samples: %d",samples));
        Renderer renderer = new Renderer(samples);

        renderer.setIntegrator(integrator);
        renderer.setCamera(builder.getCamera());
        renderer.setScene(builder.getScene());
        renderer.addRenderEventListener(reporter);

        renderer.render();

        builder.getCamera()
                .bufferToImage(String.format("./output_test_2/pathTracer/%d_%d_indirect.png",samples,4),1.0,2.2);
        builder.getCamera().rawBufferToFile(String.format("./output_test_2/pathTracer/%d_%d_indirect",samples,4));

    }

    @Test
    public void ConvergenceDepthTest() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        int samples = 64;

        System.out.println(String.format("Number of Samples: %d",samples));
        Renderer renderer = new Renderer(samples);

        for( int i = 4; i <= MAX_DEPTH; i+= 4) {
            System.out.println(String.format("Number of depth: %d",i));

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);
            renderer.setIntegrator(new PathIntegrator(i,1));
            renderer.render();
            builder.getCamera()
                    .bufferToImage(String.format("./output_test_3/pathIntegrator/%d_%d_indirect.png",samples,i),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_test_3/pathIntegrator/%d_%d_indirect",samples,i));
        }
    }

    @Test
    public void CausticsPathIntegratorTest() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);


        for( int samples = 1; samples <= MAX_SAMPLES; samples*= 2) {
            System.out.println(String.format("Number of Samples: %d",samples));
            Renderer renderer = new Renderer(samples);

            SceneBuilder builder = new GlassTeapot();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(new PathIntegrator());
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.render();
            builder.getCamera()
                    .bufferToImage(String.format("./output_scene_4/pathIntegrator/%d_indirect.png",samples),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_scene_4/pathIntegrator/%d_indirect",samples));
        }
    }

    @Test
    public void CausticsPathTracerTest() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        for( int samples = 1; samples <= MAX_SAMPLES; samples*= 2) {
            System.out.println(String.format("Number of Samples: %d",samples));
            Renderer renderer = new Renderer(samples);

            SceneBuilder builder = new GlassTeapot();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(new PathTracer());
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.render();

            builder.getCamera()
                    .bufferToImage(String.format("./output_scene_4/pathTracer/%d_indirect.png",samples),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_scene_4/pathTracer/%d_indirect",samples));
        }
    }

    @Test
    public void BranchFactorTest() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        for( int branch = 1; branch <= MAX_SAMPLES; branch*= 2) {
            System.out.println(String.format("Number of Branch: %d",branch));
            Renderer renderer = new Renderer(MAX_BRANCH/branch);

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(new PathIntegrator(2,branch));
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.render();

            builder.getCamera()
                    .bufferToImage(String.format("./output_test_4/pathIntegrator/%d_indirect.png",branch),1.0,2.2);
            builder.getCamera().rawBufferToFile(String.format("./output_test_4/pathIntegrator/%d_indirect",branch));
        }
    }

    @Test
    public void PerDepthTestIntegrator() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);

        for( int depth = 1; depth <= 5; depth+= 1) {
            System.out.println(String.format("Number of depth: %d",depth));
            Renderer renderer = new Renderer(512);

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(new PathIntegrator());
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.renderDepth(depth);

            builder.getCamera()
                    .bufferToImage(String.format("./output_test_5/pathIntegrator/%d_indirect.png",depth),1.0,2.2);
            // builder.getCamera().rawBufferToFile(String.format("./output_test_5/pathIntegrator/%d_indirect",depth));
        }
    }

    @Test
    public void PerDepthTestTracer() throws IOException {
        // initialize the progress reporter
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height,true);


        for( int depth = 1; depth <= 5; depth+= 1) {
            System.out.println(String.format("Number of depth: %d",depth));
            Renderer renderer = new Renderer(128);

            SceneBuilder builder = new TestScene1();
            builder.buildScene();
            builder.buildCamera(width,height);

            renderer.setIntegrator(new PathTracer());
            renderer.setCamera(builder.getCamera());
            renderer.setScene(builder.getScene());
            renderer.addRenderEventListener(reporter);

            renderer.renderDepth(depth);

            builder.getCamera()
                    .bufferToImage(String.format("./output_test_5/pathTracer/%d_indirect.png",depth),1.0,2.2);
            // builder.getCamera().rawBufferToFile(String.format("./output_test_5/pathTracer/%d_indirect",depth));
        }
    }
}
