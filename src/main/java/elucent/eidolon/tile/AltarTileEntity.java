package elucent.eidolon.tile;

import elucent.eidolon.reagent.IReagentTankProvider;
import elucent.eidolon.reagent.ReagentTank;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class AltarTileEntity extends TileEntity implements IReagentTankProvider {
    private static final int REAGENT_CAPACITY = 512;

    private ItemStack offering = ItemStack.EMPTY;
    private final ReagentTank tank = new ReagentTank(REAGENT_CAPACITY);

    public boolean hasOffering() {
        return !offering.isEmpty();
    }

    public ItemStack getOffering() {
        return offering.copy();
    }

    public boolean addOffering(ItemStack stack) {
        if (stack.isEmpty() || hasOffering()) {
            return false;
        }
        offering = stack.splitStack(1);
        markDirty();
        notifyStateChanged();
        return true;
    }

    public ItemStack removeOffering() {
        if (offering.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = offering;
        offering = ItemStack.EMPTY;
        markDirty();
        notifyStateChanged();
        return stack;
    }

    public void setOffering(ItemStack stack) {
        offering = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
        if (!offering.isEmpty()) {
            offering.setCount(1);
        }
        markDirty();
        notifyStateChanged();
    }

    private void notifyStateChanged() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public ReagentTank getTank() {
        return tank;
    }

    @Override
    public boolean isOutput(EnumFacing direction) {
        return false;
    }

    @Override
    public boolean isInput(EnumFacing direction) {
        return direction != EnumFacing.UP;
    }

    @Override
    public void onContentsChanged() {
        markDirty();
        notifyStateChanged();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!offering.isEmpty()) {
            compound.setTag("Offering", offering.writeToNBT(new NBTTagCompound()));
        }
        compound.setTag("ReagentTank", tank.writeToNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        offering = compound.hasKey("Offering", Constants.NBT.TAG_COMPOUND)
                ? new ItemStack(compound.getCompoundTag("Offering")) : ItemStack.EMPTY;
        if (compound.hasKey("ReagentTank", Constants.NBT.TAG_COMPOUND)) {
            tank.readFromNBT(compound.getCompoundTag("ReagentTank"));
        }
        notifyStateChanged();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        if (world != null && world.isRemote) {
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
    }
}
