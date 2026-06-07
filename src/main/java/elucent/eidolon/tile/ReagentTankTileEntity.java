package elucent.eidolon.tile;

import elucent.eidolon.reagent.IReagentTankProvider;
import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.reagent.ReagentTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import java.util.ArrayList;
import java.util.List;

public abstract class ReagentTankTileEntity extends TileEntity implements ITickable, IReagentTankProvider {
    protected ReagentTank tank;

    protected ReagentTankTileEntity(int capacity) {
        tank = new ReagentTank(capacity);
    }

    @Override
    public void update() {
        if (world == null || world.isRemote || tank.isEmpty()) {
            return;
        }

        List<IReagentTankProvider> destinations = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (!isOutput(facing)) {
                continue;
            }
            TileEntity tile = world.getTileEntity(pos.offset(facing));
            if (tile instanceof IReagentTankProvider) {
                IReagentTankProvider provider = (IReagentTankProvider) tile;
                if (provider.isInput(facing.getOpposite())
                        && provider.getTank().canFill(tank.getContents())
                        && (provider.getTank().getPressure() < tank.getPressure() || facing == EnumFacing.DOWN)) {
                    destinations.add(provider);
                }
            }
        }

        if (destinations.isEmpty()) {
            return;
        }

        float totalPressure = tank.getPressure();
        int count = 1;
        for (IReagentTankProvider provider : destinations) {
            totalPressure += provider.getTank().getPressure();
            count++;
        }
        float averagePressure = totalPressure / count;
        int shared = Math.max(0, tank.getContents().amount - (int) Math.ceil(tank.getCapacity() * averagePressure));

        float totalNeedPressure = 0.0F;
        for (IReagentTankProvider provider : destinations) {
            totalNeedPressure += Math.max(0.0F, averagePressure - provider.getTank().getPressure());
        }

        int[] toSend = new int[destinations.size()];
        int sum = 0;
        for (int i = 0; i < destinations.size(); i++) {
            IReagentTankProvider provider = destinations.get(i);
            float pressureDiff = averagePressure - provider.getTank().getPressure();
            int targetCapacity = provider.getTank().getCapacity();
            if (targetCapacity <= 0 || provider.getTank().getRemainingCapacity() <= 0) {
                continue;
            }
            float minimumDiff = 2.0F / targetCapacity;
            if (pressureDiff > 0.0F && pressureDiff < minimumDiff) {
                pressureDiff = minimumDiff;
            }

            int weightedShare = totalNeedPressure == 0.0F ? 0
                    : (int) (shared * ((averagePressure - provider.getTank().getPressure()) / totalNeedPressure));
            int needed = (int) (pressureDiff * targetCapacity);
            toSend[i] = Math.max(0, Math.min(64,
                    Math.min(provider.getTank().getRemainingCapacity(), Math.min(weightedShare, needed))));
            sum += toSend[i];
        }
        if (tank.getContents().amount - sum <= destinations.size() * 4) {
            for (int i = 0; i < tank.getContents().amount - sum; i++) {
                toSend[i % destinations.size()]++;
            }
        }

        boolean dirty = false;
        for (int i = 0; i < destinations.size(); i++) {
            IReagentTankProvider provider = destinations.get(i);
            int change = Math.min(tank.getContents().amount, toSend[i]);
            if (change <= 0) {
                continue;
            }

            ReagentStack sent = new ReagentStack(tank.getContents().reagent, change);
            int accepted = provider.getTank().fill(world, ((TileEntity) provider).getPos(), sent);
            if (accepted > 0) {
                tank.drain(accepted);
                provider.onContentsChanged();
                dirty = true;
            }
        }

        if (dirty) {
            onContentsChanged();
        }
    }

    @Override
    public ReagentTank getTank() {
        return tank;
    }

    @Override
    public void onContentsChanged() {
        markDirty();
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("ReagentTank", tank.writeToNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("ReagentTank")) {
            tank.readFromNBT(compound.getCompoundTag("ReagentTank"));
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
