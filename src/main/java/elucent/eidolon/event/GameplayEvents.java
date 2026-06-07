package elucent.eidolon.event;

import elucent.eidolon.capability.SoulData;
import elucent.eidolon.entity.ZombieBruteEntity;
import elucent.eidolon.entity.ai.GenericBarterGoal;
import elucent.eidolon.item.BonelordArmorItem;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.SoulSyncPacket;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.registries.ModPotions;
import elucent.eidolon.spell.ActiveRituals;
import elucent.eidolon.spell.Signs;
import elucent.eidolon.tile.GobletTileEntity;
import elucent.eidolon.util.KnowledgeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Comparator;
import java.util.List;

public class GameplayEvents {
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isRemote) {
            ActiveRituals.tick(event.world);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        burnUndeadInSun(entity);
        restoreBonelordEtherealHealth(entity);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        applyWarlockDamageRules(event);
        absorbDamageWithEtherealHealth(event);
    }

    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        EntityLivingBase entity = event.getEntityLiving();
        if (effect.getPotion() == MobEffects.HUNGER && entity.isPotionActive(ModPotions.UNDEATH)) {
            event.setResult(Event.Result.DENY);
        }
        if (effect.getPotion() == MobEffects.SLOWNESS && isWearing(entity, EntityEquipmentSlot.FEET, ModItems.WARLOCK_BOOTS)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (event.getPotionEffect().getPotion() == ModPotions.UNDEATH && entity.isPotionActive(MobEffects.HUNGER)) {
            entity.removePotionEffect(MobEffects.HUNGER);
        }
        if (event.getPotionEffect().getPotion() == MobEffects.HUNGER && entity.isPotionActive(ModPotions.UNDEATH)) {
            entity.removePotionEffect(MobEffects.HUNGER);
        }
    }

    @SubscribeEvent
    public void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.isPotionActive(ModPotions.UNDEATH)) {
            return;
        }
        ItemStack stack = event.getItem();
        if (!stack.isEmpty() && !isZombieFood(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (event.getEntityLiving().isPotionActive(ModPotions.CHILLED)) {
            event.setAmount(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        dropHeldCodex(event, entity);
        replaceWitheredZombieHeart(event, entity);
        if (entity.world.isRemote || entity instanceof IMob) {
            return;
        }
        List<GobletTileEntity> goblets = findGoblets(entity);
        if (goblets.isEmpty()) {
            return;
        }
        GobletTileEntity goblet = goblets.stream()
                .min(Comparator.comparingDouble(g -> g.getPos().distanceSq(entity.getPosition())))
                .orElse(null);
        if (goblet == null) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            goblet.setEntityType(GobletTileEntity.PLAYER);
            return;
        }
        if (EntityList.getKey(entity) != null) {
            goblet.setEntityType(EntityList.getKey(entity));
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof EntityWitch) {
            ((EntityWitch) entity).tasks.addTask(1, new GenericBarterGoal<>((EntityWitch) entity,
                    stack -> !stack.isEmpty() && stack.getItem() == ModItems.CODEX,
                    stack -> ModItems.withSign(stack, Signs.WICKED_SIGN)));
        } else if (entity instanceof EntityVillager && ((EntityVillager) entity).getProfession() == 2) {
            ((EntityVillager) entity).tasks.addTask(1, new GenericBarterGoal<>((EntityVillager) entity,
                    stack -> !stack.isEmpty() && stack.getItem() == ModItems.CODEX,
                    stack -> ModItems.withSign(stack, Signs.SACRED_SIGN)));
        }
    }

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (event.getEntityLiving().isPotionActive(ModPotions.ANCHORED)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer player = event.getEntityPlayer();
        NBTTagCompound originalPersisted = original.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (originalPersisted.hasKey("eidolonSoul")) {
            getPersistedTag(player).setTag("eidolonSoul", originalPersisted.getCompoundTag("eidolonSoul").copy());
        }
        KnowledgeUtil.copyKnowledge(original, player);
        syncPlayerData(player);
    }

    private void burnUndeadInSun(EntityLivingBase entity) {
        if (!entity.isPotionActive(ModPotions.UNDEATH) || entity.world.isRemote || !entity.world.isDaytime()) {
            return;
        }
        float brightness = entity.getBrightness();
        BlockPos pos = new BlockPos(entity.posX, Math.round(entity.posY), entity.posZ);
        if (entity.getRidingEntity() instanceof EntityBoat) {
            pos = pos.up();
        }
        if (brightness > 0.5F
                && entity.getRNG().nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F
                && entity.world.canSeeSky(pos)) {
            entity.setFire(8);
        }
    }

    private void restoreBonelordEtherealHealth(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer) || entity.world.isRemote || entity.ticksExisted % 80 != 0) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        if (player.getHealth() < player.getMaxHealth() * 0.999F) {
            return;
        }
        float persistent = getPersistentEtherealHealth(player);
        if (persistent <= 0.0F) {
            return;
        }
        if (SoulData.getMaxEtherealHealth(player) < persistent) {
            SoulData.setMaxEtherealHealth(player, persistent);
        }
        SoulData.healEtherealHealth(player, 1.0F, persistent);
        syncSoul(player);
    }

    private List<GobletTileEntity> findGoblets(EntityLivingBase entity) {
        java.util.ArrayList<GobletTileEntity> goblets = new java.util.ArrayList<>();
        BlockPos center = entity.getPosition();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    net.minecraft.tileentity.TileEntity tile = entity.world.getTileEntity(center.add(x, y, z));
                    if (tile instanceof GobletTileEntity) {
                        goblets.add((GobletTileEntity) tile);
                    }
                }
            }
        }
        return goblets;
    }

    private void dropHeldCodex(LivingDropsEvent event, EntityLivingBase entity) {
        if (entity.world.isRemote || !(entity instanceof EntityWitch || entity instanceof EntityVillager)) {
            return;
        }
        ItemStack held = entity.getHeldItemMainhand();
        if (!held.isEmpty() && held.getItem() == ModItems.CODEX) {
            event.getDrops().add(new EntityItem(entity.world, entity.posX, entity.posY, entity.posZ, held.copy()));
        }
    }

    private void replaceWitheredZombieHeart(LivingDropsEvent event, EntityLivingBase entity) {
        if (entity.world.isRemote || !(entity instanceof ZombieBruteEntity) || !entity.isPotionActive(MobEffects.WITHER)) {
            return;
        }
        for (EntityItem drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!stack.isEmpty() && stack.getItem() == ModItems.ZOMBIE_HEART) {
                drop.setItem(new ItemStack(ModItems.WITHERED_HEART, stack.getCount()));
            }
        }
    }

    private void applyWarlockDamageRules(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!isWither(source) && !source.isMagicDamage()) {
            return;
        }
        Entity directSource = source.getTrueSource();
        if (directSource instanceof EntityLivingBase
                && isWearing((EntityLivingBase) directSource, EntityEquipmentSlot.HEAD, ModItems.WARLOCK_HAT)) {
            event.setAmount(event.getAmount() * 1.5F);
            if (isWither(source)) {
                ((EntityLivingBase) directSource).heal(event.getAmount() / 2.0F);
            }
        }
        if (isWearing(event.getEntityLiving(), EntityEquipmentSlot.CHEST, ModItems.WARLOCK_CLOAK)) {
            event.setAmount(event.getAmount() / 2.0F);
        }
    }

    private void absorbDamageWithEtherealHealth(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getAmount() <= 0.0F) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (SoulData.getEtherealHealth(player) <= 0.0F || !SoulData.hasEtherealHealth(player)) {
            return;
        }
        SoulData.hurtEtherealHealth(player, event.getAmount(), getPersistentEtherealHealth(player));
        event.setAmount(0.0F);
        syncSoul(player);
    }

    private boolean isWither(DamageSource source) {
        return source == DamageSource.WITHER || "wither".equals(source.getDamageType());
    }

    private boolean isZombieFood(ItemStack stack) {
        return stack.getItem() == Items.ROTTEN_FLESH || stack.getItem() == ModItems.ZOMBIE_HEART;
    }

    private float getPersistentEtherealHealth(EntityPlayer player) {
        float persistent = 0.0F;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (!stack.isEmpty() && stack.getItem() instanceof BonelordArmorItem) {
                persistent += ((BonelordArmorItem) stack.getItem()).getPersistentEtherealHealth();
            }
        }
        return persistent;
    }

    private boolean isWearing(EntityLivingBase entity, EntityEquipmentSlot slot, net.minecraft.item.Item item) {
        ItemStack stack = entity.getItemStackFromSlot(slot);
        return !stack.isEmpty() && stack.getItem() == item;
    }

    private void syncSoul(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            ModNetwork.CHANNEL.sendTo(new SoulSyncPacket(player), (EntityPlayerMP) player);
        }
    }

    private void syncPlayerData(EntityPlayer player) {
        SoulData.ensureDefaults(player);
        KnowledgeUtil.syncAll(player);
        syncSoul(player);
    }

    private NBTTagCompound getPersistedTag(EntityPlayer player) {
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            entityData.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
}
