package math;

import core.Constants;
import textures.Constant;

public class Point3D extends Triple<java.lang.Double> {
    public Point3D(double x, double y, double z) {
        super(x, y, z);
    }
    public Point3D(double x) {
        super(x, x, x);
    }

    public Point3D(Vector3D v) {
        super(v.x,v.y,v.z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Point3D add(double x, double y, double z){
        return new Point3D(this.x + x, this.y + y,this.z + z);
    }

    /**
     *
     * @param v
     * @return
     */
    public Point3D add(Point3D v){
        return this.add(v.x, v.y,v.z);
    }

    public Point3D add(Vector3D v) {
        return add(v.x,v.y,v.z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Vector3D subtract(double x, double y, double z){
        return new Vector3D(this.x - x, this.y - y,this.z - z);
    }

    /**
     *
     * @param v
     * @return
     */
    public Vector3D subtract(Point3D v){
        return subtract(v.x, v.y,v.z);
    }

    /**
     *
     * @param factor
     * @return
     */
    public Point3D scale(double factor){
        return new Point3D(this.x*factor,this.y*factor, this.z*factor);
    }

    public Vector3D toVector(){
        return new Vector3D(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point3D)) return false;
        Point3D p = (Point3D) o;
        return  Math.abs(getX() - p.getX()) < Constants.kEps &&
                Math.abs(getY() - p.getY()) < Constants.kEps &&
                Math.abs(getZ() - p.getZ()) < Constants.kEps;
    }
}
