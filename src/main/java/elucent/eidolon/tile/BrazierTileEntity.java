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
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class BrazierTileEntity extends TileEntity implements ITickable {
    private static final int FIND_RITUAL_TICKS = 80;
    private static final int RITUAL_STEP_TICKS = 40;
    private static final double BRAZIER_FLAME_HEIGHT = 1.12D;
    private static final double BRAZIER_SEEKING_WISP_HEIGHT = 1.22D;
    private static final double BRAZIER_RITUAL_WISP_HEIGHT = 1.32D;
    private static final double BRAZIER_RITUAL_COLUMN_HEIGHT = 1.56D;

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
            spawnFindingParticles();
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
                    startRitual(ritual);
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
        if (ritual != null) {
            float[] color = ritualColor(ritual);
            sendRitualVisual(VisualEffectPacket.RITUAL_COMPLETE, color[0], color[1], color[2]);
        }
        sendBrazierVisual(VisualEffectPacket.EXTINGUISH, 0.5F, 0.5F, 0.55F);
        burning = false;
        ritual = null;
        activatorId = null;
        findingCounter = 0;
        stepCounter = 0;
        step = 0;
        notifyStateChanged();
    }

    private void extinguish() {
        if (ritual != null) {
            float[] color = ritualColor(ritual);
            sendRitualVisual(VisualEffectPacket.FLAME, color[0], color[1], color[2]);
        }
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
        sendBrazierVisual(VisualEffectPacket.IGNITE, 1.0F, 0.5F, 0.25F);
    }

    private void sendBrazierVisual(int effect, float r, float g, float b) {
        sendBrazierVisual(effect, pos.getX() + 0.5D, pos.getY() + BRAZIER_FLAME_HEIGHT, pos.getZ() + 0.5D, r, g, b);
    }

    private void sendRitualVisual(int effect, float r, float g, float b) {
        sendBrazierVisual(effect, pos.getX() + 0.5D, pos.getY() + 3.0D, pos.getZ() + 0.5D, r, g, b);
    }

    private void sendBrazierVisual(int effect, double x, double y, double z, float r, float g, float b) {
        if (world == null || world.isRemote) {
            return;
        }
        VisualEffectPacket.sendAround(world, x, y, z, VisualEffectPacket.at(effect, x, y, z, r, g, b));
    }

    private void spawnFindingParticles() {
        if (ritual != null || findingCounter >= FIND_RITUAL_TICKS || findingCounter < FIND_RITUAL_TICKS / 2) {
            return;
        }
        float progress = (findingCounter - FIND_RITUAL_TICKS / 2) / (float) (FIND_RITUAL_TICKS / 2);
        for (int i = 0; i < 8; i++) {
            float angle = progress * (float) Math.PI / 4.0F + i * (float) Math.PI / 4.0F;
            float radius = 0.625F * (float) Math.sin(4.0F * angle);
            angle += (float) Math.PI / 4.0F;
            double x = pos.getX() + 0.5D + Math.sin(angle) * radius;
            double y = pos.getY() + 0.875D;
            double z = pos.getZ() + 0.5D + Math.cos(angle) * radius;
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.38F * progress, 0.0F)
                    .scale(0.18F, 0.05F)
                    .lifetime(24)
                    .randomVelocity(0.012D, 0.012D)
                    .addVelocity(0.0D, 0.018D, 0.0D)
                    .color(1.0F, 0.5F, 0.25F, 1.0F, 0.25F, 0.375F)
                    .spawn(world, x, y, z);
        }
    }

    private void startRitual(AltarRitual ritual) {
        float[] color = ritualColor(ritual);
        findingCounter = 0;
        stepCounter = RITUAL_STEP_TICKS - 1;
        step = 0;
        sendRitualVisual(VisualEffectPacket.FLAME, color[0], color[1], color[2]);
        notifyStateChanged();
    }

    private float[] ritualColor(AltarRitual ritual) {
        if (ritual == null) {
            return color(1.0F, 0.5F, 0.25F);
        }
        return color(ritual.getRed(), ritual.getGreen(), ritual.getBlue());
    }

    private float[] color(float r, float g, float b) {
        return new float[] {r, g, b};
    }

    private void spawnBurningParticles() {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + BRAZIER_FLAME_HEIGHT;
        double z = pos.getZ() + 0.5D;
        float[] color = ritualColor(ritual);
        float r = color[0];
        float g = color[1];
        float b = color[2];
        EidolonParticles.create(EidolonParticles.FLAME)
                .alpha(0.68F, 0.0F)
                .scale(0.38F, 0.14F)
                .lifetime(24)
                .randomOffset(0.3D, 0.14D)
                .randomVelocity(0.012D, 0.024D)
                .addVelocity(0.0D, 0.012D, 0.0D)
                .color(r, g, b, r, g * 0.5F, b * 1.5F)
                .repeat(world, x, y, z, ritual == null ? 2 : 3);
        if (world.rand.nextInt(5) == 0) {
            EidolonParticles.create(EidolonParticles.SMOKE)
                    .alpha(0.125F, 0.0F)
                    .scale(0.375F, 0.125F)
                    .lifetime(80)
                    .randomOffset(0.25D, 0.125D)
                    .randomVelocity(0.025D, 0.025D)
                    .addVelocity(0.0D, 0.1D, 0.0D)
                    .color(0.5F, 0.5F, 0.5F, 0.25F, 0.25F, 0.25F)
                    .fullbright(false)
                    .spawn(world, x, y + 0.125D, z);
        }
        if (ritual == null) {
            spawnSeekingParticles(x, y, z);
            return;
        }
        if (ritual != null && world.rand.nextInt(6) == 0) {
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(1.0F, 0.0F)
                    .scale(0.11F, 0.0F)
                    .lifetime(36)
                    .randomOffset(0.18D, 0.04D)
                    .randomVelocity(0.08D, 0.035D)
                    .addVelocity(0.0D, 0.085D, 0.0D)
                    .color(Math.min(1.0F, r * 1.4F), Math.min(1.0F, g * 1.65F),
                            Math.min(1.0F, b * 1.9F), r, g, b)
                    .enableGravity()
                    .spin(0.4F)
                    .spawn(world, x, y + 0.18D, z);
        }
        if (ritual != null) {
            int wispCount = 7 + world.rand.nextInt(4);
            for (int i = 0; i < wispCount; i++) {
                EidolonParticles.create(EidolonParticles.WISP)
                        .alpha(0.9F, 0.0F)
                        .scale(0.34F + world.rand.nextFloat() * 0.12F, 0.05F)
                        .lifetime(34 + world.rand.nextInt(14))
                        .randomOffset(0.48D, 0.18D)
                        .randomVelocity(0.055D, 0.07D)
                        .addVelocity(0.0D, 0.055D, 0.0D)
                        .color(r, g, b, Math.min(1.0F, r * 1.75F), Math.min(1.0F, g * 1.75F),
                                Math.min(1.0F, b * 1.75F))
                        .spawn(world, x, pos.getY() + BRAZIER_RITUAL_WISP_HEIGHT, z);
            }
            EidolonParticles.create(EidolonParticles.FLAME)
                    .alpha(0.92F, 0.0F)
                    .scale(0.46F, 0.12F)
                    .lifetime(30)
                    .randomOffset(0.4D, 0.16D)
                    .randomVelocity(0.026D, 0.052D)
                    .addVelocity(0.0D, 0.044D, 0.0D)
                    .color(r, g, b, r, g * 0.5F, b * 1.5F)
                    .repeat(world, x, y + 0.18D, z, 4);
            spawnRitualColumnParticles(x, y, z, r, g, b);
        }
    }

    private void spawnSeekingParticles(double x, double y, double z) {
        long time = world.getTotalWorldTime();
        for (int i = 0; i < 3; i++) {
            double angle = time * 0.16D + i * Math.PI * 2.0D / 3.0D;
            double radius = 0.18D + world.rand.nextDouble() * 0.18D;
            double px = x + Math.sin(angle) * radius;
            double pz = z + Math.cos(angle) * radius;
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.52F, 0.0F)
                    .scale(0.2F + world.rand.nextFloat() * 0.06F, 0.035F)
                    .lifetime(24 + world.rand.nextInt(8))
                    .randomOffset(0.12D, 0.08D)
                    .randomVelocity(0.018D, 0.026D)
                    .addVelocity(0.0D, 0.028D, 0.0D)
                    .color(1.0F, 0.42F, 0.18F, 1.0F, 0.18F, 0.28F)
                    .spawn(world, px, pos.getY() + BRAZIER_SEEKING_WISP_HEIGHT + i * 0.035D, pz);
        }
        if (time % 4L == 0L) {
            EidolonParticles.create(EidolonParticles.SPARKLE)
                    .alpha(0.85F, 0.0F)
                    .scale(0.095F, 0.0F)
                    .lifetime(22)
                    .randomOffset(0.28D, 0.1D)
                    .randomVelocity(0.05D, 0.04D)
                    .addVelocity(0.0D, 0.045D, 0.0D)
                    .color(1.0F, 0.62F, 0.24F, 1.0F, 0.18F, 0.3F)
                    .spin(0.18F)
                    .repeat(world, x, y + 0.18D, z, 2);
        }
        if (time % 18L == 0L) {
            EidolonParticles.create(EidolonParticles.RING)
                    .alpha(0.34F, 0.0F)
                    .scale(0.5F, 0.12F)
                    .lifetime(16)
                    .randomVelocity(0.004D, 0.003D)
                    .addVelocity(0.0D, 0.012D, 0.0D)
                    .color(1.0F, 0.42F, 0.18F, 1.0F, 0.16F, 0.28F)
                    .spin(0.12F)
                    .spawn(world, x, y + 0.08D, z);
        }
    }

    private void spawnRitualColumnParticles(double x, double y, double z, float r, float g, float b) {
        double columnY = pos.getY() + BRAZIER_RITUAL_COLUMN_HEIGHT;
        long time = world.getTotalWorldTime();
        for (int i = 0; i < 5; i++) {
            double angle = time * 0.22D + i * Math.PI * 2.0D / 5.0D;
            double radius = 0.24D + 0.1D * Math.sin(time * 0.08D + i);
            double px = x + Math.sin(angle) * radius;
            double pz = z + Math.cos(angle) * radius;
            EidolonParticles.create(EidolonParticles.WISP)
                    .alpha(0.82F, 0.0F)
                    .scale(0.26F, 0.04F)
                    .lifetime(28)
                    .randomOffset(0.08D, 0.08D)
                    .randomVelocity(0.025D, 0.055D)
                    .addVelocity(0.0D, 0.045D, 0.0D)
                    .color(Math.min(1.0F, r * 1.5F), Math.min(1.0F, g * 1.5F),
                            Math.min(1.0F, b * 1.5F), r, g, b)
                    .spawn(world, px, y + 0.18D + i * 0.035D, pz);
        }
        EidolonParticles.create(EidolonParticles.SPARKLE)
                .alpha(1.0F, 0.0F)
                .scale(0.14F, 0.0F)
                .lifetime(28)
                .randomOffset(0.32D, 0.16D)
                .randomVelocity(0.08D, 0.07D)
                .addVelocity(0.0D, 0.085D, 0.0D)
                .color(Math.min(1.0F, r * 1.8F), Math.min(1.0F, g * 1.8F),
                        Math.min(1.0F, b * 1.8F), 1.0F, 1.0F, 1.0F)
                .spin(0.28F)
                .repeat(world, x, columnY, z, 3);
        if (time % 8L == 0L) {
            EidolonParticles.create(EidolonParticles.RING)
                    .alpha(0.72F, 0.0F)
                    .scale(0.78F, 0.16F)
                    .lifetime(18)
                    .randomVelocity(0.006D, 0.004D)
                    .addVelocity(0.0D, 0.018D, 0.0D)
                    .color(r, g, b, Math.min(1.0F, r * 1.6F), Math.min(1.0F, g * 1.6F),
                            Math.min(1.0F, b * 1.6F))
                    .spin(0.2F)
                    .spawn(world, x, y + 0.28D, z);
        }
        if (time % 14L == 0L) {
            EidolonParticles.create(EidolonParticles.BURST)
                    .alpha(0.7F, 0.0F)
                    .scale(0.48F, 0.08F)
                    .lifetime(16)
                    .randomOffset(0.18D, 0.08D)
                    .randomVelocity(0.04D, 0.055D)
                    .addVelocity(0.0D, 0.035D, 0.0D)
                    .color(Math.min(1.0F, r * 1.65F), Math.min(1.0F, g * 1.65F),
                            Math.min(1.0F, b * 1.65F), r, g, b)
                    .repeat(world, x, columnY, z, 4);
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
