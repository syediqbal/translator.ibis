package org.jboss.teiid.translator.ibis.converter;

/**
 * ConverterStrategies are used by the model converter to canonicalize a
 * data-source-specific piece of the model to the model Teiid is expecting.
 * A good example is a rich text converter that converts WordPress rich text
 * to the canonical rich text model defined by Content API.
 *
 * TODO Some of these interfaces should probably reside outside of Ibis in
 * some generic Content API library.
 *
 * @author ntan
 *
 */
public interface ConverterStrategy {

    /**
     * Return the native type (e.g. "rich_text") supported by this strategy.
     *
     * @return
     */
    public String getSupportedNativeType();

    /**
     * Given the source data, return the canonicalized model.
     *
     * @param jsonValue
     * @return
     */
    public Object convert(Object json);
}
