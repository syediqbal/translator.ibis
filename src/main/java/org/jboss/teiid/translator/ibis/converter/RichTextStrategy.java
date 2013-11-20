package org.jboss.teiid.translator.ibis.converter;

public class RichTextStrategy implements ConverterStrategy {

    public static final String TYPE = "rich_text";

    @Override
    public String getSupportedNativeType() {
        return TYPE;
    }

    @Override
    public Object convert(Object json) {

        // If we declare that the rich text model of Content API as that of the
        // Ibis format, then we do not need any conversion other than
        // stringifying the rich text JSON object.

        // NOT CORRECT. Really should be something like
        // new JSONObject((Map)json).toString();
        return json.toString();
    }

}
