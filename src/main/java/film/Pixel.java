package film;

import math.RGBSpectrum;

public class Pixel {

    private RGBSpectrum rgbColor;
    double pixelWeight;

    public Pixel() {
        this.rgbColor = RGBSpectrum.BLACK;
    }

    /**
     *
     * Add a given RGBSpectrum to this pixels rgbColor.
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

    public void setWeight(double weight) {
        this.pixelWeight = weight;
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

    public RGBSpectrum getSpectrum() {
        if (pixelWeight == 0)
            return RGBSpectrum.BLACK;
        return this.rgbColor.scale(1/pixelWeight);
    }
}