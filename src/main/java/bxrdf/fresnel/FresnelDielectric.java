package bxrdf.fresnel;

public class FresnelDielectric extends Fresnel {

    double etaI, etaT;

    public FresnelDielectric(double etaI, double etaT) {
        this.etaI = etaI;
        this.etaT = etaT;
    }

    @Override
    public double eval(double cosThetaI) {
        cosThetaI = Math.max(-1,Math.min(cosThetaI,1));
        // 1. check if we have to swap IOR
        boolean entering = cosThetaI > 0;
        if (! entering) {
            double tmp = etaI;
            etaI = etaT;
            etaT = tmp;
            cosThetaI = Math.abs(cosThetaI);
        }

        double sinThetaI = Math.sqrt(Math.max(0, 1 - cosThetaI * cosThetaI));
        double sinThetaT =  etaI/etaT * sinThetaI;

        if (sinThetaT >= 1) return 0;
        double cosThetaT = Math.sqrt(Math.max(0, 1 - sinThetaT * sinThetaT));

        double rParallel =  ((etaT * cosThetaI) - (etaI * cosThetaT)) /
                            ((etaT * cosThetaI) + (etaI * cosThetaT));
        double rPerpendicular = ((etaI * cosThetaI) - (etaT * cosThetaT)) /
                                ((etaI * cosThetaI) + (etaT * cosThetaT));
        return (rParallel * rParallel + rPerpendicular * rPerpendicular) * 0.5;
    }
}
