package distributeRenderer;

import film.FrameBuffer;
import film.Pixel;
import film.Tile;
import integrator.PathTracer;
import math.*;
import org.json.JSONArray;
import org.json.JSONObject;
import parser.MeshFactory;
import parser.TextureFactory;
import renderer.Renderer;
import scene.GlassTeapot;
import scene.ReflectiveCylinder;
import scene.SceneBuilder;
import scene.TestScene1;
import java.io.IOException;
import java.io.PrintStream;

public class SlaveRenderer {

    private static PrintStream DEFAULT_OUTPUT;

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
        final Renderer renderer = new Renderer(10000);

        /**
         *
         * Initialize empty scene.
         *
         */
        SceneBuilder builder = new GlassTeapot();
        builder.buildCamera(width,height);
        builder.buildScene();

        /**
         *
         *
         * Set render settings
         *
         */
        renderer.setScene(builder.getScene());
        renderer.setCamera(builder.getCamera());
        renderer.setIntegrator(new PathTracer());


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
