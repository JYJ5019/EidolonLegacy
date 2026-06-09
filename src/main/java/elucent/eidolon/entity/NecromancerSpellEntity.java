package elucent.eidolon.entity;

import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class NecromancerSpellEntity extends EntityThrowable {
    private int delayTicks;

    public NecromancerSpellEntity(World world) {
        super(world);
        setSize(0.4F, 0.4F);
    }

    public NecromancerSpellEntity(World world, EntityLivingBase thrower, int delayTicks) {
        super(world, thrower);
        setSize(0.4F, 0.4F);
        this.delayTicks = delayTicks;
    }

    @Override
    public void onUpdate() {
        if (delayTicks > 0) {
            delayTicks--;
            return;
        }
        super.onUpdate();
        if (world.isRemote) {
            double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            double vx = speed > 0.0D ? -motionX / speed * 0.025D : 0.0D;
            double vy = speed > 0.0D ? -motionY / speed * 0.025D : 0.0D;
            double vz = speed > 0.0D ? -motionZ / speed * 0.025D : 0.0D;
            for (int i = 0; i < 8; i++) {
                double t = i / 8.0D;
                double x = prevPosX + (posX - prevPosX) * t;
                double y = prevPosY + (posY - prevPosY) * t;
                double z = prevPosZ + (posZ - prevPosZ) * t;
                EidolonParticles.create(EidolonParticles.WISP)
                        .addVelocity(vx, vy, vz)
                        .alpha(0.375F, 0.0F)
                        .scale(0.25F, 0.0F)
                        .color(1.0F, 0.3125F, 0.375F, 0.75F, 0.375F, 1.0F)
                        .lifetime(5)
                        .spawn(world, x, y, z);
                EidolonParticles.create(EidolonParticles.SMOKE)
                        .addVelocity(vx, vy, vz)
                        .alpha(0.0625F, 0.0F)
                        .scale(0.3125F, 0.125F)
                        .color(0.625F, 0.375F, 1.0F, 0.25F, 0.25F, 0.75F)
                        .randomVelocity(0.025D, 0.025D)
                        .lifetime(20)
                        .fullbright(false)
                        .spawn(world, x, y, z);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit instanceof EntityLivingBase && result.entityHit != getThrower()) {
                EntityLivingBase target = (EntityLivingBase) result.entityHit;
                target.attackEntityFrom(new EntityDamageSourceIndirect(DamageSource.WITHER.getDamageType(), this, getThrower()),
                        3.0F + world.getDifficulty().getId());
            }
            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_WITHER_SHOOT,
                    SoundCategory.HOSTILE, 0.5F, rand.nextFloat() * 0.2F + 0.9F);
            VisualEffectPacket.sendAround(world, posX, posY, posZ,
                    VisualEffectPacket.at(VisualEffectPacket.NECROMANCER_BURST, posX, posY, posZ,
                            158.0F / 255.0F, 92.0F / 255.0F, 1.0F,
                            60.0F / 255.0F, 62.0F / 255.0F, 186.0F / 255.0F));
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
