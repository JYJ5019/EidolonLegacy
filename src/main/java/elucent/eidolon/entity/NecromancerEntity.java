package elucent.eidolon.entity;

import elucent.eidolon.capability.ReputationData;
import elucent.eidolon.deity.Deities;
import elucent.eidolon.item.SummoningStaffItem;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class NecromancerEntity extends EntityMob {
    private int castingTicks;

    public NecromancerEntity(World world) {
        super(world);
        setSize(0.6F, 1.9F);
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new CastBoltAI());
        tasks.addTask(2, new SummonUndeadAI());
        tasks.addTask(7, new EntityAIWander(this, 1.0D));
        tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        tasks.addTask(9, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, 10, true, false,
                player -> ReputationData.get(world).getReputation((EntityPlayer) player, Deities.DARK_DEITY.getId()) >= 50.0D));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    public boolean isCastingSpellLegacy() {
        return castingTicks > 0;
    }

    @Override
    public void onLivingUpdate() {
        if (castingTicks > 0) {
            castingTicks--;
            spawnCastingParticles();
        }
        super.onLivingUpdate();
    }

    private void spawnCastingParticles() {
        if (world.isRemote) {
            for (int i = 0; i < 2; i++) {
                double angle = ticksExisted * 0.22D + i * Math.PI;
                EidolonParticles.spawnWisp(world,
                        posX + Math.cos(angle) * 0.55D,
                        posY + 1.65D + rand.nextDouble() * 0.25D,
                        posZ + Math.sin(angle) * 0.55D,
                        -Math.sin(angle) * 0.012D,
                        0.008D,
                        Math.cos(angle) * 0.012D,
                        0.7F, 0.25F, 0.95F);
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH,
                        posX + Math.cos(angle) * 0.55D,
                        posY + 1.65D + rand.nextDouble() * 0.25D,
                        posZ + Math.sin(angle) * 0.55D,
                        0.7D, 0.25D, 0.95D);
            }
            if (ticksExisted % 5 == 0) {
                EidolonParticles.spawnSparkle(world,
                        posX + (rand.nextDouble() - 0.5D) * 0.65D,
                        posY + 1.45D + rand.nextDouble() * 0.35D,
                        posZ + (rand.nextDouble() - 0.5D) * 0.65D,
                        (rand.nextDouble() - 0.5D) * 0.025D,
                        0.025D,
                        (rand.nextDouble() - 0.5D) * 0.025D,
                        0.95F, 0.55F, 1.0F);
            }
        }
    }

    private void startCasting(int ticks) {
        castingTicks = ticks;
        playSound(SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT, 1.0F, 0.9F + rand.nextFloat() * 0.2F);
        if (!world.isRemote) {
            VisualEffectPacket.sendAround(world, posX, posY + 1.35D, posZ,
                    VisualEffectPacket.at(VisualEffectPacket.NECROMANCER_BURST, posX, posY + 1.35D, posZ,
                            0.7F, 0.2F, 0.95F));
        }
    }

    private void castBolts(EntityLivingBase target) {
        if (target == null || world.isRemote) {
            return;
        }
        Vec3d diff = new Vec3d(target.posX - posX, target.getEntityBoundingBox().minY + target.height * 0.5D - (posY + getEyeHeight()), target.posZ - posZ);
        Vec3d dir = diff.normalize();
        for (int i = 0; i < 3; i++) {
            NecromancerSpellEntity spell = new NecromancerSpellEntity(world, this, i * 5);
            spell.setLocationAndAngles(posX, posY + getEyeHeight(), posZ, rotationYaw, rotationPitch);
            spell.shoot(dir.x + (rand.nextDouble() - 0.5D) * 0.1D,
                    dir.y + 0.04D * diff.length() / 2.0D + (rand.nextDouble() - 0.5D) * 0.1D,
                    dir.z + (rand.nextDouble() - 0.5D) * 0.1D, 0.9F, 0.4F);
            world.spawnEntity(spell);
        }
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_WITCH, posX, posY + 1.2D, posZ,
                    18, 0.4D, 0.4D, 0.4D, 0.02D);
        }
        VisualEffectPacket.sendAround(world, posX, posY + 1.2D, posZ,
                VisualEffectPacket.at(VisualEffectPacket.NECROMANCER_BURST, posX, posY + 1.2D, posZ,
                        0.7F, 0.2F, 0.95F));
        playSound(SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT, 1.0F, 1.0F);
    }

    private void summonUndead(EntityLivingBase target) {
        if (world.isRemote) {
            return;
        }
        EntityLiving summon = rand.nextBoolean() ? new EntitySkeleton(world) : new EntityZombie(world);
        summon.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
        if (target != null) {
            summon.setAttackTarget(target);
        }
        summon.getEntityData().setBoolean(SummoningStaffItem.SUMMONED_TAG, true);
        summon.getEntityData().setString(SummoningStaffItem.OWNER_TAG, getUniqueID().toString());
        world.spawnEntity(summon);
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, summon.posX, summon.posY + 0.7D, summon.posZ,
                    24, 0.35D, 0.35D, 0.35D, 0.04D);
        }
        VisualEffectPacket.sendAround(world, summon.posX, summon.posY + summon.height * 0.5D, summon.posZ,
                VisualEffectPacket.at(VisualEffectPacket.SUMMON_BURST, summon.posX,
                        summon.posY + summon.height * 0.5D, summon.posZ, 0.55F, 0.25F, 0.85F));
        playSound(SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT, 1.0F, 0.85F);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOCATION_ILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.util.DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_EVOCATION_ILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    private abstract class NecromancerSpellAI extends EntityAIBase {
        private final int castTime;
        private final int cooldownTime;
        private int cooldown;
        private int cast;

        NecromancerSpellAI(int castTime, int cooldownTime) {
            this.castTime = castTime;
            this.cooldownTime = cooldownTime;
            setMutexBits(3);
        }

        @Override
        public boolean shouldExecute() {
            EntityLivingBase target = getAttackTarget();
            if (cooldown > 0) {
                cooldown--;
                return false;
            }
            return target != null && target.isEntityAlive() && !isCastingSpellLegacy() && canCast(target);
        }

        @Override
        public boolean shouldContinueExecuting() {
            EntityLivingBase target = getAttackTarget();
            return cast > 0 && target != null && target.isEntityAlive();
        }

        @Override
        public void startExecuting() {
            cast = castTime;
            cooldown = cooldownTime;
            startCasting(castTime);
        }

        @Override
        public void updateTask() {
            EntityLivingBase target = getAttackTarget();
            if (target != null) {
                getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }
            cast--;
            if (cast == 1) {
                perform(target);
            }
        }

        protected boolean canCast(EntityLivingBase target) {
            return true;
        }

        protected abstract void perform(EntityLivingBase target);
    }

    private class CastBoltAI extends NecromancerSpellAI {
        CastBoltAI() {
            super(40, 80);
        }

        @Override
        protected void perform(EntityLivingBase target) {
            castBolts(target);
        }
    }

    private class SummonUndeadAI extends NecromancerSpellAI {
        SummonUndeadAI() {
            super(40, 200);
        }

        @Override
        protected boolean canCast(EntityLivingBase target) {
            return true;
        }

        @Override
        protected void perform(EntityLivingBase target) {
            summonUndead(target);
        }
    }
}
