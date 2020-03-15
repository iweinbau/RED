package parser;

import geometry.TriangleMesh;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Class for creating new Triangle Meshes and storing already loaded meshes.
 *
 */
public class MeshFactory {

    HashMap<String,TriangleMesh> loadedMeshes = new HashMap<>();

    /**
     * Create new triangle mesh from resource file.
     * @param fileName
     * @return TriangleMesh
     */
    public TriangleMesh getTriangleMesh(String fileName) {
        // Check if the mesh is already loaded?
        if ( loadedMeshes.containsKey(fileName) ) {
            return loadedMeshes.get(fileName);
        }
        // Get file extension
        String extension = fileName.substring(fileName.lastIndexOf("."));
        if ( extension.equals(".obj") ) {
            // Load file
            TriangleMesh mesh = new OBJLoader(fileName).loadMesh();
            // Add loaded mesh to already loaded meshes;
            loadedMeshes.put(fileName,mesh);
            // Return mesh
            return mesh;
        }
        throw new IllegalArgumentException("File extension not supported");
    }
}
