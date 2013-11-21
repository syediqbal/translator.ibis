package org.jboss.teiid.translator.ibis.converter.strategy;

import java.util.List;

import org.jboss.teiid.translator.ibis.converter.ConversionException;
import org.jboss.teiid.translator.ibis.converter.NativeTypes;

public class ArrayStrategy extends ConverterStrategy {

    @Override
    public NativeTypes getSupportedNativeType() {
        return NativeTypes.ARRAY;
    }

    @Override
    public String getSupportedTeiidType() {
        return "string";
    }

    @Override
    public Object convert(Object json) throws ConversionException {
        if (json == null) {
            return json;
        }
        else {
            validate(List.class, json);
            // NOT CORRECT. Really should be something like
            // new JSONObject((List)json).toString();
            return json.toString();
        }
    }

}
