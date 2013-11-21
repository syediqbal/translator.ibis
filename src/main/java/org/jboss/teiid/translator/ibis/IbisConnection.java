package org.jboss.teiid.translator.ibis;

import java.util.List;

import javax.resource.cci.Connection;

public interface IbisConnection extends Connection {
	
	/**
	 * Executes a solr like query against ibis api
	 * @return List<json docs>
	 * @throws Exception 
	 */
	public List<String> executeQuery(String query) throws Exception;
	
}
