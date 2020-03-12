package sampler;

import core.Constants;
import math.Point2D;
import math.Point3D;

import java.awt.*;
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

    public static Point3D samplePointOnHemisphere(Point2D sample) {

        double cos_phi = Math.cos(2.0 * Constants.PI * sample.getX());
        double sin_phi = Math.sin(2.0 * Constants.PI * sample.getX());
        double cos_theta = (1.0 - sample.getY());
        double sin_theta = Math.sqrt (1.0 - cos_theta * cos_theta);

        double pu = sin_theta * cos_phi;
        double pv = sin_theta * sin_phi;
        double pw = cos_theta;

        return new Point3D(pu,pv,pw);
    }

    public static Point3D samplePointOnUnitSphere(Point2D sample) {

        double theta = 2 * Constants.PI * sample.getX();
        double phi = Math.acos(1 - 2 * sample.getY());
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);

        return new Point3D( x, y, z);
    }

    public static Point2D samplePointOnUnitDisk(Point2D sample) {
        double theta = 2 * Constants.PI * sample.getX();
        double radius = sample.getY();

        return new Point2D(Math.sin(theta) * radius,Math.cos(theta) * radius);
    }

    public static Point2D uniformSampleTriangle(Point2D sample) {
        double s = Math.sqrt(sample.getX());
        return new Point2D(1 - s, s * sample.getY());
    }
}
