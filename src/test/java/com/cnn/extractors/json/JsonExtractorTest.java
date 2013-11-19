package com.cnn.extractors.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonExtractorTest {
	
	private JsonExtractor jsonExtractor;
	private static final String JSON = "{\"string\":\"string value\",\"number\":7777777,\"boolean\":true,\"array\":[],\"object\":{}}";
	
	@Test
	(expected = NullPointerException.class)
	public void testResolveNullJson(){
		String json = null;
		Assert.assertNull(jsonExtractor.resolve(json, "String", "String", "somepath"));
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testResolveNullJPath(){
		String jpath = null;
		Assert.assertNull(jsonExtractor.resolve(JSON, "String", "String", jpath));
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testResolveNullNativeType(){
		String nativeType = null;
		Assert.assertNull(jsonExtractor.resolve(JSON, "String", nativeType, "somepath"));
	}
	
	@Test
	public void testResolveEmptyStringJson(){}
	
	@Test
	public void testResolveEmptyStringJPath(){}
	
	@Test
	public void testResolveEmptyStringNativeType(){}
	
	@Test
	public void testResolveStringMisMatch(){}
	
	@Test
	public void testResolveNumberMisMatch(){}
	
	@Test
	public void testResolveBooleanMisMatch(){}
	
	@Test
	public void testResolveArrayMisMatch(){}
	
	@Test
	public void testResolveObjectMisMatch(){}

	@Test
	public void testResolveGoodString(){}
	
	@Test
	public void testResolveGoodNumber(){}
	
	@Test
	public void testResolveGoodBoolean(){}
	
	@Test
	public void testResolveGoodArray(){}
	
	@Test
	public void testResolveGoodObject(){}
	
}
