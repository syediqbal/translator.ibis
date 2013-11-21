package org.jboss.teiid.translator.ibis.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.resource.cci.ResultSet;

//import org.apache.solr.client.solrj.SolrQuery;
//import org.apache.solr.client.solrj.impl.HttpSolrServer;
//import org.apache.solr.client.solrj.response.QueryResponse;
//import org.apache.solr.common.SolrDocument;
//import org.apache.solr.common.SolrDocumentList;
import org.teiid.language.DerivedColumn;
import org.teiid.language.QueryExpression;
import org.teiid.language.Select;
import org.teiid.logging.LogManager;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.DataNotAvailableException;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.TranslatorException;
import org.jboss.teiid.translator.ibis.IbisConnection;
import org.jboss.teiid.translator.ibis.IbisExecutionFactory;

/**
 * @author Jason Marley
 * @author Syed Iqbal
 *
 */
public class IbisQueryExecution implements ResultSetExecution {

	private RuntimeMetadata metadata;
	private Select query;
	@SuppressWarnings("unused")
	private ExecutionContext executionContext;
	private IbisConnection connection;
	private IbisSQLHierarchyVistor visitor;
	private String queryParams;
	private List<String> queryResponse;
	private Iterator<String> docItr;
	private Class<?>[] expectedTypes;
	private IbisExecutionFactory executionFactory;

	public IbisQueryExecution(QueryExpression command,
			ExecutionContext executionContext, RuntimeMetadata metadata,
			IbisConnection connection) {
		this.metadata = metadata;
		this.query = (Select) command;
		this.executionContext = executionContext;
		this.connection = connection;
		this.expectedTypes = command.getColumnTypes();

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel() throws TranslatorException {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("static-access")
	@Override
	public void execute() throws TranslatorException {
		this.visitor = new IbisSQLHierarchyVistor(metadata);
		// visitor.translateSQL(query);
		// build query in solr instance
		// setQuery
		// set query order
		// sort clause
		// setFields

		// translate sql query into ibis query string
		this.visitor.visitNode(query);

		// get query fields
		// fieldList = this.visitor.getFieldNameList();

		/*
		 * TODO to optimize query, look into returning specifying exactly which
		 * columns to return. // add query Solr response fields for
		 * (DerivedColumn field : fieldList) {
		 * params.addField(visitor.getShortName((field.toString()))); }
		 */

		// get ibis query string
		queryParams = this.visitor.getTranslatedSQL();

		LogManager.logInfo("This is the ibis query", queryParams);
		// TODO set offset
		// TODO set row result limit
		//

		// execute ibis query
		try {
			queryResponse = connection.executeQuery(queryParams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogManager.logCritical("query execution issue", queryParams.toString());
			e.printStackTrace();
		}

		docItr = queryResponse.iterator(); // change to iterator?
														// how does iterator
														// work?

		// docNum = (int) docs.getNumFound();

		/*
		 * TODO write logic to handle limiting the number of docs found
		 * logger.logDetail("Total docs returned: " + numFound); if(initialLimit
		 * != -1 && initialLimit < numFound) { numToRetrieve = initialLimit; }
		 * else { numToRetrieve = numFound; }
		 */
	}

	/*
	 * This iterates through the documents from Solr and maps their fields to
	 * rows in the Teiid table
	 * 
	 * @see org.teiid.translator.ResultSetExecution#next()
	 */
	@Override
	public List<?> next() throws TranslatorException, DataNotAvailableException {

//		final List<Object> row = new ArrayList<Object>();
//		String columnName;

		// is there any solr docs
		if (this.docItr != null && this.docItr.hasNext()) {
			
			LogManager.logInfo("this is json document in string format", this.docItr.toString());
//			SolrDocument doc = this.docItr.next();
//
//			for (int i = 0; i < this.visitor.fieldNameList.size(); i++) {
//				// TODO handle multiple tables
//				columnName = this.visitor.getShortFieldName(i);
//
//				row.add(this.executionFactory.convertToTeiid(
//						doc.getFieldValue(columnName), this.expectedTypes[i]));
//			}

			return null;
		}
		return null;
	}
}
