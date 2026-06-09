package elucent.eidolon.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class AngelArrowEntity extends EntityTippedArrow {
    private static final double SEEK_RANGE = 12.0D;
    private static final double STEER_WEIGHT = 0.5D;
    private ItemStack pickupStack = ItemStack.EMPTY;
    private boolean spectral;

    public AngelArrowEntity(World worldIn) {
        super(worldIn);
    }

    public AngelArrowEntity(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    public void configureFromAmmo(ItemStack ammo) {
        if (ammo.isEmpty()) {
            pickupStack = new ItemStack(Items.ARROW);
            spectral = false;
            return;
        }
        pickupStack = ammo.copy();
        pickupStack.setCount(1);
        spectral = pickupStack.getItem() == Items.SPECTRAL_ARROW;
        if (pickupStack.getItem() == Items.TIPPED_ARROW) {
            setPotionEffect(pickupStack);
        }
    }

    public void configureFromArrow(EntityArrow arrow, ItemStack ammo) {
        if (arrow != null) {
            NBTTagCompound arrowData = new NBTTagCompound();
            arrow.writeEntityToNBT(arrowData);
            readEntityFromNBT(arrowData);
            setDamage(arrow.getDamage());
            setIsCritical(arrow.getIsCritical());
            pickupStatus = arrow.pickupStatus;
        }
        configureFromAmmo(ammo);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!inGround) {
            steerTowardTarget();
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return pickupStack.isEmpty() ? super.getArrowStack() : pickupStack.copy();
    }

    @Override
    protected void arrowHit(EntityLivingBase living) {
        super.arrowHit(living);
        if (spectral) {
            living.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200, 0));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (!pickupStack.isEmpty()) {
            compound.setTag("EidolonPickupStack", pickupStack.writeToNBT(new NBTTagCompound()));
        }
        compound.setBoolean("EidolonSpectral", spectral);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("EidolonPickupStack")) {
            pickupStack = new ItemStack(compound.getCompoundTag("EidolonPickupStack"));
        }
        spectral = compound.getBoolean("EidolonSpectral");
    }

    private void steerTowardTarget() {
        Vec3d motion = new Vec3d(motionX, motionY, motionZ);
        double speed = motion.length();
        if (speed <= 0.001D) {
            return;
        }
        EntityLivingBase target = findTarget();
        if (target == null) {
            return;
        }
        Vec3d targetPos = new Vec3d(target.posX, target.posY + target.height * 0.5D, target.posZ);
        Vec3d desired = targetPos.subtract(posX, posY, posZ);
        if (desired.length() <= 0.001D) {
            return;
        }
        Vec3d steered = motion.add(desired.normalize().scale(speed)).scale(STEER_WEIGHT);
        double steeredLength = steered.length();
        if (steeredLength <= 0.001D) {
            return;
        }
        Vec3d result = steered.scale(speed / steeredLength);
        motionX = result.x;
        motionY = result.y;
        motionZ = result.z;
        velocityChanged = true;
    }

    private EntityLivingBase findTarget() {
        AxisAlignedBB area = new AxisAlignedBB(posX - SEEK_RANGE, posY - SEEK_RANGE, posZ - SEEK_RANGE,
                posX + SEEK_RANGE, posY + SEEK_RANGE, posZ + SEEK_RANGE);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, area,
                entity -> entity != null && entity.isEntityAlive() && isValidTarget(entity));
        if (entities.isEmpty()) {
            return null;
        }
        return entities.stream().min(Comparator.comparingDouble(entity -> entity.getDistanceSq(this))).orElse(null);
    }

    private boolean isValidTarget(EntityLivingBase entity) {
        Entity shooter = shootingEntity;
        return entity != shooter;
    }

}
