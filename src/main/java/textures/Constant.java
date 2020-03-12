package textures;

import pathnode.ScatterNode;

public class Constant extends Texture<Double> {

    double constant;

    public Constant(double constant) {
        this.constant = constant;
    }

    @Override
    public Double evaluate(ScatterNode scatterNode) {
        return constant;
    }
}
