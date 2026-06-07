package elucent.eidolon.item.curio;

import elucent.eidolon.Reference;
import elucent.eidolon.capability.SoulData;
import elucent.eidolon.entity.AngelArrowEntity;
import elucent.eidolon.entity.BonechillProjectileEntity;
import elucent.eidolon.entity.NecromancerSpellEntity;
import elucent.eidolon.entity.SoulfireProjectileEntity;
import elucent.eidolon.item.BonelordArmorItem;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.network.SoulSyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class CurioEvents {
    private static final String KEEP_XP = "eidolonMindShieldKeepXp";
    private static final String XP_LEVEL = "eidolonMindShieldLevel";
    private static final String XP_PROGRESS = "eidolonMindShieldProgress";
    private static final String XP_TOTAL = "eidolonMindShieldTotal";
    private static final UUID BASIC_RING_ID = new UUID(7207179027447911419L, 1628308750126455317L);
    private static final UUID BASIC_AMULET_ID = new UUID(1821688469367197801L, 2986247575840977557L);
    private static final UUID BASIC_BELT_ID = new UUID(5892388994722937059L, 8235504439637777033L);
    private static final UUID RESOLUTE_BELT_ID = new UUID(3701779382882225399L, 5035874982077300549L);
    private static final AttributeModifier BASIC_RING_TOUGHNESS =
            new AttributeModifier(BASIC_RING_ID, Reference.MOD_ID + ":basic_ring", 0.5D, 0);
    private static final AttributeModifier BASIC_AMULET_TOUGHNESS =
            new AttributeModifier(BASIC_AMULET_ID, Reference.MOD_ID + ":basic_amulet", 1.0D, 0);
    private static final AttributeModifier BASIC_BELT_TOUGHNESS =
            new AttributeModifier(BASIC_BELT_ID, Reference.MOD_ID + ":basic_belt", 1.0D, 0);
    private static final AttributeModifier RESOLUTE_BELT_KNOCKBACK =
            new AttributeModifier(RESOLUTE_BELT_ID, Reference.MOD_ID + ":resolute_belt", 1.0D, 0);

    private CurioEvents() {
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        DamageSource source = event.getSource();
        if (target == null || source == null) {
            return;
        }

        ItemStack mirror = BaubleUtil.findBauble(target, ModItems.TERMINUS_MIRROR);
        if (!mirror.isEmpty() && (source.isProjectile() || isEidolonSpellProjectile(source.getImmediateSource()))) {
            event.setCanceled(true);
            playShieldSound(target);
            return;
        }

        ItemStack voidAmulet = BaubleUtil.findBauble(target, ModItems.VOID_AMULET);
        if (!voidAmulet.isEmpty() && VoidAmuletItem.getCooldown(voidAmulet) <= 0 && isVoidBlocked(source)) {
            event.setCanceled(true);
            VoidAmuletItem.setCooldown(voidAmulet, VoidAmuletItem.MAX_COOLDOWN);
            playShieldSound(target);
            return;
        }

        if (isWearingWardedMail(target) && source.isMagicDamage()) {
            event.setCanceled(true);
            if (!target.world.isRemote) {
                target.attackEntityFrom(new DamageSource(source.getDamageType()), event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target == null) {
            return;
        }

        float amount = event.getAmount();
        if (BaubleUtil.hasBauble(target, ModItems.RESOLUTE_BELT)) {
            repelAttacker(target, event.getSource());
        }
        if (BaubleUtil.hasBauble(target, ModItems.GLASS_HAND)) {
            amount *= 5.0F;
        }

        Entity attacker = event.getSource() == null ? null : event.getSource().getTrueSource();
        if (attacker instanceof EntityLivingBase) {
            EntityLivingBase livingAttacker = (EntityLivingBase) attacker;
            if (BaubleUtil.hasBauble(livingAttacker, ModItems.GLASS_HAND)) {
                amount *= 2.0F;
            }
        }

        event.setAmount(amount);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target instanceof EntityPlayer && BaubleUtil.hasBauble(target, ModItems.MIND_SHIELDING_PLATE)) {
            storeExperience((EntityPlayer) target);
        }

        DamageSource source = event.getSource();
        Entity killer = source == null ? null : source.getTrueSource();
        if (killer instanceof EntityLivingBase && killer != target
                && BaubleUtil.hasBauble((EntityLivingBase) killer, ModItems.SOULBONE_AMULET)) {
            grantSoulboneEtherealHealth((EntityLivingBase) killer);
        }
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && shouldKeepExperience((EntityPlayer) event.getEntityLiving())) {
            event.setDroppedExperience(0);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        EntityPlayer original = event.getOriginal();
        if (!shouldKeepExperience(original)) {
            return;
        }
        NBTTagCompound tag = original.getEntityData();
        EntityPlayer player = event.getEntityPlayer();
        player.experienceLevel = tag.getInteger(XP_LEVEL);
        player.experience = tag.getFloat(XP_PROGRESS);
        player.experienceTotal = tag.getInteger(XP_TOTAL);
        tag.removeTag(KEEP_XP);
        tag.removeTag(XP_LEVEL);
        tag.removeTag(XP_PROGRESS);
        tag.removeTag(XP_TOTAL);
    }

    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        PotionEffect effect = event.getPotionEffect();
        if (effect != null
                && effect.getPotion() == MobEffects.NAUSEA
                && BaubleUtil.hasBauble(event.getEntityLiving(), ModItems.MIND_SHIELDING_PLATE)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (BaubleUtil.hasBauble(event.getEntityLiving(), ModItems.GRAVITY_BELT)) {
            event.setDistance(event.getDistance() / 4.0F);
        }
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || !BaubleUtil.hasBauble(player, ModItems.ANGELS_SIGHT)) {
            return;
        }
        ItemStack bow = event.getBow();
        if (bow.isEmpty() || !(bow.getItem() instanceof ItemBow)) {
            return;
        }
        boolean infinite = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0;
        ItemStack ammo = findArrow(player);
        if (ammo.isEmpty() && !infinite) {
            return;
        }
        if (ammo.isEmpty()) {
            ammo = new ItemStack(Items.ARROW);
        }

        float velocity = ItemBow.getArrowVelocity(event.getCharge());
        if (velocity < 0.1F) {
            return;
        }
        velocity = Math.min(velocity, 1.0F);
        event.setCanceled(true);

        if (player.world.isRemote) {
            return;
        }

        AngelArrowEntity arrow = new AngelArrowEntity(player.world, player);
        arrow.configureFromAmmo(ammo);
        arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity * 3.0F, 1.0F);
        arrow.setIsCritical(velocity >= 1.0F);
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
        if (power > 0) {
            arrow.setDamage(arrow.getDamage() + power * 0.5D + 0.5D);
        }
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
        if (punch > 0) {
            arrow.setKnockbackStrength(punch);
        }
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0) {
            arrow.setFire(100);
        }
        if (infinite) {
            arrow.pickupStatus = net.minecraft.entity.projectile.EntityArrow.PickupStatus.CREATIVE_ONLY;
        }
        player.world.spawnEntity(arrow);
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS, 1.0F, 1.0F / (player.world.rand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
        bow.damageItem(1, player);
        if (!infinite && !player.capabilities.isCreativeMode) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.inventory.deleteStack(ammo);
            }
        }
    }

    private static ItemStack findArrow(EntityPlayer player) {
        ItemStack offhand = player.getHeldItemOffhand();
        if (isArrow(offhand)) {
            return offhand;
        }
        ItemStack mainhand = player.getHeldItemMainhand();
        if (isArrow(mainhand)) {
            return mainhand;
        }
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (isArrow(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean isArrow(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemArrow || item instanceof ItemTippedArrow || item == Items.SPECTRAL_ARROW;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player == null || event.player.world.isRemote) {
            return;
        }
        updateBaubleAttributes(event.player);
        PrestigiousPalmItem.updateReach(event.player, BaubleUtil.hasBauble(event.player, ModItems.PRESTIGIOUS_PALM));
    }

    private static void updateBaubleAttributes(EntityPlayer player) {
        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS),
                BASIC_RING_TOUGHNESS, BaubleUtil.hasBauble(player, ModItems.BASIC_RING));
        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS),
                BASIC_AMULET_TOUGHNESS, BaubleUtil.hasBauble(player, ModItems.BASIC_AMULET));
        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS),
                BASIC_BELT_TOUGHNESS, BaubleUtil.hasBauble(player, ModItems.BASIC_BELT));
        updateModifier(player.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE),
                RESOLUTE_BELT_KNOCKBACK, BaubleUtil.hasBauble(player, ModItems.RESOLUTE_BELT));
    }

    private static void updateModifier(IAttributeInstance attribute, AttributeModifier modifier, boolean equipped) {
        if (attribute == null) {
            return;
        }
        AttributeModifier existing = attribute.getModifier(modifier.getID());
        if (equipped && existing == null) {
            attribute.applyModifier(modifier);
        } else if (!equipped && existing != null) {
            attribute.removeModifier(existing);
        }
    }

    private static boolean isVoidBlocked(DamageSource source) {
        Entity direct = source.getImmediateSource();
        return direct instanceof EntityLargeFireball
                || direct instanceof EntitySmallFireball
                || direct instanceof EntityPotion
                || direct instanceof EntityShulkerBullet
                || direct instanceof EntityLlamaSpit
                || isEidolonSpellProjectile(direct);
    }

    private static boolean isEidolonSpellProjectile(Entity entity) {
        return entity instanceof SoulfireProjectileEntity
                || entity instanceof BonechillProjectileEntity
                || entity instanceof NecromancerSpellEntity;
    }

    private static boolean isWearingWardedMail(EntityLivingBase entity) {
        ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        return !chest.isEmpty() && chest.getItem() == ModItems.WARDED_MAIL;
    }

    private static void grantSoulboneEtherealHealth(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer) || entity.world.isRemote) {
            return;
        }
        EntityPlayer player = (EntityPlayer) entity;
        SoulData.ensureDefaults(player);
        float persistent = getPersistentEtherealHealth(player);
        float max = SoulData.getMaxEtherealHealth(player);
        float ethereal = SoulData.getEtherealHealth(player);
        float steppedMax = 2.0F * (int) Math.floor((ethereal + 3.0F) / 2.0F);
        SoulData.setMaxEtherealHealth(player, Math.max(Math.min(persistent, max), steppedMax));
        SoulData.setEtherealHealth(player, ethereal + 2.0F);
        if (player instanceof EntityPlayerMP) {
            ModNetwork.CHANNEL.sendTo(new SoulSyncPacket(player), (EntityPlayerMP) player);
        }
    }

    private static float getPersistentEtherealHealth(EntityPlayer player) {
        float persistent = 0.0F;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (!stack.isEmpty() && stack.getItem() instanceof BonelordArmorItem) {
                persistent += ((BonelordArmorItem) stack.getItem()).getPersistentEtherealHealth();
            }
        }
        return persistent;
    }

    private static void repelAttacker(EntityLivingBase target, DamageSource source) {
        if (source == null || target.world.isRemote || !(source.getTrueSource() instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
        attacker.knockBack(target, 0.8F, target.posX - attacker.posX, target.posZ - attacker.posZ);
        target.world.playSound(null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_IRONGOLEM_HURT,
                SoundCategory.PLAYERS, 1.0F, 1.9F + target.getRNG().nextFloat() * 0.2F);
    }

    private static void playShieldSound(EntityLivingBase entity) {
        if (!entity.world.isRemote) {
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_WITHER_HURT,
                    SoundCategory.PLAYERS, 1.0F, 0.75F);
        }
    }

    private static void storeExperience(EntityPlayer player) {
        NBTTagCompound tag = player.getEntityData();
        tag.setBoolean(KEEP_XP, true);
        tag.setInteger(XP_LEVEL, player.experienceLevel);
        tag.setFloat(XP_PROGRESS, player.experience);
        tag.setInteger(XP_TOTAL, player.experienceTotal);
    }

    private static boolean shouldKeepExperience(EntityPlayer player) {
        return player.getEntityData().getBoolean(KEEP_XP);
    }
}
