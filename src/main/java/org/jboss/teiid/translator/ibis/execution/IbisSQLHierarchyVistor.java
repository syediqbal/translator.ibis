package org.jboss.teiid.translator.ibis.execution;

import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.teiid.language.AndOr;
import org.teiid.language.Comparison;
import org.teiid.language.DerivedColumn;
import org.teiid.language.Expression;
import org.teiid.language.In;
import org.teiid.language.LanguageObject;
import org.teiid.language.Like;
import org.teiid.language.NamedTable;
import org.teiid.language.Not;
import org.teiid.language.SQLConstants;
import org.teiid.language.SQLConstants.Reserved;
import org.teiid.language.Select;
import org.teiid.language.With;
import org.teiid.language.SQLConstants.Tokens;
import org.teiid.language.visitor.HierarchyVisitor;
import org.teiid.logging.LogManager;
import org.teiid.metadata.RuntimeMetadata;
//import org.teiid.query.parser.Token;

/**
 * @author student
 * 
 */
public class IbisSQLHierarchyVistor extends HierarchyVisitor {

	private RuntimeMetadata metadata;
	protected static final String UNDEFINED = "<undefined>"; //$NON-NLS-1$
	private SolrQuery params = new SolrQuery();
	private LogManager logger;
	protected StringBuilder buffer = new StringBuilder();
	private SQLConstants token;
	List<DerivedColumn> fieldNameList;

	// private LogManager logger;

	public IbisSQLHierarchyVistor(RuntimeMetadata metadata) {
		this.metadata = metadata;

	}

	@Override
	public void visit(Select obj) {
		
		super.visit(obj);
		if (obj.getFrom() != null && !obj.getFrom().isEmpty()) {
			NamedTable table = (NamedTable) obj.getFrom().get(0);
		}

		fieldNameList = obj.getDerivedColumns();

	}

	/**
	 * @param elementName
	 * @return
	 * @since 4.3
	 */
	public static String getShortName(String elementName) {
		int lastDot = elementName.lastIndexOf("."); //$NON-NLS-1$
		if (lastDot >= 0) {
			elementName = elementName.substring(lastDot + 1);
		}
		return elementName;
	}

	/**
	 * @return the full column names tableName.columnNames
	 */
	public List<DerivedColumn> getFieldNameList() {
		return fieldNameList;
	}

	/*
	 * (non-Javadoc) Note: Solr does not support <,> exclusively. It is always
	 * <=, >=
	 * 
	 * @see
	 * org.teiid.language.visitor.HierarchyVisitor#visit(org.teiid.language.
	 * Comparison)
	 */
	@Override
	public void visit(Comparison obj) {
		LogManager.logInfo(
				"Parsing compound criteria. Current query string is: ",
				buffer.toString());
		String lhs = getShortName(obj.getLeftExpression().toString());
		Expression rhs = obj.getRightExpression();
		if (lhs != null) {
			switch (obj.getOperator()) {
			case EQ:
				buffer.append(lhs).append(":").append(rhs.toString());
				break;
			case NE:
				buffer.append("NOT").append(Tokens.SPACE).append(lhs)
						.append(":").append(rhs.toString());
				break;
			case LE:
			case LT:
				buffer.append(lhs).append(":[* TO").append(Tokens.SPACE)
						.append(rhs.toString()).append("]");
				break;
			case GE:
			case GT:
				buffer.append(lhs).append(":[").append(rhs.toString())
						.append(" TO *]");
				break;
			}
		}

	}

	@Override
	public void visit(AndOr obj) {

		// prepare statement
		buffer.append(Tokens.LPAREN);
		buffer.append(Tokens.LPAREN);

		// walk left node
		super.visitNode(obj.getLeftCondition());

		buffer.append(Tokens.RPAREN);

		switch (obj.getOperator()) {
		case AND:
			buffer.append(Tokens.SPACE).append(Reserved.AND)
					.append(Tokens.SPACE);
			break;
		case OR:
			buffer.append(Tokens.SPACE).append(Reserved.OR)
					.append(Tokens.SPACE);
			break;
		}
		
		buffer.append(Tokens.LPAREN);
		
		//walk right node
		super.visitNode(obj.getRightCondition());
		buffer.append(Tokens.RPAREN);
		buffer.append(Tokens.RPAREN);
		
	}

	@Override
	public void visit(In obj) {	
		
		Expression rhsExpression;
		String lhs = getShortName(obj.getLeftExpression().toString());
		
		if (obj.isNegated()){
			buffer.append(Reserved.NOT).append(Tokens.SPACE);
		}
		
		//start solr expression
		buffer.append(lhs).append(Tokens.COLON).append(Tokens.LPAREN);
		
		List<Expression> rhs = obj.getRightExpressions();
		Iterator<Expression> i = rhs.iterator();
		
		while(i.hasNext())
		{
			rhsExpression = i.next();
			//append rhs side as we iterates
			buffer.append(rhsExpression.toString());
			
			if(i.hasNext())
			{				
				buffer.append(Tokens.SPACE).append(Reserved.OR).append(Tokens.SPACE);
			}
			
		}
		buffer.append(Tokens.RPAREN);
		
	}

	
	/* (non-Javadoc)
	 * @see org.teiid.language.visitor.HierarchyVisitor#visit(org.teiid.language.Like)
	 * Description: transforms the like statements into solor syntax
	 */
	@Override
	public void visit(Like obj) {
		
		String lhs = getShortName(obj.getLeftExpression().toString());
		String rhs = formatSolrQuery(obj.getRightExpression().toString());
		
		System.out.println(obj.isNegated());
		if (obj.isNegated()){
			buffer.append(Reserved.NOT).append(Tokens.SPACE);
		}
		buffer.append(lhs).append(Tokens.COLON).append(rhs);
	}

	private String formatSolrQuery(String solrQuery) {

		solrQuery = solrQuery.replace("%", "*");
		solrQuery = solrQuery.replace("'","");
		// solrQuery = solrQuery.replace("_", "?");

		return solrQuery;

	}

	public String getShortFieldName(int i) {
		return getShortName(fieldNameList.get(i).toString());

	}

	public String getFullFieldName(int i) {
		return fieldNameList.get(i).toString();

	}

	public String getTranslatedSQL() {
		if (buffer == null || buffer.length() == 0) {
			return "*:*";
		} else {
			return buffer.toString();
		}

	}
}
