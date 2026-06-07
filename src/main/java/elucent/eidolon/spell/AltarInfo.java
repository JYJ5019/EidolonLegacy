package elucent.eidolon.spell;

import elucent.eidolon.reagent.ReagentStack;
import elucent.eidolon.tile.AltarTileEntity;
import elucent.eidolon.tile.OffertoryPlateTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class AltarInfo {
    private static final class AltarAttributes {
        private double capacity;
        private double power;
    }

    private final Map<ResourceLocation, AltarAttributes> attributes = new HashMap<>();
    private final Map<BlockPos, ItemStack> offerings = new HashMap<>();
    private final Set<BlockPos> altarPositions;
    private int reagentAmount;
    private int reagentCapacity;
    private double reagentCapacityBonus;
    private double reagentPowerBonus;

    private AltarInfo(Set<BlockPos> altarPositions) {
        this.altarPositions = altarPositions;
    }

    public static AltarInfo scan(World world, BlockPos pos) {
        Set<BlockPos> altarPositions = getAltarPositions(world, pos);
        AltarInfo info = new AltarInfo(altarPositions);
        for (BlockPos altarPos : altarPositions) {
            TileEntity altarTile = world.getTileEntity(altarPos);
            if (altarTile instanceof AltarTileEntity) {
                AltarTileEntity altar = (AltarTileEntity) altarTile;
                ItemStack offering = altar.getOffering();
                if (!offering.isEmpty()) {
                    info.offerings.put(altarPos, offering);
                }
                info.applyReagentSupport(altar);
            }

            IBlockState above = world.getBlockState(altarPos.up());
            AltarEntry blockEntry = AltarEntries.find(above);
            if (blockEntry != null) {
                blockEntry.apply(info);
                continue;
            }
            TileEntity tile = world.getTileEntity(altarPos.up());
            if (tile instanceof OffertoryPlateTileEntity) {
                AltarEntry itemEntry = AltarEntries.findPlateOffering(((OffertoryPlateTileEntity) tile).getStack());
                if (itemEntry != null) {
                    itemEntry.apply(info);
                }
            }
        }
        return info;
    }

    public static Set<BlockPos> getAltarPositions(World world, BlockPos pos) {
        Set<BlockPos> result = new HashSet<>();
        Queue<BlockPos> visit = new ArrayDeque<>();
        Block altar = world.getBlockState(pos).getBlock();
        visit.add(pos);
        while (!visit.isEmpty()) {
            BlockPos current = visit.remove();
            if (result.contains(current) || world.getBlockState(current).getBlock() != altar) {
                continue;
            }
            result.add(current);
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                visit.add(current.offset(facing));
            }
        }
        return result;
    }

    void increaseCapacity(ResourceLocation key, double value) {
        AltarAttributes attrs = attributes.computeIfAbsent(key, k -> new AltarAttributes());
        attrs.capacity = Math.max(attrs.capacity, value);
    }

    void increasePower(ResourceLocation key, double value) {
        AltarAttributes attrs = attributes.computeIfAbsent(key, k -> new AltarAttributes());
        attrs.power = Math.max(attrs.power, value);
    }

    public int getAltarCount() {
        return altarPositions.size();
    }

    public Set<BlockPos> getAltarPositions() {
        return Collections.unmodifiableSet(altarPositions);
    }

    public ItemStack getOffering(BlockPos pos) {
        ItemStack stack = offerings.get(pos);
        return stack == null ? ItemStack.EMPTY : stack.copy();
    }

    public int getOfferingCount() {
        return offerings.size();
    }

    public double getCapacity() {
        double sum = 0.0D;
        for (AltarAttributes attrs : attributes.values()) {
            sum += attrs.capacity;
        }
        return sum + reagentCapacityBonus;
    }

    public double getPower() {
        double sum = 0.0D;
        for (AltarAttributes attrs : attributes.values()) {
            sum += attrs.power;
        }
        return sum + reagentPowerBonus;
    }

    public int getReagentAmount() {
        return reagentAmount;
    }

    public int getReagentCapacity() {
        return reagentCapacity;
    }

    private void applyReagentSupport(AltarTileEntity altar) {
        reagentCapacity += altar.getTank().getCapacity();
        ReagentStack stack = altar.getTank().getContents();
        if (stack == null || stack.isEmpty()) {
            return;
        }
        reagentAmount += stack.amount;
        double pressure = Math.min(1.0D, altar.getTank().getPressure());
        String reagent = stack.reagent.getRegistryName().getPath();
        if ("crimsol".equals(reagent)) {
            reagentCapacityBonus += pressure;
            reagentPowerBonus += pressure * 3.0D;
        } else if ("esprit".equals(reagent)) {
            reagentCapacityBonus += pressure * 3.0D;
            reagentPowerBonus += pressure;
        } else {
            reagentCapacityBonus += pressure * 2.0D;
            reagentPowerBonus += pressure * 2.0D;
        }
    }
}
