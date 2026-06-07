package elucent.eidolon.entity;

import elucent.eidolon.Reference;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public class SlimySlugEntity extends EntityAnimal {
    private static final UUID SLUG_NEMESIS = UUID.fromString("0ca54301-6170-4c44-b3e0-b8afa6b81ed2");
    private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(SlimySlugEntity.class, DataSerializers.VARINT);
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(Reference.MOD_ID, "entities/slimy_slug");

    public SlimySlugEntity(World world) {
        super(world);
        setSize(0.5F, 0.25F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(VARIANT, 0);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.PUMPKIN_SEEDS, false));
        tasks.addTask(5, new EntityAIWander(this, 0.8D));
        tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        tasks.addTask(8, new EntityAILookIdle(this));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false,
                player -> SLUG_NEMESIS.equals(((EntityPlayer) player).getGameProfile().getId())));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        if (getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) == null) {
            getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        }
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(999.0D);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source != null && source.getTrueSource() instanceof EntityPlayer
                && SLUG_NEMESIS.equals(((EntityPlayer) source.getTrueSource()).getGameProfile().getId())) {
            source.getTrueSource().attackEntityFrom(source, amount);
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    public int getVariant() {
        return dataManager.get(VARIANT);
    }

    public void setVariant(int variant) {
        dataManager.set(VARIANT, Math.max(0, Math.min(2, variant)));
    }

    @Override
    public net.minecraft.entity.IEntityLivingData onInitialSpawn(net.minecraft.world.DifficultyInstance difficulty, net.minecraft.entity.IEntityLivingData livingdata) {
        net.minecraft.entity.IEntityLivingData data = super.onInitialSpawn(difficulty, livingdata);
        setVariant(0);
        return data;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Variant", getVariant());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setVariant(compound.getInteger("Variant"));
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT_TABLE;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int slime = rand.nextInt(2) + randomLootingBonus(lootingModifier, 1);
        if (slime > 0) {
            entityDropItem(new ItemStack(Items.SLIME_BALL, slime), 0.0F);
        }
    }

    private int randomLootingBonus(int lootingModifier, int maxPerLevel) {
        return lootingModifier <= 0 || maxPerLevel <= 0 ? 0 : rand.nextInt(lootingModifier * maxPerLevel + 1);
    }
}
