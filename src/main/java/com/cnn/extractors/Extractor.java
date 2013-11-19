package com.cnn.extractors;

public interface Extractor {
	
    public <T> T resolve(String json, String teiidType, String nativeType, String jpath);

}
