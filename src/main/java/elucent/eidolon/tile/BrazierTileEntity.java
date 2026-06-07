package elucent.eidolon.tile;

import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.particle.EidolonParticles;
import elucent.eidolon.spell.AltarRitual;
import elucent.eidolon.spell.AltarRituals;
import elucent.eidolon.spell.AltarInfo;
import elucent.eidolon.registries.ModBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraft.world.WorldServer;

import java.util.UUID;

public class BrazierTileEntity extends TileEntity implements ITickable {
    private static final int FIND_RITUAL_TICKS = 20;
    private static final int RITUAL_STEP_TICKS = 40;

    private ItemStack sacrifice = ItemStack.EMPTY;
    private boolean burning;
    private int findingCounter;
    private int stepCounter;
    private int step;
    private AltarRitual ritual;
    private UUID activatorId;

    public ItemStack getSacrifice() {
        return sacrifice.copy();
    }

    public boolean activate(EntityPlayer player, EnumHand hand) {
        ItemStack held = player.getHeldItem(hand);
        if (burning && player.isSneaking() && held.isEmpty()) {
            extinguish();
            return true;
        }
        if (!burning && held.isEmpty() && !sacrifice.isEmpty()) {
            ItemStack removed = sacrifice;
            sacrifice = ItemStack.EMPTY;
            if (!player.addItemStackToInventory(removed) && world != null) {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, removed));
            }
            notifyStateChanged();
            return true;
        }
        if (!burning && !sacrifice.isEmpty() && held.getItem() == Items.FLINT_AND_STEEL) {
            held.damageItem(1, player);
            startBurning(player);
            return true;
        }
        if (!burning && !held.isEmpty() && sacrifice.isEmpty()) {
            sacrifice = held.splitStack(1);
            notifyStateChanged();
            return true;
        }
        return false;
    }

    public void dropContents() {
        if (world != null && !world.isRemote && !sacrifice.isEmpty()) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sacrifice));
            sacrifice = ItemStack.EMPTY;
        }
    }

    @Override
    public void update() {
        if (world == null || !burning) {
            return;
        }
        if (world.isRemote) {
            spawnBurningParticles();
            if (ritual == null && findingCounter < FIND_RITUAL_TICKS) {
                findingCounter++;
            }
            return;
        }
        if (ritual == null) {
            findingCounter++;
            if (findingCounter >= FIND_RITUAL_TICKS) {
                AltarInfo altarInfo = scanNearestAltar();
                ritual = findMatchingRitual(altarInfo);
                if (ritual == null) {
                    sacrifice = ItemStack.EMPTY;
                    extinguish();
                } else {
                    findingCounter = 0;
                    stepCounter = RITUAL_STEP_TICKS - 1;
                    step = 0;
                    notifyStateChanged();
                }
            }
            return;
        }
        stepCounter++;
        if (stepCounter >= RITUAL_STEP_TICKS) {
            if (step < ritual.getProviderOfferingCount()) {
                AltarRitual.SetupResult result = ritual.setupFromProviders(world, pos, step);
                if (result == AltarRitual.SetupResult.FAIL) {
                    sacrifice = ItemStack.EMPTY;
                    extinguish();
                } else {
                    step++;
                    stepCounter = 0;
                    notifyStateChanged();
                }
                return;
            }
            int focusStep = ritual.getProviderOfferingCount();
            if (ritual.hasProviderFocusStep() && step == focusStep) {
                AltarRitual.PerformResult focusResult = ritual.processFocusFromProviders(world, pos, getActivator());
                if (focusResult != AltarRitual.PerformResult.SUCCESS) {
                    sacrifice = ItemStack.EMPTY;
                    extinguish();
                } else {
                    step++;
                    stepCounter = 0;
                    notifyStateChanged();
                }
                return;
            }
            AltarRitual.PerformResult performResult = ritual.finishFromProviders(world, pos, getActivator());
            if (performResult == AltarRitual.PerformResult.SUCCESS) {
                stepCounter = 0;
                consumeSacrifice();
                complete();
            } else {
                sacrifice = ItemStack.EMPTY;
                extinguish();
            }
        }
    }

    private void startBurning(EntityPlayer player) {
        burning = true;
        findingCounter = 0;
        stepCounter = 0;
        step = 0;
        ritual = null;
        activatorId = player == null ? null : player.getUniqueID();
        playIgniteEffects();
        notifyStateChanged();
    }

    private void complete() {
        sendBrazierVisual(VisualEffectPacket.RITUAL_COMPLETE, 0.65F, 0.28F, 1.0F);
        burning = false;
        ritual = null;
        activatorId = null;
        findingCounter = 0;
        stepCounter = 0;
        step = 0;
        notifyStateChanged();
    }

    private void extinguish() {
        sendBrazierVisual(VisualEffectPacket.EXTINGUISH, 0.5F, 0.5F, 0.55F);
        burning = false;
        ritual = null;
        activatorId = null;
        findingCounter = 0;
        stepCounter = 0;
        step = 0;
        notifyStateChanged();
    }

    private void consumeSacrifice() {
        sacrifice = ItemStack.EMPTY;
    }

    private EntityPlayer getActivator() {
        if (world == null || activatorId == null) {
            return null;
        }
        return world.getPlayerEntityByUUID(activatorId);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!sacrifice.isEmpty()) {
            compound.setTag("Sacrifice", sacrifice.writeToNBT(new NBTTagCompound()));
        }
        compound.setBoolean("Burning", burning);
        compound.setInteger("FindingCounter", findingCounter);
        compound.setInteger("StepCounter", stepCounter);
        compound.setInteger("Step", step);
        if (ritual != null) {
            compound.setString("Ritual", ritual.getId().toString());
        }
        if (activatorId != null) {
            compound.setString("Activator", activatorId.toString());
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        sacrifice = compound.hasKey("Sacrifice", Constants.NBT.TAG_COMPOUND)
                ? new ItemStack(compound.getCompoundTag("Sacrifice")) : ItemStack.EMPTY;
        burning = compound.getBoolean("Burning");
        findingCounter = compound.getInteger("FindingCounter");
        stepCounter = compound.getInteger("StepCounter");
        step = compound.getInteger("Step");
        ritual = compound.hasKey("Ritual", Constants.NBT.TAG_STRING)
                ? AltarRituals.find(new ResourceLocation(compound.getString("Ritual"))) : null;
        activatorId = compound.hasKey("Activator", Constants.NBT.TAG_STRING)
                ? UUID.fromString(compound.getString("Activator")) : null;
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

    private void notifyStateChanged() {
        if (world != null && !world.isRemote) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    private void playIgniteEffects() {
        if (world == null || world.isRemote) {
            return;
        }
        sendBrazierVisual(VisualEffectPacket.IGNITE, 1.0F, 0.45F, 0.18F);
        if (world instanceof WorldServer) {
            ((WorldServer) world).spawnParticle(EnumParticleTypes.FLAME,
                    pos.getX() + 0.5D, pos.getY() + 1.22D, pos.getZ() + 0.5D,
                    10, 0.1D, 0.08D, 0.1D, 0.02D);
            ((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    pos.getX() + 0.5D, pos.getY() + 1.28D, pos.getZ() + 0.5D,
                    5, 0.12D, 0.06D, 0.12D, 0.01D);
        }
    }

    private void sendBrazierVisual(int effect, float r, float g, float b) {
        if (world == null || world.isRemote) {
            return;
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.18D;
        double z = pos.getZ() + 0.5D;
        VisualEffectPacket.sendAround(world, x, y, z, VisualEffectPacket.at(effect, x, y, z, r, g, b));
    }

    private void spawnBurningParticles() {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.24D;
        double z = pos.getZ() + 0.5D;
        float r = ritual == null ? 1.0F : 0.65F;
        float g = ritual == null ? 0.5F : 0.28F;
        float b = ritual == null ? 0.25F : 1.0F;
        EidolonParticles.spawnFlame(world,
                x + (world.rand.nextDouble() - 0.5D) * 0.18D,
                y + world.rand.nextDouble() * 0.08D,
                z + (world.rand.nextDouble() - 0.5D) * 0.18D,
                (world.rand.nextDouble() - 0.5D) * 0.012D,
                0.018D + world.rand.nextDouble() * 0.012D,
                (world.rand.nextDouble() - 0.5D) * 0.012D,
                r, g, b);
        world.spawnParticle(EnumParticleTypes.FLAME,
                x + (world.rand.nextDouble() - 0.5D) * 0.1D,
                y + world.rand.nextDouble() * 0.08D,
                z + (world.rand.nextDouble() - 0.5D) * 0.1D,
                0.0D, 0.018D, 0.0D);
        if (world.rand.nextInt(5) == 0) {
            EidolonParticles.spawnSmoke(world,
                    x + (world.rand.nextDouble() - 0.5D) * 0.16D,
                    y + 0.08D,
                    z + (world.rand.nextDouble() - 0.5D) * 0.16D,
                    (world.rand.nextDouble() - 0.5D) * 0.018D,
                    0.07D,
                    (world.rand.nextDouble() - 0.5D) * 0.018D,
                    0.48F, 0.48F, 0.5F);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                    x + (world.rand.nextDouble() - 0.5D) * 0.14D,
                    y + 0.08D,
                    z + (world.rand.nextDouble() - 0.5D) * 0.14D,
                    0.0D, 0.035D, 0.0D);
        }
        if (ritual != null && world.rand.nextInt(8) == 0) {
            EidolonParticles.spawnSparkle(world,
                    x + (world.rand.nextDouble() - 0.5D) * 0.26D,
                    y + 0.12D,
                    z + (world.rand.nextDouble() - 0.5D) * 0.26D,
                    (world.rand.nextDouble() - 0.5D) * 0.04D,
                    0.055D,
                    (world.rand.nextDouble() - 0.5D) * 0.04D,
                    r, Math.min(1.0F, g * 1.5F), Math.min(1.0F, b * 1.5F));
            world.spawnParticle(EnumParticleTypes.SPELL_MOB,
                    x + (world.rand.nextDouble() - 0.5D) * 0.3D,
                    y + 0.12D,
                    z + (world.rand.nextDouble() - 0.5D) * 0.3D,
                    0.55D, 0.2D, 0.8D);
        }
    }

    private AltarInfo scanNearestAltar() {
        BlockPos nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (int x = pos.getX() - 8; x <= pos.getX() + 8; x++) {
            for (int y = pos.getY() - 3; y <= pos.getY() + 3; y++) {
                for (int z = pos.getZ() - 8; z <= pos.getZ() + 8; z++) {
                    BlockPos candidate = new BlockPos(x, y, z);
                    if (world.getBlockState(candidate).getBlock() == ModBlocks.STONE_ALTAR
                            || world.getBlockState(candidate).getBlock() == ModBlocks.WOODEN_ALTAR) {
                        double dx = candidate.getX() - pos.getX();
                        double dy = candidate.getY() - pos.getY();
                        double dz = candidate.getZ() - pos.getZ();
                        double distance = dx * dx + dy * dy + dz * dz;
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            nearest = candidate;
                        }
                    }
                }
            }
        }
        return nearest == null ? null : AltarInfo.scan(world, nearest);
    }

    private AltarRitual findMatchingRitual(AltarInfo altarInfo) {
        if (altarInfo == null) {
            return null;
        }
        for (AltarRitual candidate : AltarRituals.getRituals()) {
            if (candidate.matchesSacrifice(sacrifice) && candidate.canStartFromProviders(world, pos, altarInfo)) {
                return candidate;
            }
        }
        return null;
    }
}
