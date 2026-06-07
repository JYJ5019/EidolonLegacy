package elucent.eidolon.spell;

import elucent.eidolon.Reference;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Signs {
    private static final List<Sign> SIGNS = new ArrayList<>();
    private static final Map<ResourceLocation, Sign> SIGN_MAP = new LinkedHashMap<>();

    public static final Sign WICKED_SIGN = register("wicked", 154, 77, 255);
    public static final Sign SACRED_SIGN = register("sacred", 255, 230, 117);
    public static final Sign BLOOD_SIGN = register("blood", 255, 51, 85);
    public static final Sign SOUL_SIGN = register("soul", 230, 138, 226);
    public static final Sign MIND_SIGN = register("mind", 90, 121, 255);
    public static final Sign FLAME_SIGN = register("flame", 255, 128, 64);
    public static final Sign WINTER_SIGN = register("winter", 112, 149, 210);
    public static final Sign HARMONY_SIGN = register("harmony", 141, 141, 195);
    public static final Sign DEATH_SIGN = register("death", 123, 140, 70);
    public static final Sign WARDING_SIGN = register("warding", 118, 204, 175);
    public static final Sign MAGIC_SIGN = register("magic", 167, 85, 192);

    private Signs() {
    }

    private static Sign register(String name, int red, int green, int blue) {
        ResourceLocation id = new ResourceLocation(Reference.MOD_ID, name);
        return register(new Sign(id, new ResourceLocation(Reference.MOD_ID, "particle/" + name + "_sign"),
                packColor(255, red, green, blue)));
    }

    public static Sign register(Sign sign) {
        SIGNS.add(sign);
        SIGN_MAP.put(sign.getRegistryName(), sign);
        return sign;
    }

    public static Sign find(ResourceLocation id) {
        return SIGN_MAP.get(id);
    }

    public static List<Sign> getSigns() {
        return Collections.unmodifiableList(SIGNS);
    }

    private static int packColor(int alpha, int red, int green, int blue) {
        return ((alpha & 255) << 24) | ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255);
    }
}
