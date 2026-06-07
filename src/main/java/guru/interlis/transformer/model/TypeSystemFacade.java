package guru.interlis.transformer.model;

import ch.interlis.ili2c.metamodel.AttributeDef;
import ch.interlis.ili2c.metamodel.Domain;
import ch.interlis.ili2c.metamodel.Element;
import ch.interlis.ili2c.metamodel.Extendable;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.RoleDef;
import ch.interlis.ili2c.metamodel.Table;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class TypeSystemFacade {

    private final TransferDescription td;

    public TypeSystemFacade(TransferDescription td) {
        this.td = td;
    }

    public boolean classExists(String qualifiedPath) {
        return resolveClass(qualifiedPath) != null;
    }

    public Table resolveClass(String qualifiedPath) {
        IliPath path = IliPath.parse(qualifiedPath);
        if (path.length() < 3) return null;

        Iterator<Model> modelIt = td.iterator();
        while (modelIt.hasNext()) {
            Model model = modelIt.next();
            Iterator<Element> elIt = model.iterator();
            while (elIt.hasNext()) {
                Element el = elIt.next();
                if (el instanceof Topic topic) {
                    if (!topicNameMatches(topic, path.topic())) continue;
                    if (!modelNameMatches(model, path.model())) continue;
                    Iterator<Element> telIt = topic.iterator();
                    while (telIt.hasNext()) {
                        Element tel = telIt.next();
                        if (tel instanceof Table table) {
                            if (table.getName() != null
                                    && table.getName().equals(path.className())) {
                                return table;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean attributeExists(String classPath, String attrName) {
        Table table = resolveClass(classPath);
        if (table == null) return false;
        return findAttribute(table, attrName) != null;
    }

    public AttributeDef findAttribute(Table table, String attrName) {
        Iterator<Extendable> it = table.getAttributes();
        while (it.hasNext()) {
            Extendable ext = it.next();
            if (ext instanceof AttributeDef attr) {
                if (attr.getName() != null && attr.getName().equals(attrName)) {
                    return attr;
                }
            }
        }
        return null;
    }

    public boolean roleExists(String classPath, String roleName) {
        Table table = resolveClass(classPath);
        if (table == null) return false;
        @SuppressWarnings("rawtypes")
        Iterator it = table.getDefinedRoles();
        if (it == null) return false;
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof RoleDef role) {
                if (role.getName() != null && role.getName().equals(roleName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getAttributeTypeString(String classPath, String attrName) {
        AttributeDef attr = resolveAttribute(classPath, attrName);
        if (attr == null) return null;
        ch.interlis.ili2c.metamodel.Type type = attr.getDomain();
        return type != null ? type.toString() : null;
    }

    public boolean isMandatory(String classPath, String attrName) {
        AttributeDef attr = resolveAttribute(classPath, attrName);
        if (attr == null) return false;
        var card = attr.getCardinality();
        return card != null && card.getMinimum() > 0;
    }

    public List<ModelInventory> listAllModels() {
        IliModelService service = new IliModelService();
        return List.of(service.buildInventory(td, null));
    }

    public TransferDescription getTransferDescription() {
        return td;
    }

    public String getOidType(String classPath) {
        Topic topic = resolveTopic(classPath);
        if (topic == null) return null;
        return formatOidDomainType(topic.getOid());
    }

    public String getBasketOidType(String classPath) {
        Topic topic = resolveTopic(classPath);
        if (topic == null) return null;
        return formatOidDomainType(topic.getBasketOid());
    }

    private Topic resolveTopic(String classPath) {
        IliPath path = IliPath.parse(classPath);
        if (path.length() < 2) return null;
        for (Iterator<Model> modelIt = td.iterator(); modelIt.hasNext(); ) {
            Model model = modelIt.next();
            if (!modelNameMatches(model, path.model())) continue;
            Iterator<Element> elIt = model.iterator();
            while (elIt.hasNext()) {
                Element el = elIt.next();
                if (el instanceof Topic topic) {
                    if (topicNameMatches(topic, path.topic())) {
                        return topic;
                    }
                }
            }
        }
        return null;
    }

    private static String formatOidDomainType(Domain oidDomain) {
        if (oidDomain == null) return "STANDARDOID";
        if (oidDomain.getName() != null && !oidDomain.getName().isBlank()) {
            return oidDomain.getName();
        }
        ch.interlis.ili2c.metamodel.Type type = oidDomain.getType();
        if (type != null) {
            String className = type.getClass().getSimpleName();
            return switch (className) {
                case "TextOIDType" -> "TEXT OID";
                case "NumericOIDType" -> "NUMERIC OID";
                case "AnyOIDType" -> "UUIDOID";
                case "NoOid" -> "NO OID";
                default -> className;
            };
        }
        return "STANDARDOID";
    }

    private AttributeDef resolveAttribute(String classPath, String attrName) {
        Table table = resolveClass(classPath);
        if (table == null) return null;
        return findAttribute(table, attrName);
    }

    private static boolean modelNameMatches(Model model, String name) {
        String modelName = model.getName();
        return modelName != null && modelName.equals(name);
    }

    private static boolean topicNameMatches(Topic topic, String name) {
        String topicName = topic.getName();
        return topicName != null && topicName.equals(name);
    }
}
