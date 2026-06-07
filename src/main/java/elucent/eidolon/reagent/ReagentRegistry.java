package elucent.eidolon.reagent;

import elucent.eidolon.Reference;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReagentRegistry {
    private static final Map<ResourceLocation, Reagent> REAGENTS = new LinkedHashMap<>();

    public static final Reagent STEAM = register(new SteamReagent(new ResourceLocation(Reference.MOD_ID, "steam")));
    public static final Reagent ESPRIT = register(new EspritReagent(new ResourceLocation(Reference.MOD_ID, "esprit")));
    public static final Reagent CRIMSOL = register(new CrimsolReagent(new ResourceLocation(Reference.MOD_ID, "crimsol")));

    private ReagentRegistry() {
    }

    public static Reagent register(Reagent reagent) {
        REAGENTS.put(reagent.getRegistryName(), reagent);
        return reagent;
    }

    public static Collection<Reagent> getReagents() {
        return REAGENTS.values();
    }

    @Nullable
    public static Reagent find(ResourceLocation location) {
        return REAGENTS.get(location);
    }
}
