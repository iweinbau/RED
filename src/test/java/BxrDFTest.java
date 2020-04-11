import bxrdf.*;
import math.RGBSpectrum;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.EnumSet;

public class BxrDFTest {

    @Test
    public void BxRDFTypeTest(){

        long type = (BxrdfType.BSDF_TRANSMISSION.getFlag() | BxrdfType.BSDF_SPECULAR.getFlag()) & BxrdfType.BSDF_TRANSMISSION.getFlag();

    }
}
