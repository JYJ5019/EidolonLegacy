package elucent.eidolon.deity;

import elucent.eidolon.Reference;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Deities {
    private static final Map<ResourceLocation, Deity> DEITIES = new LinkedHashMap<>();

    public static final Deity DARK_DEITY = register(new DarkDeity(
            new ResourceLocation(Reference.MOD_ID, "dark"), 154, 77, 255));
    public static final Deity LIGHT_DEITY = register(new LightDeity(
            new ResourceLocation(Reference.MOD_ID, "light"), 255, 230, 117));

    private Deities() {
    }

    public static void init() {
        // Forces static registration.
    }

    public static Deity register(Deity deity) {
        DEITIES.put(deity.getId(), deity);
        return deity;
    }

    public static Deity find(ResourceLocation id) {
        return DEITIES.get(id);
    }

    public static Collection<Deity> getDeities() {
        return DEITIES.values();
    }
}
