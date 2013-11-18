package org.jboss.teiid.translator.ibis.converter;

import java.util.Map;

public class RichTextStrategy implements ConverterStrategy {

    @Override
    public String getSupportedNativeType() {
        return "rich_text";
    }

    @Override
    public Object convert(Object json) {

        // If we declare that the rich text model of Content API as that of the
        // Ibis format, then we do not need any conversion other than
        // stringifying the rich text JSON object.

        assert json instanceof Map;

        // NOT CORRECT. Really should be something like
        // new JSONObject((Map)json).toString();
        return json.toString();
    }

}
