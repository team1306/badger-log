package badgerlog.transformations;

import badgerlog.annotations.StructType;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.aspects.ProcessingUtils;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.StructValueEntry;
import badgerlog.networktables.SubtableEntry;
import badgerlog.networktables.ValueEntry;
import badgerlog.utilities.TypeParser;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.Measure;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructFetcher;

import java.util.Optional;

public class EntryFactory {
    @SuppressWarnings("unchecked")
    public static <T> NTEntry<T> createMapping(String key, T value, Mapping<T, ?> mapping, NetworkTableType networkTableType){
        return new ValueEntry<>(key, (Class<T>) value.getClass(), value, mapping, networkTableType);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> NTEntry<T> createStruct(String key, T value, StructType structType){
        Optional<Struct<?>> structOptional = StructFetcher.fetchStructDynamic(value.getClass());
        
        Struct<T> struct;
        if(structOptional.isPresent()){
            struct = (Struct<T>) structOptional.get();
        }else{
            Optional<Struct<Object>> generatedStruct = TypeParser.generateStructFromTypeIfPossible((Class<Object>) value.getClass());
            
            if(generatedStruct.isPresent()){
                struct = (Struct<T>) generatedStruct.get();
            }
            else{
                return null;
            }
        }
        
        return switch (structType){
            case STRUCT -> new StructValueEntry<>(key, struct, value);
            case SUB_TABLE -> new SubtableEntry<>(key, struct, value);
        };
    }

    @SuppressWarnings("unchecked")
    public static NTEntry<?> createEntry(String key, Object value, Configuration config){
        Class<?> type = value.getClass();

        String tableType = ProcessingUtils.getStringFromClass(type);

        NTEntry<?> entry = null;

        //todo add annotation processing for structs

        if (!tableType.isEmpty()){
            entry = EntryFactory.createMapping(key, value, Mapping.identity(), NetworkTableType.getFromString(tableType));
        }

        if(type.isAssignableFrom(Measure.class)){
            if(!config.getUnit().isEmpty()){
                entry = EntryFactory.createMapping(key, value, (Mapping<Object, ?>) UnitConversions.createMapping(config.getUnit()), NetworkTableType.kDouble);
            }
            else{
                Measure<?> measure = (Measure<?>) value;
                entry = EntryFactory.createMapping(key, measure, (Mapping<? super Measure<?>, ?>) UnitConversions.createMapping(measure.baseUnit()), NetworkTableType.kDouble);
            }
        }

        if(config.getStructType() != null){
            entry = EntryFactory.createStruct(key, value, config.getStructType());
        }

        if(entry == null){
            //todo better logging
        }

        return entry;
    }
}