package distributeRenderer;

import film.FrameBuffer;
import film.Pixel;
import film.Tile;
import film.ViewPlane;
import gui.ProgressReporter;
import gui.RenderFrame;
import math.RGBSpectrum;
import org.json.JSONArray;
import org.json.JSONObject;
import renderer.RenderEventInterface;
import renderer.RenderEventListener;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MasterRenderer {

    /**
     * Slave executable name
     */
    static String slaveExeName = "RED-1.0-SNAPSHOT.jar";

    /**
     * listeners that listen to an render event
     */
    static Collection<RenderEventListener> renderEventListeners = new LinkedList<>();

    /**
     *
     * Tiles that need to be rendered
     */
    static HashMap<Integer,Tile> renderQueue = new HashMap<>();

    /**
     * Frame buffer containing already rendered pixels.
     */
    static FrameBuffer frameBuffer;

    /**
     * Output file name
     */
    static String filename = "output.png";

    /**
     * Image dimensions
     */
    static int width = 200;
    static int height = 200;

    static int devision = 100;

    /**
     * number of attempts on a failed render task
     */
    static int renderAttempts = 5;


    public static void main(String[] arguments) throws IOException {

        /**
         *
         * Initialize variables with default values.
         *
         */
        double sensitivity = 1.0;
        double gamma = 2.2;
        boolean gui = true;
        boolean quiet = true;
        double fov = 90;

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
                    else if ("-output".equals(flag)) {
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


        /**
         *
         * Set up a progress reporter
         *
         */
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                width * height, quiet);

        addRenderEventListener(reporter);



        /**
         *
         *
         * Construct a viewplane, this is only used to subdivide the image.
         *
         */
        ViewPlane vp = new ViewPlane(width,height);

        /**
         * Subdivide viewplane and add the tiles to the render queue
         */
        int i = 0;
        for (Tile tile: vp.subdivide(devision,devision)) {
            renderQueue.put(i++,tile);
        }

        /**
         *
         * Construct output frame.
         *
         */
        frameBuffer = new FrameBuffer(width,height);

        /**
         *
         * Initialize render interface with custom renderEventInterface to start distribute rendering
         */
        RenderFrame userInterface;
        if (gui) {
            try {
                userInterface = RenderFrame.buildRenderFrame(new RenderEventInterface() {
                    @Override
                    public void startRender() {
                        start();
                    }

                    @Override
                    public void clearBuffers() {
                        clear();
                    }

                    @Override
                    public void stopRender() {
                        stop();
                    }
                }, gamma, sensitivity);

                reporter.addProgressListener(userInterface);

                addRenderEventListener(userInterface);

            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        /**
         * Setup buffer for all listeners
         */
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyBufferChange(frameBuffer);
        }

    }

    /**
     *
     * Prepare distributed render by generating batch file for all tiles in the render queue.
     */
    public static void prepareDistribution(){
        System.out.println("Prepare Renderer");
        try {
            BufferedWriter bashWriter = new BufferedWriter(new FileWriter("./batch_file.txt", false));
            String batchCommand = String.format("java -jar dependencies/%s",slaveExeName);
            for (Map.Entry<Integer,Tile> entry: renderQueue.entrySet()) {
                Tile tile = entry.getValue();
                bashWriter.write(String.format("%s -width %d -height %d -startX %d -startY %d -endX %d -endY %d",batchCommand,
                        width,height,tile.xStart,tile.yStart,tile.xEnd,tile.yEnd));
                bashWriter.newLine();
            }
            bashWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Ready Distribution");
    }

    /**
     * Start distributed renderer
     * @return Process of the distribution toolkit
     */
    public static Process startDistribution() {

        Process anet;

        try {
            anet = Runtime.getRuntime()
                    .exec(String.format("python3 anet/anet.py batch_file.txt build/libs/%s",slaveExeName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return anet;
    }

    /**
     *
     * Wait and Collect responses from process.
     * @param process Process of the distribution toolkit.
     */
    public static void collector(Process process) {

        System.out.println("Collecting Renderer");

        while (!renderQueue.isEmpty()){

            // Read header
            int request_id = readInt(process.getInputStream());
            if (request_id == -1) {
                // Nothing left, leave loop
                break;
            }

            int length = readInt(process.getInputStream());

            if (length == 0) {
                break;
            }

            try {

                renderQueue.remove(request_id);

                byte[] response = process.getInputStream().readNBytes(length);
                String string = new String(response,"UTF-8");
                JSONObject json = new JSONObject(string);

                int startX = json.getInt("startX");
                int startY = json.getInt("startY");
                int endX = json.getInt("endX");
                int endY = json.getInt("endY");

                JSONArray pixelArray = json.getJSONArray("pixels");

                Pixel[] pixels = new Pixel[pixelArray.length()];

                for (int i = 0; i < pixelArray.length(); i++) {
                    JSONObject pixel  = pixelArray.getJSONObject(i);
                    pixels[i] = new Pixel(
                            new RGBSpectrum(pixel.getDouble("r"),
                            pixel.getDouble("g"),
                            pixel.getDouble("b")),1);
                }

                frameBuffer.addTileToBuffer(startX,startY,
                        endX,endY,pixels);

                for (RenderEventListener listener:renderEventListeners) {
                    listener.finished(new Tile(startX,startY,endX,endY));
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        System.out.println();
        System.out.println("End Collecting");
    }

    /**
     *
     * Made by Wouter Baert
     * @param stream
     * @return integer value or -1 if something went wrong
     */
    private static int readInt(InputStream stream) {

        // Reads and returns a 4-byte little-endian integer from the given stream
        // Returns -1 if something went wrong (including EOF)
        byte[] bytes = new byte[4];
        try {
            if (stream.read(bytes) == -1) {
                return -1;
            } else {
                // Bytes were read successfully, parse and return value
                int value = 0;
                for(int i = 0; i < 4; i++)
                    value += (bytes[i] & 0xFF) << (8*i);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     *
     * Callback function when a start render event is called. Start render process by
     * 1) prepare distribution
     * 2) start distribution
     * 3) listen for responses
     *
     * when precess land in an error state and render queue is not empty start process for tiles in queue.
     *
     */
    public static void start() {

        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStartRender();
        }
        System.out.println("Starting Renderer");

        int attempts = 0;
        while (!renderQueue.isEmpty() && attempts++ < renderAttempts){
            System.out.println("attempt: "+ attempts+ " nodes left: " + renderQueue.size());
            prepareDistribution();
            Process process = startDistribution();
            if (process == null)
                return;
            collector(process);
        }

        stop();
        try {
            frameBuffer.writeBufferToImage(filename,1.0,2.2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function callback when a buffer clear event is called
     */
    public static void clear() {
        frameBuffer.clear();
    }

    public static void stop() {
        System.out.println("Stop Rendering");
        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStopRender();
        }

        try {
            Runtime.getRuntime().exec("python3 anet/anet.py --killall \"java -jar RED\" ");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not stop!");
        }
    }

    public static void addRenderEventListener(RenderEventListener listener){
        renderEventListeners.add(listener);
    }

    public static RenderResponse deserializeTile(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (RenderResponse) is.readObject();
    }
}
