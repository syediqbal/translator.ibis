package org.jboss.teiid.translator.ibis.converter.strategy;

import org.jboss.teiid.translator.ibis.converter.ConversionException;
import org.jboss.teiid.translator.ibis.converter.NativeTypes;

public class ObjectStrategy extends ConverterStrategy {

    @Override
    public NativeTypes getSupportedNativeType() {
        return NativeTypes.OBJECT;
    }

    @Override
    public String getSupportedTeiidType() {
        return "string";
    }

    @Override
    public Object convert(Object json) throws ConversionException {

        // NOT CORRECT. Really should be something like
        // new JSONObject((Map)json).toString();
        return json == null ? json : json.toString();
    }

}
