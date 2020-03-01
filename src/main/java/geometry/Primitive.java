package geometry;


import core.SurfaceSample;
import light.AreaLight;
import math.*;

public interface Primitive{

    double getArea();

    double pdf(SurfaceSample sample);

    SurfaceSample sample(Point2D sample);

    RGBSpectrum Le(Point3D point, Normal normal, Vector3D wi);

}
