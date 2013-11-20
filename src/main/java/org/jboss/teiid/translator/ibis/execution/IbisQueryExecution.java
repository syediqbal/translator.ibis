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
import org.jboss.teiid.translator.ibis.execution.IbisConnection;
import org.jboss.teiid.translator.ibis.IbisExecutionFactory;

public class IbisQueryExecution implements ResultSetExecution {

	private RuntimeMetadata metadata;
	private Select query;
	@SuppressWarnings("unused")
	private ExecutionContext executionContext;
	private IbisConnection connection;
	private IbisSQLHierarchyVistor visitor;
	private LogManager logger;
	private SolrQuery params = new SolrQuery();
	private QueryResponse queryResponse = null;
	private List<DerivedColumn> fieldList = null;
	private Iterator<SolrDocument> docItr;
	private int docNum = 0;
	private int docIndex = 0;
	private Class<?>[] expectedTypes;
	private IbisExecutionFactory executionFactory;

	public IbisQueryExecution(QueryExpression command,
			ExecutionContext executionContext, RuntimeMetadata metadata,
			SolrConnection connection) {
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

		// traverse commands
		this.visitor.visitNode(query);

		// get query fields
		// fieldList = this.visitor.getFieldNameList();

		// add query Solr response fields
		for (DerivedColumn field : fieldList) {
			params.addField(visitor.getShortName((field.toString())));
		}
		
		//set Solr Query
		
		params.setQuery(this.visitor.getTranslatedSQL());
		
		LogManager.logInfo("This is the solr query", params.getQuery());
		// TODO set offset
		// TODO set row result limit
		//
		
		// execute query and somewhere in here do translation
		queryResponse = connection.executeQuery(params);

		docItr = queryResponse.getResults().iterator(); // change to iterator?
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

		final List<Object> row = new ArrayList<Object>();
		String columnName;

		// is there any solr docs
		if (this.docItr != null && this.docItr.hasNext()) {

			SolrDocument doc = this.docItr.next();

			for (int i = 0; i < this.visitor.fieldNameList.size(); i++) {
				// TODO handle multiple tables
				columnName = this.visitor.getShortFieldName(i);

				row.add(this.executionFactory.convertToTeiid(
						doc.getFieldValue(columnName), this.expectedTypes[i]));
			}

			return row;
		}
		return null;
	}
}
