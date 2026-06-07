package elucent.eidolon.item;

import elucent.eidolon.Eidolon;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ReaperScytheItem extends EidolonSwordItem {
    public ReaperScytheItem(ToolMaterial material) {
        super(material, 7.0D, -2.9D);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDrops(LivingDropsEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (!isReapable(target)) {
            return;
        }
        DamageSource source = event.getSource();
        if (source == null || !(source.getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        ItemStack weapon = attacker.getHeldItemMainhand();
        if (!(weapon.getItem() instanceof ReaperScytheItem) && source != Eidolon.RITUAL_DAMAGE) {
            return;
        }
        if (target.world.isRemote) {
            return;
        }
        if (!(target instanceof EntityPlayer)) {
            event.getDrops().clear();
        }
        int count = target.world.rand.nextInt(2 + Math.max(0, event.getLootingLevel()));
        if (count > 0) {
            EntityItem drop = new EntityItem(target.world, target.posX, target.posY, target.posZ,
                    new ItemStack(ModItems.SOUL_SHARD, count));
            drop.setDefaultPickupDelay();
            event.getDrops().add(drop);
        }
        VisualEffectPacket.sendAround(target.world, target.posX, target.posY + target.height * 0.5D, target.posZ,
                VisualEffectPacket.at(VisualEffectPacket.CRYSTALLIZE, target.posX, target.posY + target.height * 0.5D,
                        target.posZ, 0.97F, 0.61F, 0.86F));
    }

    private boolean isReapable(EntityLivingBase target) {
        return Eidolon.getCreatureAttribute(target) == EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World worldIn, List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flagIn) {
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_PURPLE.toString() + TextFormatting.ITALIC
                + I18n.translateToLocal("lore.eidolon.reaper_scythe"));
    }
}
