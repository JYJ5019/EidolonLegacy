package elucent.eidolon.tile;

import elucent.eidolon.registries.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class SoulEnchanterTileEntity extends MachineInventoryTileEntity implements ITickable {
    public static final int SLOT_ITEM = 0;
    public static final int SLOT_SHARD = 1;

    private static final Random RANDOM = new Random();

    public int tickCount;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float nextPageAngle;
    public float pageAngle;
    public float tRot;

    public SoulEnchanterTileEntity() {
        super("container.eidolon.soul_enchanter", 2);
    }

    @Override
    public void update() {
        if (world == null) {
            return;
        }

        tickCount++;
        pageTurningSpeed = nextPageTurningSpeed;
        pageAngle = nextPageAngle;
        EntityPlayer player = world.getClosestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3.0D, false);
        if (player != null) {
            double dx = player.posX - (pos.getX() + 0.5D);
            double dz = player.posZ - (pos.getZ() + 0.5D);
            tRot = (float) MathHelper.atan2(dz, dx);
            nextPageTurningSpeed += 0.1F;
            if (nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float previousFlipTarget = flipT;
                do {
                    flipT += RANDOM.nextInt(4) - RANDOM.nextInt(4);
                } while (previousFlipTarget == flipT);
            }
        } else {
            tRot += 0.02F;
            nextPageTurningSpeed -= 0.1F;
        }

        while (nextPageAngle >= (float) Math.PI) {
            nextPageAngle -= (float) Math.PI * 2.0F;
        }
        while (nextPageAngle < -(float) Math.PI) {
            nextPageAngle += (float) Math.PI * 2.0F;
        }
        while (tRot >= (float) Math.PI) {
            tRot -= (float) Math.PI * 2.0F;
        }
        while (tRot < -(float) Math.PI) {
            tRot += (float) Math.PI * 2.0F;
        }

        float rotationDelta;
        for (rotationDelta = tRot - nextPageAngle; rotationDelta >= (float) Math.PI; rotationDelta -= (float) Math.PI * 2.0F) {
        }
        while (rotationDelta < -(float) Math.PI) {
            rotationDelta += (float) Math.PI * 2.0F;
        }

        nextPageAngle += rotationDelta * 0.4F;
        nextPageTurningSpeed = MathHelper.clamp(nextPageTurningSpeed, 0.0F, 1.0F);
        oFlip = flip;
        float flipDelta = (flipT - flip) * 0.4F;
        flipDelta = MathHelper.clamp(flipDelta, -0.2F, 0.2F);
        flipA += (flipDelta - flipA) * 0.9F;
        flip += flipA;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == SLOT_ITEM) {
            return isEnchantableInput(stack);
        }
        return index == SLOT_SHARD && stack.getItem() == ModItems.SOUL_SHARD;
    }

    public static boolean isEnchantableInput(ItemStack stack) {
        return !stack.isEmpty()
                && (stack.getItem() == Items.BOOK
                || stack.getItem() == Items.ENCHANTED_BOOK
                || stack.isItemEnchantable()
                || stack.isItemEnchanted());
    }
}
