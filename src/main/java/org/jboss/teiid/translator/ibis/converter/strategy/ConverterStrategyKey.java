package org.jboss.teiid.translator.ibis.converter;

/**
 * This is the lookup key for the map that contains a number of converter
 * strategies.
 *
 * @author ntan
 *
 */
public class ConverterStrategyKey {

    private String teiidType;
    private NativeTypes ibisType;

    public ConverterStrategyKey(String teiidType, NativeTypes ibisType) {
        this.teiidType = teiidType;
        this.ibisType = ibisType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((ibisType == null) ? 0 : ibisType.hashCode());
        result = prime * result
                + ((teiidType == null) ? 0 : teiidType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConverterStrategyKey other = (ConverterStrategyKey) obj;
        if (ibisType != other.ibisType)
            return false;
        if (teiidType == null) {
            if (other.teiidType != null)
                return false;
        } else if (!teiidType.equals(other.teiidType))
            return false;
        return true;
    }
}
