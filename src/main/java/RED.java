import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import camera.PerspectiveCamera;
import geometry.*;
import gui.ProgressReporter;
import gui.RenderFrame;
import integrator.DirectLightIntegrator;
import light.AreaLight;
import light.PointLight;
import material.Emission;
import material.Matte;
import math.Point3D;
import math.RGBSpectrum;
import math.Transform;
import math.Vector3D;
import parser.MeshFactory;
import renderer.Renderer;
import scene.Scene;
import textures.Color;
import textures.Constant;

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
		Point3D origin = new Point3D(0,2,2);
		Vector3D lookup = new Vector3D(0,1,0);
		Point3D destination = new Point3D(0, 1, 0);
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

		final Renderer renderer = new Renderer(1);

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

		renderer.setScene(scene);
		renderer.setCamera(camera);
		renderer.setIntegrator(new DirectLightIntegrator());

		Random rand = new Random();
		Transform objT = new Transform();

		// CUBE DEMO:
//		objT.rotate(new Vector3D(
//				90 * rand.nextDouble(),
//				90 * rand.nextDouble(),
//				90 * rand.nextDouble()));
//		Box s = new Box(objT,new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
//		scene.addGeometry(s);

//		// SPHERE DEMO
		objT = new Transform();
		objT.translate(new Point3D(0,-0.5,0));
		Plane p1 = new Plane(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1)));
		//scene.addGeometry(p1);

//		objT = new Transform();
//		objT.translate(new Point3D(0,1,0));
//		objT.scale(new Vector3D(2,0,0));
//		Sphere s = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
//		scene.addGeometry(s);

//		BVH bvh = new BVH(new Transform());
//		objT = new Transform();
//		objT.scale(new Vector3D(2,1,1));
//		//objT.translate(new Point3D(2,0,0));
//		Sphere s1 = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
//		bvh.addGeometry(s1);
//		objT = new Transform();
//		objT.translate(new Point3D(-1,0,0));
//		Sphere s2 = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1)));
//		//bvh.addGeometry(s2);
//		objT = new Transform();
//		objT.translate(new Point3D(2.5,0,0));
//		Sphere s3 = new Sphere(objT,new Matte(new Color(new RGBSpectrum(1)),new Constant(1)));
//		//bvh.addGeometry(s3);
//
//		bvh.buildAccelerationStructure();
//
//		scene.addGeometry(bvh);

		// TRIANGLE SPHERE DEMO SMOOTH
		MeshFactory factory = new MeshFactory();
		TriangleMesh mesh = factory.getTriangleMesh("armadillo.obj");
		objT = new Transform();
		objT.rotateY(-90);
		BVH comp = new BVH(objT,mesh,
				new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
		comp.buildAccelerationStructure();
		scene.addGeometry(comp);

		// TRIANGLE SPHERE DEMO FLAT
//		MeshFactory factory = new MeshFactory();
//		TriangleMesh mesh = factory.getTriangleMesh("sphere.obj");
//		objT = new Transform();
//		BVH comp = new BVH(objT,mesh,
//				new Matte(new Color(new RGBSpectrum(1,0,0)),new Constant(1)));
//		comp.buildAccelerationStructure();
//		scene.addGeometry(comp);

		Transform lightT = new Transform();
		lightT.rotateX(90);
		lightT.translate(new Point3D(0,2,-2));
		Emission emit = new Emission(new RGBSpectrum(0.5,0.5,0));
		Quad lObjq = new Quad(lightT, emit);

		//scene.addGeometry(lObjq);
		//scene.addLight(new AreaLight(lObjq, emit));

		lightT = new Transform();
		lightT.translate(new Point3D(2,2,3));
		emit = new Emission(new RGBSpectrum(2));
		Sphere lObjs = new Sphere(lightT,0.5,emit);

		//scene.addGeometry(lObjs);
		//scene.addLight(new AreaLight(lObjs,emit));

		lightT = new Transform();
		lightT.translate(new Point3D(2,3,3));
		scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

		lightT = new Transform();
		lightT.translate(new Point3D(0,3,3));
		scene.addLight(new PointLight(new RGBSpectrum(1),lightT));

		lightT = new Transform();
		lightT.translate(new Point3D(-2,2,3));
		scene.addLight(new PointLight(new RGBSpectrum(1,1,0),lightT));
	}
}
