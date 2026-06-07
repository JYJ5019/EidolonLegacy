package elucent.eidolon.spell;

import elucent.eidolon.Reference;
import elucent.eidolon.deity.Deities;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Spells {
    private static final List<Spell> SPELLS = new ArrayList<>();
    private static final Map<ResourceLocation, Spell> SPELL_MAP = new LinkedHashMap<>();

    public static final Spell DARK_PRAYER = register(new PrayerSpell(
            id("dark_prayer"),
            Deities.DARK_DEITY,
            Signs.WICKED_SIGN, Signs.WICKED_SIGN, Signs.WICKED_SIGN));
    public static final Spell DARK_ANIMAL_SACRIFICE = register(new AnimalSacrificeSpell(
            id("dark_animal_sacrifice"),
            Deities.DARK_DEITY,
            Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.WICKED_SIGN));
    public static final Spell DARK_TOUCH = register(new DarkTouchSpell(
            id("dark_touch"),
            Signs.WICKED_SIGN, Signs.SOUL_SIGN, Signs.WICKED_SIGN, Signs.SOUL_SIGN));
    public static final Spell DARK_VILLAGER_SACRIFICE = register(new VillagerSacrificeSpell(
            id("dark_villager_sacrifice"),
            Deities.DARK_DEITY,
            Signs.BLOOD_SIGN, Signs.WICKED_SIGN, Signs.BLOOD_SIGN, Signs.SOUL_SIGN));
    public static final Spell LIGHT_PRAYER = register(new PrayerSpell(
            id("light_prayer"),
            Deities.LIGHT_DEITY,
            Signs.SACRED_SIGN, Signs.SACRED_SIGN, Signs.SACRED_SIGN));

    private Spells() {
    }

    public static void init() {
        DarkTouchSpell.init();
    }

    public static Spell register(Spell spell) {
        SPELLS.add(spell);
        SPELL_MAP.put(spell.getRegistryName(), spell);
        return spell;
    }

    public static Spell find(ResourceLocation id) {
        return SPELL_MAP.get(id);
    }

    public static Spell find(SignSequence signs) {
        for (Spell spell : SPELLS) {
            if (spell.matches(signs)) {
                return spell;
            }
        }
        return null;
    }

    public static List<Spell> getSpells() {
        return Collections.unmodifiableList(SPELLS);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(Reference.MOD_ID, path);
    }
}
