package distributeRenderer;

import camera.PerspectiveCamera;
import film.FrameBuffer;
import film.Pixel;
import film.Tile;
import geometry.Plane;
import geometry.Quad;
import geometry.Sphere;
import integrator.PathTracer;
import light.AreaLight;
import material.Emission;
import material.Glass;
import material.Matte;
import math.*;
import parser.MeshFactory;
import parser.TextureFactory;
import renderer.Renderer;
import scene.Scene;
import textures.Color;
import textures.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SlaveRenderer {

    final static MeshFactory factory = new MeshFactory();
    final static TextureFactory textureFactory = new TextureFactory();

    public static void main(String[] arguments) throws IOException {

        /**
         *
         * Init render settings with default values.
         *
         */
        Point3D origin = new Point3D(0,2.5,4);
        Point3D destination = new Point3D(0,2.5,0);
        Vector3D lookup = new Vector3D(0,1,0);
        double fov = 90;

        int startX = 0;
        int startY = 0;
        int endX = 10;
        int endY = 10;
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
        final Renderer renderer = new Renderer(10);

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
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1))));

        // RIGHT green wall
        objT = new Transform3D();
        objT.rotateZ(90);
        objT.translate(new Point3D(3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(0,1,0)),new Constant(1))));

        // LEFT red wall
        objT = new Transform3D();
        objT.rotateZ(-90);
        objT.translate(new Point3D(-3,0,0));
        scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1))));

        // MIRROR SPHERE
        objT= new Transform3D();
        objT.translate(new Point3D(-1,2,0));
        scene.addGeometry(new Sphere(objT,
                new Glass(new Color(new RGBSpectrum(1)),new Color(new RGBSpectrum(1)),new Constant(1.5))));

        lightT  = new Transform3D();
        lightT.scale(new Vector3D(1));
        lightT.rotateX(180);
        lightT.translate(new Point3D(0,4.9999,1));
        Emission emit = new Emission(new RGBSpectrum(1),5);
        Quad lObjq = new Quad(lightT, emit);

        scene.addGeometry(lObjq);
        scene.addLight(new AreaLight(lObjq,emit));

        /**
         *
         *
         * Render tile to framebuffer
         *
         */
        FrameBuffer frameBuffer = renderer.render(tile);


        /**
         *
         * Setup slave response.
         */
        Pixel[] pixels = new Pixel[tile.getWidth() * tile.getHeight()];
        int k = 0;
        for (int i =0; i < tile.getHeight(); i++) {
           for (int j =0; j < tile.getWidth(); j++) {
                pixels[k++] = frameBuffer.getPixel(i,j);
           }
        }

        RenderResponse renderResponse = new RenderResponse(startX,startY,endX,endY, pixels);
        byte[] toSend = serializeBuffer(renderResponse);
        System.out.write(toSend);

    }

    /**
     *
     * Serialize RenderResponse object to byte array
     *
     * @param frameBufferRenderResponse
     * @return byte array containing the serialized object
     * @throws IOException
     */
    public static byte[] serializeBuffer(RenderResponse frameBufferRenderResponse) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(frameBufferRenderResponse);
        return out.toByteArray();
    }
}
