package guru.interlis.transformer.state;

public interface SourceLookupIndex {

    void index(SourceRecord record);

    java.util.List<SourceRecord> lookup(LookupKey key);
}
