import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import camera.PerspectiveCamera;
import geometry.*;
import gui.ProgressReporter;
import gui.RenderFrame;
import integrator.DirectLightIntegrator;
import integrator.PathIntegrator;
import integrator.PathTracer;
import light.AreaLight;
import light.EnvironmentLight;
import light.PointLight;
import material.Emission;
import material.Glass;
import material.Matte;
import material.Mirror;
import math.*;
import parser.Image;
import parser.MeshFactory;
import parser.OBJLoader;
import parser.TextureFactory;
import renderer.Renderer;
import scene.Scene;
import textures.*;
import textures.texturemap.CylindricalMap;
import textures.texturemap.PlanarMap;
import textures.texturemap.SphericalMap;
import textures.texturemap.UVMap;

/**
 * Entry point of your renderer.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class RED {
	
	/**
	 * Entry point of your renderer.
	 * 
	 * @param arguments
	 *            command line arguments.
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void main(String[] arguments) {
		int width = 400;
		int height = 400;
		double sensitivity = 1.0;
		double gamma = 2.2;
		boolean gui = true;
		boolean quiet = false;
		//Point3D origin = new Point3D(-0.283894,-0.794405,4.53327);
		//Point3D destination = new Point3D(-0.9951571822166443,0.00454461295157671,-0.09819173067808151);

//		Point3D origin = new Point3D(0,1,2.5);
//		Point3D destination = new Point3D(0.5,0.5,0);

		Point3D origin = new Point3D(0,2,3);
		Point3D destination = new Point3D(0,2,0);

		Vector3D lookup = new Vector3D(0,1,0);
		double fov = 90;
		String filename = "output.png";

		/**********************************************************************
		 * Parse the command line arguments
		 *********************************************************************/

		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i].startsWith("-")) {
				String flag = arguments[i];

				try {
					if ("-width".equals(flag))
						width = Integer.parseInt(arguments[++i]);
					else if ("-height".equals(flag))
						height = Integer.parseInt(arguments[++i]);
					else if ("-gui".equals(flag))
						gui = Boolean.parseBoolean(arguments[++i]);
					else if ("-quiet".equals(flag))
						quiet = Boolean.parseBoolean(arguments[++i]);
					else if ("-sensitivity".equals(flag))
						sensitivity = Double.parseDouble(arguments[++i]);
					else if ("-gamma".equals(flag))
						gamma = Double.parseDouble(arguments[++i]);
					else if ("-origin".equals(flag)) {
						double x = Double.parseDouble(arguments[++i]);
						double y = Double.parseDouble(arguments[++i]);
						double z = Double.parseDouble(arguments[++i]);
						origin = new Point3D(x, y, z);
					} else if ("-destination".equals(flag)) {
						double x = Double.parseDouble(arguments[++i]);
						double y = Double.parseDouble(arguments[++i]);
						double z = Double.parseDouble(arguments[++i]);
						destination = new Point3D(x, y, z);
					} else if ("-lookup".equals(flag)) {
						double x = Double.parseDouble(arguments[++i]);
						double y = Double.parseDouble(arguments[++i]);
						double z = Double.parseDouble(arguments[++i]);
						lookup = new Vector3D(x, y, z);
					} else if ("-fov".equals(flag)) {
						fov = Double.parseDouble(arguments[++i]);
					} else if ("-output".equals(flag)) {
						filename = arguments[++i];
					} else if ("-help".equals(flag)) {
						System.out
								.println("usage: java -jar cgpracticum.jar\n"
										+ "  -width <integer>      width of the image\n"
										+ "  -height <integer>     height of the image\n"
										+ "  -sensitivity <double> scaling factor for the radiance\n"
										+ "  -gamma <double>       gamma correction factor\n"
										+ "  -origin <point>       origin for the camera\n"
										+ "  -destination <point>  destination for the camera\n"
										+ "  -lookup <vector>      up direction for the camera\n"
										+ "  -output <string>      filename for the image\n"
										+ "  -gui <boolean>        whether to start a graphical user interface\n"
										+ "  -quiet <boolean>      whether to print the progress bar");
						return;
					} else {
						System.err.format("unknown flag \"%s\" encountered!\n",
								flag);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.format("could not find a value for "
							+ "flag \"%s\"\n!", flag);
				}
			} else
				System.err.format("unknown value \"%s\" encountered! "
						+ "This will be skipped!\n", arguments[i]);
		}

		/**********************************************************************
		 * Validate the input
		 *********************************************************************/

		if (width <= 0)
			throw new IllegalArgumentException("the given width cannot be "
					+ "smaller than or equal to zero!");
		if (height <= 0)
			throw new IllegalArgumentException("the given height cannot be "
					+ "smaller than or equal to zero!");
		if (gamma <= 0)
			throw new IllegalArgumentException("the gamma cannot be "
					+ "smaller than or equal to zero!");
		if (sensitivity <= 0)
			throw new IllegalArgumentException("the sensitivity cannot be "
					+ "smaller than or equal to zero!");
		if (fov <= 0)
			throw new IllegalArgumentException("the field of view cannot be "
					+ "smaller than or equal to zero!");
		if (fov >= 180)
			throw new IllegalArgumentException("the field of view cannot be "
					+ "larger than or equal to 180!");
		if (filename.isEmpty())
			throw new IllegalArgumentException("the filename cannot be the "
					+ "empty string!");

		/**********************************************************************
		 * Initialize the camera and graphical user interface
		 *********************************************************************/

		final Renderer renderer = new Renderer(10);

		// initialize the progress reporter
		final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
				width * height, quiet);

		// initialize the graphical user interface
		RenderFrame userInterface;
		if (gui) {
			try {
				userInterface = RenderFrame.buildRenderFrame(renderer, gamma, sensitivity);
				reporter.addProgressListener(userInterface);
				renderer.addRenderEventListener(userInterface);
			} catch (Exception e) {
				e.getStackTrace();
			}
		}

		renderer.addRenderEventListener(reporter);

		/**********************************************************************
		 * Initialize the scene
		 *********************************************************************/

		final PerspectiveCamera camera = new PerspectiveCamera(origin,
				destination,width,height,fov);

		final Scene scene = new Scene();

		final MeshFactory factory = new MeshFactory();
		final TextureFactory textureFactory = new TextureFactory();

		renderer.setScene(scene);
		renderer.setCamera(camera);
		renderer.setIntegrator(new PathTracer());


		Transform3D objT = new Transform3D();
		Transform2D T = new Transform2D();
		Transform3D lightT = new Transform3D();

//		TriangleMesh mesh = factory.getTriangleMesh("helmets.obj");
//		BVH bvh = new BVH(objT,mesh,new Matte(
//				new Color(new RGBSpectrum(1,1,1)),
//				new Constant(1)));
//		bvh.buildAccelerationStructure();
//		scene.addGeometry(bvh);
//		mesh = factory.getTriangleMesh("soldiers.obj");
//		bvh = new BVH(objT,mesh,new Matte(
//				new Color(new RGBSpectrum(0,0,0)),
//				new Constant(1)));
//		bvh.buildAccelerationStructure();
//		scene.addGeometry(bvh);
//		mesh = factory.getTriangleMesh("box.obj");
//		bvh = new BVH(objT,mesh,new Matte(
//				new Color(new RGBSpectrum(0.1,0.1,0.1)),
//				new Constant(1)));
//		bvh.buildAccelerationStructure();
//		scene.addGeometry(bvh);
//
//		lightT  = new Transform3D();
//		lightT.scale(new Vector3D(0.2,1,4));
//		lightT.rotateY(-30);
//		lightT.rotateX(180);
//		lightT.translate(new Point3D(-1,3,0));
//		Emission emit = new Emission(new RGBSpectrum(0.8,1,1),4);
//		Quad lObjq = new Quad(lightT, emit);
//
//		scene.addGeometry(lObjq);
//		scene.addLight(new AreaLight(lObjq, emit));
//		scene.addLight(new EnvironmentLight(new RGBSpectrum(1)));




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


//		TriangleMesh mesh = factory.getTriangleMesh("teapot.obj");
//		objT = new Transform3D();
//		objT.rotateZ(-20);
//		objT.rotateY(-45);
//		objT.scale(1);
//		objT.translate(new Point3D(0,1,0));
//		BVH bvh = new BVH(objT,mesh,
//				new Glass(new Color(new RGBSpectrum(1)),new Color(new RGBSpectrum(1)),new Constant(1.5)));
//		bvh.buildAccelerationStructure();
//		scene.addGeometry(bvh);

		lightT  = new Transform3D();
		lightT.scale(new Vector3D(1));
		lightT.rotateX(180);
		lightT.translate(new Point3D(0,4.9999,1));
		Emission emit = new Emission(new RGBSpectrum(1),4);
		Quad lObjq = new Quad(lightT, emit);

		scene.addGeometry(lObjq);
		scene.addLight(new AreaLight(lObjq,emit));



//		TriangleMesh mesh = factory.getTriangleMesh("teapot.obj");
//		objT = new Transform3D();
//		objT.scale(1);
//		objT.rotateY(180);
//		objT.translate(new Point3D(-.5,0,0));
//		BVH bvh = new BVH(objT,mesh,
//				new Glass(new Color(new RGBSpectrum(1)),new Color(new RGBSpectrum(1)),new Constant(1.5)));
//		bvh.buildAccelerationStructure();
//		scene.addGeometry(bvh);
//
//		scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(0.6))));
//
//		objT = new Transform3D();
//		objT.rotateZ(90);
//		objT.translate(new Point3D(3,0,0));
//		scene.addGeometry(new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(0.6))));
//
//		lightT  = new Transform3D();
//		lightT.scale(new Vector3D(0.5));
//		lightT.rotateZ(-90);
//		lightT.translate(new Point3D(-3,0.5,0));
//		Emission emit = new Emission(new RGBSpectrum(1),5);
//		Quad lObjq = new Quad(lightT, emit);
//
//		scene.addGeometry(lObjq);
//		scene.addLight(new AreaLight(lObjq,emit));

	}

}
