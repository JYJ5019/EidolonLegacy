package elucent.eidolon.item;

import elucent.eidolon.Eidolon;
import elucent.eidolon.network.DeathbringerSlashEffectPacket;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.registries.ModPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.List;

public class DeathbringerScytheItem extends EidolonSwordItem {
    private static final int UNDEATH_DURATION = 900;
    private static final double SLASH_EFFECT_RANGE = 32.0D;

    public DeathbringerScytheItem(ToolMaterial material) {
        super(material, 10.0D, -2.9D);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!attacker.world.isRemote) {
            if (Eidolon.getCreatureAttribute(target) != EnumCreatureAttribute.UNDEAD) {
                target.addPotionEffect(new PotionEffect(ModPotions.UNDEATH, UNDEATH_DURATION, 0));
            }
            ModNetwork.CHANNEL.sendToAllAround(
                    new DeathbringerSlashEffectPacket(attacker.posX, attacker.posY + attacker.height * 0.5D,
                            attacker.posZ, target.posX, target.posY + target.height * 0.5D, target.posZ,
                            packColor(255, 33, 26, 23),
                            packColor(255, 10, 10, 11),
                            packColor(255, 161, 255, 123),
                            packColor(255, 194, 171, 70)),
                    new NetworkRegistry.TargetPoint(attacker.dimension, target.posX, target.posY, target.posZ,
                            SLASH_EFFECT_RANGE));
        }
        return super.hitEntity(stack, target, attacker);
    }

    public static boolean hasUndeath(EntityLivingBase entity) {
        return entity.isPotionActive(ModPotions.UNDEATH);
    }

    private static int packColor(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_PURPLE.toString() + TextFormatting.ITALIC
                + I18n.translateToLocal("lore.eidolon.deathbringer_scythe"));
    }
}
