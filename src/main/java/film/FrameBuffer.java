package film;

import math.RGBSpectrum;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Locale;

/**
 * Class representing collection of pixel values.
 */
public class FrameBuffer {

    /**
     * FrameBuffer width.
     */
    public final int bufferWidth;
    /**
     * FrameBuffer height.
     */
    public final int bufferHeight;
    /**
     * FrameBuffer pixel values.
     */
    private Pixel[][] pixels;

    /**
     *
     * Create new FrameBuffer object to store Pixels data.
     *
     * @param width the width of the frame buffer.
     * @param height the height of the frame buffer.
     * @throws IllegalArgumentException if the height of width is smaller or equal than zero.
     */
    public FrameBuffer(int width, int height) throws IllegalArgumentException {
        if(width <= 0)
            throw new IllegalArgumentException("Width must be larger then 0!");
        if(height <= 0)
            throw new IllegalArgumentException("Height must be larger then 0!");

        this.bufferHeight = height;
        this.bufferWidth = width;

        pixels = new Pixel[height][width];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                pixels[i][j] = new Pixel();
            }
        }
    }

    public static FrameBuffer loadFromImage(String filename, double gamma) throws IOException {
        BufferedImage imageBuffer = ImageIO.read(new File(filename));
        FrameBuffer buffer = new FrameBuffer(imageBuffer.getWidth(),imageBuffer.getHeight());
        for(int i = 0; i < buffer.getBufferWidth(); i++) {
            for (int j = 0 ; j < buffer.getBufferHeight(); j++) {
                int pixel = imageBuffer.getRGB(i,j);
                double r = ((pixel & 0xff0000) >> 16) / 255.;
                double g = ((pixel & 0xff00) >> 8) / 255.;
                double b = ((pixel & 0xff)) / 255.;
                buffer.addPixel(buffer.getBufferHeight()-j-1,i,new RGBSpectrum(Math.pow(r,gamma),Math.pow(g,gamma),Math.pow(b,gamma)));
            }
        }
        return buffer;
    }

    /**
     *
     * Return the Pixel at given position.
     *
     * @param height height index
     * @param width row index
     * @return
     */
    public Pixel getPixel(int height, int width){
        return pixels[height][width];
    }

    /**
     *
     * Add RGB spectrum to the pixel at height height and width width.
     *
     * @param height height index of the pixel.
     * @param width width index of the pixel.
     * @param color the RGBSpectrum which will be added to the given pixel.
     */
    public void addPixel(int height, int width, RGBSpectrum color){
        pixels[height][width].add(color);
    }

    /**
     *
     * Add RGB spectrum to the pixel at height height and width width.
     *
     * @param height height index of the pixel.
     * @param width width index of the pixel.
     * @param color the RGBSpectrum which will be added to the given pixel.
     */
    public void addPixel(int height, int width, RGBSpectrum color, int weight){
        pixels[height][width].add(color,weight);
    }

    public void addBufferWeight(double weight) {
        for (int i = 0; i < this.bufferHeight; i++){
            for (int j = 0; j < this.bufferWidth; j++){
                pixels[i][j].addWeight(weight);
            }
        }
    }

    /**
     *
     * Write this buffer to PNG file.
     *
     * @param fileName output filename
     * @param sensitivity image sensitivity
     * @param gamma gamma correction.
     * @throws IOException
     */
    public void writeBufferToImage(String fileName, double sensitivity, double gamma) throws IOException {
        double invSensitivity = 1.0 / sensitivity;
        double invGamma = 1.0 / gamma;

        BufferedImage image = new BufferedImage(bufferWidth, bufferHeight,
                BufferedImage.TYPE_INT_ARGB);

        WritableRaster raster = image.getRaster();
        DataBufferInt rasterBuffer = (DataBufferInt) raster.getDataBuffer();
        int[] rasterData = rasterBuffer.getData();

        for (int y = 0; y < bufferHeight; ++y) {
            int yOffset = (bufferHeight - y - 1) * bufferWidth;

            for (int x = 0; x < bufferWidth; ++x) {
                Pixel pixel = getPixel(y,x);
                RGBSpectrum spectrum = pixel.getSpectrum();

                int rgb = spectrum.clamp(0, invSensitivity).scale(sensitivity)
                        .pow(invGamma).scale(255).toRGB();

                rasterData[x + yOffset] = rgb;
            }
        }
        ImageIO.write(image, "png", new File(fileName));
    }

    /**
     *
     * Write this buffer to an BufferedImage
     * @param sensitivity image sensitivity
     * @param gamma gamma correction.
     * @return new BufferedImage.
     */
    public BufferedImage toBufferedImage(double sensitivity, double gamma) {

        double invSensitivity = 1.0 / sensitivity;
        double invGamma = 1.0 / gamma;

        BufferedImage image = new BufferedImage(bufferWidth, bufferHeight,
                BufferedImage.TYPE_INT_ARGB);

        WritableRaster raster = image.getRaster();
        DataBufferInt rasterBuffer = (DataBufferInt) raster.getDataBuffer();
        int[] rasterData = rasterBuffer.getData();

        for (int y = 0; y < bufferHeight; ++y) {
            int yOffset = (bufferHeight - y - 1) * bufferWidth;

            for (int x = 0; x < bufferWidth; ++x) {
                Pixel pixel = getPixel(y,x);
                RGBSpectrum spectrum = pixel.getSpectrum();

                int rgb = spectrum.clamp(0, invSensitivity).scale(sensitivity)
                        .pow(invGamma).scale(255).toRGB();

                rasterData[x + yOffset] = rgb;
            }
        }
        return image;
    }

    /**
     * Returns the width of the buffer in pixels.
     * @return int
     */
    public int getBufferWidth() {
        return bufferWidth;
    }

    /**
     * Returns the height of the buffer in pixels.
     * @return
     */
    public int getBufferHeight() {
        return bufferHeight;
    }

    /**
     * Clear all pixels in this buffer.
     */
    public void clear() {
        for (int i = 0; i < this.bufferHeight; i++){
            for (int j = 0; j < this.bufferWidth; j++){
                pixels[i][j] = new Pixel();
            }
        }
    }

    public void rgbToGrayMap(double maxDepth) {
        for (int i = 0; i < this.bufferHeight; i++){
            for (int j = 0; j < this.bufferWidth; j++){
                pixels[i][j].setRgbColor(new RGBSpectrum(1.).subtract(pixels[i][j].getSpectrum().scale(1./maxDepth)));
            }
        }
    }

    public void rgbToHeatMap(int maxIntersections) {
        for (int i = 0; i < this.bufferHeight; i++){
            for (int j = 0; j < this.bufferWidth; j++){
                pixels[i][j].setRgbColor(
                        RGBSpectrum.intToHeatMap(pixels[i][j].getSpectrum().get(0),maxIntersections));
            }
        }
    }

    public void addTileToBuffer(int startX, int startY, int endX, int endY, Pixel[] buffer) {
        int k = 0;
        for (int i = startY; i < endY; i++) {
            for (int j = startX; j < endX; j++) {
                pixels[i][j] = buffer[k++];
            }
        }
    }

    public Pixel[][] getPixels() {
        return this.pixels;
    }

    public void rawBufferToFile(String fileName) throws IOException {

        BufferedWriter rBufferedWriter = new BufferedWriter(
                new FileWriter(String.format("%s_r.txt",fileName), false));
        BufferedWriter gBufferedWriter = new BufferedWriter(
                new FileWriter(String.format("%s_g.txt",fileName), false));
        BufferedWriter bBufferedWriter = new BufferedWriter(
                new FileWriter(String.format("%s_b.txt",fileName), false));

        for (int y = 0; y < bufferHeight; ++y) {
            for (int x = 0; x < bufferWidth; ++x) {
                Pixel pixel = getPixel(y,x);
                RGBSpectrum spectrum = pixel.getSpectrum();
                rBufferedWriter.write(
                        String.format(Locale.US,"%f8 ",
                                (spectrum.getX())));
                gBufferedWriter.write(
                        String.format(Locale.US,"%f8 ",
                                (spectrum.getY())));
                bBufferedWriter.write(
                        String.format(Locale.US,"%f8 ",
                                (spectrum.getZ())));
            }
            rBufferedWriter.newLine();
            gBufferedWriter.newLine();
            bBufferedWriter.newLine();

        }

        rBufferedWriter.close();
        gBufferedWriter.close();
        bBufferedWriter.close();
    }
}
