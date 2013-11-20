package org.jboss.teiid.translator.ibis.converter;

import java.util.List;

import org.teiid.translator.TranslatorException;

/**
 * This interface defines the API call to convert an Ibis (JSON) model into
 * a model that Teiid is expecting.
 *
 * @author ntan
 *
 */
public interface ModelConverter {

    /**
     * Given an Ibis model as stringified JSON, return a Teiid model.
     * TODO Accept a Jettison JSONObject instead of a String?
     * @param ibisModelJson
     * @return List of { columnName, columnValue } tuples
     * @throws TranslatorException
     */
    public List<?> convertToTeiid(String ibisModelJson) throws TranslatorException, ConversionException;
}
