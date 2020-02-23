package bxrdf;

import math.Normal;
import math.Point2D;
import math.RGBSpectrum;
import math.Vector3D;

public interface IBxRDF {


    RGBSpectrum rho();
    RGBSpectrum f(Vector3D wo, Vector3D wi, Normal normal);
    double pdf(Vector3D wo, Vector3D wi,Normal normal);
    RGBSpectrum sample_f(Vector3D wo, Vector3D wi, Normal normal);
    Vector3D sample_wi(Vector3D wo, Normal normal, Point2D u, long type);

}
