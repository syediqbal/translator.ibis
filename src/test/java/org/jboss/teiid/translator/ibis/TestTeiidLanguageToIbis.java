package org.jboss.teiid.translator.ibis;

import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;
import org.teiid.cdk.CommandBuilder;
import org.teiid.core.util.ObjectConverterUtil;
import org.teiid.core.util.UnitTestUtil;
import org.teiid.language.Command;
import org.teiid.language.Select;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.unittest.RealMetadataFactory;
import org.jboss.teiid.translator.ibis.IbisExecutionFactory;
import org.jboss.teiid.translator.ibis.execution.IbisSQLHierarchyVistor;
import org.teiid.cdk.api.TranslationUtility;

/**
 * @author student
 *
 */
@SuppressWarnings("nls")
public class TestTeiidLanguageToIbis {

	private TransformationMetadata metadata;
	private IbisExecutionFactory translator;
	private TranslationUtility utility;

	private QueryMetadataInterface setUp(String ddl, String vdbName,
			String modelName) throws Exception {

		this.translator = new IbisExecutionFactory();
		this.translator.start();

		metadata = RealMetadataFactory.fromDDL(ddl, vdbName, modelName);
		this.utility = new TranslationUtility(metadata);

		return metadata;
	}

	private String getSolrTranslation(String sql) throws IOException, Exception {
		Select select = (Select) getCommand(sql);
		IbisSQLHierarchyVistor visitor = new IbisSQLHierarchyVistor(
				this.utility.createRuntimeMetadata());
		visitor.visit(select);
		//return visitor.getTranslatedSQL();
		return visitor.getResolvedPath();

	}

	public Command getCommand(String sql) throws IOException, Exception {

		CommandBuilder builder = new CommandBuilder(setUp(
				ObjectConverterUtil.convertFileToString(UnitTestUtil
						.getTestDataFile("test.ddl")), "exampleVDB",
				"exampleModel"));
		return builder.getCommand(sql);

	}

	
	public void testSelectStar() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select id from content"), "*:*");

	}
	
	/**
	 * SELECT * FROM content WHERE type = "article"; 
	 * Resolved Query Param ?v=1&q=type:article
	 * Query Description Selects content by type. Other values for type include: video, gallery, image, section, topic, profile, specialcoverage, blog, episode, show, audio
	 * @throws Exception
	 */
	@Test
	public void testQueryOne() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select id from content where type='article'"), "?v=1&q=type:article");

	}
	
	
	/**
	 * SELECT * FROM content WHERE type = "article" ORDER BY publishdate; 
	 * Resolved Query Param ?v=1&q=type:article
	 * Selects by type order by publishdate ascending (oldest on top) 
	 * @throws Exception
	 */
	@Test
	public void testQueryTwo() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select id from content where type = 'article' order by publishdate "), "?v=1&q=type:article&sort=publishDate%20asc");

	}
	
	/**
	 * SELECT * FROM content WHERE type = "article" ORDER BY publishdate descending; 
	 * Resolved Query Param ?v=1&q=type:article
	 * Selects by type order by publishdate ascending (oldest on top) 
	 * @throws Exception
	 */
	@Test
	public void testQueryThree() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select id from content where type = 'article' order by publishdate desc"), "?v=1&q=type:article&sort=publishDate%20desc");

	}
	
	/**
	 * SELECT * FROM content WHERE type = "article" ORDER BY headline;
	 * ?v=1&q=type:article&sort=headline%20desc
	 * Selects by type orders alphanumerically on headline
	 * @throws Exception
	 */
	@Test
	public void testQueryFour() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select id from content where type = 'article' order by headline"), "?v=1&q=type:article&sort=headline%20asc");

	}
	
	
	/**
     * SELECT * FROM content WHERE type = "article" ORDER BY publishdate LIMIT 20;
     * ?v=1&q=type:article&sort=publishDate%20desc&rows=20
     * Selects by type orders by publishdate limits to 20 results
	 * @throws Exception
	 */
	@Test
	public void testQueryFive() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select id from content where type = 'article' order by publishdate desc limit 20"), "?v=1&q=type:article&sort=publishDate%20desc&rows=20");

	}
	
	/**
     * SELECT * FROM content WHERE type IN ("article", "video");
     * ?v=1&q=type:article%20OR%20type:video
     * Selects by multiple types
	 * @throws Exception
	 */
	@Test
	public void testQuerySix() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select * from content where type in ('article','video')"), "?v=1&q=type:article%20OR%20type:video");

	}
	
	/**
     * SELECT *  FROM content WHERE type != "video";
     * ?v=1&q=-type:video
     * Selects negatively by type (return everything except video)
	 * @throws Exception
	 */
	@Test
	public void testQuerySeven() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select * from content where type != 'video'"), "?v=1&q=-type:video");

	}
	
	/**
     * SELECT * FROM content WHERE type = "article" AND source = "cnn";
     * ?v=1&q=type:article%20AND%20source:CNN
     * Selects by type and source
	 * @throws Exception
	 */
	@Test
	public void testQueryEight() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select * from content where type = 'article' and source = 'CNN'"), "?v=1&q=type:article%20AND%20source:CNN");

	}
	
    //TODO Clarify query 9
	@Test
	public void testQueryNine() throws Exception {
		
	}
	
    //TODO Clarify query 10
	@Test
	public void testQueryTen() throws Exception {
		
	}
	
    /**
     * SELECT * FROM content WHERE uri = "/some/valid/uri/here/index.html";
     * Selects by uri
     * @throws Exception
     */
	@Test
	public void testQueryEleven() throws Exception {
		Assert.assertEquals(getSolrTranslation("select * from content where uri = '/some/valid/uri/here/index.html'"), "?v=1&q=uri:\"/some/valid/uri/here/index.html\"");
	}
  
	/**
     * SELECT * FROM content WHERE type = "article" AND source = "cnn";
     * ?v=1&q=type:article%20AND%20source:CNN
     * Selects by type and source
	 * @throws Exception
	 */
	@Test
	public void testQueryTwelve() throws Exception {

		// column test, all columns translates to price, weight and popularity
		Assert.assertEquals(getSolrTranslation("select * from content where type = 'article' and source = 'CNN'"), "?v=1&q=type:article%20AND%20source:CNN");

	}


	

/*	
	public void testSelectWhereEQ() throws Exception {
		Assert.assertEquals(
				getSolrTranslation("select price,weight,popularity from example where price=1"),
				"price:1.0");
	}
*/
}