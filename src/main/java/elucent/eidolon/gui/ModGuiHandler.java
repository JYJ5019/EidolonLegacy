package elucent.eidolon.gui;

import elucent.eidolon.Reference;
import elucent.eidolon.tile.IncubatorTileEntity;
import elucent.eidolon.tile.ResearchTableTileEntity;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import elucent.eidolon.tile.WoodenBrewingStandTileEntity;
import elucent.eidolon.tile.WorktableTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ModGuiHandler implements IGuiHandler {
    public static final int WORKTABLE = 0;
    public static final int RESEARCH_TABLE = 1;
    public static final int CODEX = 2;
    public static final int SOUL_ENCHANTER = 3;
    public static final int WOODEN_BREWING_STAND = 4;
    public static final int INCUBATOR = 5;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == WORKTABLE) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof WorktableTileEntity) {
                return new WorktableContainer(player.inventory, (WorktableTileEntity) tile);
            }
        } else if (id == RESEARCH_TABLE) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof ResearchTableTileEntity) {
                return new ResearchTableContainer(player.inventory, (ResearchTableTileEntity) tile);
            }
        } else if (id == CODEX) {
            return null;
        } else if (id == SOUL_ENCHANTER) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof SoulEnchanterTileEntity) {
                return new SoulEnchanterContainer(player.inventory, (SoulEnchanterTileEntity) tile);
            }
        } else if (id == WOODEN_BREWING_STAND) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof WoodenBrewingStandTileEntity) {
                return new WoodenBrewingStandContainer(player.inventory, (WoodenBrewingStandTileEntity) tile);
            }
        } else if (id == INCUBATOR) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof IncubatorTileEntity) {
                return new IncubatorContainer(player.inventory, (IncubatorTileEntity) tile);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == WORKTABLE) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof WorktableTileEntity) {
                return new WorktableGui(player.inventory, (WorktableTileEntity) tile);
            }
        } else if (id == RESEARCH_TABLE) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof ResearchTableTileEntity) {
                return new ResearchTableGui(player.inventory, (ResearchTableTileEntity) tile);
            }
        } else if (id == CODEX) {
            return new CodexGui(player);
        } else if (id == SOUL_ENCHANTER) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof SoulEnchanterTileEntity) {
                return new SoulEnchanterGui(player.inventory, (SoulEnchanterTileEntity) tile);
            }
        } else if (id == WOODEN_BREWING_STAND) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof WoodenBrewingStandTileEntity) {
                return new WoodenBrewingStandGui(player.inventory, (WoodenBrewingStandTileEntity) tile);
            }
        } else if (id == INCUBATOR) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile instanceof IncubatorTileEntity) {
                return new IncubatorGui(player.inventory, (IncubatorTileEntity) tile);
            }
        }
        return null;
    }
}
