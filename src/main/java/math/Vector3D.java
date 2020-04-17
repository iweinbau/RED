package math;

import core.Constants;

public class Vector3D extends Triple<java.lang.Double>{

    public Vector3D(double x, double y, double z) {
        super(x, y, z);
    }

    public Vector3D(double x) {
        super(x, x, x);
    }

    public Vector3D(Point3D p) {
        super(p.x,p.y,p.z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Vector3D add(double x, double y, double z){
        return new Vector3D(this.x + x, this.y + y,this.z + z);
    }

    /**
     *
     * @param v
     * @return
     */
    public Vector3D add(Vector3D v){
        return this.add(v.x, v.y,v.z);
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
    public Vector3D subtract(Vector3D v){
        return subtract(v.x, v.y,v.z);
    }

    /**
     *
     * @param factor
     * @return
     */
    public Vector3D scale(double factor){
        return new Vector3D(this.x*factor,this.y*factor, this.z*factor);
    }

    /**
     *
     * @param v
     * @return
     */
    public double dot(Vector3D v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public double maxDot(Vector3D v) {
        return Math.max(0,dot(v));
    }

    public double absDot(Vector3D v) {
        return Math.abs(this.dot(v));
    }


    public double dot(Point3D origin) {
        return this.dot(origin.toVector());
    }
    /**
     *
     * @param v
     * @return
     */
    public Vector3D cross(Vector3D v){
        double xx = this.y * v.z - this.z * v.y;
        double yy = this.z * v.x - this.x * v.z;
        double zz = this.x * v.y - this.y * v.x;

        return new Vector3D(xx, yy, zz);
    }

    /**
     *
     * @return
     */
    public double length(){
        return Math.sqrt(this.dot(this));
    }

    /**
     *
     * @return
     */
    public Vector3D normalize(){
        return scale(1/length());
    }

    /**
     *
     * @return
     */
    public Vector3D neg(){
        return this.scale(-1);
    }

    public Point3D toPoint(){
        return new Point3D(this);
    }

    public Normal toNormal() {
        return new Normal(this);
    }

    public Vector3D reflect(Normal normal) {
        return (this.neg().add(normal.scale(2 * normal.dot(this)))).normalize();
    }

    public Vector3D refract(Normal normal, double eta) {
        double cosThetaI = normal.dot(this);
        double sin2ThetaI = Math.max(0, 1 - cosThetaI * cosThetaI);
        double sin2ThetaT = eta * eta * sin2ThetaI;
        if( sin2ThetaT >= 1) {
            return null;
        }
        double cosThetaT = Math.sqrt( 1 - sin2ThetaT);

        return (this.neg().scale(eta).add(normal.scale(eta * cosThetaI - cosThetaT))).normalize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3D)) return false;
        Vector3D p = (Vector3D) o;
        return  Math.abs(getX() - p.getX()) < Constants.kEps &&
                Math.abs(getY() - p.getY()) < Constants.kEps &&
                Math.abs(getZ() - p.getZ()) < Constants.kEps;
    }

    public Vector3D inverse() {
        return new Vector3D(1/x,1/y,1/z);
    }
}
