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
    static int width = 400;
    static int height = 400;

    static int devision = 20;

    static double sensitivity = 1.0;
    static double gamma = 2.2;

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
        boolean quiet = false;
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
                                .println("Work in progress");
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


        int workToBeDone = 0;
        boolean startFromFile;

        /**
         * Check if file exist then we can start the the process from this file.
         */
        File file = new File("./batch_file.txt");
        boolean exists = file.exists();
        if( exists ){
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int tileIndex = 0;
            while ((line = reader.readLine()) != null) {
                String[] task = line.split(" ");
                int startX =0, startY =0, endX =0, endY =0;
                for (int i = 0; i < task.length; ++i) {
                    if (task[i].startsWith("-")) {
                        String flag = task[i];
                        if ("-width".equals(flag))
                            width = Integer.parseInt(task[++i]);
                        else if ("-height".equals(flag))
                            height = Integer.parseInt(task[++i]);
                        if ("-startX".equals(flag))
                            startX = Integer.parseInt(task[++i]);
                        else if ("-startY".equals(flag))
                            startY = Integer.parseInt(task[++i]);
                        else if ("-endX".equals(flag))
                            endX = Integer.parseInt(task[++i]);
                        else if ("-endY".equals(flag))
                            endY = Integer.parseInt(task[++i]);
                    }
                }
                renderQueue.put(tileIndex++,new Tile(startX,startY,endX,endY));
                workToBeDone += (endX -startX) * (endY -startY);
            }
            frameBuffer = FrameBuffer.loadFromImage(filename,gamma);
            startFromFile = true;
        } else {

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

            workToBeDone = width * height;
            startFromFile = false;
        }

        /**
         *
         * Set up progress reporter
         *
         */
        final ProgressReporter reporter = new ProgressReporter("Rendering", 40,
                workToBeDone, quiet);

        addRenderEventListener(reporter);

        /**
         *
         * Initialize render interface with custom renderEventInterface to start distribute rendering
         */
        RenderFrame userInterface;
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

            /**
             * Setup buffer for all listeners
             */
            for (RenderEventListener listener: renderEventListeners) {
                listener.notifyBufferChange(frameBuffer);
            }

            // Show already loaded tiles
            if(startFromFile) {
                userInterface.finished(new Tile(0,0,width,height));
            }

        } catch (Exception e) {
            e.getStackTrace();
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
                bashWriter.write(String.format("%s -width %d -height %d -startX %d -startY %d -endX %d -endY %d",
                        batchCommand, width, height, tile.xStart, tile.yStart, tile.xEnd, tile.yEnd));
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

        System.out.println("Collecting");

        while (!renderQueue.isEmpty()){

            // Read header
            int request_id = readInt(process.getInputStream());
            if (request_id == -1) {
                // Nothing left, leave loop
                break;
            }

            int length = readInt(process.getInputStream());

            if (length <= 0) {
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
            System.out.println("attempt: " + attempts + ", nodes left: " + renderQueue.size());
            prepareDistribution();
            Process process = startDistribution();
            if (process == null)
                return;
            collector(process);
        }

        done();
    }

    /**
     * Function callback when a buffer clear event is called
     */
    public static void clear() {
        frameBuffer.clear();
    }

    public static void stop() {
        System.out.println("Stop rendering!");

        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStopRender();
        }

        try {
            Runtime.getRuntime().exec("python3 anet/anet.py --killall \"java -jar RED\" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void done() {
        System.out.println("Done!");

        for (RenderEventListener listener: renderEventListeners) {
            listener.notifyStopRender();
        }

        try {
            frameBuffer.writeBufferToImage(filename,sensitivity,gamma);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addRenderEventListener(RenderEventListener listener){
        renderEventListeners.add(listener);
    }
}
