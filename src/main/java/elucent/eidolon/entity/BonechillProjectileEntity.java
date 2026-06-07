package elucent.eidolon.entity;

import elucent.eidolon.Eidolon;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.registries.ModPotions;
import elucent.eidolon.registries.ModSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BonechillProjectileEntity extends EntityThrowable {
    public BonechillProjectileEntity(World worldIn) {
        super(worldIn);
        setSize(0.4F, 0.4F);
    }

    public BonechillProjectileEntity(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
        setSize(0.4F, 0.4F);
    }

    public BonechillProjectileEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        setSize(0.4F, 0.4F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            for (int i = 0; i < 6; i++) {
                double t = i / 6.0D;
                EidolonParticles.spawnWisp(world,
                        posX - motionX * t,
                        posY - motionY * t,
                        posZ - motionZ * t,
                        (rand.nextDouble() - 0.5D) * 0.02D,
                        (rand.nextDouble() - 0.5D) * 0.02D,
                        (rand.nextDouble() - 0.5D) * 0.02D,
                        0.55F, 0.85F, 1.0F);
                if (i % 2 == 0) {
                    EidolonParticles.spawnSparkle(world,
                            posX - motionX * t + (rand.nextDouble() - 0.5D) * 0.08D,
                            posY - motionY * t + (rand.nextDouble() - 0.5D) * 0.08D,
                            posZ - motionZ * t + (rand.nextDouble() - 0.5D) * 0.08D,
                            0.0D, 0.0D, 0.0D,
                            0.85F, 1.0F, 1.0F);
                }
                world.spawnParticle(EnumParticleTypes.SNOWBALL,
                        posX - motionX * t,
                        posY - motionY * t,
                        posZ - motionZ * t,
                        0.0D, 0.0D, 0.0D);
                world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX - motionX * t,
                        posY - motionY * t,
                        posZ - motionZ * t,
                        0.55D, 0.8D, 1.0D);
                world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX - motionX * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        posY - motionY * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        posZ - motionZ * t + (rand.nextDouble() - 0.5D) * 0.08D,
                        0.9D, 1.0D, 1.0D);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit instanceof EntityLivingBase && result.entityHit != getThrower()) {
                EntityLivingBase target = (EntityLivingBase) result.entityHit;
                target.attackEntityFrom(new EntityDamageSourceIndirect(Eidolon.FROST_DAMAGE.getDamageType(), this, getThrower()), 4.0F);
                target.addPotionEffect(new PotionEffect(ModPotions.CHILLED, 20 * 15, 0));
            }
            VisualEffectPacket.sendAround(world, posX, posY, posZ,
                    VisualEffectPacket.at(VisualEffectPacket.BONECHILL_IMPACT, posX, posY, posZ,
                            0.55F, 0.85F, 1.0F));
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SNOWBALL,
                        posX, posY, posZ, 38, 0.4D, 0.4D, 0.4D, 0.06D);
                ((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_MOB,
                        posX, posY, posZ, 28, 0.35D, 0.35D, 0.35D, 0.8D);
                ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD,
                        posX, posY, posZ, 12, 0.25D, 0.25D, 0.25D, 0.02D);
            }
            world.playSound(null, posX, posY, posZ,
                    ModSounds.SPLASH_BONECHILL, SoundCategory.PLAYERS, 0.5F, rand.nextFloat() * 0.2F + 0.9F);
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
