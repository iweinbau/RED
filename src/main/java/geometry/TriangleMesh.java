package geometry;

import math.Normal;
import math.Point3D;

public class TriangleMesh {

    private Point3D[] vertices;
    private Normal[] normals;
    private int[] indices;

    private int numVer;
    private int numTris;

    public TriangleMesh(Point3D[] vertices, Normal[] normals, int[] indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.numVer = vertices.length;
        this.numTris = indices.length / 3;
    }

    public Point3D getVertices(int index) {
        return this.vertices[index];
    }

    public Normal getNormals(int index) {
        return this.normals[index];
    }

    public int[] getIndices() {
        return this.indices;
    }

    public int getNumVertices() {
        return this.numVer;
    }

    public int getNumTriangles() {
        return this.numTris;
    }
}
