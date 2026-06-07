package elucent.eidolon.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericBarterGoal<E extends EntityCreature> extends EntityAIBase {
    private static final Random RANDOM = new Random();
    private final Predicate<ItemStack> valid;
    private final Function<ItemStack, ItemStack> result;
    private final E entity;
    private int progress;
    private int cooldown;
    private int lastSearchTick;
    private ItemStack heldBackup = ItemStack.EMPTY;

    public GenericBarterGoal(E entity, Predicate<ItemStack> valid, Function<ItemStack, ItemStack> result) {
        this.entity = entity;
        this.valid = valid;
        this.result = result;
        setMutexBits(3);
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean shouldExecute() {
        if (--cooldown > 0) {
            return false;
        }
        if (progress > 0 || entity.ticksExisted < lastSearchTick + 20) {
            return false;
        }
        lastSearchTick = entity.ticksExisted;
        return findNearestItem() != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return progress > 0 || findNearestItem() != null;
    }

    @Override
    public void updateTask() {
        if (cooldown > 0) {
            return;
        }
        if (progress > 0 && !heldBackup.isEmpty()) {
            entity.setHeldItem(EnumHand.MAIN_HAND, heldBackup);
        }

        entity.setAttackTarget(null);
        if (progress > 0) {
            progress--;
            entity.getNavigator().clearPath();
            if (progress == 0) {
                if (!entity.world.isRemote) {
                    ItemStack output = result.apply(entity.getHeldItemMainhand().copy());
                    if (!output.isEmpty()) {
                        entity.world.spawnEntity(new EntityItem(entity.world, entity.posX, entity.posY + 0.1D, entity.posZ, output));
                    }
                }
                entity.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                heldBackup = ItemStack.EMPTY;
                cooldown = 600;
            }
            return;
        }

        EntityItem nearest = findNearestItem();
        if (nearest == null) {
            return;
        }
        if (entity.getDistanceSq(nearest.posX, nearest.posY, nearest.posZ) < 2.25D) {
            progress = 100;
            heldBackup = nearest.getItem().copy();
            entity.setHeldItem(EnumHand.MAIN_HAND, heldBackup);
            nearest.setDead();
            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.HOSTILE, 0.2F, ((RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else {
            entity.getNavigator().tryMoveToXYZ(nearest.posX, nearest.posY, nearest.posZ, 1.0D);
        }
    }

    private EntityItem findNearestItem() {
        AxisAlignedBB bounds = entity.getEntityBoundingBox().grow(8.0D, 8.0D, 8.0D);
        List<EntityItem> items = entity.world.getEntitiesWithinAABB(EntityItem.class, bounds,
                item -> item != null && !item.isDead && valid.test(item.getItem()));
        return items.stream()
                .min(Comparator.comparingDouble(item -> entity.getDistanceSq(item.posX, item.posY, item.posZ)))
                .orElse(null);
    }
}
