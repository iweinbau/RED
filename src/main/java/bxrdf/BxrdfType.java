package bxrdf;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public enum BxrdfType {
    BRDF_DIFFUSE(1<<0),
    BRDF_SPECULAR(1<<2),
    BRDF_REFLECTION(1<<3),
    BRDF_TRANSMISSION(1<<4),
    BRDF_ALL(BRDF_DIFFUSE.getFlag() |
            BRDF_SPECULAR.getFlag() |
            BRDF_TRANSMISSION.getFlag());

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
