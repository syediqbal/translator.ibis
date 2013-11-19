package org.jboss.teiid.translator.ibis.converter;

public interface JsonFieldResolver {

    public <T> T resolve(String json, String teiidType, String nativeType, String jpath);
}
