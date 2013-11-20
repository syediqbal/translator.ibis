package com.cnn.extractors.json;

import com.cnn.extractors.Extractor;
import com.jayway.jsonpath.*;

public class JsonExtractor implements Extractor {
	
	/**
	 * Default constructor
	 */
	public JsonExtractor (){}
	
	/**
	 * Takes a piece of JSON, locates a node using the jpath argument
	 * and returns it or null if there is a problem.
	 * 
	 * @param json
	 * @param teiidType
	 * @param nativeType
	 * @param jpath
	 * @return
	 */
	@Override
	public <T> T resolve (String json, String jpath) {
		T data = null;
		if ((json != null && !json.equals("")) && (jpath != null && !jpath.equals(""))){
			try {
				data = JsonPath.read(json, jpath);
			} catch (PathNotFoundException pnfe){
				// log this exception
				data = null;
			}
		}
		return data;
	}

}
