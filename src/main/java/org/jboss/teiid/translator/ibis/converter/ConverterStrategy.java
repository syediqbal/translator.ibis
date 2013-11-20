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
public abstract class ConverterStrategy {

    /**
     * Return the native type (e.g. "rich_text") supported by this strategy.
     *
     * @return
     */
    abstract public NativeTypes getSupportedNativeType();

    /**
     * Return the Teiid type (e.g. "string") supported by this strategy.
     */
    abstract public String getSupportedTeiidType();

    /**
     * Given the source data, return the canonicalized model.
     *
     * @param jsonValue
     * @return
     */
    abstract public Object convert(Object json) throws ConversionException;

    protected void validate(Class<?> expectedType, Object actualValue) throws ConversionException {
        if (!(expectedType.isInstance(actualValue))) {
            throw new ConversionException(
                expectedType.getCanonicalName(), actualValue.getClass().getCanonicalName());
        }
    }
}
