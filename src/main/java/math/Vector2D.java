package math;

import core.Constants;

public class Vector2D extends Tuple<Double> {
    public Vector2D(Double x, Double y) {
        super(x, y);
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
