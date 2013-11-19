package com.cnn.extractors.json;

import com.cnn.extractors.Extractor;
import com.jayway.jsonpath.*;

public class JsonExtractor implements Extractor {
	
	/**
	 * Default constructor
	 */
	public JsonExtractor (){}
	
	/**
	 * Takes a piece of JSON, locates a node using the jpath argument, determines
	 * what java type it is, and returns appropriately.
	 * 
	 * @param json
	 * @param teiidType
	 * @param nativeType
	 * @param jpath
	 * @return
	 */
	@Override
	public <T> T resolve (String json, String teiidType, String nativeType, String jpath){
		T data = null;
		try {
			data = JsonPath.read(json, jpath);
		} catch (PathNotFoundException pnfe){
			// log this exception
			data = null;
		}
		try {
			validateData(nativeType, data);
		} catch (ClassCastException cce){
			// log this exception
			data = null;
		}
		return data;
	}
	
	/**
	 * Validates the actual returned type to what the source definition says it 
	 * should be. Checks the source definition type and routes appropriately.
	 * 
	 * @param nativeType
	 * @param dataObj
	 * @return
	 */
	private Boolean validateData (String nativeType, Object dataObj) throws ClassCastException {
		if (nativeType.equalsIgnoreCase("String")){
			return validateString(dataObj);
		} else if (nativeType.equalsIgnoreCase("Number")){
			return validateNumber(dataObj);
		} else if (nativeType.equalsIgnoreCase("Boolean")){
			return validateBoolean(dataObj);
		} else if (nativeType.equalsIgnoreCase("Array")){
			return validateArray(dataObj);
		} else if (nativeType.equalsIgnoreCase("Object")){
			return validateObject(dataObj);
		} else {
			throw new ClassCastException("jpath expression is not evaluating to a known JSON type!");
		}
	}
	
	/**
	 * Validates that the actual return type is a String.
	 * 
	 * @param dataObj
	 * @return
	 */
	private Boolean validateString (Object dataObj) throws ClassCastException {
		if (!(dataObj instanceof String)){
			throw new ClassCastException("The result of the jpath expression is not evaluating to a String!");
		}
		return true;
	}
	
	/**
	 * Validates that the actual return type is a Number.
	 * 
	 * @param dataObj
	 * @return
	 */
	private Boolean validateNumber (Object dataObj) throws ClassCastException {
		if (!(dataObj instanceof Number)){
			throw new ClassCastException("The result of the jpath expression is not evaluating to a Number!");
		}
		return true;
	}
	
	/**
	 * Validates that the actual return type is a Boolean.
	 * 
	 * @param dataObj
	 * @return
	 */
	private Boolean validateBoolean (Object dataObj) throws ClassCastException {
		if (!(dataObj instanceof Boolean)){
			throw new ClassCastException("The result of the jpath expression is not evaluating to a Boolean!");
		}
		return true;
	}
	
	/**
	 * Validates that the actual return type is a Array.
	 * 
	 * @param dataObj
	 * @return
	 */
	private Boolean validateArray (Object dataObj) throws ClassCastException {
		if (!(dataObj instanceof String)){
			throw new ClassCastException("The result of the jpath expression is not evaluating to an Array!");
		}
		return true;
	}
	
	/**
	 * Validates that the actual return type is a Object.
	 * 
	 * @param dataObj
	 * @return
	 */
	private Boolean validateObject (Object dataObj) throws ClassCastException {
		if (!(dataObj instanceof String)){
			throw new ClassCastException("The result of the jpath expression is not evaluating to an Object!");
		}
		return true;
	}

}
