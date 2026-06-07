package elucent.eidolon.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import elucent.eidolon.spell.AltarEntries;
import elucent.eidolon.spell.AltarRitual;
import elucent.eidolon.spell.AltarRituals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.eidolon.Altar")
public final class AltarTweaker {
    private AltarTweaker() {
    }

    @ZenMethod
    public static void addItemResult(String id, IItemStack output, double capacity, double power, IIngredient[] offerings,
                                     @Optional IIngredient sacrifice) {
        addRitual(id, output, "item_result", capacity, power, null, sacrifice, null, 0.0F, offerings);
    }

    @ZenMethod
    public static void addTransform(String id, IItemStack output, IIngredient focus, double capacity, double power,
                                    IIngredient[] offerings, @Optional double health, @Optional IIngredient sacrifice) {
        addRitual(id, output, "item_transform", capacity, power, focus, sacrifice, null, (float) health, offerings);
    }

    @ZenMethod
    public static void addCharge(String id, IItemStack output, IIngredient focus, double capacity, double power,
                                 IIngredient[] offerings, @Optional IIngredient sacrifice) {
        addRitual(id, output, "item_charge", capacity, power, focus, sacrifice, null, 0.0F, offerings);
    }

    @ZenMethod
    public static void addSummon(String id, IItemStack displayOutput, String entityId, IIngredient focus,
                                 double capacity, double power, IIngredient[] offerings, @Optional IIngredient sacrifice) {
        addRitual(id, displayOutput, "entity_summon", capacity, power, focus, sacrifice, entityId, 0.0F, offerings);
    }

    @ZenMethod
    public static void addAbsorption(String id, IItemStack output, IIngredient focus, double capacity, double power,
                                     IIngredient[] offerings, @Optional IIngredient sacrifice) {
        addRitual(id, output, "absorption", capacity, power, focus, sacrifice, null, 0.0F, offerings);
    }

    @ZenMethod
    public static void addSanguine(String id, IItemStack output, IIngredient focus, IIngredient sacrifice,
                                   double capacity, double power, double health, IIngredient[] offerings) {
        addRitual(id, output, "sanguine", capacity, power, focus, sacrifice, null, (float) health, offerings);
    }

    @ZenMethod
    public static void addPurify(String id, IIngredient sacrifice, double capacity, double power,
                                 IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "purify", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addCrystal(String id, IIngredient sacrifice, double capacity, double power,
                                  IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "crystal", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addAllure(String id, IIngredient sacrifice, double capacity, double power,
                                 IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "allure", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addRepelling(String id, IIngredient sacrifice, double capacity, double power,
                                    IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "repelling", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addDeceit(String id, IIngredient sacrifice, double capacity, double power,
                                 IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "deceit", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addDaylight(String id, IIngredient sacrifice, double capacity, double power,
                                   IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "daylight", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addMoonlight(String id, IIngredient sacrifice, double capacity, double power,
                                    IIngredient[] offerings, @Optional IIngredient focus) {
        addEffectRitual(id, "moonlight", sacrifice, capacity, power, offerings, focus);
    }

    @ZenMethod
    public static void addOffering(String id, IItemStack item, double capacity, double power) {
        Item entryItem = item(item);
        boolean blockOffering = entryItem instanceof ItemBlock;
        boolean plateOffering = !(entryItem instanceof ItemBlock);
        addOffering(id, entryItem, capacity, power, blockOffering, plateOffering);
    }

    @ZenMethod
    public static void addBlockOffering(String id, IItemStack item, double capacity, double power) {
        addOffering(id, item(item), capacity, power, true, false);
    }

    @ZenMethod
    public static void addPlateOffering(String id, IItemStack item, double capacity, double power) {
        addOffering(id, item(item), capacity, power, false, true);
    }

    @ZenMethod
    public static void addOfferingModes(String id, IItemStack item, double capacity, double power,
                                        boolean blockOffering, boolean plateOffering) {
        addOffering(id, item(item), capacity, power, blockOffering, plateOffering);
    }

    @ZenMethod
    public static void removeOfferingById(String id) {
        ResourceLocation entryId = TweakerUtil.id(id);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Altar offering entry " + entryId) {
            @Override
            public void apply() {
                AltarEntries.removeEntry(entryId);
            }
        });
    }

    @ZenMethod
    public static void removeOfferingByItem(IItemStack item) {
        Item entryItem = item(item);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Altar offering entry for " + entryItem.getRegistryName()) {
            @Override
            public void apply() {
                AltarEntries.removeEntry(entryItem);
            }
        });
    }

    @ZenMethod
    public static void removeAllOfferings() {
        CraftTweakerAPI.apply(new NamedAction("Removing all Eidolon Altar offering entries") {
            @Override
            public void apply() {
                AltarEntries.removeAllEntries();
            }
        });
    }

    @ZenMethod
    public static void removeById(String id) {
        ResourceLocation recipeId = TweakerUtil.id(id);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Altar ritual " + recipeId) {
            @Override
            public void apply() {
                AltarRituals.removeRitual(recipeId);
            }
        });
    }

    @ZenMethod
    public static void removeByOutput(IIngredient output) {
        Ingredient ingredient = TweakerUtil.ingredient(output);
        CraftTweakerAPI.apply(new NamedAction("Removing Eidolon Altar rituals with output " + output.toCommandString()) {
            @Override
            public void apply() {
                AltarRituals.removeRitualsByOutput(ingredient);
            }
        });
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweakerAPI.apply(new NamedAction("Removing all Eidolon Altar rituals") {
            @Override
            public void apply() {
                AltarRituals.removeAllRituals();
            }
        });
    }

    private static void addOffering(String id, Item item, double capacity, double power,
                                    boolean blockOffering, boolean plateOffering) {
        ResourceLocation entryId = TweakerUtil.id(id);
        CraftTweakerAPI.apply(new NamedAction("Adding Eidolon Altar offering entry " + entryId) {
            @Override
            public void apply() {
                AltarEntries.addEntry(item, entryId, capacity, power, blockOffering, plateOffering);
            }
        });
    }

    private static Item item(IItemStack stack) {
        ItemStack converted = TweakerUtil.stack(stack);
        if (converted.isEmpty()) {
            throw new IllegalArgumentException("Altar offering item must not be empty");
        }
        return converted.getItem();
    }

    private static void addRitual(String id, IItemStack output, String behavior, double capacity, double power,
                                  IIngredient focus, IIngredient sacrifice, String entityId, float health,
                                  IIngredient[] offerings) {
        ResourceLocation recipeId = TweakerUtil.id(id);
        ItemStack result = TweakerUtil.stack(output);
        AltarRitual.BehaviorType behaviorType = behavior(behavior);
        Ingredient convertedFocus = focus == null ? Ingredient.EMPTY : TweakerUtil.ingredient(focus);
        Ingredient convertedSacrifice = sacrifice == null ? null : TweakerUtil.ingredient(sacrifice);
        ResourceLocation convertedEntity = entityId == null || entityId.isEmpty() ? null : TweakerUtil.id(entityId);
        List<Ingredient> convertedOfferings = offerings(offerings);
        CraftTweakerAPI.apply(new NamedAction("Adding Eidolon Altar ritual " + recipeId) {
            @Override
            public void apply() {
                AltarRituals.addRitual(recipeId, capacity, power, result, behaviorType, convertedFocus,
                        convertedSacrifice, convertedEntity, health, convertedOfferings);
            }
        });
    }

    private static void addEffectRitual(String id, String behavior, IIngredient sacrifice, double capacity,
                                        double power, IIngredient[] offerings, IIngredient focus) {
        addRitual(id, null, behavior, capacity, power, focus, sacrifice, null, 0.0F, offerings);
    }

    private static List<Ingredient> offerings(IIngredient[] offerings) {
        List<Ingredient> converted = new ArrayList<>();
        if (offerings != null) {
            for (IIngredient offering : offerings) {
                converted.add(TweakerUtil.ingredient(offering));
            }
        }
        return converted;
    }

    private static AltarRitual.BehaviorType behavior(String behavior) {
        if ("item_transform".equals(behavior)) {
            return AltarRitual.BehaviorType.ITEM_TRANSFORM;
        }
        if ("sanguine".equals(behavior)) {
            return AltarRitual.BehaviorType.SANGUINE;
        }
        if ("item_charge".equals(behavior)) {
            return AltarRitual.BehaviorType.ITEM_CHARGE;
        }
        if ("entity_summon".equals(behavior)) {
            return AltarRitual.BehaviorType.ENTITY_SUMMON;
        }
        if ("absorption".equals(behavior)) {
            return AltarRitual.BehaviorType.ABSORPTION;
        }
        if ("purify".equals(behavior)) {
            return AltarRitual.BehaviorType.PURIFY;
        }
        if ("crystal".equals(behavior)) {
            return AltarRitual.BehaviorType.CRYSTAL;
        }
        if ("allure".equals(behavior)) {
            return AltarRitual.BehaviorType.ALLURE;
        }
        if ("repelling".equals(behavior)) {
            return AltarRitual.BehaviorType.REPELLING;
        }
        if ("deceit".equals(behavior)) {
            return AltarRitual.BehaviorType.DECEIT;
        }
        if ("daylight".equals(behavior)) {
            return AltarRitual.BehaviorType.DAYLIGHT;
        }
        if ("moonlight".equals(behavior)) {
            return AltarRitual.BehaviorType.MOONLIGHT;
        }
        return AltarRitual.BehaviorType.ITEM_RESULT;
    }
}
