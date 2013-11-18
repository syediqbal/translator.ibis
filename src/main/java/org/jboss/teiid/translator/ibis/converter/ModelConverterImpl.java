package org.jboss.teiid.translator.ibis.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teiid.language.DerivedColumn;
import org.teiid.metadata.Column;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.TranslatorException;

public class ModelConverterImpl implements ModelConverter {

    /*
     * source model metadata that exposes source model definitions from the
     * xmi files.
     */
    private RuntimeMetadata sourceModelMetadata;

    /*
     * the column names specified in the "select [column list] from ..." SQL
     * query.
     */
    private List<DerivedColumn> columns;

    private Map<String, ConverterStrategy> converterStrategies;

    public ModelConverterImpl(RuntimeMetadata sourceModelMetadata,
            List<DerivedColumn> columns) {

        this.sourceModelMetadata = sourceModelMetadata;
        this.columns = columns;

        // low-priority TODO: discover through annotation instead of hard-coding
        converterStrategies = new HashMap<String, ConverterStrategy>();
        ConverterStrategy richTextStrategy = new RichTextStrategy();
        converterStrategies.put(richTextStrategy.getSupportedNativeType(),
            richTextStrategy);
    }

    @Override
    public List<?> convertToTeiid(String ibisModelJson) throws TranslatorException {

        List<Object> row = new ArrayList<Object>();

        for (DerivedColumn column: columns) {
            // TODO Do we need the full name or a short field name as the key?
            // If latter, we need to "borrow" the code to get the short name
            // available in the Solr translator.
            Column sourceModelColumn = sourceModelMetadata.getColumn(column.toString());
            String nativeType = sourceModelColumn.getNativeType();
            String jpath = sourceModelColumn.getNameInSource();
            Object rawValue = getRawValue(ibisModelJson, jpath);
            ConverterStrategy strategy = converterStrategies.get(nativeType);
            // TODO May need additional type-based conversion to adhere to
            // the expected type of the column.
            Object convertedValue = strategy != null ?
                strategy.convert(rawValue) :
                rawValue;
            row.add(convertedValue);
        }

        return row;
    }

    private Object getRawValue(String ibisModelJson, String jpath) {
        // TODO Use jpath to resolve the value.
        return null;
    }

}
