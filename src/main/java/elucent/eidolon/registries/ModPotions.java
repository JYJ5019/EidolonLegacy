package elucent.eidolon.registries;

import elucent.eidolon.Reference;
import elucent.eidolon.potion.AnchoredPotion;
import elucent.eidolon.potion.ChilledPotion;
import elucent.eidolon.potion.ReinforcedPotion;
import elucent.eidolon.potion.UndeathPotion;
import elucent.eidolon.potion.VulnerablePotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class ModPotions {
    public static final Potion UNDEATH = setup(new UndeathPotion(), "undeath");
    public static final Potion VULNERABLE = setup(new VulnerablePotion(), "vulnerable");
    public static final Potion REINFORCED = setup(new ReinforcedPotion(), "reinforced");
    public static final Potion ANCHORED = setup(new AnchoredPotion(), "anchored");
    public static final Potion CHILLED = setup(new ChilledPotion(), "chilled");

    public static final PotionType UNDEATH_TYPE = potionType("undeath", UNDEATH, 3600);
    public static final PotionType LONG_UNDEATH_TYPE = potionType("long_undeath", UNDEATH, 9600);
    public static final PotionType VULNERABLE_TYPE = potionType("vulnerable", VULNERABLE, 3600);
    public static final PotionType LONG_VULNERABLE_TYPE = potionType("long_vulnerable", VULNERABLE, 9600);
    public static final PotionType STRONG_VULNERABLE_TYPE = potionType("strong_vulnerable", VULNERABLE, 1800, 1);
    public static final PotionType REINFORCED_TYPE = potionType("reinforced", REINFORCED, 3600);
    public static final PotionType LONG_REINFORCED_TYPE = potionType("long_reinforced", REINFORCED, 9600);
    public static final PotionType STRONG_REINFORCED_TYPE = potionType("strong_reinforced", REINFORCED, 1800, 1);
    public static final PotionType ANCHORED_TYPE = potionType("anchored", ANCHORED, 3600);
    public static final PotionType LONG_ANCHORED_TYPE = potionType("long_anchored", ANCHORED, 9600);
    public static final PotionType CHILLED_TYPE = potionType("chilled", CHILLED, 3600);
    public static final PotionType LONG_CHILLED_TYPE = potionType("long_chilled", CHILLED, 9600);
    public static final PotionType DECAY_TYPE = potionType("decay", MobEffects.WITHER, 900);
    public static final PotionType LONG_DECAY_TYPE = potionType("long_decay", MobEffects.WITHER, 1800);
    public static final PotionType STRONG_DECAY_TYPE = potionType("strong_decay", MobEffects.WITHER, 450, 1);

    private static boolean brewingAdded;

    private ModPotions() {
    }

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(UNDEATH, VULNERABLE, REINFORCED, ANCHORED, CHILLED);
    }

    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        event.getRegistry().registerAll(
                UNDEATH_TYPE, LONG_UNDEATH_TYPE,
                VULNERABLE_TYPE, LONG_VULNERABLE_TYPE, STRONG_VULNERABLE_TYPE,
                REINFORCED_TYPE, LONG_REINFORCED_TYPE, STRONG_REINFORCED_TYPE,
                ANCHORED_TYPE, LONG_ANCHORED_TYPE,
                CHILLED_TYPE, LONG_CHILLED_TYPE,
                DECAY_TYPE, LONG_DECAY_TYPE, STRONG_DECAY_TYPE
        );
    }

    public static void addBrewingRecipes() {
        if (brewingAdded) {
            return;
        }
        brewingAdded = true;
        PotionHelper.addMix(PotionTypes.WATER, ModItems.FUNGUS_SPROUTS, PotionTypes.AWKWARD);
        PotionHelper.addMix(PotionTypes.AWKWARD, ModItems.WRAITH_HEART, CHILLED_TYPE);
        PotionHelper.addMix(CHILLED_TYPE, Items.REDSTONE, LONG_CHILLED_TYPE);
        PotionHelper.addMix(PotionTypes.AWKWARD, ModItems.WARPED_SPROUTS, ANCHORED_TYPE);
        PotionHelper.addMix(ANCHORED_TYPE, Items.REDSTONE, LONG_ANCHORED_TYPE);
        PotionHelper.addMix(PotionTypes.AWKWARD, Items.PRISMARINE_CRYSTALS, REINFORCED_TYPE);
        PotionHelper.addMix(REINFORCED_TYPE, Items.REDSTONE, LONG_REINFORCED_TYPE);
        PotionHelper.addMix(REINFORCED_TYPE, Items.GLOWSTONE_DUST, STRONG_REINFORCED_TYPE);
        PotionHelper.addMix(PotionTypes.AWKWARD, ModItems.TATTERED_CLOTH, VULNERABLE_TYPE);
        PotionHelper.addMix(VULNERABLE_TYPE, Items.REDSTONE, LONG_VULNERABLE_TYPE);
        PotionHelper.addMix(VULNERABLE_TYPE, Items.GLOWSTONE_DUST, STRONG_VULNERABLE_TYPE);
        PotionHelper.addMix(PotionTypes.AWKWARD, ModItems.DEATH_ESSENCE, UNDEATH_TYPE);
        PotionHelper.addMix(UNDEATH_TYPE, Items.REDSTONE, LONG_UNDEATH_TYPE);
        PotionHelper.addMix(PotionTypes.AWKWARD, ModItems.WITHERED_HEART, DECAY_TYPE);
        PotionHelper.addMix(DECAY_TYPE, Items.REDSTONE, LONG_DECAY_TYPE);
        PotionHelper.addMix(DECAY_TYPE, Items.GLOWSTONE_DUST, STRONG_DECAY_TYPE);
    }

    private static Potion setup(Potion potion, String name) {
        potion.setRegistryName(Reference.MOD_ID, name);
        potion.setPotionName("potion." + Reference.MOD_ID + "." + name);
        if (potion instanceof VulnerablePotion) {
            ((VulnerablePotion) potion).registerAttributeModifiers();
        } else if (potion instanceof ReinforcedPotion) {
            ((ReinforcedPotion) potion).registerAttributeModifiers();
        }
        return potion;
    }

    private static PotionType potionType(String name, Potion potion, int duration) {
        return potionType(name, potion, duration, 0);
    }

    private static PotionType potionType(String name, Potion potion, int duration, int amplifier) {
        PotionType type = new PotionType(Reference.MOD_ID + "." + name,
                new PotionEffect(potion, duration, amplifier));
        type.setRegistryName(Reference.MOD_ID, name);
        return type;
    }
}
