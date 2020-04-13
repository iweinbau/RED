package bxrdf;

import math.Normal;
import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BxRDFContainer implements IBxRDF {

    private List<BxRDF> bxRDFS = new LinkedList<>();

    public BxRDF sampledBRDF;

    public BxRDFContainer() {}

    public void addBxRDF(BxRDF... bxrdf) {
        Collections.addAll(this.bxRDFS, bxrdf);
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        RGBSpectrum f = RGBSpectrum.BLACK;
        for (BxRDF bxrdf : this.bxRDFS) {
            boolean reflect = wi.dot(normal) * wo.dot(normal) > 0;
            if ( (reflect && (bxrdf.flag & BxrdfType.BSDF_REFLECTION.getFlag()) != 0) ||
                    (!reflect && (bxrdf.flag & BxrdfType.BSDF_TRANSMISSION.getFlag()) != 0) ) {
                f = f.add(bxrdf.f(wo, wi, normal));
            }
        }
        return f;
    }

    /**
     * PDF of all brdf's
     *
     * @param wo
     * @param wi
     * @param normal
     * @return
     */
    @Override
    public double pdf(Vector3D wo, Vector3D wi, Normal normal) {
        assert(this.bxRDFS.size() > 0);

        double pdf = sampledBRDF.sample_pdf(wo,wi,normal);
        if ( (sampledBRDF.flag & BxrdfType.BSDF_SPECULAR.getFlag()) == 0 && this.bxRDFS.size() > 0) {
            for (BxRDF bxrdf : this.bxRDFS) {
                if (bxrdf != sampledBRDF) {
                    pdf += bxrdf.pdf(wo, wi, normal);
                }
            }
        }

        return pdf/this.bxRDFS.size();
    }

    @Override
    public RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal) {
        assert (this.bxRDFS.size() > 0);

        RGBSpectrum f = sampledBRDF.sample_f(wo,wi,normal);
        if ( (sampledBRDF.flag & BxrdfType.BSDF_SPECULAR.getFlag()) == 0 && this.bxRDFS.size() > 0) {
            f = RGBSpectrum.BLACK;
            for (BxRDF bxrdf : this.bxRDFS) {
                boolean reflect = wi.dot(normal) * wo.dot(normal) > 0;
                if ( (reflect && (bxrdf.flag & BxrdfType.BSDF_REFLECTION.getFlag()) != 0) ||
                        (!reflect && (bxrdf.flag & BxrdfType.BSDF_TRANSMISSION.getFlag()) != 0) ) {
                    f = f.add(bxrdf.sample_f(wo, wi, normal));
                }
            }
        }
        return f;
    }

    @Override
    public Vector3D sample_wi(Vector3D wo, Normal normal,Point2D u) {
        // 1. pick a random brdf.
        int bsdfIndex = Math.min((int)Math.floor(u.getX() * this.bxRDFS.size()), this.bxRDFS.size() - 1);
        sampledBRDF = this.bxRDFS.get(bsdfIndex);

        // 2. sample direction for this brdf.
        return sampledBRDF.sample_wi(wo,normal,u);
    }

    public int numComponents() {
        return this.bxRDFS.size();
    }
}
