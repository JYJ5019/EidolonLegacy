package elucent.eidolon.entity;

import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

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
            if (world.isRemote) {
                EidolonParticles.spawnWisp(world, posX, posY + 0.1D, posZ,
                        (rand.nextDouble() - 0.5D) * 0.018D,
                        (rand.nextDouble() - 0.5D) * 0.018D,
                        (rand.nextDouble() - 0.5D) * 0.018D,
                        0.7F, 0.25F, 0.9F);
                world.spawnParticle(EnumParticleTypes.SPELL_WITCH, posX, posY + 0.1D, posZ, 0.7D, 0.25D, 0.9D);
            }
            return;
        }
        super.onUpdate();
        if (world.isRemote) {
            EidolonParticles.spawnWisp(world, posX, posY, posZ,
                    (rand.nextDouble() - 0.5D) * 0.015D,
                    (rand.nextDouble() - 0.5D) * 0.015D,
                    (rand.nextDouble() - 0.5D) * 0.015D,
                    0.7F, 0.15F, 0.95F);
            if (ticksExisted % 2 == 0) {
                EidolonParticles.spawnSmoke(world, posX, posY, posZ,
                        -motionX * 0.05D,
                        -motionY * 0.05D,
                        -motionZ * 0.05D,
                        0.22F, 0.16F, 0.27F);
            }
            world.spawnParticle(EnumParticleTypes.SPELL_WITCH, posX, posY, posZ, 0.7D, 0.15D, 0.95D);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
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
            VisualEffectPacket.sendAround(world, posX, posY, posZ,
                    VisualEffectPacket.at(VisualEffectPacket.NECROMANCER_BURST, posX, posY, posZ,
                            0.7F, 0.2F, 0.95F));
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_WITCH, posX, posY, posZ,
                        24, 0.25D, 0.25D, 0.25D, 0.04D);
            }
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
