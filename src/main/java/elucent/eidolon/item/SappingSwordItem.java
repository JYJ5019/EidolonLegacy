package elucent.eidolon.item;

import elucent.eidolon.network.VisualEffectPacket;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.common.MinecraftForge;

public class SappingSwordItem extends EidolonSwordItem {
    public SappingSwordItem(ToolMaterial material) {
        super(material, 4.0D, -2.4D);
    }

    @Override
    public boolean hitEntity(net.minecraft.item.ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!attacker.world.isRemote && target.hurtResistantTime > 0) {
            target.hurtResistantTime = 0;
            float before = target.getHealth();
            target.attackEntityFrom(new EntityDamageSource("wither", attacker).setDamageBypassesArmor(), 2.0F);
            float healing = before - target.getHealth();
            if (healing > 0.0F) {
                attacker.heal(healing);
                VisualEffectPacket.sendAround(attacker.world, target.posX, target.posY + target.height * 0.5D,
                        target.posZ,
                        VisualEffectPacket.line(VisualEffectPacket.LIFESTEAL,
                                target.posX, target.posY + target.height * 0.55D, target.posZ,
                                attacker.posX, attacker.posY + attacker.height * 0.55D, attacker.posZ,
                                1.0F, 0.12F, 0.18F));
            }
        }
        return super.hitEntity(stack, target, attacker);
    }
}
