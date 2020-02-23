package math;

import core.Constants;

public class Normal extends Vector3D {
    public Normal(double x, double y, double z) {
        super(x, y, z);
    }
    public Normal(double x) {
        super(x,x,x);
    }

    public Normal(Vector3D v) {
        super(v.x,v.y,v.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Normal)) return false;
        Normal p = (Normal) o;
        return  Math.abs(getX() - p.getX()) < Constants.kEps &&
                Math.abs(getY() - p.getY()) < Constants.kEps &&
                Math.abs(getZ() - p.getZ()) < Constants.kEps;
    }

    /**
     *
     * @param factor
     * @return
     */
    public Normal scale(double factor){
        return new Normal(this.x*factor,this.y*factor, this.z*factor);
    }

    /**
     *
     * @param n
     * @return
     */
    public Normal add(Normal n){
        return new Normal(this.x + n.x, this.y + n.y, this.z + n.z);
    }
}
