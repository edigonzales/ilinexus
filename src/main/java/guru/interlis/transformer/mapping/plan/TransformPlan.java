package guru.interlis.transformer.mapping.plan;

import guru.interlis.transformer.diag.DiagnosticCollector;
import guru.interlis.transformer.model.TypeSystemFacade;
import guru.interlis.transformer.state.BasketStrategy;
import guru.interlis.transformer.state.OidStrategy;

public record TransformPlan(
        String name,
        String direction,
        String failPolicy,
        java.util.List<RulePlan> rules,
        java.util.Map<String, TypeSystemFacade> sourceTypeSystems,
        java.util.Map<String, TypeSystemFacade> targetTypeSystems,
        DiagnosticCollector diagnostics,
        OidStrategy oidStrategy,
        String oidNamespace,
        BasketStrategy basketStrategy,
        java.util.Map<String, java.util.Map<String, String>> enumMaps
) {}
