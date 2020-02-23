package textures;

import math.Point2D;

public class Constant extends Texture<Double> {

    double constant;

    public Constant(double constant) {
        this.constant = constant;
    }

    @Override
    public Double evaluate() {
        return constant;
    }

    @Override
    public Double evaluate(Point2D uv) {
        return constant;
    }
}
