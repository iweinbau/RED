package bxrdf;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public enum BxrdfType {
    BSDF_REFLECTION (1 << 0),
    BSDF_TRANSMISSION ( 1 << 1),
    BSDF_DIFFUSE (1 << 2),
    BSDF_GLOSSY ( 1 << 3),
    BSDF_SPECULAR (1 << 4),
    BSDF_ALL (BSDF_DIFFUSE.getFlag() | BSDF_GLOSSY.getFlag() | BSDF_SPECULAR.getFlag() |
            BSDF_REFLECTION.getFlag() | BSDF_TRANSMISSION.getFlag());

    private final long flag;

    BxrdfType(long flag) {
        this.flag = flag;
    }

    public long getFlag(){
        return flag;
    }

    public static EnumSet<BxrdfType> getFlag(long flagValue) {
        EnumSet flags = EnumSet.noneOf(BxrdfType.class);
        Arrays.asList(BxrdfType.values()).forEach( statusFlag -> {
            long flag = statusFlag.flag;
            if ((flag & flagValue) == flag) {
                flags.add(statusFlag);
            }
        });
        return flags;
    }

    public static long getFlag( Collection<BxrdfType> flags) {
        long value=0;
        for (BxrdfType flag:flags) {
            value |= flag.getFlag();
        }
        return value;
    }

}
