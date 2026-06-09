package elucent.eidolon.event;

import elucent.eidolon.CommonConfig;
import elucent.eidolon.Eidolon;
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
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameplayEvents {
    private static final double SOUL_SAND_SPEED_FACTOR = 0.4D;
    private static final double WARLOCK_SOUL_SAND_FACTOR = (1.0D + SOUL_SAND_SPEED_FACTOR) * 0.5D;
    private static final double WARLOCK_SOUL_SAND_COMPENSATION = WARLOCK_SOUL_SAND_FACTOR / SOUL_SAND_SPEED_FACTOR;
    private static Field entityIsInWebField;
    private static boolean triedResolveEntityIsInWebField;
    private static boolean loggedEntityIsInWebFieldFailure;

    private final Map<Integer, List<InstantPotionContext>> recentInstantPotionContexts = new HashMap<>();

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.world.isRemote) {
            pruneExpiredInstantPotionContexts(event.world.getTotalWorldTime());
            ActiveRituals.tick(event.world);
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        compensateWarlockBootsSlowBlocks(entity);
        burnUndeadInSun(entity);
        restoreBonelordEtherealHealth(entity);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (invertUndeathInstantDamage(event)) {
            return;
        }
        applyWarlockDamageRules(event);
        applyUndeathCreatureEnchantments(event);
        absorbDamageWithEtherealHealth(event);
    }

    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.isPotionActive(ModPotions.UNDEATH)) {
            if (effect.getPotion() == MobEffects.HUNGER
                    || effect.getPotion() == MobEffects.REGENERATION
                    || effect.getPotion() == MobEffects.POISON) {
                event.setResult(Event.Result.DENY);
            }
            if (isInstantHealOrHarm(effect.getPotion())) {
                event.setResult(Event.Result.DENY);
                if (!entity.world.isRemote) {
                    applyUndeathInstantEffect(entity, effect.getPotion(), effect.getAmplifier(), 1.0D);
                }
            }
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
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getEntity() instanceof EntityPotion) || event.getEntity().world.isRemote) {
            return;
        }
        EntityPotion potion = (EntityPotion) event.getEntity();
        List<PotionEffect> effects = getInstantHealOrHarmEffects(PotionUtils.getEffectsFromStack(potion.getPotion()));
        if (effects.isEmpty()) {
            return;
        }

        RayTraceResult result = event.getRayTraceResult();
        List<EntityLivingBase> entities = potion.world.getEntitiesWithinAABB(EntityLivingBase.class,
                potion.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D));
        long expiresAt = potion.world.getTotalWorldTime() + 2L;
        for (EntityLivingBase target : entities) {
            if (!target.isPotionActive(ModPotions.UNDEATH) || !target.canBeHitWithPotion()) {
                continue;
            }
            double potency = target == result.entityHit ? 1.0D
                    : 1.0D - Math.sqrt(target.getDistanceSq(potion)) / 4.0D;
            if (potency <= 0.0D) {
                continue;
            }
            for (PotionEffect effect : effects) {
                addInstantPotionContext(recentInstantPotionContexts, target,
                        new InstantPotionContext(effect.getPotion(), effect.getAmplifier(), potency, expiresAt));
            }
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event) {
        if (invertUndeathInstantHeal(event)) {
            return;
        }
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

    private void compensateWarlockBootsSlowBlocks(EntityLivingBase entity) {
        if (!isWearing(entity, EntityEquipmentSlot.FEET, ModItems.WARLOCK_BOOTS)) {
            return;
        }
        compensateWarlockSoulSand(entity);
        clearWarlockWebSlowdown(entity);
    }

    private void compensateWarlockSoulSand(EntityLivingBase entity) {
        if (!isTouchingBlockBelow(entity, Blocks.SOUL_SAND)) {
            return;
        }
        entity.motionX *= WARLOCK_SOUL_SAND_COMPENSATION;
        entity.motionZ *= WARLOCK_SOUL_SAND_COMPENSATION;
    }

    private void clearWarlockWebSlowdown(EntityLivingBase entity) {
        Field field = getEntityIsInWebField();
        if (field == null) {
            return;
        }
        try {
            field.setBoolean(entity, false);
        } catch (RuntimeException | IllegalAccessException e) {
            logEntityIsInWebFieldFailure("Could not clear web slowdown for warlock boots.", e);
        }
    }

    private static Field getEntityIsInWebField() {
        if (!triedResolveEntityIsInWebField) {
            triedResolveEntityIsInWebField = true;
            try {
                entityIsInWebField = ReflectionHelper.findField(Entity.class, "isInWeb", "field_70134_J");
            } catch (RuntimeException e) {
                logEntityIsInWebFieldFailure("Could not find Entity.isInWeb for warlock boots web slowdown.", e);
            }
        }
        return entityIsInWebField;
    }

    private static void logEntityIsInWebFieldFailure(String message, Exception e) {
        if (!loggedEntityIsInWebFieldFailure) {
            loggedEntityIsInWebFieldFailure = true;
            Eidolon.LOGGER.warn(message, e);
        }
    }

    private boolean isTouchingBlockBelow(Entity entity, Block block) {
        AxisAlignedBB box = entity.getEntityBoundingBox();
        double minX = box.minX + 0.001D;
        double maxX = box.maxX - 0.001D;
        double minZ = box.minZ + 0.001D;
        double maxZ = box.maxZ - 0.001D;
        double y = box.minY - 0.01D;
        double centerX = (box.minX + box.maxX) * 0.5D;
        double centerZ = (box.minZ + box.maxZ) * 0.5D;
        return isBlockAt(entity.world, centerX, y, centerZ, block)
                || isBlockAt(entity.world, minX, y, minZ, block)
                || isBlockAt(entity.world, minX, y, maxZ, block)
                || isBlockAt(entity.world, maxX, y, minZ, block)
                || isBlockAt(entity.world, maxX, y, maxZ, block);
    }

    private boolean isBlockAt(World world, double x, double y, double z, Block block) {
        BlockPos pos = new BlockPos(x, y, z);
        return world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() == block;
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

    private void applyUndeathCreatureEnchantments(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (!target.isPotionActive(ModPotions.UNDEATH) || event.getAmount() <= 0.0F) {
            return;
        }
        DamageSource source = event.getSource();
        if (!isDirectMeleeDamage(source)) {
            return;
        }
        Entity attacker = source.getTrueSource();
        if (!(attacker instanceof EntityLivingBase)) {
            return;
        }
        ItemStack weapon = ((EntityLivingBase) attacker).getHeldItemMainhand();
        if (weapon.isEmpty()) {
            return;
        }
        float nativeBonus = EnchantmentHelper.getModifierForCreature(weapon, Eidolon.getTrueCreatureAttribute(target));
        float undeadBonus = EnchantmentHelper.getModifierForCreature(weapon, EnumCreatureAttribute.UNDEAD);
        float delta = undeadBonus - nativeBonus;
        if (delta != 0.0F) {
            event.setAmount(Math.max(0.0F, event.getAmount() + delta));
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

    private boolean isDirectMeleeDamage(DamageSource source) {
        return "mob".equals(source.getDamageType()) || "player".equals(source.getDamageType());
    }

    private boolean isZombieFood(ItemStack stack) {
        for (String id : CommonConfig.zombieFood()) {
            if (matchesItemId(stack, id)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesItemId(ItemStack stack, String id) {
        if (stack.isEmpty() || id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            ResourceLocation name = new ResourceLocation(id.trim());
            return stack.getItem() == ForgeRegistries.ITEMS.getValue(name);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private boolean invertUndeathInstantHeal(LivingHealEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (!target.isPotionActive(ModPotions.UNDEATH)) {
            return false;
        }
        InstantPotionContext context = findInstantPotionContext(target, MobEffects.INSTANT_HEALTH, true);
        if (context == null) {
            return false;
        }
        event.setAmount(0.0F);
        event.setCanceled(true);
        applyUndeathInstantEffect(target, MobEffects.INSTANT_HEALTH, context.amplifier, context.potency);
        return true;
    }

    private boolean invertUndeathInstantDamage(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (!target.isPotionActive(ModPotions.UNDEATH) || !event.getSource().isMagicDamage()) {
            return false;
        }
        InstantPotionContext context = findInstantPotionContext(target, MobEffects.INSTANT_DAMAGE, true);
        if (context == null) {
            return false;
        }
        event.setAmount(0.0F);
        applyUndeathInstantEffect(target, MobEffects.INSTANT_DAMAGE, context.amplifier, context.potency);
        return true;
    }

    private InstantPotionContext findInstantPotionContext(EntityLivingBase entity, Potion potion, boolean consumeRecent) {
        long now = entity.world.getTotalWorldTime();
        InstantPotionContext context = findContext(recentInstantPotionContexts, entity, potion, now, consumeRecent);
        if (context != null) {
            return context;
        }
        return findAreaEffectCloudContext(entity, potion);
    }

    private InstantPotionContext findContext(Map<Integer, List<InstantPotionContext>> contexts,
            EntityLivingBase entity, Potion potion, long now, boolean consume) {
        List<InstantPotionContext> list = contexts.get(entity.getEntityId());
        if (list == null) {
            return null;
        }
        InstantPotionContext found = null;
        Iterator<InstantPotionContext> iterator = list.iterator();
        while (iterator.hasNext()) {
            InstantPotionContext context = iterator.next();
            if (context.expiresAt < now) {
                iterator.remove();
                continue;
            }
            if (found == null && context.potion == potion) {
                found = context;
                if (consume) {
                    iterator.remove();
                }
            }
        }
        if (list.isEmpty()) {
            contexts.remove(entity.getEntityId());
        }
        return found;
    }

    private InstantPotionContext findAreaEffectCloudContext(EntityLivingBase entity, Potion potion) {
        if (entity.world.isRemote) {
            return null;
        }
        List<EntityAreaEffectCloud> clouds = entity.world.getEntitiesWithinAABB(EntityAreaEffectCloud.class,
                entity.getEntityBoundingBox().grow(8.0D, 2.0D, 8.0D));
        for (EntityAreaEffectCloud cloud : clouds) {
            double dx = entity.posX - cloud.posX;
            double dz = entity.posZ - cloud.posZ;
            float radius = cloud.getRadius();
            if (dx * dx + dz * dz > radius * radius) {
                continue;
            }
            for (PotionEffect effect : getAreaEffectCloudEffects(cloud)) {
                if (effect.getPotion() == potion) {
                    return new InstantPotionContext(potion, effect.getAmplifier(), 0.5D,
                            entity.world.getTotalWorldTime());
                }
            }
        }
        return null;
    }

    private List<PotionEffect> getAreaEffectCloudEffects(EntityAreaEffectCloud cloud) {
        NBTTagCompound tag = new NBTTagCompound();
        cloud.writeToNBT(tag);
        List<PotionEffect> effects = new ArrayList<>();
        PotionType type = PotionUtils.getPotionTypeFromNBT(tag);
        effects.addAll(type.getEffects());
        if (tag.hasKey("Effects", 9)) {
            NBTTagList list = tag.getTagList("Effects", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(list.getCompoundTagAt(i));
                if (effect != null) {
                    effects.add(effect);
                }
            }
        }
        return getInstantHealOrHarmEffects(effects);
    }

    private List<PotionEffect> getInstantHealOrHarmEffects(List<PotionEffect> effects) {
        List<PotionEffect> instantEffects = new ArrayList<>();
        for (PotionEffect effect : effects) {
            if (isInstantHealOrHarm(effect.getPotion())) {
                instantEffects.add(effect);
            }
        }
        return instantEffects;
    }

    private boolean isInstantHealOrHarm(Potion potion) {
        return potion == MobEffects.INSTANT_HEALTH || potion == MobEffects.INSTANT_DAMAGE;
    }

    private void applyUndeathInstantEffect(EntityLivingBase target, Potion potion, int amplifier, double potency) {
        if (potion == MobEffects.INSTANT_HEALTH) {
            float damage = instantPotionAmount(6, amplifier, potency);
            if (damage > 0.0F) {
                target.attackEntityFrom(DamageSource.MAGIC, damage);
            }
        } else if (potion == MobEffects.INSTANT_DAMAGE) {
            float healing = instantPotionAmount(4, amplifier, potency);
            if (healing > 0.0F) {
                target.heal(healing);
            }
        }
    }

    private float instantPotionAmount(int base, int amplifier, double potency) {
        return (float) ((int) (potency * (base << amplifier) + 0.5D));
    }

    private void addInstantPotionContext(Map<Integer, List<InstantPotionContext>> contexts,
            EntityLivingBase entity, InstantPotionContext context) {
        contexts.computeIfAbsent(entity.getEntityId(), id -> new ArrayList<>()).add(context);
    }

    private void pruneExpiredInstantPotionContexts(long now) {
        Iterator<Map.Entry<Integer, List<InstantPotionContext>>> entries =
                recentInstantPotionContexts.entrySet().iterator();
        while (entries.hasNext()) {
            List<InstantPotionContext> contexts = entries.next().getValue();
            contexts.removeIf(context -> context.expiresAt < now);
            if (contexts.isEmpty()) {
                entries.remove();
            }
        }
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

    private static final class InstantPotionContext {
        private final Potion potion;
        private final int amplifier;
        private final double potency;
        private final long expiresAt;

        private InstantPotionContext(Potion potion, int amplifier, double potency, long expiresAt) {
            this.potion = potion;
            this.amplifier = amplifier;
            this.potency = potency;
            this.expiresAt = expiresAt;
        }
    }
}
