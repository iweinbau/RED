package parser;

import geometry.TriangleMesh;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class MeshFactory {

    public TriangleMesh getTriangleMesh(String fileName) {
        // Get file extension
        String extension = fileName.substring(fileName.lastIndexOf("."));
        if(extension.equals(".obj"))
            return new OBJLoader(fileName).loadMesh();

        throw new IllegalArgumentException("File extension not supported");
    }
}
