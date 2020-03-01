package sampler;

import core.Constants;
import math.Point2D;
import math.Point3D;

import java.util.Random;

public class Sampler {

    Random random;

    public Sampler(long seed) {
        random = new Random(seed);
    }

    public Sampler() {
        random = new Random();
    }

    public double sample1D() {
        return random.nextDouble();
    }

    public Point2D sample2D() {
        return new Point2D(random.nextDouble(),random.nextDouble());
    }

    public static Point3D samplePointOnUnitSphere(Point2D sample) {

        double theta = 2 * Constants.PI * sample.getX();
        double phi = Math.acos(1 - 2 * sample.getY());
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);

        return new Point3D( x, y, z);
    }
}
