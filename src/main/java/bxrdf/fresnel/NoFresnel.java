package bxrdf.fresnel;

public class NoFresnel extends Fresnel {
    @Override
    public double eval(double cosThetaI) {
        return 1;
    }
}
