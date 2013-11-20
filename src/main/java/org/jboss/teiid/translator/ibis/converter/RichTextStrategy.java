package org.jboss.teiid.translator.ibis.converter;

public class RichTextStrategy extends ConverterStrategy {

    @Override
    public NativeTypes getSupportedNativeType() {
        return NativeTypes.RICH_TEXT;
    }

    @Override
    public String getSupportedTeiidType() {
        return "string";
    }

    @Override
    public Object convert(Object json) throws ConversionException {

        // If we declare that the rich text model of Content API as that of the
        // Ibis format, then we do not need any conversion other than
        // stringifying the rich text JSON object.

        // TODO Is it Map that we are getting from jpath?
        //validate(Map.class, json);

        // NOT CORRECT. Really should be something like
        // new JSONObject((Map)json).toString();
        return json.toString();
    }
}
