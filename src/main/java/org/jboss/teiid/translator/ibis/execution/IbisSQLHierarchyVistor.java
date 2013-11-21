package org.jboss.teiid.translator.ibis.execution;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.saxon.expr.Token;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.teiid.language.AndOr;
import org.teiid.language.Comparison;
import org.teiid.language.DerivedColumn;
import org.teiid.language.Expression;
import org.teiid.language.In;
import org.teiid.language.LanguageObject;
import org.teiid.language.Like;
import org.teiid.language.Limit;
import org.teiid.language.NamedTable;
import org.teiid.language.Not;
import org.teiid.language.OrderBy;
import org.teiid.language.SQLConstants;
import org.teiid.language.SQLConstants.Reserved;
import org.teiid.language.Select;
import org.teiid.language.SortSpecification;
import org.teiid.language.SortSpecification.Ordering;
import org.teiid.language.With;
import org.teiid.language.SQLConstants.Tokens;
import org.teiid.language.visitor.HierarchyVisitor;
import org.teiid.logging.LogManager;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.utilities.IbisConstants;
import org.teiid.translator.utilities.IbisConstants.QueryParams;
import org.teiid.translator.utilities.IbisStringUtil;
//import org.teiid.query.parser.Token;

/**
 * @author student
 * 
 */
public class IbisSQLHierarchyVistor extends HierarchyVisitor {

	private RuntimeMetadata metadata;
	protected static final String UNDEFINED = "<undefined>"; 
	private SolrQuery params = new SolrQuery();
	private LogManager logger;
	protected StringBuilder buffer = new StringBuilder();
	private SQLConstants token;
	List<DerivedColumn> fieldNameList;
	private IbisStringUtil ibisStringUtil;
	private String sortQueryParam;
	private String rowLimit;
	public static String V_Q  = "?v=${v}&q=${q}";
	public static String V_Q_S  = "?v=${v}&q=${q}&sort=${sort}";
	public static String V_Q_S_R = "?v=${v}&q=${q}&sort=${sort}&rows=${rows}";
	
	public String getRowLimit(){
		return rowLimit;
	}
	
	public void setRowLimit(String rowLimit){
		this.rowLimit = rowLimit;
	}
	
	public String getSortQueryParam(){
		return sortQueryParam;
	}
	
	public void setSortQueryParam(String pSortQueryParam){
		this.sortQueryParam=pSortQueryParam;	
	}

	// private LogManager logger;

	public IbisSQLHierarchyVistor(RuntimeMetadata metadata) {
		this.metadata = metadata;

	}

	@Override
	public void visit(Select obj) {
		
		super.visit(obj);
		if (obj.getFrom() != null && !obj.getFrom().isEmpty()) {
			NamedTable table = (NamedTable) obj.getFrom().get(0);
			String tableName = table.getName();
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
		boolean isUri = lhs.equals("uri");
		Expression rhs = obj.getRightExpression();
		if (lhs != null) {
			switch (obj.getOperator()) {
			case EQ:
				String rExpression = (isUri)?IbisStringUtil.addDoubleQuotes(rhs.toString()):IbisStringUtil.trimSingleQuotes(rhs.toString());
				buffer.append(lhs).append(":").append(rExpression);
				break;
			case NE:
				buffer.append(IbisConstants.Tokens.NEGATION).append(lhs)
						.append(":").append(IbisStringUtil.trimSingleQuotes(rhs.toString()));
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
		//buffer.append(Tokens.LPAREN);
		//buffer.append(Tokens.LPAREN);

		// walk left node
		super.visitNode(obj.getLeftCondition());

		//buffer.append(Tokens.RPAREN);

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
		
		//buffer.append(Tokens.LPAREN);
		
		//walk right node
		super.visitNode(obj.getRightCondition());
		//buffer.append(Tokens.RPAREN);
		//buffer.append(Tokens.RPAREN);
		
	}

	@Override
	public void visit(In obj) {	
		
		Expression rhsExpression;
		String lhs = IbisStringUtil.getShortName(obj.getLeftExpression().toString());
		
		if (obj.isNegated()){
			buffer.append(Reserved.NOT).append(Tokens.SPACE);
		}
		
		//start solr expression
		//buffer.append(lhs).append(Tokens.COLON);
		
		List<Expression> rightExpressions = obj.getRightExpressions();
		int i = 0;
		for(Expression rightExpression : rightExpressions){
			buffer.append(lhs).append(IbisConstants.Tokens.COLON);
			buffer.append(IbisStringUtil.trimSingleQuotes(rightExpression.toString()));
			i++;
			if(rightExpressions.size()>1&&i<rightExpressions.size()){
				buffer.append(Tokens.SPACE).append(Reserved.OR).append(Tokens.SPACE);
			}
			
		}
/*		Iterator<Expression> expressionIterator = rightExpressions.iterator();
		
		while(expressionIterator.hasNext())
		{
			rhsExpression = expressionIterator.next();
			lhs=lhs+rhsExpression.toString();
			buffer.append(rhsExpression.toString());
			
			if(i.hasNext())
			{				
				buffer.append(Tokens.SPACE).append(Reserved.OR).append(Tokens.SPACE);
			}
			
		}
		buffer.append(Tokens.RPAREN);
		
	*/}

	
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
		buffer.append(lhs).append(IbisConstants.Tokens.COLON).append(rhs);
	}
	
	/* (non-Javadoc)
	 * @see org.teiid.language.visitor.HierarchyVisitor#visit(org.teiid.language.OrderBy)
	 * Description: transforms the order by statements into SOLR syntax
	 */
	@Override
	public void visit(OrderBy obj) {
		List<SortSpecification> sortSpecifications = obj.getSortSpecifications();
		for(SortSpecification sortSpecification : sortSpecifications){
			Expression expression = sortSpecification.getExpression();
			sortQueryParam = IbisStringUtil.getShortName(expression.toString());
			sortQueryParam = sortQueryParam+Tokens.SPACE+sortSpecification.getOrdering().name().toLowerCase();
			try {
				sortQueryParam = IbisStringUtil.encode(sortQueryParam);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			}
	/* (non-Javadoc)
	 * @see org.teiid.language.visitor.HierarchyVisitor#visit(org.teiid.language.Limit)
	 * Description: transforms the limit statements
	 */
	@Override
	public void visit(Limit obj){
		int limit = obj.getRowLimit();
		rowLimit = String.valueOf(limit);
		
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
	
	private String getVersion(){
		//TODO get this version from a config file
		return "1";
	}

	public String getTranslatedSQL() {
		if (buffer == null || buffer.length() == 0) {
			return "*:*";
		} else {
			return buffer.toString();
		}

	}
	
	public String getResolvedPath() {
		StrSubstitutor strSubstitutor = new StrSubstitutor(getParamValueMap());
		String resolvedPath = strSubstitutor.replace(getPathToResolve());
		return resolvedPath;

	}

	private String getPathToResolve() {
		String pathToResolve = V_Q;
		if(!StringUtils.isBlank(getSortQueryParam())&&!StringUtils.isBlank(getRowLimit())){
			pathToResolve = V_Q_S_R;

		}
		else if (!StringUtils.isBlank(getSortQueryParam())) {
            pathToResolve = V_Q_S;
		} 
		return pathToResolve;
	}

	private Map<String,String> getParamValueMap(){
		Map<String,String> paramValuesMap = new HashMap<String,String>();
		paramValuesMap.put(QueryParams.V,getVersion());
		try {
			paramValuesMap.put(QueryParams.Q,IbisStringUtil.encode(getTranslatedSQL()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paramValuesMap.put(QueryParams.SORT,getSortQueryParam());
		paramValuesMap.put(QueryParams.ROWS,getRowLimit());
		
		return paramValuesMap;
	}
	

}
