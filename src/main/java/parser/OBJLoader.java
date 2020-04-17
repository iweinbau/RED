package parser;

import geometry.TriangleMesh;
import math.Normal;
import math.Point2D;
import math.Point3D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Class specific for loading .OBJ file format.
 */
public class OBJLoader extends MeshLoader {

    /**
     * Vertices in the obj file.
     */
    List<Vertex> vertices = new ArrayList<>();

    /**
     * Normals in obj file.
     */
    List<Normal> normals = new ArrayList<>();

    /**
     * UVs in obj file;
     */
    List<Point2D> uvs = new ArrayList<>();

    /**
     * Indices array.
     */
    List<Integer> indices = new ArrayList<>();

    /**
     * Construct new OBJLoader object with given resource file.
     * @param fileName
     */
    public OBJLoader(String fileName) {
        super(fileName);
    }

    /**
     * Load mesh from file into a Triangle Mesh.
     * @return TriangleMesh
     */
    @Override
    TriangleMesh loadMesh() {
            try {
                loadResource();

                String line;
                while ((line = getNextLine()) != null) {

                    // New Vertex.
                    if (line.startsWith("v ")) {
                        String[] vertex = line.split( " ");
                        Point3D point = new Point3D(Double.valueOf(vertex[1]),
                                                    Double.valueOf(vertex[2]),
                                                    Double.valueOf(vertex[3]));

                        vertices.add(new Vertex(point,vertices.size()));
                    }

                    // New Normal;
                    if (line.startsWith("vn ")) {
                        String[] n = line.split( " ");
                        Normal normal = new Normal(Double.valueOf(n[1]),
                                Double.valueOf(n[2]),
                                Double.valueOf(n[3]));

                        normals.add(normal);
                    }

                    // New UV
                    if (line.startsWith("vt ")) {
                        String[] uvString = line.split( " ");
                        Point2D uv = new Point2D(Double.valueOf(uvString[1]),
                                Double.valueOf(uvString[2]));

                        uvs.add(uv);
                    }

                    // New face
                    if (line.startsWith("f ")) {
                        String[] face = line.split(" ");

                        List<String> faces = new LinkedList<>(triangulateFace(face));
                        for (int i = 0; i < faces.size(); i+=3) {
                            processTriangle(
                                    faces.get(i).split("/"),
                                    faces.get(i+1).split("/"),
                                    faces.get(i+2).split("/"));
                        }

                    }

                }

                closeBufferStream();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        // Extract data and create triangle mesh
        Point3D[] verticesArray = new Point3D[vertices.size()];
        Point2D[] texturesArray = new Point2D[vertices.size()];
        Normal[] normalsArray = new Normal[vertices.size()];

        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i<indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = vertices.get(i).position;
            if (vertices.get(i).normalIndex == -1)
                normalsArray[i] = normals.get(0);
            else
                normalsArray[i] = normals.get(vertices.get(i).normalIndex);
            if (vertices.get(i).hasUv)
                texturesArray[i] = uvs.get(vertices.get(i).uvIndex);
        }

        return new TriangleMesh(verticesArray,texturesArray,normalsArray,indicesArray);
    }

    /**
     * Triangulate faces.
     * @param face
     * @return
     */
    private Collection<String> triangulateFace(String[] face) {
        int numVertices = face.length - 1;
        List<String> vertices = new ArrayList<>();
        for(int i = 1; i<= numVertices - 2; i++) {
            vertices.add(face[1]);
            vertices.add(face[i+1]);
            vertices.add(face[i+2]);
        }
        return vertices;
    }

    private void processTriangle(String[] vertex1, String[] vertex2, String[] vertex3) {
        //TODO: check which type op face it is
        // v//vn, v/vt/vn, v//
        int index1 = Integer.parseInt(vertex1[0]) - 1;
        Vertex v1 = vertices.get(index1);

        int index2 = Integer.parseInt(vertex2[0]) - 1;
        Vertex v2 = vertices.get(index2);

        int index3 = Integer.parseInt(vertex3[0]) - 1;
        Vertex v3 = vertices.get(index3);

        /**
         *
         *
         * Process v1
         *
         */
        // Check if vertex has uvs.
        int normalIndex;
        if (vertex1[2].equals("")) {
            // add new normal
            normals.add(v2.position.subtract(v1.position)
                    .cross(v3.position.subtract(v1.position)).normalize().toNormal());
            normalIndex = normals.size() - 1;
        } else {
            normalIndex = Integer.parseInt(vertex1[2]) - 1;
        }

        int uvIndex = -1;
        if (vertex1[1].equals("")) {
            v1.setUv(false);
        } else {
            v1.setUv(true);
            uvIndex = Integer.parseInt(vertex1[1]) - 1;
        }

        // Current vertex is not set add new index.
        if(!v1.isSet()) {
            v1.setNormalIndex(normalIndex);
            v1.setUvIndex(uvIndex);
            indices.add(index1);
        } else {
            // Current vertex is already set
            processDuplicate(v1, uvIndex, normalIndex);
        }

        /**
         *
         * Process v2
         *
         */

        if (vertex2[2].equals("")) {
            // add new normal
            normals.add(v2.position.subtract(v1.position)
                    .cross(v3.position.subtract(v1.position)).normalize().toNormal());
            normalIndex = normals.size() - 1;
        } else {
            normalIndex = Integer.parseInt(vertex2[2]) - 1;
        }

        // Check if vertex has uvs.
        uvIndex = -1;
        if (vertex2[1].equals("")) {
            v2.setUv(false);
        } else {
            v2.setUv(true);
            uvIndex = Integer.parseInt(vertex2[1]) - 1;
        }

        // Current vertex is not set add new index.
        if(!v2.isSet()) {
            v2.setNormalIndex(normalIndex);
            v2.setUvIndex(uvIndex);
            indices.add(index2);
        } else {
            // Current vertex is already set
            processDuplicate(v2, uvIndex, normalIndex);
        }

        /**
         *
         * Process v3
         */

        if (vertex3[2].equals("")) {
            // add new normal
            normals.add(v2.position.subtract(v1.position)
                    .cross(v3.position.subtract(v1.position)).normalize().toNormal());
            normalIndex = normals.size() - 1;
        } else {
            normalIndex = Integer.parseInt(vertex3[2]) - 1;
        }

        // Check if vertex has uvs.
        uvIndex = -1;
        if (vertex3[1].equals("")) {
            v3.setUv(false);
        } else {
            v3.setUv(true);
            uvIndex = Integer.parseInt(vertex3[1]) - 1;
        }

        // Current vertex is not set add new index.
        if(!v3.isSet()) {
            v3.setNormalIndex(normalIndex);
            v3.setUvIndex(uvIndex);
            indices.add(index3);
        } else {
            // Current vertex is already set
            processDuplicate(v3, uvIndex, normalIndex);
        }
    }

    private void processDuplicate(Vertex currentVertex, int uvIndex, int normalIndex) {
        // 1. check if vertex has the same uv and normal
        if( currentVertex.hasSameUv(uvIndex) && currentVertex.hasSameNormal(normalIndex)) {
            indices.add(currentVertex.index);
        } else {
            Vertex duplicate = currentVertex.getDuplicate();
            if (duplicate != null) {
                processDuplicate(duplicate,uvIndex,normalIndex);
            } else {
                duplicate = new Vertex(currentVertex.position,vertices.size());
                duplicate.setUvIndex(uvIndex);
                duplicate.setNormalIndex(normalIndex);
                duplicate.setUv(currentVertex.hasUv);
                currentVertex.setDuplicate(duplicate);
                vertices.add(duplicate);
                indices.add(duplicate.index);
            }
        }
    }
}
