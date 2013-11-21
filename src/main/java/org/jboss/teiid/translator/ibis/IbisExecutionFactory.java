package org.jboss.teiid.translator.ibis;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.resource.cci.ConnectionFactory;

import org.jboss.teiid.translator.ibis.execution.IbisQueryExecution;
import org.teiid.language.QueryExpression;
import org.teiid.logging.LogManager;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;
import org.jboss.teiid.translator.ibis.IbisConnection;

/**
 * Creates a execution factory
 * 
 * @author Jason Marley
 * 
 */
@Translator(name = "ibis", description = "A translator for Ibis web service api")
public class IbisExecutionFactory extends
		ExecutionFactory<ConnectionFactory, IbisConnection> {

	@Override
	public void start() throws TranslatorException {
		super.start();
//		LogManager.logTrace(LogConstants.CTX_CONNECTOR,
//				"Solr Executionfactory Started");
	}
//	@Override
//	public IbisConnection getConnection(ConnectionFactory factory,
//			ExecutionContext executionContext) throws TranslatorException {
//		// TODO Auto-generated method stub
//		return super.getConnection(factory, executionContext);
//	}
//
//	public IbisExecutionFactory() {
//		// connect to eis
//
//		// query eis
//	}


	@Override
	public ResultSetExecution createResultSetExecution(QueryExpression command,
			ExecutionContext executionContext, RuntimeMetadata metadata,
			IbisConnection connection) throws TranslatorException {
		return new IbisQueryExecution(command, executionContext, metadata,
				connection);
	}

	/**
	 * Description: casts the column value returned by Solr to what is expected
	 * my the Teiid table
	 * 
	 * @param columnValue
	 * @param columnType
	 * @return
	 * 
	 */
	public Object convertToTeiid(Object columnValue, Class<?> columnType) {
		if (columnValue == null) {
			return null;
		}

		try {
			if (columnType.equals(java.sql.Date.class)) {
				return new java.sql.Date(
						((java.util.Date) columnValue).getTime());
			} else if (columnType.equals(java.sql.Timestamp.class)) {
				return new java.sql.Timestamp(
						((java.util.Date) columnValue).getTime());
			} else if (columnType.equals(java.sql.Time.class)) {
				return new java.sql.Time(
						((java.util.Date) columnValue).getTime());
			} else if (columnType.equals(String.class)) {
				return new String((String) columnValue);
			} else if (columnType.equals(Integer.class)) {
				return new Integer((Integer) columnValue);
			} else if (columnType.equals(BigDecimal.class)) {
				return new BigDecimal((String) columnValue);
			} else if (columnType.equals(BigInteger.class)) {
				return new BigInteger((String) columnValue);
			} else if (columnType.equals(Character.class)) {
				return new Character(((String) columnValue).charAt(0));
			} else {
				LogManager
						.logWarning(
								columnType.toString(),
								"This '"
										+ columnType.toString()
										+ "' column type is not supported. Attempting to cast as string.");

				return new String((String) columnValue);
			}
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Could not cast field class type, check model and verify support. Field Name: "
							+ columnValue + " and Field Type: " + columnType);
		}

	}

	/*
	 * TODO
	 * 
	 * @Override public boolean supportsOrderBy() { return true; }	
	 * /*
	 * 
	 */

	@Override
	public boolean supportsCompareCriteriaEquals() {
		return true;
	}

	@Override
	public boolean supportsInCriteria() {
		return true;
	}

	@Override
	public boolean supportsRowLimit() {
		return true;
	}

	@Override
	public boolean supportsNotCriteria() {
		return true;
	}



	@Override
	public boolean supportsLikeCriteria() {
		return true;
	}

}
