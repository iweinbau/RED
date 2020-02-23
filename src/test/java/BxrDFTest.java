import bxrdf.*;
import math.RGBSpectrum;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.EnumSet;

public class BxrDFTest {

    @Test
    public void BxRDFTypeTest(){

        EnumSet<BxrdfType> type = BxrdfType.getFlag(BxrdfType.BRDF_DIFFUSE.getFlag() | BxrdfType.BRDF_SPECULAR.getFlag());
        long flag = BxrdfType.getFlag(type);

        Assert.assertEquals(BxrdfType.BRDF_DIFFUSE.getFlag() | BxrdfType.BRDF_SPECULAR.getFlag(),flag);

        BxRDFContainer Bxrdfs = new BxRDFContainer();
        Bxrdfs.addBxRDF(new LambertianReflection(RGBSpectrum.BLACK));
        Bxrdfs.addBxRDF(new PhongReflection(RGBSpectrum.BLACK,10));
        Bxrdfs.addBxRDF(new BlinnPhongReflection(RGBSpectrum.BLACK,10));
        Bxrdfs.addBxRDF(new SpecularReflection(RGBSpectrum.BLACK));

        Assert.assertEquals(3,Bxrdfs.getTypes(BxrdfType.BRDF_ALL.getFlag()).size());
        Assert.assertEquals(1,Bxrdfs.getTypes(BxrdfType.BRDF_ALL.getFlag() &
                ~BxrdfType.BRDF_SPECULAR.getFlag()).size());
        Assert.assertEquals(2,Bxrdfs.getTypes(BxrdfType.BRDF_SPECULAR.getFlag()).size());
        Assert.assertEquals(1,Bxrdfs.getTypes(BxrdfType.BRDF_REFLECTION.getFlag()).size());

    }
}
