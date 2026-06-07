package elucent.eidolon.entity;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ZombieBruteEntity extends EntityMob {
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(Reference.MOD_ID, "entities/zombie_brute");

    public ZombieBruteEntity(World world) {
        super(world);
        setSize(1.2F, 2.5F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
        tasks.addTask(7, new EntityAIWander(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityVillager.class, false));
        targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(6.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
    }

    @Override
    public void onLivingUpdate() {
        if (world.isDaytime() && !world.isRemote) {
            float brightness = getBrightness();
            BlockPos blockpos = new BlockPos(posX, Math.round(posY), posZ);
            if (getRidingEntity() instanceof EntityBoat) {
                blockpos = blockpos.up();
            }
            if (brightness > 0.5F
                    && world.canSeeSky(blockpos)
                    && rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
                setFire(8);
            }
        }
        super.onLivingUpdate();
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 8;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT_TABLE;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int flesh = 1 + rand.nextInt(4) + randomLootingBonus(lootingModifier, 2);
        entityDropItem(new ItemStack(Items.ROTTEN_FLESH, flesh), 0.0F);

        int bones = rand.nextInt(2) + randomLootingBonus(lootingModifier, 1);
        if (bones > 0) {
            entityDropItem(new ItemStack(Items.BONE, bones), 0.0F);
        }

        if (wasRecentlyHit && rand.nextFloat() < 0.1F + 0.05F * lootingModifier) {
            entityDropItem(new ItemStack(ModItems.ZOMBIE_HEART), 0.0F);
        }
    }

    private int randomLootingBonus(int lootingModifier, int maxPerLevel) {
        return lootingModifier <= 0 || maxPerLevel <= 0 ? 0 : rand.nextInt(lootingModifier * maxPerLevel + 1);
    }
}
