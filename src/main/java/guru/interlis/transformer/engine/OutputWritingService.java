package guru.interlis.transformer.engine;

import ch.interlis.iom.IomObject;
import ch.interlis.iox.IoxWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class OutputWritingService {

    public long writeOutputs(
            Map<String, IoxWriter> writersByOutputId,
            Map<String, Map<String, List<IomObject>>> objectsByOutputAndBasket) throws Exception {
        long written = 0;
        for (var entry : writersByOutputId.entrySet()) {
            String outputId = entry.getKey();
            IoxWriter writer = entry.getValue();
            writer.write(new ch.interlis.iox_j.StartTransferEvent("ili-transformer", null, null));
            Map<String, List<IomObject>> byBasket = objectsByOutputAndBasket.getOrDefault(outputId, Map.of());
            for (var basketEntry : byBasket.entrySet()) {
                String[] parts = basketEntry.getKey().split("::", 2);
                String topic = parts[0];
                String basketId = parts.length > 1 && !parts[1].isEmpty() ? parts[1] : null;
                writer.write(new ch.interlis.iox_j.StartBasketEvent(topic, basketId));
                List<IomObject> sorted = new ArrayList<>(basketEntry.getValue());
                sorted.sort(Comparator.comparing(IomObject::getobjecttag)
                        .thenComparing(IomObject::getobjectoid));
                for (IomObject target : sorted) {
                    writer.write(new ch.interlis.iox_j.ObjectEvent(target));
                    written++;
                }
                writer.write(new ch.interlis.iox_j.EndBasketEvent());
            }
            writer.write(new ch.interlis.iox_j.EndTransferEvent());
            writer.flush();
            writer.close();
        }
        return written;
    }
}
