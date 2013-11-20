package com.cnn.extractors.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class JsonExtractorTest {
	
	private JsonExtractor jsonExtractor = new JsonExtractor();
	private static final String JSON = "{\"stringField\":\"string value\",\"numberField\":7777777,\"booleanField\":true,\"arrayField\":[],\"objectField\":{}}";
	
	@Test
	public void testResolveNullJson(){
		String json = null;
		Assert.assertNull(jsonExtractor.resolve(json, "somepath"));
	}
	
	@Test
	public void testResolveNullJPath(){
		String jpath = null;
		Assert.assertNull(jsonExtractor.resolve(JSON, jpath));
	}
	
	@Test
	public void testResolveEmptyStringJson(){
		Assert.assertNull(jsonExtractor.resolve("", "somepath"));
	}
	
	@Test
	public void testResolveEmptyStringJPath(){
		Assert.assertNull(jsonExtractor.resolve(JSON, ""));
	}

	@Test
	public void testResolveGoodString(){
		Assert.assertNotNull(jsonExtractor.resolve(JSON, "stringField"));
	}
	
	@Test
	public void testResolveGoodNumber(){
		Assert.assertNotNull(jsonExtractor.resolve(JSON, "numberField"));
	}
	
	@Test
	public void testResolveGoodBoolean(){
		Assert.assertNotNull(jsonExtractor.resolve(JSON, "booleanField"));
	}
	
	@Test
	public void testResolveGoodArray(){
		Assert.assertNotNull(jsonExtractor.resolve(JSON, "arrayField"));
	}
	
	@Test
	public void testResolveGoodObject(){
		Assert.assertNotNull(jsonExtractor.resolve(JSON, "objectField"));
	}
	
}
