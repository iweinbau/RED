package film;

import math.RGBSpectrum;

public class Pixel {

    /**
     * RGB spectrum of the pixel.
     */
    private RGBSpectrum rgbColor;

    /**
     * Weight factor of this pixel.
     */
    double pixelWeight;

    /**
     * Construct new black pixel
     */
    public Pixel() {
        this.rgbColor = RGBSpectrum.BLACK;
        this.pixelWeight = 0;
    }

    /**
     *
     * Add a given RGBSpectrum to this pixels rgbColor. This also increases the weight value with one.
     *
     * @param color the RGBSpectrum to add.
     */
    public void add(RGBSpectrum color){
        this.rgbColor = rgbColor.add(color);
        this.pixelWeight += 1;
    }

    /**
     *
     * Add a given RGBSpectrum to this pixels rgbColor.
     *
     * @param color the RGBSpectrum to add.
     */
    public void add(RGBSpectrum color,double weight){
        this.rgbColor = rgbColor.add(color);
        this.pixelWeight += weight;
    }

    /**
     * Set the pixel weight to the new value specified by weight.
     *
     * @param weight new Pixel weight.
     */
    public void addWeight(double weight) {
        this.pixelWeight += weight;
    }

    public void setRgbColor(RGBSpectrum color) {
        this.rgbColor = color;
        this.pixelWeight = 1;
    }

    /**
     *
     * Subtract a given RGBSpectrum to this pixels rgbColor.
     *
     * @param color the RGBSpectrum to add.
     */
    public void subtract(RGBSpectrum color){
        this.rgbColor = rgbColor.subtract(color);
        this.pixelWeight--;
    }

    /**
     * get spectrum value of this pixel. The result is the rgbColor divided by the weight factor.
     * @return RGBSpectrum
     */
    public RGBSpectrum getSpectrum() {
        if (pixelWeight == 0)
            return RGBSpectrum.BLACK;
        return this.rgbColor.scale(1/pixelWeight);
    }
}