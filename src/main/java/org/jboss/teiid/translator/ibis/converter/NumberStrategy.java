package org.jboss.teiid.translator.ibis.converter;


public class NumberStrategy extends ConverterStrategy {

    @Override
    public NativeTypes getSupportedNativeType() {
        return NativeTypes.NUMBER;
    }

    @Override
    public String getSupportedTeiidType() {
        return "float"; // TODO need to think how to map {float,integer,etc} to JSON's single Number type.
    }

    @Override
    public Object convert(Object json) throws ConversionException {
        validate(Number.class, json);
        return ((Number)json).floatValue();
    }

}
