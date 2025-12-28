package badgerlog.transformations;

import badgerlog.annotations.StructType;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.StructValueEntry;
import badgerlog.networktables.SubtableEntry;
import badgerlog.networktables.ValueEntry;
import badgerlog.utilities.TypeParser;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructFetcher;

import java.util.Optional;

public class EntryFactory {
    @SuppressWarnings("unchecked")
    public static <T> NTEntry<T> create(String key, T value, Mapping<T, ?> mapping, NetworkTableType networkTableType){
        return new ValueEntry<>(key, (Class<T>) value.getClass(), value, mapping, networkTableType);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> NTEntry<T> create(String key, T value, StructType structType){
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
}