package math;

import java.io.Serializable;
import java.util.Objects;

public abstract class Triple<T extends Serializable> implements Serializable {

    final T x;
    final T y;
    final T z;

    public Triple(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public T getZ() {
        return z;
    }

    public T get(int index) throws IllegalArgumentException {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            default:
                throw new IllegalArgumentException("Index out of bounds.");
        }
    }

    public boolean isZero() {
        return this.equals(new Vector3D(0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        Triple<?> triple = (Triple<?>) o;
        return  getX().equals(triple.getX()) &&
                getY().equals(triple.getY()) &&
                getZ().equals(triple.getZ());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }
}
