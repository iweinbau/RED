package material;

import pathnode.ScatterNode;

/**
 * Base class for representing materials.
 */
public abstract class Material {
    /**
     * Calculate BRDF at scatterNode.
     * @param scatterNode
     */
    public abstract void calculateBRDF(ScatterNode scatterNode);
}
