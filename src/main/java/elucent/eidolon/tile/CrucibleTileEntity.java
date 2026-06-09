package elucent.eidolon.tile;

import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.reagent.IReagentTankProvider;
import elucent.eidolon.reagent.ReagentRegistry;
import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.reagent.ReagentTank;
import elucent.eidolon.recipes.CrucibleRecipe;
import elucent.eidolon.recipes.CrucibleRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrucibleTileEntity extends TileEntity implements IReagentTankProvider, ITickable {
    private final List<CrucibleRecipe.ProvidedStep> completedSteps = new ArrayList<>();
    private final NonNullList<ItemStack> currentContents = NonNullList.create();
    private final Random particleRandom = new Random();
    private final ReagentTank reagentTank = new ReagentTank(Fluid.BUCKET_VOLUME) {
        @Override
        public boolean canFill(ReagentStack stack) {
            return stack != null
                    && stack.reagent == ReagentRegistry.STEAM
                    && !hasFluid()
                    && super.canFill(stack);
        }
    };
    private int currentStirs;
    private int steamProgress;
    private ItemStack currentStirrer = ItemStack.EMPTY;
    private FluidStack fluid;
    private long lastStirTime = -100L;

    public boolean addOne(ItemStack stack) {
        if (stack.isEmpty() || !hasFluid()) {
            return false;
        }
        ItemStack stored = stack.splitStack(1);
        currentContents.add(stored);
        markDirty();
        notifyStateChanged();
        return true;
    }

    public void stir(ItemStack stirrer) {
        if (!hasFluid()) {
            return;
        }
        if (currentStirrer.isEmpty()) {
            currentStirrer = stirrer.copy();
            currentStirrer.setCount(1);
        }
        currentStirs++;
        if (world != null) {
            lastStirTime = world.getTotalWorldTime();
        }
        markDirty();
        notifyStateChanged();
    }

    public ItemStack commitStep() {
        if (!hasFluid() || (currentContents.isEmpty() && currentStirs == 0)) {
            return ItemStack.EMPTY;
        }
        completedSteps.add(new CrucibleRecipe.ProvidedStep(currentStirs, currentStirrer, copyStacks(currentContents)));
        currentContents.clear();
        currentStirs = 0;
        currentStirrer = ItemStack.EMPTY;
        notifyStateChanged();

        CrucibleRecipe recipe = CrucibleRecipes.find(completedSteps, fluid);
        if (recipe != null) {
            ItemStack result = recipe.getResult();
            reset();
            return result;
        }
        if (!CrucibleRecipes.matchesAnyPrefix(completedSteps, fluid)) {
            reset();
            return ItemStack.EMPTY;
        }
        markDirty();
        return ItemStack.EMPTY;
    }

    public boolean hasPendingStepAction() {
        return !currentContents.isEmpty() || currentStirs > 0;
    }

    public boolean fill(FluidStack stack) {
        if (stack == null || hasFluid() || !CrucibleRecipes.acceptsFluid(stack)) {
            return false;
        }
        fluid = stack.copy();
        reagentTank.clear();
        steamProgress = 0;
        markDirty();
        notifyStateChanged();
        return true;
    }

    public List<ItemStack> getDroppedStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (CrucibleRecipe.ProvidedStep step : completedSteps) {
            stacks.addAll(copyStacks(step.getContents()));
        }
        stacks.addAll(copyStacks(currentContents));
        return stacks;
    }

    public void reset() {
        completedSteps.clear();
        currentContents.clear();
        currentStirs = 0;
        currentStirrer = ItemStack.EMPTY;
        fluid = null;
        reagentTank.clear();
        steamProgress = 0;
        markDirty();
        notifyStateChanged();
    }

    public boolean hasContents() {
        return hasFluid() || !reagentTank.isEmpty() || steamProgress > 0
                || !completedSteps.isEmpty() || !currentContents.isEmpty()
                || currentStirs > 0;
    }

    public boolean hasFluid() {
        return fluid != null && fluid.amount > 0;
    }

    public boolean isBoiling() {
        if (!hasFluid() || fluid.getFluid() != FluidRegistry.WATER || world == null) {
            return false;
        }
        IBlockState state = world.getBlockState(pos.down());
        Block block = state.getBlock();
        return block == Blocks.FIRE
                || block == Blocks.LAVA
                || block == Blocks.FLOWING_LAVA
                || block == Blocks.MAGMA;
    }

    @Override
    public void update() {
        if (world == null) {
            return;
        }
        if (world.isRemote) {
            if (isBoiling()) {
                spawnBoilingParticles();
            }
        }
    }

    public FluidStack getFluid() {
        return fluid == null ? null : fluid.copy();
    }

    public List<ItemStack> getCurrentContents() {
        List<ItemStack> contents = new ArrayList<>();
        for (CrucibleRecipe.ProvidedStep step : completedSteps) {
            contents.addAll(copyStacks(step.getContents()));
        }
        contents.addAll(copyStacks(currentContents));
        return contents;
    }

    public int getCompletedStepCount() {
        return completedSteps.size();
    }

    public int getSteamProgress() {
        return steamProgress;
    }

    public long getLastStirTime() {
        return lastStirTime;
    }

    public float[] getCurrentSteamColor() {
        if (completedSteps.isEmpty()) {
            return new float[] { 1.0F, 1.0F, 1.0F };
        }
        long seed = getParticleSeed();
        return new float[] {
                steamColor(seededFloat(seed)),
                steamColor(seededFloat(seed * 2L)),
                steamColor(seededFloat(seed * 3L))
        };
    }

    private void notifyStateChanged() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    private void tryConsumeReagentFluid() {
        if (hasFluid() || reagentTank.isEmpty() || reagentTank.getContents().reagent != ReagentRegistry.STEAM) {
            return;
        }
        steamProgress += reagentTank.getContents().amount;
        reagentTank.clear();
        if (steamProgress < Fluid.BUCKET_VOLUME) {
            return;
        }
        fluid = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
        steamProgress = 0;
    }

    @Override
    public ReagentTank getTank() {
        return reagentTank;
    }

    @Override
    public boolean isOutput(EnumFacing direction) {
        return false;
    }

    @Override
    public boolean isInput(EnumFacing direction) {
        return !hasFluid();
    }

    @Override
    public void onContentsChanged() {
        tryConsumeReagentFluid();
        markDirty();
        notifyStateChanged();
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                copies.add(stack.copy());
            }
        }
        return copies;
    }

    private void spawnBoilingParticles() {
        boolean hasSteps = !completedSteps.isEmpty() || !currentContents.isEmpty() || currentStirs > 0;
        long seed = getParticleSeed();
        float red = seededFloat(seed);
        float green = seededFloat(seed * 2L);
        float blue = seededFloat(seed * 3L);
        float bubbleRed = hasSteps ? Math.min(1.0F, red * 1.25F) : 0.25F;
        float bubbleGreen = hasSteps ? Math.min(1.0F, green * 1.25F) : 0.5F;
        float bubbleBlue = hasSteps ? Math.min(1.0F, blue * 1.25F) : 1.0F;
        float steamRed = hasSteps ? steamColor(red) : 1.0F;
        float steamGreen = hasSteps ? steamColor(green) : 1.0F;
        float steamBlue = hasSteps ? steamColor(blue) : 1.0F;

        for (int i = 0; i < 3; i++) {
            double x = pos.getX() + 0.25D + 0.5D * world.rand.nextDouble();
            double z = pos.getZ() + 0.25D + 0.5D * world.rand.nextDouble();
            EidolonParticles.create(EidolonParticles.BUBBLE)
                    .scale(0.15F, 0.24F)
                    .lifetime(20)
                    .randomVelocity(0.006D, 0.006D)
                    .addVelocity(0.0D, 0.024D, 0.0D)
                    .color(bubbleRed, bubbleGreen, bubbleBlue)
                    .alpha(0.95F, 0.0F)
                    .spawn(world, x, pos.getY() + 0.82D, z);
            if (world.rand.nextInt(2) == 0) {
                EidolonParticles.create(EidolonParticles.SPARKLE)
                        .scale(0.08F, 0.0F)
                        .lifetime(12)
                        .randomVelocity(0.018D, 0.012D)
                        .addVelocity(0.0D, 0.026D, 0.0D)
                        .color(bubbleRed, bubbleGreen, bubbleBlue, 1.0F, 1.0F, 1.0F)
                        .alpha(0.85F, 0.0F)
                        .spin(0.25F)
                        .spawn(world, x, pos.getY() + 0.86D, z);
            }
            if (world.rand.nextInt(8) == 0) {
                EidolonParticles.create(EidolonParticles.SMOKE)
                        .alpha(0.16F, 0.0F)
                        .scale(0.375F, 0.125F)
                        .lifetime(80)
                        .randomOffset(0.375D, 0.125D)
                        .randomVelocity(0.0125D, 0.025D)
                        .addVelocity(0.0D, 0.05D, 0.0D)
                        .color(steamRed, steamGreen, steamBlue)
                        .sourceSmokeCurve(0.99F)
                        .spawn(world, pos.getX() + 0.5D, pos.getY() + 0.78D, pos.getZ() + 0.5D);
            }
        }
    }

    private long getParticleSeed() {
        long seed = 0L;
        for (CrucibleRecipe.ProvidedStep step : completedSteps) {
            seed ^= step.hashCode();
        }
        return seed;
    }

    private float seededFloat(long seed) {
        particleRandom.setSeed(seed);
        return particleRandom.nextFloat();
    }

    private float steamColor(float value) {
        return Math.min(1.0F, 1.0F - (float) Math.pow(1.0F - value, 2.0D));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("CompletedSteps", writeSteps(completedSteps));
        compound.setTag("CurrentContents", writeStacks(currentContents));
        compound.setInteger("CurrentStirs", currentStirs);
        compound.setInteger("SteamProgress", steamProgress);
        compound.setLong("LastStirTime", lastStirTime);
        if (!currentStirrer.isEmpty()) {
            compound.setTag("CurrentStirrer", currentStirrer.writeToNBT(new NBTTagCompound()));
        }
        if (fluid != null) {
            NBTTagCompound fluidTag = new NBTTagCompound();
            fluid.writeToNBT(fluidTag);
            compound.setTag("Fluid", fluidTag);
        }
        compound.setTag("ReagentTank", reagentTank.writeToNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        completedSteps.clear();
        NBTTagList stepTags = compound.getTagList("CompletedSteps", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < stepTags.tagCount(); i++) {
            NBTTagCompound stepTag = stepTags.getCompoundTagAt(i);
            int stirs = stepTag.getInteger("Stirs");
            ItemStack stirrer = stepTag.hasKey("Stirrer", Constants.NBT.TAG_COMPOUND)
                    ? new ItemStack(stepTag.getCompoundTag("Stirrer")) : ItemStack.EMPTY;
            completedSteps.add(new CrucibleRecipe.ProvidedStep(stirs, stirrer, readStacks(stepTag.getTagList("Contents", Constants.NBT.TAG_COMPOUND))));
        }

        currentContents.clear();
        currentContents.addAll(readStacks(compound.getTagList("CurrentContents", Constants.NBT.TAG_COMPOUND)));
        currentStirs = compound.getInteger("CurrentStirs");
        steamProgress = compound.getInteger("SteamProgress");
        lastStirTime = compound.hasKey("LastStirTime", Constants.NBT.TAG_LONG) ? compound.getLong("LastStirTime") : -100L;
        currentStirrer = compound.hasKey("CurrentStirrer", Constants.NBT.TAG_COMPOUND)
                ? new ItemStack(compound.getCompoundTag("CurrentStirrer")) : ItemStack.EMPTY;
        fluid = compound.hasKey("Fluid", Constants.NBT.TAG_COMPOUND)
                ? FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("Fluid")) : null;
        if (compound.hasKey("ReagentTank", Constants.NBT.TAG_COMPOUND)) {
            reagentTank.readFromNBT(compound.getCompoundTag("ReagentTank"));
        }
        tryConsumeReagentFluid();
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

    private static NBTTagList writeSteps(List<CrucibleRecipe.ProvidedStep> steps) {
        NBTTagList tags = new NBTTagList();
        for (CrucibleRecipe.ProvidedStep step : steps) {
            NBTTagCompound stepTag = new NBTTagCompound();
            stepTag.setInteger("Stirs", step.getStirs());
            if (!step.getStirrer().isEmpty()) {
                stepTag.setTag("Stirrer", step.getStirrer().writeToNBT(new NBTTagCompound()));
            }
            stepTag.setTag("Contents", writeStacks(step.getContents()));
            tags.appendTag(stepTag);
        }
        return tags;
    }

    private static NBTTagList writeStacks(List<ItemStack> stacks) {
        NBTTagList tags = new NBTTagList();
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                tags.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
        }
        return tags;
    }

    private static List<ItemStack> readStacks(NBTTagList tags) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < tags.tagCount(); i++) {
            ItemStack stack = new ItemStack(tags.getCompoundTagAt(i));
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }
}
