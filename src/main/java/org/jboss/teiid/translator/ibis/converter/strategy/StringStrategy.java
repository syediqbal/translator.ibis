package org.jboss.teiid.translator.ibis.converter.strategy;

import org.jboss.teiid.translator.ibis.converter.ConversionException;
import org.jboss.teiid.translator.ibis.converter.NativeTypes;

public class StringStrategy extends ConverterStrategy {

    @Override
    public NativeTypes getSupportedNativeType() {
        return NativeTypes.STRING;
    }

    @Override
    public String getSupportedTeiidType() {
        return "string";
    }

    @Override
    public Object convert(Object json) throws ConversionException {
        if (json == null) {
            return null;
        }
        validate(String.class, json);
        return (String)json;
    }
}
