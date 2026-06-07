package elucent.eidolon.tile;

import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.reagent.ReagentTank;
import elucent.eidolon.reagent.ReagentRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CisternTileEntity extends ReagentTankTileEntity {
    private static final int CAPACITY = 2048;

    private final ReagentTank localTank = new ReagentTank(CAPACITY);
    private float upPressure;
    private float downPressure;

    private static final class CisternTank extends ReagentTank {
        private final CisternTileEntity tile;

        private CisternTank(CisternTileEntity tile) {
            super(CAPACITY);
            this.tile = tile;
        }

        @Override
        public ReagentStack getContents() {
            ReagentStack aggregate = new ReagentStack(getStoredReagent(), 0);
            for (CisternTileEntity cistern : tile.getColumnTiles()) {
                if (!cistern.localTank.isEmpty()) {
                    aggregate.reagent = cistern.localTank.getContents().reagent;
                    aggregate.amount += cistern.localTank.getContents().amount;
                }
            }
            return aggregate;
        }

        @Override
        public int getCapacity() {
            return tile.getColumnTiles().size() * CAPACITY;
        }

        @Override
        public int getRemainingCapacity() {
            return Math.max(0, getCapacity() - getContents().amount);
        }

        @Override
        public float getPressure() {
            return getCapacity() <= 0 ? 0.0F : (float) getContents().amount / (float) getCapacity();
        }

        @Override
        public boolean isEmpty() {
            return getContents().isEmpty();
        }

        @Override
        public boolean canFill(ReagentStack stack) {
            if (stack == null || stack.isEmpty()) {
                return false;
            }
            for (CisternTileEntity cistern : tile.getColumnTiles()) {
                if (!cistern.localTank.isEmpty() && cistern.localTank.getContents().reagent != stack.reagent) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int fill(net.minecraft.world.World world, BlockPos pos, ReagentStack stack) {
            if (!canFill(stack)) {
                return 0;
            }
            int accepted = Math.min(stack.amount, getRemainingCapacity());
            if (accepted <= 0) {
                return 0;
            }
            int newAmount = getContents().amount + accepted;
            tile.setColumnContents(new ReagentStack(stack.reagent, newAmount), true);
            if (world != null && newAmount > getCapacity() * 1.25F) {
                stack.reagent.worldEffect(world, pos, newAmount);
                world.destroyBlock(pos, false);
            }
            return accepted;
        }

        @Override
        public ReagentStack drain(int amount) {
            ReagentStack contents = getContents();
            if (amount <= 0 || contents.isEmpty()) {
                return new ReagentStack(contents.reagent, 0);
            }
            int drained = Math.min(amount, contents.amount);
            tile.setColumnContents(new ReagentStack(contents.reagent, contents.amount - drained), true);
            return new ReagentStack(contents.reagent, drained);
        }

        @Override
        public void clear() {
            tile.setColumnContents(new ReagentStack(ReagentRegistry.STEAM, 0), true);
        }

        @Override
        public NBTTagCompound writeToNBT() {
            return tile.localTank.writeToNBT();
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            tile.localTank.readFromNBT(compound);
        }

        private elucent.eidolon.reagent.Reagent getStoredReagent() {
            for (CisternTileEntity cistern : tile.getColumnTiles()) {
                if (!cistern.localTank.isEmpty()) {
                    return cistern.localTank.getContents().reagent;
                }
            }
            return elucent.eidolon.reagent.ReagentRegistry.STEAM;
        }
    }

    public CisternTileEntity() {
        super(CAPACITY);
        tank = new CisternTank(this);
    }

    public float getUpPressure() {
        return upPressure;
    }

    public float getDownPressure() {
        return downPressure;
    }

    public ReagentTank getLocalTank() {
        return localTank;
    }

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        upPressure = getAdjacentPressure(EnumFacing.UP);
        downPressure = getAdjacentPressure(EnumFacing.DOWN);
        if (world.isRemote) {
            return;
        }
        if (getBottomCistern() == this) {
            normalizeColumn();
        }
        super.update();
    }

    private float getAdjacentPressure(EnumFacing facing) {
        TileEntity tile = world.getTileEntity(pos.offset(facing));
        return tile instanceof CisternTileEntity ? ((CisternTileEntity) tile).localTank.getPressure() : 0.0F;
    }

    private void normalizeColumn() {
        ReagentStack contents = getTank().getContents();
        setColumnContents(contents, true);
    }

    private void setColumnContents(ReagentStack contents, boolean notify) {
        List<CisternTileEntity> column = getColumnTiles();
        ReagentStack[] previous = new ReagentStack[column.size()];
        for (int i = 0; i < column.size(); i++) {
            previous[i] = column.get(i).localTank.getContents().copy();
        }
        for (CisternTileEntity cistern : column) {
            cistern.localTank.clear();
        }
        if (!contents.isEmpty()) {
            int remaining = contents.amount;
            for (CisternTileEntity cistern : column) {
                int change = Math.min(remaining, CAPACITY);
                if (change > 0) {
                    cistern.localTank.fill(null, cistern.getPos(), new ReagentStack(contents.reagent, change));
                    remaining -= change;
                }
            }
        }
        if (notify) {
            notifyColumnChanged(column, previous);
        }
    }

    private void notifyColumnChanged(List<CisternTileEntity> column, ReagentStack[] previous) {
        for (int i = 0; i < column.size(); i++) {
            CisternTileEntity cistern = column.get(i);
            ReagentStack current = cistern.localTank.getContents();
            ReagentStack old = previous[i];
            if (old.amount != current.amount || old.reagent != current.reagent) {
                cistern.markDirty();
                if (cistern.world != null && !cistern.world.isRemote) {
                    cistern.world.notifyBlockUpdate(cistern.pos,
                            cistern.world.getBlockState(cistern.pos), cistern.world.getBlockState(cistern.pos), 3);
                }
            }
        }
    }

    private CisternTileEntity getBottomCistern() {
        CisternTileEntity current = this;
        while (current.world != null) {
            TileEntity tile = current.world.getTileEntity(current.pos.down());
            if (!(tile instanceof CisternTileEntity)) {
                break;
            }
            current = (CisternTileEntity) tile;
        }
        return current;
    }

    private List<CisternTileEntity> getColumnTiles() {
        List<CisternTileEntity> column = new ArrayList<>();
        CisternTileEntity current = getBottomCistern();
        while (current != null) {
            column.add(current);
            TileEntity above = current.world == null ? null : current.world.getTileEntity(current.pos.up());
            current = above instanceof CisternTileEntity ? (CisternTileEntity) above : null;
        }
        if (column.isEmpty()) {
            column.add(this);
        }
        return column;
    }

    @Override
    public boolean isOutput(EnumFacing direction) {
        TileEntity tile = world == null ? null : world.getTileEntity(pos.offset(direction));
        return tile instanceof GlassTubeTileEntity;
    }

    @Override
    public boolean isInput(EnumFacing direction) {
        return true;
    }

    @Override
    public void onContentsChanged() {
        if (world == null || world.isRemote) {
            super.onContentsChanged();
            return;
        }
        for (CisternTileEntity cistern : getColumnTiles()) {
            cistern.markDirty();
            cistern.world.notifyBlockUpdate(cistern.pos,
                    cistern.world.getBlockState(cistern.pos), cistern.world.getBlockState(cistern.pos), 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setFloat("UpPressure", upPressure);
        compound.setFloat("DownPressure", downPressure);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        upPressure = compound.getFloat("UpPressure");
        downPressure = compound.getFloat("DownPressure");
    }
}
