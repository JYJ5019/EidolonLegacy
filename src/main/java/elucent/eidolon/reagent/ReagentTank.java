package elucent.eidolon.reagent;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ReagentTank {
    private int capacity;
    private ReagentStack contents;

    public ReagentTank(int capacity) {
        this.capacity = capacity;
        this.contents = new ReagentStack(ReagentRegistry.STEAM, 0);
    }

    public ReagentStack getContents() {
        return contents;
    }

    public int getCapacity() {
        return capacity;
    }

    public float getPressure() {
        return capacity <= 0 ? 0.0F : (float) contents.amount / (float) capacity;
    }

    public int getRemainingCapacity() {
        return Math.max(0, capacity - (contents == null ? 0 : contents.amount));
    }

    public boolean isEmpty() {
        return contents == null || contents.isEmpty();
    }

    public boolean canFill(ReagentStack stack) {
        return stack != null && !stack.isEmpty()
                && getRemainingCapacity() > 0
                && (isEmpty() || contents.reagent == stack.reagent);
    }

    public int fill(World world, BlockPos pos, ReagentStack stack) {
        if (!canFill(stack)) {
            return 0;
        }
        if (isEmpty()) {
            contents.reagent = stack.reagent;
        }
        int accepted = Math.min(stack.amount, getRemainingCapacity());
        contents.amount += accepted;
        if (world != null && contents.amount > capacity * 1.25F) {
            contents.reagent.worldEffect(world, pos, contents.amount);
            world.destroyBlock(pos, false);
        }
        return accepted;
    }

    public ReagentStack drain(int amount) {
        if (amount <= 0 || isEmpty()) {
            return new ReagentStack(contents == null ? ReagentRegistry.STEAM : contents.reagent, 0);
        }
        int drained = Math.min(amount, contents.amount);
        ReagentStack stack = new ReagentStack(contents.reagent, drained);
        contents.amount -= drained;
        if (contents.amount <= 0) {
            contents.amount = 0;
            contents.reagent = ReagentRegistry.STEAM;
        }
        return stack;
    }

    public void clear() {
        contents.amount = 0;
        contents.reagent = ReagentRegistry.STEAM;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("Capacity", capacity);
        if (contents != null && !contents.isEmpty()) {
            compound.setTag("Contents", contents.writeToNBT());
        }
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        capacity = compound.hasKey("Capacity", Constants.NBT.TAG_INT) ? compound.getInteger("Capacity") : capacity;
        contents = compound.hasKey("Contents", Constants.NBT.TAG_COMPOUND)
                ? new ReagentStack(compound.getCompoundTag("Contents"))
                : new ReagentStack(ReagentRegistry.STEAM, 0);
    }
}
