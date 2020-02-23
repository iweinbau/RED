package math;

import core.Constants;

public class Point2D extends Tuple<Double> {
    public Point2D(double x, double y) {
        super(x, y);
    }
    public Point2D(double x) {
        super(x, x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D)) return false;
        Point2D p = (Point2D) o;
        return  Math.abs(getX() - p.getX()) < Constants.kEps &&
                Math.abs(getY() - p.getY()) < Constants.kEps;
    }
}
