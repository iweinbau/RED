package sampler;


import math.Point2D;

public class Stratified extends Sampler {

    int ndx;
    int ndy;

    double dx;
    double dy;

    public Stratified(int samplesPerPixel, long seed) {
        super(samplesPerPixel,seed);
        ndx = (int) Math.sqrt(samplesPerPixel);
        ndy = (int) Math.sqrt(samplesPerPixel);

        dx = 1./ndx;
        dy = 1./ndy;
    }

    public Stratified(int samplesPerPixel) {
        super(samplesPerPixel);
        ndx = (int) Math.sqrt(samplesPerPixel);
        ndy = (int) Math.sqrt(samplesPerPixel);

        dx = 1./ndx;
        dy = 1./ndy;
    }

    @Override
    public double sample1D() {
        return random.nextDouble();
    }

    @Override
    public Point2D sample2D() {
        return new Point2D(random.nextDouble(),random.nextDouble());
    }

    @Override
    public Point2D nextPixelSample() {
        int x = sampleIndex % ndx;
        int y = (sampleIndex - x) / ndx;

        if ( sampleIndex++ < samplesPerPixel) {
            return new Point2D((random.nextDouble() + x) * dx,(random.nextDouble() + y) * dy);
        }
        return null;
    }
}
