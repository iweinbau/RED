package sampler;

import math.Point2D;

public class Uniform extends Sampler {

    int ndx;
    int ndy;

    double dx;
    double dy;

    public Uniform(int samplesPerPixel, long seed) {
        super(samplesPerPixel, seed);
        ndx = (int) Math.sqrt(samplesPerPixel);
        ndy = (int) Math.sqrt(samplesPerPixel);

        dx = 1./ndx;
        dy = 1./ndy;
    }

    public Uniform(int samplesPerPixel) {
        super(samplesPerPixel);
        ndx = (int) Math.sqrt(samplesPerPixel);
        ndy = (int) Math.sqrt(samplesPerPixel);

        dx = 1./ndx;
        dy = 1./ndy;
    }

    public double sample1D() {
        return random.nextDouble();
    }

    public Point2D sample2D() {
        return new Point2D(random.nextDouble(),random.nextDouble());
    }

    @Override
    public Point2D nextPixelSample() {
        int x = sampleIndex % ndx;
        int y = (sampleIndex - x) / ndx;

        if ( sampleIndex++ < samplesPerPixel) {
            return new Point2D((0.5 + x) * dx,(0.5 + y) * dy);
        }
        return null;
    }
}
