package org.jboss.teiid.translator.ibis.converter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        DerivedColumn arrayColumn = mock(DerivedColumn.class);
        DerivedColumn booleanColumn = mock(DerivedColumn.class);
        DerivedColumn numberColumn = mock(DerivedColumn.class);
        DerivedColumn objectColumn = mock(DerivedColumn.class);
        DerivedColumn rickTextColumn = mock(DerivedColumn.class);
        DerivedColumn stringColumn = mock(DerivedColumn.class);
        when(arrayColumn.toString()).thenReturn("highlights");
        when(booleanColumn.toString()).thenReturn("hasImage");
        when(numberColumn.toString()).thenReturn("trt");
        when(objectColumn.toString()).thenReturn("elements");
        when(rickTextColumn.toString()).thenReturn("description");
        when(stringColumn.toString()).thenReturn("id");
        derivedColumns.add(arrayColumn);
        derivedColumns.add(booleanColumn);
        derivedColumns.add(numberColumn);
        derivedColumns.add(objectColumn);
        derivedColumns.add(rickTextColumn);
        derivedColumns.add(stringColumn);

        Column column1 = mock(Column.class);
        Column column2 = mock(Column.class);
        Column column3 = mock(Column.class);
        Column column4 = mock(Column.class);
        Column column5 = mock(Column.class);
        Column column6 = mock(Column.class);
        when(column1.getRuntimeType()).thenReturn("string");
        when(column2.getRuntimeType()).thenReturn("boolean");
        when(column3.getRuntimeType()).thenReturn("float");
        when(column4.getRuntimeType()).thenReturn("string");
        when(column5.getRuntimeType()).thenReturn("string");
        when(column6.getRuntimeType()).thenReturn("string");
        when(column1.getNativeType()).thenReturn("array");
        when(column2.getNativeType()).thenReturn("boolean");
        when(column3.getNativeType()).thenReturn("number");
        when(column4.getNativeType()).thenReturn("object");
        when(column5.getNativeType()).thenReturn("rich_text");
        when(column6.getNativeType()).thenReturn("string");

        when(column1.getNameInSource()).thenReturn("highlights");
        when(column2.getNameInSource()).thenReturn("hasImage");
        when(column3.getNameInSource()).thenReturn("trt");
        when(column4.getNameInSource()).thenReturn("elements");
        when(column5.getNameInSource()).thenReturn("seo.description");
        when(column6.getNameInSource()).thenReturn("_id");

        when(sourceModelMetadata.getColumn("highlights")).thenReturn(column1);
        when(sourceModelMetadata.getColumn("hasImage")).thenReturn(column2);
        when(sourceModelMetadata.getColumn("trt")).thenReturn(column3);
        when(sourceModelMetadata.getColumn("elements")).thenReturn(column4);
        when(sourceModelMetadata.getColumn("description")).thenReturn(column5);
        when(sourceModelMetadata.getColumn("id")).thenReturn(column6);

        when(jsonExtractor.resolve(jsonDoc, "highlights")).thenReturn(new ArrayList<String>());
        when(jsonExtractor.resolve(jsonDoc, "hasImage")).thenReturn(true);
        when(jsonExtractor.resolve(jsonDoc, "trt")).thenReturn(2.34f);
        when(jsonExtractor.resolve(jsonDoc, "elements")).thenReturn(new HashMap<String,Object>());
        Map<String, Object> description = new HashMap<String, Object>();
        description.put("paragraphs", new ArrayList<Object>());
        when(jsonExtractor.resolve(jsonDoc, "seo.description")).thenReturn(description);
        when(jsonExtractor.resolve(jsonDoc, "_id")).thenReturn("section_1");

        converter = new ModelConverterImpl(sourceModelMetadata, derivedColumns, jsonExtractor);
    }

    @Test
    public void testConverter() throws TranslatorException, ConversionException {

        List<?> row = converter.convertToTeiid(jsonDoc);

        verify(jsonExtractor).resolve(jsonDoc, "highlights");
        verify(jsonExtractor).resolve(jsonDoc, "hasImage");
        verify(jsonExtractor).resolve(jsonDoc, "trt");
        verify(jsonExtractor).resolve(jsonDoc, "elements");
        verify(jsonExtractor).resolve(jsonDoc, "seo.description");
        verify(jsonExtractor).resolve(jsonDoc, "_id");

        Assert.assertEquals(6, row.size());
        Assert.assertEquals("[]", row.get(0));
        Assert.assertEquals(true, row.get(1));
        Assert.assertTrue(2.34f - (Float)row.get(2) < 0.000001);
        Assert.assertEquals("{}", row.get(3));
        Assert.assertEquals("{paragraphs=[]}", row.get(4)); // TODO This is not the final stringified rich text we are going to store
        Assert.assertEquals("section_1", row.get(5));
    }
}
