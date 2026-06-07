package elucent.eidolon.spell;

import elucent.eidolon.Eidolon;
import elucent.eidolon.item.IRechargeableWand;
import elucent.eidolon.item.SummoningStaffItem;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.tile.AltarTileEntity;
import elucent.eidolon.tile.ItemHolderTileEntity;
import elucent.eidolon.tile.OffertoryPlateTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.lang.reflect.Method;

public class AltarRitual {
    private static final Method ZOMBIE_VILLAGER_FINISH_CONVERSION = findZombieVillagerFinishConversion();

    public enum PerformResult {
        SUCCESS,
        NO_MATCH,
        ABSORPTION_TARGET_TOO_HEALTHY
    }

    public enum SetupResult {
        FAIL,
        PASS,
        SUCCEED
    }

    private static final class MatchResult {
        private final BlockPos focus;
        private final List<BlockPos> offerings;

        private MatchResult(BlockPos focus, List<BlockPos> offerings) {
            this.focus = focus;
            this.offerings = offerings;
        }

        private int size() {
            return offerings.size() + (focus == null ? 0 : 1);
        }
    }

    private static final class AbsorptionResult {
        private final List<NBTTagCompound> charges = new ArrayList<>();

        private void add(EntityLivingBase entity) {
            NBTTagCompound tag = new NBTTagCompound();
            if (entity.writeToNBTOptional(tag)) {
                tag.removeTag("UUID");
                tag.removeTag("UUIDMost");
                tag.removeTag("UUIDLeast");
                for (int i = 0; i < 5; i++) {
                    charges.add(tag.copy());
                }
            }
        }

        private int size() {
            return charges.size();
        }
    }

    public enum BehaviorType {
        ITEM_RESULT,
        ITEM_TRANSFORM,
        SANGUINE,
        ITEM_CHARGE,
        ENTITY_SUMMON,
        ABSORPTION,
        PURIFY,
        CRYSTAL,
        ALLURE,
        REPELLING,
        DECEIT,
        DAYLIGHT,
        MOONLIGHT
    }

    private final ResourceLocation id;
    private final double requiredCapacity;
    private final double requiredPower;
    private final List<Ingredient> requiredOfferings;
    private final ItemStack result;
    private final BehaviorType behaviorType;
    private final Ingredient focus;
    private final Ingredient sacrifice;
    private final boolean explicitSacrifice;
    private final int providerOfferingStart;
    private final ResourceLocation entityId;
    private final float healthCost;

    AltarRitual(ResourceLocation id, double requiredCapacity, double requiredPower,
                ItemStack result, BehaviorType behaviorType, Ingredient focus, Ingredient... requiredOfferings) {
        this(id, requiredCapacity, requiredPower, result, behaviorType, focus, null, null, 0.0F, requiredOfferings);
    }

    AltarRitual(ResourceLocation id, double requiredCapacity, double requiredPower,
                ItemStack result, BehaviorType behaviorType, Ingredient focus, ResourceLocation entityId,
                Ingredient... requiredOfferings) {
        this(id, requiredCapacity, requiredPower, result, behaviorType, focus, null, entityId, 0.0F, requiredOfferings);
    }

    AltarRitual(ResourceLocation id, double requiredCapacity, double requiredPower,
                ItemStack result, BehaviorType behaviorType, Ingredient focus, ResourceLocation entityId,
                float healthCost, Ingredient... requiredOfferings) {
        this(id, requiredCapacity, requiredPower, result, behaviorType, focus, null, entityId, healthCost, requiredOfferings);
    }

    AltarRitual(ResourceLocation id, double requiredCapacity, double requiredPower,
                ItemStack result, BehaviorType behaviorType, Ingredient focus, Ingredient sacrifice,
                ResourceLocation entityId, float healthCost, Ingredient... requiredOfferings) {
        this.id = id;
        this.requiredCapacity = requiredCapacity;
        this.requiredPower = requiredPower;
        this.requiredOfferings = Collections.unmodifiableList(Arrays.asList(requiredOfferings));
        this.result = result.copy();
        this.behaviorType = behaviorType;
        this.focus = focus;
        this.sacrifice = sacrifice == null ? defaultSacrifice(result, focus, requiredOfferings) : sacrifice;
        this.explicitSacrifice = sacrifice != null;
        this.providerOfferingStart = sacrifice == null && requiredOfferings.length > 0 ? 1 : 0;
        this.entityId = entityId;
        this.healthCost = healthCost;
    }

    private static Ingredient defaultSacrifice(ItemStack result, Ingredient focus, Ingredient[] offerings) {
        if (offerings.length > 0) {
            return offerings[0];
        }
        if (focus != Ingredient.EMPTY) {
            return focus;
        }
        return result.isEmpty() ? Ingredient.EMPTY : Ingredient.fromStacks(result.copy());
    }

    public ResourceLocation getId() {
        return id;
    }

    public double getRequiredCapacity() {
        return requiredCapacity;
    }

    public double getRequiredPower() {
        return requiredPower;
    }

    public List<Ingredient> getRequiredOfferings() {
        return requiredOfferings;
    }

    public List<Ingredient> getProviderOfferings() {
        return requiredOfferings.subList(Math.min(providerOfferingStart, requiredOfferings.size()), requiredOfferings.size());
    }

    public Ingredient getFocus() {
        return focus;
    }

    public Ingredient getSacrifice() {
        return sacrifice;
    }

    public boolean matchesSacrifice(ItemStack stack) {
        return matchesIngredient(sacrifice, stack, behaviorType == BehaviorType.ITEM_CHARGE);
    }

    public boolean hasFocus() {
        return focus != Ingredient.EMPTY;
    }

    public int getRequiredOfferingCount() {
        return requiredOfferings.size() + (hasFocus() ? 1 : 0) + (explicitSacrifice ? 1 : 0);
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public boolean hasResult() {
        return !result.isEmpty();
    }

    public ItemStack getDisplayStack() {
        if (!result.isEmpty()) {
            return result.copy();
        }
        if (behaviorType == BehaviorType.PURIFY) {
            return new ItemStack(Items.SPECKLED_MELON);
        }
        ItemStack sacrificeStack = firstStack(sacrifice);
        if (!sacrificeStack.isEmpty()) {
            return sacrificeStack;
        }
        ItemStack focusStack = firstStack(focus);
        if (!focusStack.isEmpty()) {
            return focusStack;
        }
        for (Ingredient ingredient : requiredOfferings) {
            ItemStack stack = firstStack(ingredient);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public BehaviorType getBehaviorType() {
        return behaviorType;
    }

    public String getBehaviorTranslationKey() {
        return "gui.eidolon.codex.altar_behavior." + behaviorType.name().toLowerCase(java.util.Locale.ROOT);
    }

    public String getResultDescriptionTranslationKey() {
        return "gui.eidolon.codex.altar_result." + id.getPath();
    }

    public ResourceLocation getEntityId() {
        return entityId;
    }

    public float getHealthCost() {
        return healthCost;
    }

    public boolean hasHealthCost() {
        return healthCost > 0.0F;
    }

    public boolean isFieldRitual() {
        return behaviorType == BehaviorType.ALLURE
                || behaviorType == BehaviorType.REPELLING
                || behaviorType == BehaviorType.DECEIT
                || behaviorType == BehaviorType.DAYLIGHT
                || behaviorType == BehaviorType.MOONLIGHT;
    }

    public SetupResult setupFromProviders(World world, BlockPos origin, int step) {
        List<Ingredient> providerOfferings = getProviderOfferings();
        if (step >= providerOfferings.size()) {
            return SetupResult.SUCCEED;
        }
        TileEntity providerTile = findProviderTile(world, origin, providerOfferings.get(step), Collections.emptySet());
        if (!(providerTile instanceof IRitualItemProvider)) {
            return SetupResult.FAIL;
        }
        IRitualItemProvider provider = (IRitualItemProvider) providerTile;
        sendRitualConsumeEffect(world, providerTile.getPos(), origin);
        provider.take();
        return SetupResult.PASS;
    }

    public int getProviderOfferingCount() {
        return getProviderOfferings().size();
    }

    public boolean hasProviderFocusStep() {
        return hasFocus();
    }

    public PerformResult processFocusFromProviders(World world, BlockPos origin, EntityPlayer player) {
        if (!hasRequiredHealth(world, origin, player)) {
            return PerformResult.NO_MATCH;
        }
        if (behaviorType == BehaviorType.ITEM_TRANSFORM) {
            return findFocusProvider(world, origin) == null ? PerformResult.NO_MATCH : PerformResult.SUCCESS;
        } else if (behaviorType == BehaviorType.ITEM_CHARGE) {
            return rechargeFocusFromProvider(world, origin);
        } else if (behaviorType == BehaviorType.SANGUINE) {
            return consumeFocusFromProvider(world, origin);
        } else if (behaviorType == BehaviorType.ENTITY_SUMMON) {
            return consumeFocusFromProvider(world, origin);
        } else if (behaviorType == BehaviorType.ABSORPTION) {
            return absorbFromFocusProvider(world, origin);
        }
        return PerformResult.SUCCESS;
    }

    public PerformResult finishFromProviders(World world, BlockPos origin, EntityPlayer player) {
        if (!hasRequiredHealth(world, origin, player)) {
            return PerformResult.NO_MATCH;
        }
        consumeHealth(world, origin, player);
        if (behaviorType == BehaviorType.ITEM_TRANSFORM) {
            return transformFocusFromProvider(world, origin);
        } else if (behaviorType == BehaviorType.SANGUINE) {
            spawnSanguineResult(world, origin);
            playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
        } else if (behaviorType == BehaviorType.ENTITY_SUMMON) {
            summonEntity(world, origin);
            playSuccessEffects(world, origin, EnumParticleTypes.SMOKE_LARGE, 24);
        } else if (behaviorType == BehaviorType.PURIFY) {
            return purifyNearby(world, origin);
        } else if (behaviorType == BehaviorType.CRYSTAL) {
            performCrystal(world, origin);
        } else if (isFieldRitual()) {
            startFieldRitual(world, origin);
        } else if (behaviorType == BehaviorType.ITEM_RESULT) {
            spawnResult(world, origin);
            playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
        }
        return PerformResult.SUCCESS;
    }

    public PerformResult performFromProviders(World world, BlockPos origin, EntityPlayer player) {
        if (!hasRequiredHealth(world, origin, player)) {
            return PerformResult.NO_MATCH;
        }
        consumeHealth(world, origin, player);
        if (behaviorType == BehaviorType.ITEM_TRANSFORM) {
            return transformFocusFromProvider(world, origin);
        } else if (behaviorType == BehaviorType.SANGUINE) {
            PerformResult result = consumeFocusFromProvider(world, origin);
            if (result != PerformResult.SUCCESS) {
                return result;
            }
            spawnSanguineResult(world, origin);
            playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
        } else if (behaviorType == BehaviorType.ITEM_CHARGE) {
            return rechargeFocusFromProvider(world, origin);
        } else if (behaviorType == BehaviorType.ENTITY_SUMMON) {
            PerformResult result = consumeFocusFromProvider(world, origin);
            if (result != PerformResult.SUCCESS) {
                return result;
            }
            summonEntity(world, origin);
            playSuccessEffects(world, origin, EnumParticleTypes.SMOKE_LARGE, 24);
        } else if (behaviorType == BehaviorType.ABSORPTION) {
            return absorbFromFocusProvider(world, origin);
        } else if (behaviorType == BehaviorType.PURIFY) {
            return purifyNearby(world, origin);
        } else if (behaviorType == BehaviorType.CRYSTAL) {
            performCrystal(world, origin);
        } else if (isFieldRitual()) {
            startFieldRitual(world, origin);
        } else {
            spawnResult(world, origin);
            playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
        }
        return PerformResult.SUCCESS;
    }

    public boolean matches(AltarInfo info, EntityPlayer player) {
        return matches(info, null, null, player);
    }

    public boolean matches(AltarInfo info, World world, BlockPos origin, EntityPlayer player) {
        MatchResult match = findMatch(info);
        return info.getOfferingCount() >= getRequiredOfferingCount()
                && match.size() == getRequiredOfferingCount()
                && hasAltarPower(info)
                && hasRequiredHealth(world, origin, player);
    }

    public boolean hasAltarPower(AltarInfo info) {
        return info.getCapacity() >= requiredCapacity && info.getPower() >= requiredPower;
    }

    public boolean canStartFromProviders(World world, BlockPos origin, AltarInfo info) {
        if (!hasAltarPower(info)) {
            return false;
        }
        IRitualItemFocus focusProvider = null;
        if (hasFocus()) {
            focusProvider = findFocusProvider(world, origin);
            if (focusProvider == null) {
                return false;
            }
        }
        Set<TileEntity> used = new HashSet<>();
        if (focusProvider instanceof TileEntity) {
            used.add((TileEntity) focusProvider);
        }
        for (Ingredient ingredient : getProviderOfferings()) {
            TileEntity provider = findProviderTile(world, origin, ingredient, used);
            if (provider == null) {
                return false;
            }
            used.add(provider);
        }
        return true;
    }

    public PerformResult perform(World world, BlockPos origin, AltarInfo info, EntityPlayer player) {
        MatchResult match = findMatch(info);
        if (match.size() != getRequiredOfferingCount()) {
            return PerformResult.NO_MATCH;
        }
        if (!hasRequiredHealth(world, origin, player)) {
            return PerformResult.NO_MATCH;
        }
        consumeHealth(world, origin, player);
        if (behaviorType == BehaviorType.ITEM_TRANSFORM) {
            performItemTransform(world, origin, match);
        } else if (behaviorType == BehaviorType.SANGUINE) {
            performSanguine(world, origin, match);
        } else if (behaviorType == BehaviorType.ITEM_CHARGE) {
            performItemCharge(world, origin, match);
        } else if (behaviorType == BehaviorType.ENTITY_SUMMON) {
            performEntitySummon(world, origin, match);
        } else if (behaviorType == BehaviorType.ABSORPTION) {
            return performAbsorption(world, origin, match);
        } else if (behaviorType == BehaviorType.PURIFY) {
            return performPurify(world, origin, match);
        } else if (behaviorType == BehaviorType.CRYSTAL) {
            performCrystal(world, origin, match);
        } else if (isFieldRitual()) {
            performFieldRitual(world, origin, match);
        } else if (behaviorType == BehaviorType.ITEM_RESULT) {
            performItemResult(world, origin, match);
        }
        return PerformResult.SUCCESS;
    }

    private void performItemResult(World world, BlockPos origin, MatchResult match) {
        consumeOfferings(world, match.offerings, origin);
        spawnResult(world, origin);
        playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
    }

    private void performItemTransform(World world, BlockPos origin, MatchResult match) {
        transformFocus(world, match.focus);
        consumeOfferings(world, match.offerings, match.focus == null ? origin : match.focus);
        playSuccessEffects(world, match.focus == null ? origin : match.focus, EnumParticleTypes.VILLAGER_HAPPY, 12);
    }

    private void performSanguine(World world, BlockPos origin, MatchResult match) {
        consumeOffering(world, match.focus, origin);
        consumeOfferings(world, match.offerings, origin);
        spawnSanguineResult(world, origin);
        playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
    }

    private void performItemCharge(World world, BlockPos origin, MatchResult match) {
        rechargeFocus(world, match.focus);
        consumeOfferings(world, match.offerings, match.focus == null ? origin : match.focus);
        playSuccessEffects(world, match.focus == null ? origin : match.focus, EnumParticleTypes.SPELL, 18);
    }

    private void performEntitySummon(World world, BlockPos origin, MatchResult match) {
        consumeOffering(world, match.focus, origin);
        consumeOfferings(world, match.offerings, origin);
        summonEntity(world, origin);
        playSuccessEffects(world, origin, EnumParticleTypes.SMOKE_LARGE, 24);
    }

    private void performCrystal(World world, BlockPos origin, MatchResult match) {
        consumeOffering(world, match.focus, origin);
        consumeOfferings(world, match.offerings, origin);
        performCrystal(world, origin);
    }

    private void performFieldRitual(World world, BlockPos origin, MatchResult match) {
        consumeOffering(world, match.focus, origin);
        consumeOfferings(world, match.offerings, origin);
        startFieldRitual(world, origin);
    }

    private PerformResult performAbsorption(World world, BlockPos origin, MatchResult match) {
        AbsorptionResult absorbed = absorbNearbyUndead(world, origin);
        if (absorbed.size() <= 0) {
            return PerformResult.ABSORPTION_TARGET_TOO_HEALTHY;
        }
        chargeSummoningStaff(world, match.focus, absorbed.charges);
        consumeOfferings(world, match.offerings, match.focus == null ? origin : match.focus);
        sendRitualVisual(world, match.focus == null ? origin : match.focus, VisualEffectPacket.CRYSTALLIZE, 0.7F, 0.25F, 1.0F);
        playSuccessEffects(world, match.focus == null ? origin : match.focus, EnumParticleTypes.SPELL_MOB, 24);
        return PerformResult.SUCCESS;
    }

    private PerformResult performPurify(World world, BlockPos origin, MatchResult match) {
        PerformResult result = purifyNearby(world, origin);
        if (result != PerformResult.SUCCESS) {
            return result;
        }
        consumeOfferings(world, match.offerings, origin);
        return PerformResult.SUCCESS;
    }

    private void performCrystal(World world, BlockPos origin) {
        ActiveRituals.performCrystal(world, origin);
        playSuccessEffects(world, origin, EnumParticleTypes.SPELL_MOB, 24);
    }

    private void startFieldRitual(World world, BlockPos origin) {
        ActiveRituals.activate(world, origin, this);
        playSuccessEffects(world, origin, EnumParticleTypes.SPELL_WITCH, 18);
    }

    private void consumeOfferings(World world, List<BlockPos> matchedOfferings, BlockPos destination) {
        for (BlockPos altarPos : matchedOfferings) {
            consumeOffering(world, altarPos, destination);
        }
    }

    private void transformFocus(World world, BlockPos focusPos) {
        if (focusPos != null) {
            TileEntity tile = world.getTileEntity(focusPos);
            if (tile instanceof AltarTileEntity) {
                ((AltarTileEntity) tile).setOffering(result.copy());
            }
        }
    }

    private void rechargeFocus(World world, BlockPos focusPos) {
        if (focusPos != null) {
            TileEntity tile = world.getTileEntity(focusPos);
            if (tile instanceof AltarTileEntity) {
                AltarTileEntity altar = (AltarTileEntity) tile;
                ItemStack stack = altar.getOffering();
                if (!stack.isEmpty() && stack.getItem() instanceof IRechargeableWand) {
                    altar.setOffering(((IRechargeableWand) stack.getItem()).recharge(stack));
                }
            }
        }
    }

    private void chargeSummoningStaff(World world, BlockPos focusPos, List<NBTTagCompound> charges) {
        if (focusPos != null) {
            TileEntity tile = world.getTileEntity(focusPos);
            if (tile instanceof AltarTileEntity) {
                AltarTileEntity altar = (AltarTileEntity) tile;
                ItemStack stack = altar.getOffering();
                if (!stack.isEmpty() && stack.getItem() instanceof SummoningStaffItem) {
                    altar.setOffering(((SummoningStaffItem) stack.getItem()).addAbsorbedUndeadCharges(stack, charges));
                }
            }
        }
    }

    private void consumeOffering(World world, BlockPos altarPos, BlockPos destination) {
        if (altarPos != null) {
            TileEntity tile = world.getTileEntity(altarPos);
            if (tile instanceof AltarTileEntity) {
                sendRitualConsumeEffect(world, altarPos, destination == null ? altarPos : destination);
                ((AltarTileEntity) tile).removeOffering();
            }
        }
    }

    private void spawnResult(World world, BlockPos origin) {
        if (result.isEmpty()) {
            return;
        }
        EntityItem item = new EntityItem(world, origin.getX() + 0.5D, origin.getY() + 1.1D, origin.getZ() + 0.5D, result.copy());
        world.spawnEntity(item);
    }

    private void spawnSanguineResult(World world, BlockPos origin) {
        if (result.isEmpty()) {
            return;
        }
        EntityItem item = new EntityItem(world, origin.getX() + 0.5D, origin.getY() + 2.5D, origin.getZ() + 0.5D, result.copy());
        world.spawnEntity(item);
    }

    private void summonEntity(World world, BlockPos origin) {
        if (entityId == null) {
            return;
        }
        Entity entity = EntityList.createEntityByIDFromName(entityId, world);
        if (entity == null) {
            return;
        }
        entity.setPosition(origin.getX() + 0.5D, origin.getY() + 1.5D, origin.getZ() + 0.5D);
        if (entity instanceof EntityLiving) {
            ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(origin.up()), null);
        }
        world.spawnEntity(entity);
        VisualEffectPacket.sendAround(world, entity.posX, entity.posY + entity.height * 0.5D, entity.posZ,
                VisualEffectPacket.at(VisualEffectPacket.SUMMON_BURST, entity.posX, entity.posY + entity.height * 0.5D,
                        entity.posZ, 0.55F, 0.25F, 0.85F));
    }

    private AbsorptionResult absorbNearbyUndead(World world, BlockPos origin) {
        AxisAlignedBB bounds = new AxisAlignedBB(origin).grow(8.0D, 4.0D, 8.0D);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds,
                entity -> Eidolon.getTrueCreatureAttribute(entity) == EnumCreatureAttribute.UNDEAD
                        && !entity.getEntityData().getBoolean(SummoningStaffItem.SUMMONED_TAG)
                        && entity.getHealth() <= entity.getMaxHealth() / 5.0F);
        AbsorptionResult absorbed = new AbsorptionResult();
        for (EntityLivingBase entity : entities) {
            if (entity instanceof EntityPlayer) {
                continue;
            }
            entity.setHealth(entity.getMaxHealth());
            absorbed.add(entity);
            VisualEffectPacket.sendAround(world, entity.posX, entity.posY + entity.height * 0.5D, entity.posZ,
                    VisualEffectPacket.line(VisualEffectPacket.LIFESTEAL, entity.posX, entity.posY + entity.height * 0.5D,
                            entity.posZ, origin.getX() + 0.5D, origin.getY() + 1.0D, origin.getZ() + 0.5D,
                            0.65F, 0.15F, 0.95F));
            entity.setDead();
        }
        return absorbed;
    }

    private PerformResult purifyNearby(World world, BlockPos origin) {
        AxisAlignedBB bounds = new AxisAlignedBB(origin).grow(8.0D, 4.0D, 8.0D);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, bounds,
                entity -> entity instanceof EntityZombieVillager || entity instanceof EntityPigZombie);
        if (entities.isEmpty()) {
            return PerformResult.NO_MATCH;
        }
        if (!world.isRemote) {
            world.playSound(null, origin.getX() + 0.5D, origin.getY() + 0.8D, origin.getZ() + 0.5D,
            SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS, 0.8F, 1.0F);
            for (EntityLivingBase entity : entities) {
                if (entity instanceof EntityZombieVillager) {
                    finishZombieVillagerConversion((EntityZombieVillager) entity);
                } else if (entity instanceof EntityPigZombie) {
                    EntityPig pig = new EntityPig(world);
                    pig.copyLocationAndAnglesFrom(entity);
                    entity.setDead();
                    world.spawnEntity(pig);
                }
                if (world instanceof WorldServer) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
                            entity.posX, entity.posY + entity.height + 0.2D, entity.posZ,
                            12, 0.32D, 0.18D, 0.32D, 0.02D);
                }
            }
        }
        playSuccessEffects(world, origin, EnumParticleTypes.VILLAGER_HAPPY, 24);
        return PerformResult.SUCCESS;
    }

    private static Method findZombieVillagerFinishConversion() {
        for (String name : new String[]{"finishConversion", "func_190738_dp"}) {
            try {
                Method method = EntityZombieVillager.class.getDeclaredMethod(name);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
            }
        }
        Eidolon.LOGGER.error("Unable to find EntityZombieVillager finishConversion method.");
        return null;
    }

    private static void finishZombieVillagerConversion(EntityZombieVillager villager) {
        if (ZOMBIE_VILLAGER_FINISH_CONVERSION == null) {
            return;
        }
        try {
            ZOMBIE_VILLAGER_FINISH_CONVERSION.invoke(villager);
        } catch (ReflectiveOperationException e) {
            Eidolon.LOGGER.error("Failed to finish zombie villager conversion.", e);
        }
    }

    private boolean hasRequiredHealth(World world, BlockPos origin, EntityPlayer player) {
        return !hasHealthCost() || getAvailableRitualHealth(world, origin, player) >= healthCost;
    }

    private float getAvailableRitualHealth(World world, BlockPos origin, EntityPlayer player) {
        float health = 0.0F;
        for (EntityLivingBase target : getRitualHealthTargets(world, origin, player)) {
            health += Math.max(0.0F, target.getHealth());
            if (health >= healthCost) {
                return health;
            }
        }
        return health;
    }

    private void consumeHealth(World world, BlockPos origin, EntityPlayer player) {
        if (!hasHealthCost()) {
            return;
        }
        float consumed = 0.0F;
        for (EntityLivingBase target : getRitualHealthTargets(world, origin, player)) {
            float targetHealth = Math.max(0.0F, target.getHealth());
            if (targetHealth <= 0.0F) {
                continue;
            }
            float damage = Math.min(healthCost - consumed, targetHealth);
            if (damage <= 0.0F) {
                return;
            }
            target.attackEntityFrom(Eidolon.RITUAL_DAMAGE, damage);
            sendRitualHealthEffect(world, target, origin);
            consumed += targetHealth;
            if (consumed >= healthCost) {
                return;
            }
        }
    }

    private List<EntityLivingBase> getRitualHealthTargets(World world, BlockPos origin, EntityPlayer player) {
        List<EntityLivingBase> targets = new ArrayList<>();
        Set<Integer> used = new HashSet<>();
        if (world != null && origin != null) {
            AxisAlignedBB bounds = new AxisAlignedBB(
                    origin.getX() - 8.0D, origin.getY() - 6.0D, origin.getZ() - 8.0D,
                    origin.getX() + 9.0D, origin.getY() + 11.0D, origin.getZ() + 9.0D);
            List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, bounds,
                    entity -> Eidolon.getCreatureAttribute(entity) != EnumCreatureAttribute.UNDEAD);
            for (EntityLiving mob : mobs) {
                if (used.add(mob.getEntityId())) {
                    targets.add(mob);
                }
            }
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, bounds);
            for (EntityPlayer nearbyPlayer : players) {
                if (used.add(nearbyPlayer.getEntityId())) {
                    targets.add(nearbyPlayer);
                }
            }
        }
        if (player != null && used.add(player.getEntityId())) {
            targets.add(player);
        }
        return targets;
    }

    private void sendRitualHealthEffect(World world, EntityLivingBase target, BlockPos origin) {
        if (world == null || origin == null || target == null) {
            return;
        }
        VisualEffectPacket.sendAround(world, target.posX, target.posY + target.height * 0.5D, target.posZ,
                VisualEffectPacket.line(VisualEffectPacket.RITUAL_CONSUME,
                        target.posX, target.posY + target.height * 0.5D, target.posZ,
                        origin.getX() + 0.5D, origin.getY() + 1.0D, origin.getZ() + 0.5D,
                        0.85F, 0.05F, 0.12F));
    }

    private void playSuccessEffects(World world, BlockPos pos, EnumParticleTypes particle, int count) {
        world.playSound(null, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D,
                SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 0.45F, 1.35F);
        sendRitualVisual(world, pos, VisualEffectPacket.RITUAL_COMPLETE, 0.65F, 0.28F, 1.0F);
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(particle,
                    pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                    count, 0.28D, 0.2D, 0.28D, 0.02D);
        }
    }

    private IRitualItemProvider findProvider(World world, BlockPos origin, Ingredient ingredient) {
        TileEntity tile = findProviderTile(world, origin, ingredient, Collections.emptySet());
        return tile instanceof IRitualItemProvider ? (IRitualItemProvider) tile : null;
    }

    private TileEntity findProviderTile(World world, BlockPos origin, Ingredient ingredient, Set<TileEntity> used) {
        AxisAlignedBB bounds = new AxisAlignedBB(origin).grow(8.0D, 6.0D, 8.0D);
        for (TileEntity tile : getTilesWithin(world, bounds)) {
            if (used.contains(tile)) {
                continue;
            }
            if (tile instanceof IRitualItemFocus) {
                continue;
            }
            if (tile instanceof OffertoryPlateTileEntity) {
                continue;
            }
            if (tile instanceof IRitualItemProvider) {
                IRitualItemProvider provider = (IRitualItemProvider) tile;
                if (matchesIngredient(ingredient, provider.provide(), false)) {
                    return tile;
                }
            }
        }
        return null;
    }

    private IRitualItemFocus findFocusProvider(World world, BlockPos origin) {
        AxisAlignedBB bounds = new AxisAlignedBB(origin).grow(8.0D, 6.0D, 8.0D);
        for (TileEntity tile : getTilesWithin(world, bounds)) {
            if (tile instanceof IRitualItemFocus) {
                IRitualItemFocus focusProvider = (IRitualItemFocus) tile;
                if (!hasFocus() || matchesIngredient(focus, focusProvider.provide(), behaviorType == BehaviorType.ITEM_CHARGE)) {
                    return focusProvider;
                }
            }
        }
        return null;
    }

    private List<TileEntity> getTilesWithin(World world, AxisAlignedBB bounds) {
        List<TileEntity> tiles = new ArrayList<>();
        int minX = (int) Math.floor(bounds.minX);
        int minY = (int) Math.floor(bounds.minY);
        int minZ = (int) Math.floor(bounds.minZ);
        int maxX = (int) Math.ceil(bounds.maxX);
        int maxY = (int) Math.ceil(bounds.maxY);
        int maxZ = (int) Math.ceil(bounds.maxZ);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
                    if (tile != null) {
                        tiles.add(tile);
                    }
                }
            }
        }
        return tiles;
    }

    private PerformResult consumeFocusFromProvider(World world, BlockPos origin) {
        IRitualItemFocus focusProvider = findFocusProvider(world, origin);
        if (focusProvider != null) {
            sendProviderConsumeEffect(world, focusProvider, origin);
            focusProvider.take();
            return PerformResult.SUCCESS;
        }
        return PerformResult.NO_MATCH;
    }

    private PerformResult transformFocusFromProvider(World world, BlockPos origin) {
        IRitualItemFocus focusProvider = findFocusProvider(world, origin);
        if (focusProvider == null) {
            return PerformResult.NO_MATCH;
        }
        focusProvider.replace(result.copy());
        sendProviderConsumeEffect(world, focusProvider, origin);
        playFocusProviderEffect(focusProvider);
        playSuccessEffects(world, origin, EnumParticleTypes.VILLAGER_HAPPY, 12);
        return PerformResult.SUCCESS;
    }

    private PerformResult rechargeFocusFromProvider(World world, BlockPos origin) {
        IRitualItemFocus focusProvider = findFocusProvider(world, origin);
        if (focusProvider == null) {
            return PerformResult.NO_MATCH;
        }
        ItemStack stack = focusProvider.provide();
        if (!stack.isEmpty() && stack.getItem() instanceof IRechargeableWand) {
            focusProvider.replace(((IRechargeableWand) stack.getItem()).recharge(stack));
            sendProviderConsumeEffect(world, focusProvider, origin);
            playFocusProviderEffect(focusProvider);
            playSuccessEffects(world, origin, EnumParticleTypes.SPELL, 18);
            return PerformResult.SUCCESS;
        }
        return PerformResult.NO_MATCH;
    }

    private PerformResult absorbFromFocusProvider(World world, BlockPos origin) {
        IRitualItemFocus focusProvider = findFocusProvider(world, origin);
        if (focusProvider == null) {
            return PerformResult.NO_MATCH;
        }
        AbsorptionResult absorbed = absorbNearbyUndead(world, origin);
        if (absorbed.size() <= 0) {
            return PerformResult.ABSORPTION_TARGET_TOO_HEALTHY;
        }
        ItemStack stack = focusProvider.provide();
        if (!stack.isEmpty() && stack.getItem() instanceof SummoningStaffItem) {
            focusProvider.replace(((SummoningStaffItem) stack.getItem()).addAbsorbedUndeadCharges(stack, absorbed.charges));
            sendProviderConsumeEffect(world, focusProvider, origin);
            playFocusProviderEffect(focusProvider);
            sendRitualVisual(world, origin, VisualEffectPacket.CRYSTALLIZE, 0.7F, 0.25F, 1.0F);
            playSuccessEffects(world, origin, EnumParticleTypes.SPELL_MOB, 24);
            return PerformResult.SUCCESS;
        }
        return PerformResult.NO_MATCH;
    }

    private void playFocusProviderEffect(IRitualItemFocus focusProvider) {
        if (focusProvider instanceof ItemHolderTileEntity) {
            ((ItemHolderTileEntity) focusProvider).playFocusEffect();
        }
    }

    private void sendProviderConsumeEffect(World world, IRitualItemProvider provider, BlockPos destination) {
        if (provider instanceof TileEntity) {
            sendRitualConsumeEffect(world, ((TileEntity) provider).getPos(), destination);
        }
    }

    private void sendRitualConsumeEffect(World world, BlockPos source, BlockPos destination) {
        if (source == null || destination == null) {
            return;
        }
        VisualEffectPacket.sendAround(world, source,
                VisualEffectPacket.line(VisualEffectPacket.RITUAL_CONSUME, source, destination, 0.62F, 0.22F, 0.9F));
    }

    private void sendRitualVisual(World world, BlockPos pos, int effect, float r, float g, float b) {
        if (pos == null) {
            return;
        }
        VisualEffectPacket.sendAround(world, pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D,
                VisualEffectPacket.at(effect, pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D, r, g, b));
    }

    private MatchResult findMatch(AltarInfo info) {
        List<BlockPos> matchedOfferings = new ArrayList<>();
        Set<BlockPos> used = new HashSet<>();
        BlockPos focusMatch = null;
        if (hasFocus()) {
            focusMatch = findOffering(info, focus, used, behaviorType == BehaviorType.ITEM_CHARGE);
            if (focusMatch == null) {
                return new MatchResult(null, Collections.emptyList());
            }
            used.add(focusMatch);
        }
        if (explicitSacrifice && sacrifice != Ingredient.EMPTY) {
            BlockPos match = findOffering(info, sacrifice, used, false);
            if (match == null) {
                return new MatchResult(null, Collections.emptyList());
            }
            matchedOfferings.add(match);
            used.add(match);
        }
        for (Ingredient ingredient : requiredOfferings) {
            BlockPos match = findOffering(info, ingredient, used, false);
            if (match == null) {
                return new MatchResult(null, Collections.emptyList());
            }
            matchedOfferings.add(match);
            used.add(match);
        }
        return new MatchResult(focusMatch, matchedOfferings);
    }

    private BlockPos findOffering(AltarInfo info, Ingredient ingredient, Set<BlockPos> used, boolean ignoreDamage) {
        for (BlockPos altarPos : info.getAltarPositions()) {
            if (used.contains(altarPos)) {
                continue;
            }
            ItemStack offering = info.getOffering(altarPos);
            if (!offering.isEmpty() && matchesIngredient(ingredient, offering, ignoreDamage)) {
                return altarPos;
            }
        }
        return null;
    }

    private boolean matchesIngredient(Ingredient ingredient, ItemStack stack, boolean ignoreDamage) {
        if (ingredient.apply(stack)) {
            return true;
        }
        if (!ignoreDamage) {
            return false;
        }
        for (ItemStack match : ingredient.getMatchingStacks()) {
            if (!match.isEmpty() && match.getItem() == stack.getItem()) {
                return true;
            }
        }
        return false;
    }

    private ItemStack firstStack(Ingredient ingredient) {
        if (ingredient == Ingredient.EMPTY) {
            return ItemStack.EMPTY;
        }
        ItemStack[] stacks = ingredient.getMatchingStacks();
        return stacks.length == 0 ? ItemStack.EMPTY : stacks[0].copy();
    }
}
