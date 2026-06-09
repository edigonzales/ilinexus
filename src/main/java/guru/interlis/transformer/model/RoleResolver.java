package guru.interlis.transformer.model;

import ch.interlis.ili2c.metamodel.AbstractClassDef;
import ch.interlis.ili2c.metamodel.AssociationDef;
import ch.interlis.ili2c.metamodel.Cardinality;
import ch.interlis.ili2c.metamodel.Container;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Table;
import guru.interlis.transformer.mapping.plan.RefPlan;

public final class RoleResolver {

    private final TypeSystemFacade targetTypeSystem;

    public RoleResolver(TypeSystemFacade targetTypeSystem) {
        this.targetTypeSystem = targetTypeSystem;
    }

    public record RoleCardinality(long min, long max) {
        public boolean isRequired() {
            return min > 0;
        }

        public boolean isUnbounded() {
            return max == Long.MAX_VALUE;
        }
    }

    public ResolvedRole requireRole(
            TypeSystemFacade targetTypeSystem,
            String ownerClass,
            String roleName,
            String associationName) {
        if (ownerClass == null || roleName == null) return null;
        RoleDef role = targetTypeSystem.resolveRole(ownerClass, roleName);
        if (role == null) return null;

        Container container = role.getContainer();
        AssociationDef association = null;
        if (container instanceof AssociationDef assoc) {
            if (associationName != null && !associationName.equals(assoc.getName())) {
                return null;
            }
            association = assoc;
        } else if (associationName != null) {
            return null;
        }

        String destinationClass = null;
        if (association != null) {
            for (RoleDef other : association.getRoles()) {
                if (other == role) continue;
                AbstractClassDef dest = other.getDestination();
                if (dest instanceof Table table) {
                    destinationClass = TypeSystemFacade.getScopedName(table);
                } else if (dest != null && dest.getName() != null) {
                    destinationClass = dest.getName();
                }
            }
        }
        if (destinationClass == null) {
            AbstractClassDef dest = role.getDestination();
            if (dest instanceof Table table) {
                destinationClass = TypeSystemFacade.getScopedName(table);
            } else if (dest != null && dest.getName() != null) {
                destinationClass = dest.getName();
            }
        }

        Cardinality card = role.getCardinality();
        long min = card != null ? card.getMinimum() : 0;
        long max = card != null ? card.getMaximum() : Cardinality.UNBOUND;

        return new ResolvedRole(role, association, destinationClass, min, max);
    }

    public String resolveExpectedTargetClass(RefPlan refPlan, String targetClassPath) {
        if (refPlan == null || targetClassPath == null) return null;
        String roleName = refPlan.targetRoleName();
        if (roleName == null) return null;
        return targetTypeSystem.getRoleTargetClass(targetClassPath, roleName);
    }

    public RoleCardinality getTargetRoleCardinality(RefPlan refPlan, String targetClassPath) {
        if (refPlan == null || targetClassPath == null) return new RoleCardinality(0, Long.MAX_VALUE);
        String roleName = refPlan.targetRoleName();
        if (roleName == null) return new RoleCardinality(0, Long.MAX_VALUE);
        long min = targetTypeSystem.getRoleCardinalityMin(targetClassPath, roleName);
        long max = targetTypeSystem.getRoleCardinalityMax(targetClassPath, roleName);
        return new RoleCardinality(min, max);
    }

    public String getAssociationName(RefPlan refPlan, String targetClassPath) {
        if (refPlan == null || targetClassPath == null) return null;
        String roleName = refPlan.targetRoleName();
        if (roleName == null) return null;
        return targetTypeSystem.getRoleAssociation(targetClassPath, roleName);
    }

    public boolean roleExists(String classPath, String roleName) {
        return targetTypeSystem.roleExists(classPath, roleName);
    }
}
