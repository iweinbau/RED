package math;

public interface ITransform {
    /**
     *
     * @param p
     */
    void translate(Point3D p);

    /**
     *
     * @param s
     */
    void scale(double s);

    /**
     *
     * @param s
     */
    void scale(Vector3D s);

    /**
     *
     * @param angle
     */
    void rotateX(double angle);

    /**
     *
     * @param angle
     */
    void rotateY(double angle);

    /**
     *
     * @param angel
     */
    void rotateZ(double angel);

    /**
     *
     * @param angle
     */
    void rotate(Vector3D angle);
}
