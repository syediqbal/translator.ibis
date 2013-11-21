package org.jboss.teiid.translator.ibis.converter;

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
