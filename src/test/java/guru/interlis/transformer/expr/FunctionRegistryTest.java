package guru.interlis.transformer.expr;

import guru.interlis.transformer.mapping.plan.TypeInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FunctionRegistryTest {

    @Test
    void registersAndResolvesFunction() {
        FunctionRegistry registry = new FunctionRegistry();
        registry.register("myFunc", TypeInfo.TEXT, List.of(),
                (args, ctx) -> new TextValue("hello"));

        assertThat(registry.resolve("myFunc")).isPresent();
        assertThat(registry.resolve("myFunc").get().returnType()).isEqualTo(TypeInfo.TEXT);
        assertThat(registry.resolve("myFunc").get().nonDeterministic()).isFalse();
    }

    @Test
    void resolvesCaseInsensitively() {
        FunctionRegistry registry = new FunctionRegistry();
        registry.register("Truncate", TypeInfo.TEXT,
                List.of(new FunctionDef.FunctionParam("v", TypeInfo.TEXT)),
                (args, ctx) -> new TextValue(""));

        assertThat(registry.resolve("truncate")).isPresent();
        assertThat(registry.resolve("TRUNCATE")).isPresent();
        assertThat(registry.resolve("Truncate")).isPresent();
    }

    @Test
    void rejectsDuplicateRegistration() {
        FunctionRegistry registry = new FunctionRegistry();
        registry.register("f", TypeInfo.TEXT, List.of(), (a, c) -> new TextValue(""));
        assertThatThrownBy(() -> registry.register("f", TypeInfo.NUMERIC, List.of(), (a, c) -> new NumberValue(0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void returnsEmptyForUnknownFunction() {
        FunctionRegistry registry = new FunctionRegistry();
        assertThat(registry.resolve("nonexistent")).isEmpty();
    }

    @Test
    void nonDeterministicRegistration() {
        FunctionRegistry registry = new FunctionRegistry();
        registry.registerNonDeterministic("rand", TypeInfo.NUMERIC, List.of(),
                (args, ctx) -> new NumberValue(Math.random()));

        var def = registry.resolve("rand");
        assertThat(def).isPresent();
        assertThat(def.get().nonDeterministic()).isTrue();
    }
}
