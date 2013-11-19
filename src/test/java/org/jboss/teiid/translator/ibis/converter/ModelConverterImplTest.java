package org.jboss.teiid.translator.ibis.converter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.teiid.language.DerivedColumn;
import org.teiid.metadata.Column;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.TranslatorException;

import com.cnn.extractors.json.JsonExtractor;

public class ModelConverterImplTest {

    private ModelConverterImpl converter;
    private RuntimeMetadata sourceModelMetadata = mock(RuntimeMetadata.class);
    private List<DerivedColumn> derivedColumns = new ArrayList<DerivedColumn>();
    private JsonExtractor jsonExtractor = mock(JsonExtractor.class);

    private String jsonDoc = "dummy_doc";

    @Before
    public void setUp() throws Exception {

        DerivedColumn derivedColumn1 = mock(DerivedColumn.class);
        DerivedColumn derivedColumn2 = mock(DerivedColumn.class);
        DerivedColumn derivedColumn3 = mock(DerivedColumn.class);
        when(derivedColumn1.toString()).thenReturn("id");
        when(derivedColumn2.toString()).thenReturn("rev");
        when(derivedColumn3.toString()).thenReturn("description");
        derivedColumns.add(derivedColumn1);
        derivedColumns.add(derivedColumn2);
        derivedColumns.add(derivedColumn3);

        Column column1 = mock(Column.class);
        Column column2 = mock(Column.class);
        Column column3 = mock(Column.class);
        Column column4 = mock(Column.class);
        when(column1.getRuntimeType()).thenReturn("string");
        when(column2.getRuntimeType()).thenReturn("string");
        when(column3.getRuntimeType()).thenReturn("string");
        when(column4.getRuntimeType()).thenReturn("string");
        when(column1.getNativeType()).thenReturn("string");
        when(column2.getNativeType()).thenReturn("string");
        when(column3.getNativeType()).thenReturn("string");
        when(column4.getNativeType()).thenReturn("string");
        when(column1.getNameInSource()).thenReturn("_id");
        when(column2.getNameInSource()).thenReturn("_rev");
        when(column3.getNameInSource()).thenReturn("seo.description");
        when(column4.getNameInSource()).thenReturn("slug");

        when(sourceModelMetadata.getColumn("id")).thenReturn(column1);
        when(sourceModelMetadata.getColumn("rev")).thenReturn(column2);
        when(sourceModelMetadata.getColumn("description")).thenReturn(column3);

        when(jsonExtractor.resolve(jsonDoc, "string", "string", "_id")).thenReturn("section_1");
        when(jsonExtractor.resolve(jsonDoc, "string", "string", "_rev")).thenReturn("rev_1");
        when(jsonExtractor.resolve(jsonDoc, "string", "string", "seo.description")).thenReturn("description_1");

        converter = new ModelConverterImpl(sourceModelMetadata, derivedColumns, jsonExtractor);
    }

    @Test
    public void testConverter() throws TranslatorException {

        List<?> row = converter.convertToTeiid(jsonDoc);

        verify(jsonExtractor).resolve(jsonDoc, "string", "string", "_id");
        verify(jsonExtractor).resolve(jsonDoc, "string", "string", "_rev");
        verify(jsonExtractor).resolve(jsonDoc, "string", "string", "seo.description");

        Assert.assertEquals(3, row.size());
        Assert.assertEquals("section_1", row.get(0));
        Assert.assertEquals("rev_1", row.get(1));
        Assert.assertEquals("description_1", row.get(2));
    }
}
