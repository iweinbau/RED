package gui;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import film.FrameBuffer;
import film.Tile;
import renderer.RenderEventInterface;
import renderer.RenderEventListener;

/**
 * A frame which display the progress of a render.
 * 
 * @author 	CGRG
 * @version 4.0.0
 */
public class RenderFrame extends JFrame implements ProgressListener, RenderEventListener {
	
	/**
	 * A unique id required for serialization (required by the Serializable
	 * interface which JFrame implements).
	 */
	private static final long serialVersionUID = -2141536191366207069L;

	/**
	 * The menu bar for the graphical user interface.
	 */
	private final Menubar menu;

	private final RenderTab renderTab;

	private final SceneTab sceneTab;

	/**
	 * Creates a user interface which shows the progress of the render which is
	 * stored in the given frame buffer. The progress will be visualized as an
	 * image, tone mapped with the given sensitivity and gamma.
	 * 
	 * This method makes sure that all the components of the user interface are
	 * created and started on the AWT event dispatching thread.
	 * 
	 * @param renderer
	 *            the frame buffer which stores the rendered image.
	 * @param gamma
	 *            the gamma exponent to tone map the image with.
	 * @param sensitivity
	 *            the sensitivity to scale the image with.
	 * @throws NullPointerException
	 *             when the given frame buffer is null.
	 * @throws IllegalArgumentException
	 *             when the sensitivity is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the sensitivity is either infinite or NaN.
	 * @throws IllegalArgumentException
	 *             when the gamma is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the gamma is either infinite or NaN.
	 */
	private RenderFrame(RenderEventInterface renderer, double sensitivity, double gamma)
			throws NullPointerException {
		super("CG Project");

		if (renderer == null)
			throw new IllegalArgumentException(
					"the given frame buffer is null!");

		JTabbedPane tabs = new JTabbedPane();

		renderTab = new RenderTab(renderer,sensitivity,gamma);
		sceneTab = new SceneTab(renderer);

		tabs.addTab("Render Tab", renderTab);

		tabs.addTab("Scene Tab", sceneTab);

		// Create the menu bar
		menu = new Menubar(renderTab);
		setJMenuBar(menu);

		add(tabs);

		// Determine the size and center
		pack();
		center();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Show the user interface.
		setVisible(true);
	}

	/**
	 * Centers this frame on the first monitor encountered.
	 */
	public void center() {
		GraphicsEnvironment environment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();

		GraphicsDevice[] devices = environment.getScreenDevices();
		if (devices.length == 0)
			return;
		center(devices[0]);
	}

	/**
	 * Centers this frame on the given graphics device.
	 * 
	 * @param device
	 *            the device to center this {@link RenderFrame} on.
	 * @throws NullPointerException
	 *             when the given device is null.
	 */
	public void center(GraphicsDevice device) throws NullPointerException {
		if (device == null)
			throw new NullPointerException(
					"the graphics device to center this image frame upon!");

		Rectangle r = device.getDefaultConfiguration().getBounds();

		int x = r.x + (r.width - getWidth()) / 2;
		int y = r.y + (r.height - getHeight()) / 2;

		setLocation(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.ProgressListener#update(double)
	 */
	@Override
	public void update(double progress) {
		renderTab.setProgress(progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.ProgressListener#finished()
	 */
	@Override
	public void finished() {
		renderTab.setProgress(1.0);
	}

	/**
	 * Creates a user interface which shows the progress of the render which is
	 * stored in the given frame buffer. The progress will be visualized as an
	 * image, tone mapped with the given sensitivity and gamma.
	 * 
	 * This method makes sure that all the components of the user interface are
	 * created and started on the AWT event dispatching thread.
	 * 
	 * @param renderer
	 *            the frame buffer which stores the rendered image.
	 * @param gamma
	 *            the gamma exponent to tone map the image with.
	 * @param sensitivity
	 *            the sensitivity to scale the image with.
	 * @throws InvocationTargetException
	 *             when an exception occurs during the construction of the
	 *             graphical user interface.
	 * @throws InterruptedException
	 *             when an interruption occurs while we are waiting for the AWT
	 *             event dispatching thread to schedule the thread which creates
	 *             the graphical user interface.
	 * @return a graphical user interface which shows the progress of the
	 *         render.
	 */
	public static RenderFrame buildRenderFrame(RenderEventInterface renderer,
											   double gamma, double sensitivity) throws InvocationTargetException,
			InterruptedException {
		RenderFrameThread thread = new RenderFrameThread(renderer, gamma,
				sensitivity);
		SwingUtilities.invokeAndWait(thread);
		return thread.getRenderFrame();
	}

	@Override
	public void finished(Tile tile) {
		renderTab.panel.finished(tile);
	}

	@Override
	public void notifyBufferChange(FrameBuffer buffer) {
		renderTab.panel.notifyBufferChange(buffer);

		// Determine the size and center
		pack();
		center();
	}

	@Override
	public void notifyStartRender() {
		renderTab.setRendering(true);
	}

	@Override
	public void notifyStopRender() {
		renderTab.setRendering(false);
	}

	/**
	 * A class which allows us to construct the user interface in a separate
	 * thread.
	 * 
	 * The Java Swing library requires us to build all the components of the
	 * graphical user interface on the AWT event dispatching thread.
	 * 
	 * The run method of this class constructs the graphical user interface. To
	 * create the user interface on the AWT event dispatching thread, an
	 * instance of this class has to be run using
	 * {@link SwingUtilities#invokeLater(Runnable)} or
	 * {@link SwingUtilities#invokeAndWait(Runnable)}).
	 * 
	 * @author Niels Billen
	 * @version 0.3
	 */
	private static class RenderFrameThread implements Runnable {
		/**
		 * Reference to the user interface which will be constructed in this
		 * {@link Runnable#run()} method.
		 */
		private RenderFrame frame;

		/**
		 * The frame buffer to construct the user interface for.
		 */
		private final RenderEventInterface renderer;

		/**
		 * The initial gamma value to display the render with.
		 */
		private final double gamma;

		/**
		 * The initial sensitivity value to display the render with.
		 */
		private final double sensitivity;

		/**
		 * Constructs a new runnable which builds a graphical user interface
		 * which shows the progress of the render which is stored in the given
		 * frame buffer. The progress will be visualized as an image, tone
		 * mapped with the given sensitivity and gamma.
		 * 
		 * @param renderer
		 *            the frame buffer which stores the rendered image.
		 * @param gamma
		 *            the gamma exponent to tone map the image with.
		 * @param sensitivity
		 *            the sensitivity to scale the image with.
		 */
		public RenderFrameThread(RenderEventInterface renderer, double gamma,
								 double sensitivity) {
			this.renderer = renderer;
			this.gamma = gamma;
			this.sensitivity = sensitivity;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			frame = new RenderFrame(renderer, sensitivity, gamma);
		}

		/**
		 * Returns the user interface which has been constructed by this thread.
		 * 
		 * @return the user interface which has been constructed by this thread.
		 */
		public RenderFrame getRenderFrame() {
			return frame;
		}
	}
}
