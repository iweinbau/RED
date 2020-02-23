package material;

import pathnode.ScatterNode;

public abstract class Material {
    public abstract void calculateBRDF(ScatterNode scatterNode);
}
