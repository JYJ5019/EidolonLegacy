package elucent.eidolon.tile;

import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.spell.IRitualItemProvider;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;

public class ItemHolderTileEntity extends TileEntity implements IRitualItemProvider, ITickable {
    private static final int CONSUME_EFFECT_TICKS = 32;

    private ItemStack stack = ItemStack.EMPTY;
    private ItemStack consumedStack = ItemStack.EMPTY;
    private int consumeEffectTicks;

    public boolean hasStack() {
        return !stack.isEmpty();
    }

    public ItemStack getStack() {
        return stack.copy();
    }

    public ItemStack getRenderStack() {
        return stack.isEmpty() ? consumedStack.copy() : stack.copy();
    }

    public float getConsumeEffectProgress(float partialTicks) {
        if (consumeEffectTicks <= 0) {
            return stack.isEmpty() && !consumedStack.isEmpty() ? 1.0F : 0.0F;
        }
        return Math.min(1.0F, (CONSUME_EFFECT_TICKS - consumeEffectTicks + partialTicks) / (float) CONSUME_EFFECT_TICKS);
    }

    public boolean addStack(ItemStack input) {
        if (input.isEmpty() || hasStack()) {
            return false;
        }
        stack = input.splitStack(1);
        markDirty();
        notifyStateChanged();
        return true;
    }

    public ItemStack removeStack() {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = stack;
        stack = ItemStack.EMPTY;
        markDirty();
        notifyStateChanged();
        return removed;
    }

    public boolean activate(EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty() && hasStack()) {
            ItemStack removed = removeStack();
            if (!removed.isEmpty() && !player.addItemStackToInventory(removed) && world != null) {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, removed));
            }
            return true;
        }
        return !held.isEmpty() && addStack(held);
    }

    public void dropContents() {
        if (world != null && !world.isRemote && !stack.isEmpty()) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
            stack = ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStack provide() {
        return getStack();
    }

    @Override
    public void take() {
        ItemStack removed = removeStack();
        if (!removed.isEmpty()) {
            consumedStack = removed.copy();
            consumeEffectTicks = CONSUME_EFFECT_TICKS;
            notifyStateChanged();
        }
    }

    public void playFocusEffect() {
        consumeEffectTicks = CONSUME_EFFECT_TICKS;
        notifyStateChanged();
    }

    @Override
    public void update() {
        if (world == null || consumeEffectTicks <= 0) {
            return;
        }
        consumeEffectTicks--;
        if (world.isRemote) {
            spawnConsumeParticles();
        } else if (consumeEffectTicks == 0) {
            consumedStack = ItemStack.EMPTY;
            notifyStateChanged();
        }
    }

    private void spawnConsumeParticles() {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.05D;
        double z = pos.getZ() + 0.5D;
        float progress = consumeEffectTicks / (float) CONSUME_EFFECT_TICKS;
        float flash = 1.0F - progress;
        EidolonParticles.create(EidolonParticles.SMOKE)
                .alpha(0.18F * progress, 0.0F)
                .scale(0.36F, 0.12F)
                .lifetime(46)
                .randomOffset(0.24D, 0.14D)
                .randomVelocity(0.016D, 0.016D)
                .addVelocity(0.0D, 0.025D, 0.0D)
                .color(0.5F, 0.45F, 0.65F, 0.25F, 0.25F, 0.35F)
                .fullbright(false)
                .spawn(world, x, y, z);
        EidolonParticles.create(EidolonParticles.WISP)
                .alpha(0.42F + 0.28F * flash, 0.0F)
                .scale(0.18F + 0.08F * flash, 0.0F)
                .lifetime(22)
                .randomOffset(0.2D, 0.1D)
                .randomVelocity(0.035D, 0.02D)
                .addVelocity(0.0D, 0.026D, 0.0D)
                .color(0.7F, 0.28F, 1.0F, 0.34F, 0.12F, 0.64F)
                .spawn(world, x, y, z);
        if (consumeEffectTicks > CONSUME_EFFECT_TICKS - 6 || world.rand.nextInt(4) == 0) {
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.11F + 0.05F * flash, 0.0F)
                    .lifetime(24)
                    .randomOffset(0.24D, 0.12D)
                    .randomVelocity(0.06D, 0.04D)
                    .addVelocity(0.0D, 0.04D, 0.0D)
                    .color(0.92F, 0.68F, 1.0F, 0.58F, 0.18F, 0.9F)
                    .spin(0.35F)
                    .spawn(world, x, y + 0.08D, z);
        }
        if (consumeEffectTicks == CONSUME_EFFECT_TICKS - 1) {
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.85F, 0.0F)
                    .scale(0.34F, 0.06F)
                    .lifetime(18)
                    .randomOffset(0.28D, 0.16D)
                    .randomVelocity(0.075D, 0.055D)
                    .addVelocity(0.0D, 0.04D, 0.0D)
                    .color(0.82F, 0.36F, 1.0F, 0.42F, 0.14F, 0.76F)
                    .repeat(world, x, y + 0.04D, z, 8);
            EidolonParticles.create(EidolonParticles.RING)
                    .alpha(0.62F, 0.0F)
                    .scale(0.55F, 0.08F)
                    .lifetime(16)
                    .randomVelocity(0.006D, 0.004D)
                    .color(0.74F, 0.28F, 1.0F, 0.42F, 0.12F, 0.72F)
                    .spin(0.08F)
                    .spawn(world, x, y + 0.02D, z);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!stack.isEmpty()) {
            compound.setTag("Stack", stack.writeToNBT(new NBTTagCompound()));
        }
        if (!consumedStack.isEmpty()) {
            compound.setTag("ConsumedStack", consumedStack.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("ConsumeEffectTicks", consumeEffectTicks);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        stack = compound.hasKey("Stack", Constants.NBT.TAG_COMPOUND)
                ? new ItemStack(compound.getCompoundTag("Stack")) : ItemStack.EMPTY;
        consumedStack = compound.hasKey("ConsumedStack", Constants.NBT.TAG_COMPOUND)
                ? new ItemStack(compound.getCompoundTag("ConsumedStack")) : ItemStack.EMPTY;
        consumeEffectTicks = compound.getInteger("ConsumeEffectTicks");
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

    protected void notifyStateChanged() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }
}
