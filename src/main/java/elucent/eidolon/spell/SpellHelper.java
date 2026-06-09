package elucent.eidolon.spell;

import elucent.eidolon.deity.Deity;
import elucent.eidolon.network.VisualEffectPacket;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.registries.ModSounds;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class SpellHelper {
    private SpellHelper() {
    }

    static AxisAlignedBB area(BlockPos pos, double radius) {
        return new AxisAlignedBB(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + 1.0D + radius, pos.getY() + 1.0D + radius, pos.getZ() + 1.0D + radius);
    }

    static BlockPos findNearestBlock(World world, BlockPos center, int radius, Predicate<Block> predicate) {
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = center.add(dx, dy, dz);
                    if (!predicate.test(world.getBlockState(pos).getBlock())) {
                        continue;
                    }
                    double distance = pos.distanceSq(center);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = pos;
                    }
                }
            }
        }
        return nearest;
    }

    static BlockPos findEffigy(World world, BlockPos center) {
        return findNearestBlock(world, center, 4,
                block -> block == ModBlocks.STRAW_EFFIGY || block == ModBlocks.UNHOLY_EFFIGY);
    }

    static EffigyTileEntity findEffigyTile(World world, BlockPos center) {
        return findNearestTile(world, center, 4, EffigyTileEntity.class,
                tile -> world.getBlockState(tile.getPos()).getBlock() == ModBlocks.STRAW_EFFIGY
                        || world.getBlockState(tile.getPos()).getBlock() == ModBlocks.UNHOLY_EFFIGY);
    }

    static BlockPos findUnholyEffigy(World world, BlockPos center) {
        return findNearestBlock(world, center, 4, block -> block == ModBlocks.UNHOLY_EFFIGY);
    }

    static EffigyTileEntity findUnholyEffigyTile(World world, BlockPos center) {
        return findNearestTile(world, center, 4, EffigyTileEntity.class,
                tile -> world.getBlockState(tile.getPos()).getBlock() == ModBlocks.UNHOLY_EFFIGY);
    }

    static GobletTileEntity findGoblet(World world, BlockPos center) {
        return findNearestTile(world, center, 4, GobletTileEntity.class, tile -> true);
    }

    static BlockPos findStoneAltar(World world, BlockPos center) {
        return findNearestBlock(world, center, 4, block -> block == ModBlocks.STONE_ALTAR);
    }

    static boolean isOnStoneAltar(World world, BlockPos pos) {
        BlockPos below = pos.down();
        return world.getBlockState(below).getBlock() == ModBlocks.STONE_ALTAR;
    }

    static double getNearbyAltarPower(World world, BlockPos center) {
        BlockPos altar = findNearestBlock(world, center, 5,
                block -> block == ModBlocks.STONE_ALTAR || block == ModBlocks.WOODEN_ALTAR);
        if (altar == null) {
            return 0.0D;
        }
        return AltarInfo.scan(world, altar).getPower();
    }

    static <T extends EntityLivingBase> T nearestLiving(World world, BlockPos pos, Class<T> type, double radius) {
        List<T> entities = world.getEntitiesWithinAABB(type, area(pos, radius),
                entity -> entity != null && entity.isEntityAlive());
        if (entities.isEmpty()) {
            return null;
        }
        return entities.stream().min(Comparator.comparingDouble(entity -> entity.getDistanceSq(pos))).orElse(null);
    }

    private static <T extends TileEntity> T findNearestTile(World world, BlockPos center, int radius,
                                                           Class<T> type, Predicate<T> predicate) {
        T nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    TileEntity tile = world.getTileEntity(center.add(dx, dy, dz));
                    if (!type.isInstance(tile)) {
                        continue;
                    }
                    T typed = type.cast(tile);
                    if (!predicate.test(typed)) {
                        continue;
                    }
                    double distance = typed.getPos().distanceSq(center);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = typed;
                    }
                }
            }
        }
        return nearest;
    }

    static EntityItem singleNearbyItem(World world, double x, double y, double z, double radius) {
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius),
                entity -> entity != null && !entity.isDead && !entity.getItem().isEmpty());
        return items.size() == 1 ? items.get(0) : null;
    }

    @SideOnly(Side.CLIENT)
    public static void playSpellCastVisuals(World world, BlockPos pos, EntityPlayer caster, Spell spell,
                                            SignSequence sequence) {
        if (world == null || !world.isRemote || pos == null || spell == null || sequence == null
                || !spell.matches(sequence)) {
            return;
        }
        if (spell instanceof PrayerSpell) {
            EffigyTileEntity effigy = findEffigyTile(world, pos);
            if (effigy != null) {
                playClientChantSuccessSounds(world, effigy.getPos());
                playClientChantFlames(world, effigy.getPos(), ((PrayerSpell) spell).getDeity());
            }
        } else if (spell instanceof AnimalSacrificeSpell) {
            EffigyTileEntity effigy = findEffigyTile(world, pos);
            if (effigy != null) {
                playClientChantSuccessSounds(world, effigy.getPos());
                playClientChantFlames(world, effigy.getPos(), Signs.BLOOD_SIGN);
            }
        } else if (spell instanceof VillagerSacrificeSpell) {
            EffigyTileEntity effigy = findUnholyEffigyTile(world, pos);
            if (effigy != null && SpellHelper.isOnStoneAltar(world, effigy.getPos())) {
                playClientChantSuccessSounds(world, effigy.getPos());
                playClientChantFlames(world, effigy.getPos(), Signs.SOUL_SIGN);
            }
        } else if (spell instanceof DarkTouchSpell) {
            BlockPos soundPos = caster == null ? pos : caster.getPosition();
            world.playSound(caster, soundPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                    SoundCategory.NEUTRAL, 1.0F, 0.6F + world.rand.nextFloat() * 0.2F);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void playClientChantSuccessSounds(World world, BlockPos pos) {
        if (world == null || !world.isRemote || pos == null || Minecraft.getMinecraft().player == null) {
            return;
        }
        EntityPlayer localPlayer = Minecraft.getMinecraft().player;
        world.playSound(localPlayer, pos, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.NEUTRAL,
                10000.0F, 0.6F + world.rand.nextFloat() * 0.2F);
        world.playSound(localPlayer, pos, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.NEUTRAL,
                2.0F, 0.5F + world.rand.nextFloat() * 0.2F);
    }

    static void playChantSuccess(World world, BlockPos pos) {
        if (world.isRemote) {
            return;
        }
        world.playSound(null, pos, ModSounds.CHANT_WORD, SoundCategory.PLAYERS, 0.75F,
                0.75F + world.rand.nextFloat() * 0.25F);
    }

    static void sendMagicBurst(World world, BlockPos pos, Sign primary, Sign secondary) {
        if (world == null || world.isRemote || pos == null || primary == null || secondary == null) {
            return;
        }
        sendMagicBurst(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, primary, secondary);
    }

    static void sendMagicBurst(World world, double x, double y, double z, Sign primary, Sign secondary) {
        if (world == null || world.isRemote || primary == null || secondary == null) {
            return;
        }
        VisualEffectPacket.sendAround(world, x, y, z, VisualEffectPacket.at(VisualEffectPacket.MAGIC_BURST,
                x, y, z, primary.getRed(), primary.getGreen(), primary.getBlue(),
                secondary.getRed(), secondary.getGreen(), secondary.getBlue()));
    }

    static void sendChantFlames(World world, BlockPos pos, Sign color) {
        if (world == null || world.isRemote || pos == null || color == null) {
            return;
        }
        VisualEffectPacket packet = createChantFlamePacket(world, pos, color.getRed(), color.getGreen(), color.getBlue());
        if (packet != null) {
            VisualEffectPacket.sendAround(world, pos, packet);
        }
    }

    static void sendChantFlames(World world, BlockPos pos, Deity deity) {
        if (world == null || world.isRemote || pos == null || deity == null) {
            return;
        }
        VisualEffectPacket packet = createChantFlamePacket(world, pos, deity.getRed(), deity.getGreen(), deity.getBlue());
        if (packet != null) {
            VisualEffectPacket.sendAround(world, pos, packet);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void playClientChantFlames(World world, BlockPos pos, Sign color) {
        if (world == null || !world.isRemote || pos == null || color == null) {
            return;
        }
        VisualEffectPacket packet = createChantFlamePacket(world, pos, color.getRed(), color.getGreen(), color.getBlue());
        if (packet != null) {
            VisualEffectPacket.playClient(packet);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void playClientChantFlames(World world, BlockPos pos, Deity deity) {
        if (world == null || !world.isRemote || pos == null || deity == null) {
            return;
        }
        VisualEffectPacket packet = createChantFlamePacket(world, pos, deity.getRed(), deity.getGreen(), deity.getBlue());
        if (packet != null) {
            VisualEffectPacket.playClient(packet);
        }
    }

    private static VisualEffectPacket createChantFlamePacket(World world, BlockPos pos,
                                                             float red, float green, float blue) {
        EnumFacing facing = EnumFacing.NORTH;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block != ModBlocks.STRAW_EFFIGY && block != ModBlocks.UNHOLY_EFFIGY) {
            return null;
        }
        for (IProperty<?> property : state.getPropertyKeys()) {
            if ("facing".equals(property.getName()) && state.getValue(property) instanceof EnumFacing) {
                facing = (EnumFacing) state.getValue(property);
                break;
            }
        }
        EnumFacing tangent = facing.rotateY();
        double x = pos.getX() + 0.5D + facing.getXOffset() * 0.21875D;
        double y = pos.getY() + 0.8125D;
        double z = pos.getZ() + 0.5D + facing.getZOffset() * 0.21875D;
        double x1 = x + 0.09375D * tangent.getXOffset();
        double z1 = z + 0.09375D * tangent.getZOffset();
        double x2 = x - 0.09375D * tangent.getXOffset();
        double z2 = z - 0.09375D * tangent.getZOffset();
        return new VisualEffectPacket(VisualEffectPacket.CHANT_FLAME,
                x1, y, z1, x2, y, z2, red, green, blue, red, green, blue);
    }

    static void playChantFail(World world, BlockPos pos) {
        if (!world.isRemote) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.85F,
                    0.85F + world.rand.nextFloat() * 0.25F);
        }
    }
}
