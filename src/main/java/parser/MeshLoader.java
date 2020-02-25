package parser;

import geometry.TriangleMesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Base class for loading triangle meshes from file.
 */
public abstract class MeshLoader {

    /**
     * Resource file name
     */
    String fileName;

    /**
     * file buffer for reading file.
     */
    private BufferedReader reader;

    /**
     * Construct new MeshLoader with given resource file.
     * @param fileName
     */
    public MeshLoader(String fileName) {
     this.fileName = fileName;
    }

    /**
     * Load resource in buffer
     */
    protected void loadResource() {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream resource = classLoader.getResourceAsStream(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            this.getBufferReaderFromStream(resource);
        }

    }

    private void getBufferReaderFromStream(InputStream in) {
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    /**
     * Read nex line in buffer.
     * @return the next line in the buffer.
     * @throws IOException
     */
    protected String getNextLine() throws IOException {
        return this.reader.readLine();
    }

    /**
     * Close buffer.
     * @throws IOException
     */
    protected void closeBufferStream() throws IOException {
        this.reader.close();
    }

    abstract TriangleMesh loadMesh();
}
