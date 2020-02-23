package math;

public class Matrix3 {
    private final double[][] matrix = new double[3][3];
    public static final Matrix3 IDENTITY = new Matrix3(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1);

    /**
     * Empty constructor
     */
    public Matrix3() {
    }

    /**
     * @param values
     */
    public Matrix3(double... values) {
        for (int i = 0; i < 9; ++i)
            set(i / 3, i % 3, values[i]);
    }

    /**
     * Copy constructor
     *
     * @param matrix
     */
    public Matrix3(Matrix3 matrix) {
        for (int row = 0; row < 3; ++row)
            for (int column = 0; column < 3; ++column)
                set(row, column, matrix.get(row, column));
    }

    public Matrix3(Vector3D col1, Vector3D col2, Vector3D col3) {
        for (int i = 0; i< 3; i++) {
            set(i,0,col1.get(i));
        }
        for (int i = 0; i< 3; i++) {
            set(i,1,col2.get(i));
        }
        for (int i = 0; i< 3; i++) {
            set(i,2,col3.get(i));
        }
    }

    /**
     *
     * @param mat
     * @return
     */
    public Matrix3 multiply(Matrix3 mat){
        Matrix3 result = new Matrix3();
        for (int i = 0; i<3; i++) {
            for (int j = 0; j<3; j++) {
                double value = 0;
                for (int k = 0; k < 3; ++k)
                    value += this.get(i, k) * mat.get(k, j);
                result.set(i, j, value);
            }
        }
        return result;
    }

    public Matrix3 scale(double scale){
        Matrix3 result = new Matrix3();
        for (int i = 0; i<3; i++) {
            for (int j = 0; j<3; j++) {
                result.set(i,j,this.get(i,j) * scale);
            }
        }
        return result;
    }

    public double det() {
        return matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
                - matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
                + matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
    }

    /**
     * @param row
     * @param col
     * @param el
     */
    public void set(int row, int col, double el) {
        matrix[row][col] = el;
    }

    /**
     * @param row
     * @param col
     * @return
     */
    public double get(int row, int col) {
        return matrix[row][col];
    }

    public Matrix3 setCol(int col,Vector3D values) {
        Matrix3 result = new Matrix3(this);
        for (int i = 0; i< 3; i++) {
            result.set(i,col,values.get(i));
        }
        return result;
    }
}
