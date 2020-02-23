package parser;

import geometry.TriangleMesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class MeshLoader {

    String fileName;

    private BufferedReader reader;

    public MeshLoader(String fileName) {
     this.fileName = fileName;
    }

    protected void loadResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream resource = classLoader.getResourceAsStream(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            this.getBufferReaderFromStream(resource);
        }

    }

    protected void getBufferReaderFromStream(InputStream in) {
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    protected String getNextLine() throws IOException {
        return this.reader.readLine();
    }

    protected void closeBufferStream() throws IOException {
        this.reader.close();
    }

    abstract TriangleMesh loadMesh();
}
