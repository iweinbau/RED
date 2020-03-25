package sampler;


import math.Point2D;

public class Random extends Sampler {


    public Random(int samplesPerPixel, long seed) {
        super(samplesPerPixel, seed);
    }

    public Random(int samplesPerPixel) {
        super(samplesPerPixel);
    }

    public Random() {
        super(1);
    }

    public double sample1D() {
        return random.nextDouble();
    }

    public Point2D sample2D() {
        return new Point2D(random.nextDouble(),random.nextDouble());
    }

    @Override
    public Point2D nextPixelSample() {
        if ( sampleIndex++ < samplesPerPixel) {
            return sample2D();
        }
        return null;
    }
}
