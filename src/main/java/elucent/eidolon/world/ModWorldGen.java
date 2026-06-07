package elucent.eidolon.world;

import elucent.eidolon.Eidolon;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModWorldGen {
    private ModWorldGen() {
    }

    public static void init() {
        GameRegistry.registerWorldGenerator(new EidolonWorldGenerator(), 20);
        Eidolon.LOGGER.info("Registered Eidolon world generation");
    }
}
