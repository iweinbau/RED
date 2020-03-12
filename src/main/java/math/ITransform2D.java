package math;

public interface ITransform2D {
    /**
     *
     * @param p
     */
    void translate(Point2D p);

    /**
     *
     * @param s
     */
    void scale(double s);

    /**
     *
     * @param s
     */
    void scale(Vector2D s);

    /**
     *
     * @param angle
     */
    void rotate(double angle);
}
