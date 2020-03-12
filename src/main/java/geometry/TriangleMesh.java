package geometry;

import math.Normal;
import math.Point2D;
import math.Point3D;

/**
 *
 * Class representing a triangle mesh.
 */
public class TriangleMesh {

    /**
     * vertices locations in local space.
     */
    private Point3D[] vertices;

    /**
     *
     */
    private Point2D[] uvs;

    /**
     * vertices normals in local space.
     */
    private Normal[] normals;

    /**
     * indices array.
     */
    private int[] indices;

    /**
     * number of vertices.
     */
    private int numVer;

    /**
     * Number of triangles.
     */
    private int numTris;

    /**
     * Construct new triangle mesh from vertices, normals and indices.
     * @param vertices vertices array.
     * @param normals normals array.
     * @param indices indices array.
     */
    public TriangleMesh(Point3D[] vertices, Point2D[] uvs, Normal[] normals, int[] indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.uvs = uvs;
        this.numVer = vertices.length;
        this.numTris = indices.length / 3;
    }

    /**
     * get vertex at index.
     * @param index the index in vertices array.
     * @return Point3D
     */
    public Point3D getVertices(int index) {
        return this.vertices[index];
    }

    /**
     * get uv at index.
     * @param index the index in uv array.
     * @return Point2D
     */
    public Point2D getUv(int index) {
        return this.uvs[index];
    }

    /**
     * get normal at index.
     * @param index the index of the normal.
     * @return Normal.
     */
    public Normal getNormals(int index) {
        return this.normals[index];
    }

    /**
     * Get indices arrary
     * @return int[]
     */
    public int[] getIndices() {
        return this.indices;
    }

    /**
     * Get number of vertices in this mesh.
     * @return int
     */
    public int getNumVertices() {
        return this.numVer;
    }

    /**
     *  Get number of triangles in this mesh.
     * @return int
     */
    public int getNumTriangles() {
        return this.numTris;
    }
}
