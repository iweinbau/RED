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

    List<BxRDF> bxRDFS = new LinkedList<>();
    BxRDF sampledBRDF;

    public BxRDFContainer() {}

    public void addBxRDF(BxRDF... bxrdf) {
        Collections.addAll(this.bxRDFS, bxrdf);
    }

    @Override
    public RGBSpectrum rho() {
        RGBSpectrum f = RGBSpectrum.BLACK;
        for (BxRDF bxrdf:bxRDFS) {
            f.add(bxrdf.rho());
        }
        return f;
    }

    @Override
    public RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal) {
        RGBSpectrum f = RGBSpectrum.BLACK;
        for (BxRDF bxrdf:bxRDFS) {
            f = f.add(bxrdf.f(wo,wi, normal));
        }
        return f;
    }

    @Override
    public double sample_pdf(Vector3D wo, Vector3D wi, Normal normal) {
        throw new NotImplementedException();
    }

    @Override
    public RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal) {
        throw new NotImplementedException();
    }

    // Loop over all
    @Override
    public Vector3D sample_wi(Vector3D wo, Normal normal,Point2D u, long type) {
        throw new NotImplementedException();
    }

    public List<BxRDF> getTypes(long types) {
        List<BxRDF> returnBxRDF = new ArrayList<>();
        for (BxRDF bxrdf:this.bxRDFS) {
            if(bxrdf.isOfType(types))
                returnBxRDF.add(bxrdf);
        }
        return returnBxRDF;
    }
}
