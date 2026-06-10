package guru.interlis.transformer.expr;

import ch.interlis.iom_j.Iom_jObject;
import guru.interlis.transformer.diag.DiagnosticCollector;
import guru.interlis.transformer.expr.builtins.LookupFunctions;
import guru.interlis.transformer.state.CanonicalValue;
import guru.interlis.transformer.state.InMemorySourceLookupIndex;
import guru.interlis.transformer.state.LookupKey;
import guru.interlis.transformer.state.SourceRecord;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LookupFunctionsTest {

    @Test
    void oidReturnsObjectOid() {
        FunctionRegistry registry = new FunctionRegistry();
        LookupFunctions.registerAll(registry);

        Iom_jObject obj = new Iom_jObject("Test.Topic.Class", "oid-123");
        EvalContext ctx = new EvalContext(Map.of("p", obj), null, "rule-1");

        Value result = registry.resolve("oid").get().implementation().apply(
                List.of(new TextValue("p")), ctx);

        assertThat(result).isInstanceOf(TextValue.class);
        assertThat(((TextValue) result).value()).isEqualTo("oid-123");
    }

    @Test
    void oidMissingAliasReturnsNull() {
        FunctionRegistry registry = new FunctionRegistry();
        LookupFunctions.registerAll(registry);
        DiagnosticCollector diag = new DiagnosticCollector();

        EvalContext ctx = new EvalContext(Map.of(), diag, "rule-1");
        Value result = registry.resolve("oid").get().implementation().apply(
                List.of(new TextValue("missing")), ctx);

        assertThat(result).isSameAs(NullValue.INSTANCE);
    }

    @Test
    void lookupFindsExactMatch() {
        FunctionRegistry registry = new FunctionRegistry();
        LookupFunctions.registerAll(registry);
        DiagnosticCollector diag = new DiagnosticCollector();

        InMemorySourceLookupIndex index = new InMemorySourceLookupIndex();
        Iom_jObject parent = new Iom_jObject("Test.Topic.Parent", "parent-oid");
        Iom_jObject child = new Iom_jObject("Test.Topic.Child", "child-oid");
        child.addattrobj("ParentRef", "Test.Topic.Parent").setobjectrefoid("parent-oid");
        child.setattrvalue("Ori", "150.0");

        index.index(new SourceRecord("in1", "basket1", "Test.Topic.Child", child));

        EvalContext ctx = new EvalContext(Map.of("p", parent), diag, "rule-1")
                .withLookupIndex(index);

        Value result = registry.resolve("lookup").get().implementation().apply(
                List.of(
                        new TextValue("Test.Topic.Child"),
                        new TextValue("ParentRef"),
                        new TextValue("parent-oid"),
                        new TextValue("Ori")
                ), ctx);

        assertThat(result).isInstanceOf(TextValue.class);
        assertThat(((TextValue) result).value()).isEqualTo("150.0");
    }

    @Test
    void lookupNoMatchReturnsNullWithDiagnostic() {
        FunctionRegistry registry = new FunctionRegistry();
        LookupFunctions.registerAll(registry);
        DiagnosticCollector diag = new DiagnosticCollector();

        InMemorySourceLookupIndex index = new InMemorySourceLookupIndex();
        EvalContext ctx = new EvalContext(Map.of(), diag, "rule-1")
                .withLookupIndex(index);

        Value result = registry.resolve("lookup").get().implementation().apply(
                List.of(
                        new TextValue("Test.Topic.Child"),
                        new TextValue("ParentRef"),
                        new TextValue("nonexistent"),
                        new TextValue("Ori")
                ), ctx);

        assertThat(result).isSameAs(NullValue.INSTANCE);
        assertThat(diag.warnings()).isGreaterThan(0);
    }

    @Test
    void lookupAmbiguousUsesFirstWithWarning() {
        FunctionRegistry registry = new FunctionRegistry();
        LookupFunctions.registerAll(registry);
        DiagnosticCollector diag = new DiagnosticCollector();

        InMemorySourceLookupIndex index = new InMemorySourceLookupIndex();
        Iom_jObject child1 = new Iom_jObject("Test.Topic.Child", "child-1");
        child1.addattrobj("ParentRef", "Test.Topic.Parent").setobjectrefoid("parent-oid");
        child1.setattrvalue("Ori", "100.0");

        Iom_jObject child2 = new Iom_jObject("Test.Topic.Child", "child-2");
        child2.addattrobj("ParentRef", "Test.Topic.Parent").setobjectrefoid("parent-oid");
        child2.setattrvalue("Ori", "200.0");

        index.index(new SourceRecord("in1", "basket1", "Test.Topic.Child", child1));
        index.index(new SourceRecord("in1", "basket1", "Test.Topic.Child", child2));

        EvalContext ctx = new EvalContext(Map.of(), diag, "rule-1")
                .withLookupIndex(index);

        Value result = registry.resolve("lookup").get().implementation().apply(
                List.of(
                        new TextValue("Test.Topic.Child"),
                        new TextValue("ParentRef"),
                        new TextValue("parent-oid"),
                        new TextValue("Ori")
                ), ctx);

        assertThat(result).isInstanceOf(TextValue.class);
        assertThat(((TextValue) result).value()).isIn("100.0", "200.0");
        assertThat(diag.warnings()).isGreaterThan(0);
    }

    @Test
    void lookupMissingIndexReturnsNull() {
        FunctionRegistry registry = new FunctionRegistry();
        LookupFunctions.registerAll(registry);
        DiagnosticCollector diag = new DiagnosticCollector();

        EvalContext ctx = new EvalContext(Map.of(), diag, "rule-1");
        Value result = registry.resolve("lookup").get().implementation().apply(
                List.of(new TextValue("Class"), new TextValue("Key"), new TextValue("Val"), new TextValue("Attr")),
                ctx);

        assertThat(result).isSameAs(NullValue.INSTANCE);
        assertThat(diag.errors()).isGreaterThan(0);
    }
}
