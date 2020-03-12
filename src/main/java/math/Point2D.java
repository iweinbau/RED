package math;

import core.Constants;

public class Point2D extends Tuple<Double> {
    public Point2D(double x, double y) {
        super(x, y);
    }
    public Point2D(double x) {
        super(x, x);
    }

    public Point2D() {
        super(0.,0.);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D)) return false;
        Point2D p = (Point2D) o;
        return  Math.abs(getX() - p.getX()) < Constants.kEps &&
                Math.abs(getY() - p.getY()) < Constants.kEps;
    }

    public Point2D scale(double s) {
       return new Point2D(x * s, y *s);
    }

    public Point2D add(Point2D p) {
        return new Point2D(x + p.x, y + p.y);
    }
}
