package org.jboss.teiid.translator.ibis.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teiid.language.DerivedColumn;
import org.teiid.metadata.Column;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.TranslatorException;

import com.cnn.extractors.json.JsonExtractor;

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

    /*
     * The resolver that
     */
    private JsonExtractor jsonExtractor;

    private Map<ConverterStrategyKey, ConverterStrategy> converterStrategies;

    public ModelConverterImpl(RuntimeMetadata sourceModelMetadata,
            List<DerivedColumn> columns, JsonExtractor jsonExtractor) {

        this.sourceModelMetadata = sourceModelMetadata;
        this.columns = columns;
        this.jsonExtractor = jsonExtractor;

        // low-priority TODO: discover through annotation instead of hard-coding
        converterStrategies = new HashMap<ConverterStrategyKey, ConverterStrategy>();
        addConverterStrategy(new StringStrategy());
        addConverterStrategy(new NumberStrategy());
        addConverterStrategy(new BooleanStrategy());
        addConverterStrategy(new ObjectStrategy());
        addConverterStrategy(new ArrayStrategy());
        addConverterStrategy(new RichTextStrategy());
    }

    @Override
    public List<?> convertToTeiid(String ibisModelJson) throws TranslatorException, ConversionException {

        List<Object> row = new ArrayList<Object>();

        for (DerivedColumn column: columns) {
            // TODO Do we need the full name or a short field name as the key?
            // If latter, we need to "borrow" the code to get the short name
            // available in the Solr translator.
            Column sourceModelColumn = sourceModelMetadata.getColumn(column.toString());
            Object rawValue = jsonExtractor.resolve(
                ibisModelJson,
                sourceModelColumn.getNameInSource());
            ConverterStrategy strategy = findConverterStrategy(
                sourceModelColumn.getRuntimeType(), sourceModelColumn.getNativeType());
            if (strategy == null) {
                throw new IllegalArgumentException("No converter strategy available for {" +
                    sourceModelColumn.getRuntimeType() + " <= " +
                    sourceModelColumn.getNativeType() + "}");
            }
            Object convertedValue = strategy.convert(rawValue);
            row.add(convertedValue);
        }

        return row;
    }

    private void addConverterStrategy(ConverterStrategy strategy) {
        ConverterStrategyKey key = new ConverterStrategyKey(
            strategy.getSupportedTeiidType(), strategy.getSupportedNativeType());
        converterStrategies.put(key, strategy);
    }

    private ConverterStrategy findConverterStrategy(String teiidType, String ibisTypeStr) {
        NativeTypes ibisType = NativeTypes.valueOf(ibisTypeStr.toUpperCase());
        ConverterStrategyKey key = new ConverterStrategyKey(teiidType, ibisType);
        return converterStrategies.get(key);
    }
}
