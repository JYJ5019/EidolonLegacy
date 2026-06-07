package elucent.eidolon.spell;

import elucent.eidolon.Reference;
import elucent.eidolon.item.SummoningStaffItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class SummonedEntityEvents {
    private SummonedEntityEvents() {
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!isSummoned(entity)) {
            return;
        }
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            if (isOwner(living.getAttackTarget(), entity)) {
                living.setAttackTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (isSummoned(entity) && isOwner(event.getTarget(), entity) && entity instanceof EntityLiving) {
            ((EntityLiving) entity).setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity trueSource = event.getSource().getTrueSource();
        if (trueSource instanceof EntityLivingBase
                && isSummoned((EntityLivingBase) trueSource)
                && isOwner(event.getEntityLiving(), (EntityLivingBase) trueSource)) {
            event.setCanceled(true);
            return;
        }
        if (trueSource instanceof EntityPlayer && !event.getEntityLiving().world.isRemote) {
            assignOwnerSummonsTarget((EntityPlayer) trueSource, event.getEntityLiving());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (isSummoned(event.getEntityLiving())) {
            event.getDrops().clear();
        }
    }

    private static boolean isSummoned(EntityLivingBase entity) {
        return entity != null && entity.getEntityData().getBoolean(SummoningStaffItem.SUMMONED_TAG);
    }

    private static boolean isOwner(Entity candidate, EntityLivingBase summoned) {
        return candidate instanceof EntityLivingBase
                && summoned.getEntityData().getString(SummoningStaffItem.OWNER_TAG)
                .equals(candidate.getUniqueID().toString());
    }

    private static void assignOwnerSummonsTarget(EntityPlayer owner, EntityLivingBase target) {
        AxisAlignedBB bounds = owner.getEntityBoundingBox().grow(24.0D, 12.0D, 24.0D);
        for (EntityLiving summon : owner.world.getEntitiesWithinAABB(EntityLiving.class, bounds,
                entity -> isSummoned(entity) && isOwner(owner, entity))) {
            if (summon != target) {
                summon.setAttackTarget(target);
            }
        }
    }
}
