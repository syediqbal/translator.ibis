package org.jboss.teiid.translator.ibis.converter;

public class ConversionException extends Exception {

    private static final long serialVersionUID = 1699837293670361005L;

    private String expectedType;
    private String actualType;

    public ConversionException(String expectedType, String actualType) {
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

    public String getExpectedType() {
        return expectedType;
    }

    public String getActualType() {
        return actualType;
    }
}
