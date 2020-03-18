package parser;

import math.Point3D;
import math.Vector3D;

public class Vertex {

    int index;
    int normalIndex = -1;
    int uvIndex = -1;
    Point3D position;

    boolean hasUv = false;

    Vertex duplicate = null;

    public Vertex(Point3D point, int index) {
        this.position = point;
        this.index = index;
    }

    public void setNormalIndex(int index) {
        this.normalIndex = index;
    }

    public void setUvIndex(int index) {
        this.uvIndex = index;
    }

    public boolean isSet() {
        return normalIndex != -1 && (uvIndex != -1 || !hasUv);
    }

    public boolean hasSameUv(int uvIndex) {
        return this.uvIndex == uvIndex || !hasUv;
    }

    public boolean hasSameNormal(int normalIndex) {
        return this.normalIndex == normalIndex;
    }

    public Vertex getDuplicate() {
        return this.duplicate;
    }

    public void setDuplicate(Vertex duplicate) {
        this.duplicate = duplicate;
    }

    public void setUv(boolean b) {
        hasUv = b;
    }
}
