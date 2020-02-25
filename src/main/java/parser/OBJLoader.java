package parser;

import geometry.TriangleMesh;
import math.Normal;
import math.Point2D;
import math.Point3D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
     * Constuct new OBJLoader object with given resource file.
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

                        for (String vertex:triangulateFace(face)) {
                            processVertex(vertex.split("/"));
                        }

                        //TODO: check which type op face it is
                        // v//vt, v/vn/vt, ...

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
            normalsArray[i] = normals.get(vertices.get(i).normalIndex);
            texturesArray[i] = uvs.get(vertices.get(i).uvIndex);
        }

        return new TriangleMesh(verticesArray,normalsArray,indicesArray);
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

    private void processVertex(String[] vertex) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
        int uvIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;

        // Current vertex is not set add new index.
        if(!currentVertex.isSet()) {
            currentVertex.setNormalIndex(normalIndex);
            currentVertex.setUvIndex(uvIndex);
            indices.add(index);
        } else {
            // Current vertex is already set
            processDuplicate(currentVertex, uvIndex, normalIndex);

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
                currentVertex.setDuplicate(duplicate);
                vertices.add(duplicate);
                indices.add(duplicate.index);
            }
        }
    }
}
