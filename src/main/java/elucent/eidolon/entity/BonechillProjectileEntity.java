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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
            double speed = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            double fx = speed > 0.0D ? motionX / speed : 0.0D;
            double fy = speed > 0.0D ? motionY / speed : 0.0D;
            double fz = speed > 0.0D ? motionZ / speed : 1.0D;
            double vx = speed > 0.0D ? -motionX / speed * 0.025D : 0.0D;
            double vy = speed > 0.0D ? -motionY / speed * 0.025D : 0.0D;
            double vz = speed > 0.0D ? -motionZ / speed * 0.025D : 0.0D;
            double sideX = -fz;
            double sideY = 0.0D;
            double sideZ = fx;
            double sideLength = Math.sqrt(sideX * sideX + sideZ * sideZ);
            if (sideLength < 0.0001D) {
                sideX = 1.0D;
                sideZ = 0.0D;
            } else {
                sideX /= sideLength;
                sideZ /= sideLength;
            }
            double upX = sideY * fz - sideZ * fy;
            double upY = sideZ * fx - sideX * fz;
            double upZ = sideX * fy - sideY * fx;
            for (int i = 0; i < 8; i++) {
                double t = i / 8.0D;
                double x = prevPosX + (posX - prevPosX) * t;
                double y = prevPosY + (posY - prevPosY) * t;
                double z = prevPosZ + (posZ - prevPosZ) * t;
                double swirl = ticksExisted * 0.54D + i * 1.22D;
                double radius = 0.145D + 0.05D * Math.cos(ticksExisted * 0.32D + i);
                double offsetX = (Math.cos(swirl) * sideX + Math.sin(swirl) * upX) * radius;
                double offsetY = (Math.cos(swirl) * sideY + Math.sin(swirl) * upY) * radius;
                double offsetZ = (Math.cos(swirl) * sideZ + Math.sin(swirl) * upZ) * radius;
                EidolonParticles.create(EidolonParticles.WISP)
                        .addVelocity(vx, vy, vz)
                        .alpha(0.7F, 0.0F)
                        .scale(0.72F, 0.06F)
                        .color(0.9F, 1.0F, 1.0F, 0.48F, 0.62F, 0.95F)
                        .lifetime(8)
                        .spawn(world, x, y, z);
                EidolonParticles.create(EidolonParticles.SPARKLE)
                        .addVelocity(vx * 0.7D, vy * 0.7D, vz * 0.7D)
                        .alpha(0.95F, 0.0F)
                        .scale(0.24F, 0.0F)
                        .color(0.82F, 0.98F, 1.0F, 1.0F, 0.88F, 1.0F)
                        .lifetime(7)
                        .spin(0.18F)
                        .spawn(world, x, y, z);
                EidolonParticles.create(EidolonParticles.WISP)
                        .addVelocity(vx * 0.7D, vy * 0.7D, vz * 0.7D)
                        .alpha(0.72F, 0.0F)
                        .scale(0.5F, 0.08F)
                        .color(0.95F, 1.0F, 1.0F, 0.54F, 0.58F, 0.95F)
                        .lifetime(16)
                        .spawn(world, x + offsetX, y + offsetY, z + offsetZ);
                if ((ticksExisted + i) % 2 == 0) {
                    EidolonParticles.create(EidolonParticles.WISP)
                            .addVelocity(vx * 0.65D, vy * 0.65D, vz * 0.65D)
                            .alpha(0.55F, 0.0F)
                            .scale(0.42F, 0.06F)
                            .color(1.0F, 0.9F, 1.0F, 0.44F, 0.58F, 0.92F)
                            .lifetime(14)
                            .spawn(world, x - offsetX * 0.75D, y - offsetY * 0.75D, z - offsetZ * 0.75D);
                    EidolonParticles.create(EidolonParticles.SPARKLE)
                            .addVelocity(vx * 0.45D + offsetX * 0.032D,
                                    vy * 0.45D + offsetY * 0.032D,
                                    vz * 0.45D + offsetZ * 0.032D)
                            .alpha(1.0F, 0.0F)
                            .scale(0.24F, 0.0F)
                            .color(0.76F, 0.96F, 1.0F, 1.0F, 0.88F, 1.0F)
                            .lifetime(9)
                            .spin(-0.2F)
                            .spawn(world, x + offsetX * 1.35D, y + offsetY * 1.35D, z + offsetZ * 1.35D);
                }
                if ((ticksExisted + i) % 3 == 0) {
                    EidolonParticles.create(EidolonParticles.SPARKLE)
                            .addVelocity(vx * 0.5D, vy * 0.5D, vz * 0.5D)
                            .alpha(1.0F, 0.0F)
                            .scale(0.32F, 0.0F)
                            .color(0.75F, 0.95F, 1.0F, 0.68F, 0.62F, 0.98F)
                            .lifetime(9)
                            .spin(0.22F)
                            .spawn(world, x + offsetX * 0.55D, y + offsetY * 0.55D, z + offsetZ * 0.55D);
                }
                if ((ticksExisted + i) % 4 == 0) {
                    EidolonParticles.create(EidolonParticles.RING)
                            .addVelocity(vx * 0.35D, vy * 0.35D, vz * 0.35D)
                            .alpha(0.55F, 0.0F)
                            .scale(0.48F, 0.12F)
                            .color(0.78F, 0.98F, 1.0F, 0.62F, 0.62F, 1.0F)
                            .lifetime(8)
                            .spin(-0.18F)
                            .spawn(world, x, y, z);
                }
            }
            if (ticksExisted % 4 == 0) {
                EidolonParticles.create(EidolonParticles.BURST)
                        .addVelocity(vx * 0.25D, vy * 0.25D, vz * 0.25D)
                        .alpha(0.68F, 0.0F)
                        .scale(0.52F, 0.08F)
                        .color(0.82F, 0.98F, 1.0F, 0.72F, 0.62F, 1.0F)
                        .lifetime(7)
                        .spin(0.26F)
                        .spawn(world, posX, posY, posZ);
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
            Vec3d hit = result.hitVec != null ? result.hitVec : getPositionVector();
            VisualEffectPacket.sendAround(world, hit.x, hit.y, hit.z,
                    VisualEffectPacket.at(VisualEffectPacket.BONECHILL_IMPACT, hit.x, hit.y, hit.z,
                            192.0F / 255.0F, 224.0F / 255.0F, 1.0F,
                            96.0F / 255.0F, 128.0F / 255.0F, 192.0F / 255.0F));
            world.playSound(null, hit.x, hit.y, hit.z,
                    ModSounds.SPLASH_BONECHILL, SoundCategory.PLAYERS, 0.5F, rand.nextFloat() * 0.2F + 0.9F);
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F;
    }
}
