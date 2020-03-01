package material;

import math.Normal;
import math.Point3D;
import math.RGBSpectrum;
import math.Vector3D;
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

    public RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi) {
        return RGBSpectrum.BLACK;
    }

    public boolean isShadowCaster() {
        return true;
    }
}
