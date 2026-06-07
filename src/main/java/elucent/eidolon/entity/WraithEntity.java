package elucent.eidolon.entity;

import elucent.eidolon.Reference;
import elucent.eidolon.registries.ModItems;
import elucent.eidolon.registries.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WraithEntity extends EntityMob {
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(Reference.MOD_ID, "entities/wraith");

    public WraithEntity(World world) {
        super(world);
        setSize(0.6F, 1.9F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.5D, false));
        tasks.addTask(7, new EntityAIWander(this, 1.0D));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        boolean hit = super.attackEntityAsMob(entity);
        if (hit && entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.CHILLED, 100 + world.getDifficulty().ordinal() * 100));
        }
        return hit;
    }

    @Override
    public void onLivingUpdate() {
        burnInSunlight();
        fallDistance = 0.0F;
        if (!onGround && motionY < 0.0D) {
            motionY *= 0.6D;
        }
        BlockPos below = new BlockPos(posX, posY - 0.15D, posZ);
        if (!world.getBlockState(below).getMaterial().isSolid() && world.getBlockState(below).getMaterial().isLiquid() && motionY < 0.0D) {
            motionY = 0.02D;
        }
        super.onLivingUpdate();
    }

    private void burnInSunlight() {
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
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_STRAY_DEATH;
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return 5;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return LOOT_TABLE;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int cloth = rand.nextInt(3) + randomLootingBonus(lootingModifier, 2);
        if (cloth > 0) {
            entityDropItem(new ItemStack(ModItems.TATTERED_CLOTH, cloth), 0.0F);
        }

        if (wasRecentlyHit && rand.nextFloat() < 0.05F + 0.025F * lootingModifier) {
            entityDropItem(new ItemStack(ModItems.WRAITH_HEART), 0.0F);
        }
    }

    private int randomLootingBonus(int lootingModifier, int maxPerLevel) {
        return lootingModifier <= 0 || maxPerLevel <= 0 ? 0 : rand.nextInt(lootingModifier * maxPerLevel + 1);
    }
}
