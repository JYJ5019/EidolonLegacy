package elucent.eidolon.item;

import elucent.eidolon.Eidolon;
import elucent.eidolon.network.DeathbringerSlashEffectPacket;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.VisualEffectPacket;
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
                            attacker.posZ, target.posX, target.posY + target.height * 0.5D, target.posZ),
                    new NetworkRegistry.TargetPoint(attacker.dimension, target.posX, target.posY, target.posZ,
                            SLASH_EFFECT_RANGE));
            VisualEffectPacket.sendAround(attacker.world, target.posX, target.posY + target.height * 0.5D, target.posZ,
                    VisualEffectPacket.at(VisualEffectPacket.MAGIC_BURST, target.posX,
                            target.posY + target.height * 0.5D, target.posZ, 0.45F, 1.0F, 0.35F));
        }
        return super.hitEntity(stack, target, attacker);
    }

    public static boolean hasUndeath(EntityLivingBase entity) {
        return entity.isPotionActive(ModPotions.UNDEATH);
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_PURPLE.toString() + TextFormatting.ITALIC
                + I18n.translateToLocal("lore.eidolon.deathbringer_scythe"));
    }
}
