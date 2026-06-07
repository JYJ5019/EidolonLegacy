package elucent.eidolon.spell;

import elucent.eidolon.Reference;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Runes {
    private static final Map<ResourceLocation, Rune> RUNES = new LinkedHashMap<>();
    private static boolean initialized;

    private Runes() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        register(new Rune(new ResourceLocation(Reference.MOD_ID, "sin")) {
            @Override
            public RuneResult doEffect(SignSequence sequence) {
                sequence.addRight(Signs.WICKED_SIGN);
                return RuneResult.PASS;
            }
        });

        register(new Rune(new ResourceLocation(Reference.MOD_ID, "crimson_rose")) {
            @Override
            public RuneResult doEffect(SignSequence sequence) {
                if (sequence.removeRightmostN(Signs.WICKED_SIGN, 2)) {
                    sequence.addRight(Signs.BLOOD_SIGN);
                    return RuneResult.PASS;
                }
                return RuneResult.FAIL;
            }
        });

        register(new Rune(new ResourceLocation(Reference.MOD_ID, "ascend")) {
            @Override
            public RuneResult doEffect(SignSequence sequence) {
                if (sequence.removeRightmost(Signs.BLOOD_SIGN)) {
                    sequence.addRight(Signs.SOUL_SIGN);
                    return RuneResult.PASS;
                }
                return RuneResult.FAIL;
            }
        });
    }

    public static void register(Rune rune) {
        RUNES.put(rune.getRegistryName(), rune);
    }

    public static Rune find(ResourceLocation id) {
        return RUNES.get(id);
    }

    public static Collection<Rune> getRunes() {
        return RUNES.values();
    }
}
