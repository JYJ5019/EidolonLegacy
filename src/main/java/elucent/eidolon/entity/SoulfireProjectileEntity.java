package elucent.eidolon.entity;

import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.registries.ModSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SoulfireProjectileEntity extends EntityThrowable {
    public SoulfireProjectileEntity(World worldIn) {
        super(worldIn);
        setSize(0.4F, 0.4F);
    }

    public SoulfireProjectileEntity(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        setSize(0.4F, 0.4F);
    }

    public SoulfireProjectileEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setSize(0.4F, 0.4F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i < 6; i++) {
                double t = i / 6.0D;
                EidolonParticles.spawnFlame(world,
                        posX - motionX * t,
                        posY - motionY * t,
                        posZ - motionZ * t,
                        (rand.nextDouble() - 0.5D) * 0.02D,
                        (rand.nextDouble() - 0.5D) * 0.02D,
                        (rand.nextDouble() - 0.5D) * 0.02D,
                        1.0F, 0.45F, 0.85F);
                EidolonParticles.spawnWisp(world,
                        posX - motionX * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        posY - motionY * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        posZ - motionZ * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        0.0D, 0.0D, 0.0D,
                        1.0F, 0.85F, 0.35F);
                world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX - motionX * t,
                        posY - motionY * t,
                        posZ - motionZ * t,
                        1.0D, 0.45D, 0.85D);
                world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX - motionX * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        posY - motionY * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        posZ - motionZ * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        1.0D, 0.85D, 0.35D);
                world.spawnParticle(EnumParticleTypes.FLAME,
                        posX - motionX * t,
                        posY - motionY * t,
                        posZ - motionZ * t,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit != null && result.entityHit != getThrower()) {
                result.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, getThrower()), 7.0F);
            }
            VisualEffectPacket.sendAround(world, posX, posY, posZ,
                    VisualEffectPacket.at(VisualEffectPacket.SOULFIRE_IMPACT, posX, posY, posZ,
                            1.0F, 0.45F, 0.85F));
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX, posY, posZ, 36, 0.35D, 0.35D, 0.35D, 1.0D);
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX, posY, posZ, 24, 0.35D, 0.35D, 0.35D, 0.65D);
                ((WorldServer) world).spawnParticle(EnumParticleTypes.FLAME,
                        posX, posY, posZ, 22, 0.25D, 0.25D, 0.25D, 0.04D);
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                        posX, posY, posZ, 10, 0.2D, 0.2D, 0.2D, 0.02D);
            }
            world.playSound(null, posX, posY, posZ,
                    ModSounds.SPLASH_SOULFIRE, SoundCategory.PLAYERS, 0.6F, rand.nextFloat() * 0.2F + 0.9F);
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
