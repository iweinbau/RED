package math;

public class Matrix4 {
    private final double[][] matrix = new double[4][4];
    public static final Matrix4 IDENTITY = new Matrix4(
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1);

    /**
     * Empty constructor
     */
    public Matrix4() {}

    /**
     *
     * @param values
     */
    public Matrix4(double... values){
        for (int i = 0; i < 16; ++i)
            set(i / 4, i % 4, values[i]);
    }

    /**
     *
     * Copy constructor
     *
     * @param matrix
     */
    public Matrix4(Matrix4 matrix){
        for (int row = 0; row < 4; ++row)
            for (int column = 0; column < 4; ++column)
                set(row, column, matrix.get(row, column));
    }

    /**
     *
     * @param mat
     * @return
     */
    public Matrix4 multiply(Matrix4 mat){
        Matrix4 result = new Matrix4();
        for (int i = 0; i<4; i++) {
            for (int j = 0; j<4; j++) {
                double value = 0;
                for (int k = 0; k < 4; ++k)
                    value += this.get(i, k) * mat.get(k, j);
                result.set(i, j, value);
            }
        }
        return result;
    }

    /**
     *
     * @param p
     * @return
     */
    public Point3D multiply(Point3D p){
        double x = get(0, 0) * p.x + get(0, 1) * p.y +
                get(0, 2) * p.z + get(0, 3);
        double y = get(1, 0) * p.x + get(1, 1) * p.y +
                get(1, 2) * p.z + get(1, 3);
        double z = get(2, 0) * p.x + get(2, 1) * p.y +
                get(2, 2) * p.z + get(2, 3);
        double w = get(3, 0) * p.x + get(3, 1) * p.y +
                get(3, 2) * p.z + get(3, 3);
        double invW = 1.0f / w;

        return new Point3D(x * invW,y * invW,z * invW);
    }

    /**
     *
     * @param v
     * @return
     */
    public Vector3D multiply(Vector3D v){
        double x = get(0, 0) * v.x + get(0, 1) * v.y +
                get(0, 2) * v.z;
        double y = get(1, 0) * v.x + get(1, 1) * v.y +
                get(1, 2) * v.z;
        double z = get(2, 0) * v.x + get(2, 1) * v.y +
                get(2, 2) * v.z;

        return new Vector3D(x,y ,z);
    }

    public Normal multiply(Normal n){
        double x = get(0, 0) * n.x + get(0, 1) * n.y +
                get(0, 2) * n.z;
        double y = get(1, 0) * n.x + get(1, 1) * n.y +
                get(1, 2) * n.z;
        double z = get(2, 0) * n.x + get(2, 1) * n.y +
                get(2, 2) * n.z;

        return new Normal(x,y ,z);
    }

    public Matrix4 scale(double scale){
        Matrix4 result = new Matrix4();
        for (int i = 0; i<4; i++) {
            for (int j = 0; j<4; j++) {
                result.set(i,j,this.get(i,j) * scale);
            }
        }
        return result;
    }

    public Matrix4 transpose() {
        Matrix4 result = new Matrix4();
        for (int row = 0; row < 4; ++row)
            for (int column = 0; column < 4; ++column)
                result.set(column, row, get(row, column));
        return result;
    }

    /**
     *
     * @param row
     * @param col
     * @param el
     */
    public void set(int row, int col,double el){
        matrix[row][col] = el;
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    public double get(int row, int col){
        return matrix[row][col];
    }

}
