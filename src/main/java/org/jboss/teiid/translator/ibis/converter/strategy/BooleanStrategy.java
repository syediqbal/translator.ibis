package org.jboss.teiid.translator.ibis.converter.strategy;

import org.jboss.teiid.translator.ibis.converter.ConversionException;
import org.jboss.teiid.translator.ibis.converter.NativeTypes;

public class BooleanStrategy extends ConverterStrategy {

    @Override
    public NativeTypes getSupportedNativeType() {
        return NativeTypes.BOOLEAN;
    }

    @Override
    public String getSupportedTeiidType() {
        return "boolean";
    }

    @Override
    public Object convert(Object json) throws ConversionException {
        validate(Boolean.class, json);
        return (Boolean)json;
    }

}
