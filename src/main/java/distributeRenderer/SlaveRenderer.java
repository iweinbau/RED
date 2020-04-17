package distributeRenderer;

import camera.PerspectiveCamera;
import film.FrameBuffer;
import film.Pixel;
import film.Tile;
import geometry.*;
import integrator.PathTracer;
import light.AreaLight;
import material.Emission;
import material.Glass;
import material.Matte;
import math.*;
import org.json.JSONArray;
import org.json.JSONObject;
import parser.MeshFactory;
import parser.TextureFactory;
import renderer.Renderer;
import scene.Scene;
import textures.Color;
import textures.Constant;
import java.io.IOException;
import java.io.PrintStream;

public class SlaveRenderer {

    private static PrintStream DEFAULT_OUTPUT;

    final static MeshFactory factory = new MeshFactory();
    final static TextureFactory textureFactory = new TextureFactory();

    public static void main(String[] arguments) throws IOException {

        /**
         *
         * The disable all system out prints.
         *
         */
        disableSystemOut();

        /**
         *
         * Init render settings with default values.
         *
         */

        int startX = 0;
        int startY = 0;
        int endX = 400;
        int endY = 400;
        int width = 400;
        int height = 400;

        /**
         *
         * Parse input arguments
         *
         */
        for (int i = 0; i < arguments.length; ++i) {
            if (arguments[i].startsWith("-")) {
                String flag = arguments[i];

                try {
                    if ("-startX".equals(flag))
                        startX = Integer.parseInt(arguments[++i]);
                    else if ("-startY".equals(flag))
                        startY = Integer.parseInt(arguments[++i]);
                    else if ("-endX".equals(flag))
                        endX = Integer.parseInt(arguments[++i]);
                    else if ("-endY".equals(flag))
                        endY = Integer.parseInt(arguments[++i]);
                    else if ("-width".equals(flag))
                        width = Integer.parseInt(arguments[++i]);
                    else if ("-height".equals(flag))
                        height = Integer.parseInt(arguments[++i]);

                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.format("could not find a value for "
                            + "flag \"%s\"\n!", flag);
                }
            } else
                System.err.format("unknown value \"%s\" encountered! "
                        + "This will be skipped!\n", arguments[i]);
        }

        //Point3D origin = new Point3D(0,2,4);
        //Point3D destination = new Point3D(0,2,0);

        Point3D origin = new Point3D(0,1,2.5);
        Point3D destination = new Point3D(0.5,0.5,0);


        Vector3D lookup = new Vector3D(0,1,0);
        double fov = 90;


        /**
         *
         * Construct new render tile from settings.
         *
         */
        Tile tile = new Tile(startX,startY,endX,endY);

        /**
         *
         * Create new renderer
         *
         */
        final Renderer renderer = new Renderer(100);

        /**
         *
         * Create new camera from settings.
         *
         */
        final PerspectiveCamera camera = new PerspectiveCamera(origin,destination,width,height,fov);

        /**
         *
         * Initialize empty scene.
         *
         */
        final Scene scene = new Scene();

        /**
         *
         *
         * Set render settings
         *
         */
        renderer.setScene(scene);
        renderer.setCamera(camera);
        renderer.setIntegrator(new PathTracer());

        /**
         *
         *
         * Populate scene
         *
         */
        Transform3D objT = new Transform3D();
        Transform2D T = new Transform2D();
        Transform3D lightT = new Transform3D();

//        // BOTTOM white floor
//        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));
//
//        // TOP white roof
//        objT = new Transform3D();
//        objT.rotateX(180);
//        objT.translate(new Point3D(0,5,0));
//        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));
//
//        // BACK white wall
//        objT = new Transform3D();
//        objT.rotateX(90);
//        objT.translate(new Point3D(0,0,-2));
//        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));
//
//        // Front white wall
//        objT = new Transform3D();
//        objT.rotateX(90);
//        objT.rotateY(180);
//        objT.translate(new Point3D(0,0,5));
//        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));
//
//        // RIGHT green wall
//        objT = new Transform3D();
//        objT.rotateZ(90);
//        objT.translate(new Point3D(3,0,0));
//        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(0,1,0)),new Constant(1))));
//
//        // LEFT red wall
//        objT = new Transform3D();
//        objT.rotateZ(-90);
//        objT.translate(new Point3D(-3,0,0));
//        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1))));
//
//        // MIRROR SPHERE
//        objT= new Transform3D();
//        objT.translate(new Point3D(-1,2,0));
//        scene.addGeometry(new Sphere(objT,
//                new Glass(new Color(new RGBSpectrum(1)),new Color(new RGBSpectrum(1)),new Constant(1.5))));
//
//        lightT  = new Transform3D();
//        lightT.scale(new Vector3D(1));
//        lightT.rotateX(180);
//        lightT.translate(new Point3D(0,4.9999,1));
//        Emission emit = new Emission(new RGBSpectrum(1),5);
//        Quad lObjq = new Quad(lightT, emit);
//
//        scene.addGeometry(lObjq);
//        scene.addLight(new AreaLight(lObjq,emit));



        TriangleMesh mesh = factory.getTriangleMesh("teapot.obj");
        objT = new Transform3D();
        objT.scale(1);
        objT.rotateY(180);
        objT.translate(new Point3D(-.5,0,0));
        BVH bvh = new BVH(objT,mesh,
                new Glass(new Color(new RGBSpectrum(1)),new Color(new RGBSpectrum(1)),new Constant(1.5)));
        bvh.buildAccelerationStructure();
        scene.addGeometry(bvh);

        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(0.6))));

        objT = new Transform3D();
        objT.rotateZ(90);
        objT.translate(new Point3D(3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(0.6))));

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1));
        lightT.rotateZ(-90);
        lightT.translate(new Point3D(-3,0.5,0));
        Emission emit = new Emission(new RGBSpectrum(1),5);
        Quad lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));

        /**
         *
         * Render tile to framebuffer
         *
         */
        FrameBuffer frameBuffer = renderer.render(tile);


        /**
         *
         * Setup slave response. THis is done by creating a json string of all pixels rendered by this slave and
         * the start en end pixels of this tile.
         * NOTE: it's not necessary to add the start end end pixels since this tile is linked with an id in the master.
         *      but for convenience I've added them.
         */
        JSONArray pixels = new JSONArray();

        for (int i =0; i < tile.getHeight(); i++) {
           for (int j =0; j < tile.getWidth(); j++) {
               JSONObject pixel = new JSONObject();
               Pixel p = frameBuffer.getPixel(i,j);
               pixel.put("r",p.getSpectrum().getX());
               pixel.put("g",p.getSpectrum().getY());
               pixel.put("b",p.getSpectrum().getZ());
               pixels.put(pixel);
           }
        }

        JSONObject obj = new JSONObject();

        obj.put("startX", startX);
        obj.put("startY", startY);
        obj.put("endX", endX);
        obj.put("endY", endY);
        obj.put("pixels",pixels);

        /**
         * Enable system out and send json string to stream.
         */
        enableSystemOut();
        System.out.write(obj.toString().getBytes("UTF-8"));

    }

    public static void disableSystemOut(){
        DEFAULT_OUTPUT = System.out;

        System.setOut(new java.io.PrintStream(new java.io.OutputStream() {
            @Override public void write(int b) {}
        }) {
            @Override public void flush() {}
            @Override public void close() {}
            @Override public void write(int b) {}
            @Override public void write(byte[] b) {}
            @Override public void write(byte[] buf, int off, int len) {}
            @Override public void print(boolean b) {}
            @Override public void print(char c) {}
            @Override public void print(int i) {}
            @Override public void print(long l) {}
            @Override public void print(float f) {}
            @Override public void print(double d) {}
            @Override public void print(char[] s) {}
            @Override public void print(String s) {}
            @Override public void print(Object obj) {}
            @Override public void println() {}
            @Override public void println(boolean x) {}
            @Override public void println(char x) {}
            @Override public void println(int x) {}
            @Override public void println(long x) {}
            @Override public void println(float x) {}
            @Override public void println(double x) {}
            @Override public void println(char[] x) {}
            @Override public void println(String x) {}
            @Override public void println(Object x) {}
            @Override public java.io.PrintStream printf(String format, Object... args) { return this; }
            @Override public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) { return this; }
            @Override public java.io.PrintStream format(String format, Object... args) { return this; }
            @Override public java.io.PrintStream format(java.util.Locale l, String format, Object... args) { return this; }
            @Override public java.io.PrintStream append(CharSequence csq) { return this; }
            @Override public java.io.PrintStream append(CharSequence csq, int start, int end) { return this; }
            @Override public java.io.PrintStream append(char c) { return this; }
        });
    }

    public static void enableSystemOut(){
        System.setOut(DEFAULT_OUTPUT);
    }
}
