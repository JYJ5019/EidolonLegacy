package elucent.eidolon.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;

public class GlassTubeTileEntity extends ReagentTankTileEntity {
    private static final int CAPACITY = 128;

    private EnumFacing input = EnumFacing.DOWN;
    private EnumFacing output = EnumFacing.UP;

    public GlassTubeTileEntity() {
        super(CAPACITY);
    }

    public EnumFacing getInput() {
        return input;
    }

    public EnumFacing getOutput() {
        return output;
    }

    public boolean setDirections(EnumFacing input, EnumFacing output) {
        if (input == output) {
            return false;
        }
        this.input = input;
        this.output = output;
        notifyStateChanged();
        return true;
    }

    public boolean setOutput(EnumFacing output) {
        if (output == input) {
            return false;
        }
        this.output = output;
        notifyStateChanged();
        return true;
    }

    private void notifyStateChanged() {
        onContentsChanged();
    }

    @Override
    public boolean isOutput(EnumFacing direction) {
        return direction == output;
    }

    @Override
    public boolean isInput(EnumFacing direction) {
        return direction == input;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Input", input.getIndex());
        compound.setInteger("Output", output.getIndex());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        input = EnumFacing.byIndex(compound.getInteger("Input"));
        output = EnumFacing.byIndex(compound.getInteger("Output"));
        if (input == output) {
            output = input.getOpposite();
        }
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
