package math;

import core.Constants;

import java.io.Serializable;

public class RGBSpectrum extends Triple<java.lang.Double> {

    public static final RGBSpectrum BLACK = new RGBSpectrum(0f);
    public static final RGBSpectrum RED = new RGBSpectrum(1f,0f,0f);
    public static final RGBSpectrum GREEN = new RGBSpectrum(0f,1f,0f);
    public static final RGBSpectrum BLUE = new RGBSpectrum(0f,0f,1f);


    public RGBSpectrum(double x, double y, double z) {
        super(x, y, z);
    }

    public RGBSpectrum(int x, int y, int z) {
        super(x/255., y/255., z/255.);
    }

    public RGBSpectrum(double x) {
        super(x, x, x);
    }

    public RGBSpectrum(Triple<java.lang.Double> triple) {
        super(triple.x,triple.y,triple.z);
    }

    /**
     *
     * Returns a new RGBSpectrum which is the sum of this and the given RGBSpectrum
     *
     * @param color RGB spectrum to add
     *
     * @return a new spectrum which is the sum of the spectra.
     */
    public RGBSpectrum add(RGBSpectrum color) {
        return new RGBSpectrum(x + color.x, y + color.y, z + color.z);
    }

    /**
     *
     * return the sum of the r,g,b spectrum
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @return returns the sum of the components.
     */
    public RGBSpectrum add(double r,double g, double b) {
        return new RGBSpectrum(x + r, y + g, z + b);
    }

    /**
     *
     * @param factor
     * @return
     */
    public RGBSpectrum scale(double factor) {
        return new RGBSpectrum(x * factor, y* factor, z* factor);
    }

    /**
     *
     * @param factor
     * @return
     */
    public RGBSpectrum multiply(RGBSpectrum factor) {
        return new RGBSpectrum(x * factor.x, y * factor.y, z * factor.z);
    }

    /**
     *
     * @param low
     * @param high
     * @return
     */
    public RGBSpectrum clamp(double low, double high) {
        double r = Math.min(high, Math.max(low, x));
        double g = Math.min(high, Math.max(low, y));
        double b = Math.min(high, Math.max(low, z));
        return new RGBSpectrum(r, g, b);
    }

    /**
     *
     * @param power
     * @return
     */
    public RGBSpectrum pow(double power) {
        if (power == 1.0)
            return this;
        return new RGBSpectrum(Math.pow(x, power), Math.pow(y, power),
                Math.pow(z, power));
    }

    /**
     *
     * Returns a new RGBSpectrum which is the difference of this and the given RGBSpectrum
     *
     * @param color RGB spectrum to subtract
     *
     * @return a new spectrum which is the difference of the spectra.
     */
    public RGBSpectrum subtract(RGBSpectrum color) {
        return new RGBSpectrum(x - color.x, y - color.y, z - color.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RGBSpectrum)) return false;
        RGBSpectrum p = (RGBSpectrum) o;
        return  Math.abs(getX() - p.getX()) < Constants.kEps &&
                Math.abs(getY() - p.getY()) < Constants.kEps &&
                Math.abs(getZ() - p.getZ()) < Constants.kEps;
    }

    public int toRGB() {
        int r = Math.min(255, Math.max(0, (int) Math.round(x)));
        int g = Math.min(255, Math.max(0, (int) Math.round(y)));
        int b = Math.min(255, Math.max(0, (int) Math.round(z)));

        return (255 << 24) + (r << 16) + (g << 8) + b;
    }

    public static RGBSpectrum intToHeatMap(double value,int maxValue) {
        double a= (1. - (value/ (double)maxValue))/0.25;	//invert and group
        int X= (int) (a);	//this is the integer part
        double Y= a-X; //fractional part from 0 to 255
        double r,g,b;
        switch(X)
        {
            case 0: r=1;g=Y;b=0;break;
            case 1: r=1-Y;g=1;b=0;break;
            case 2: r=0;g=1;b=Y;break;
            case 3: r=0;g=1-Y;b=1;break;
            case 4: r=0;g=0;b=1;break;
            default: r=1;g=0;b=0;
        }

        return new RGBSpectrum(r,g,b);
    }
}
