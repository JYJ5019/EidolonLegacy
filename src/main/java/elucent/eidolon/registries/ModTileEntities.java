package elucent.eidolon.registries;

import elucent.eidolon.Reference;
import elucent.eidolon.tile.AltarTileEntity;
import elucent.eidolon.tile.BrazierTileEntity;
import elucent.eidolon.tile.CisternTileEntity;
import elucent.eidolon.tile.CrucibleTileEntity;
import elucent.eidolon.tile.EffigyTileEntity;
import elucent.eidolon.tile.GlassTubeTileEntity;
import elucent.eidolon.tile.GobletTileEntity;
import elucent.eidolon.tile.IncubatorTileEntity;
import elucent.eidolon.tile.NecroticFocusTileEntity;
import elucent.eidolon.tile.OffertoryPlateTileEntity;
import elucent.eidolon.tile.ResearchTableTileEntity;
import elucent.eidolon.tile.SoulEnchanterTileEntity;
import elucent.eidolon.tile.StoneHandTileEntity;
import elucent.eidolon.tile.WoodenBrewingStandTileEntity;
import elucent.eidolon.tile.WorktableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModTileEntities {
    private ModTileEntities() {
    }

    public static void init() {
        GameRegistry.registerTileEntity(WorktableTileEntity.class, new ResourceLocation(Reference.MOD_ID, "worktable"));
        GameRegistry.registerTileEntity(CrucibleTileEntity.class, new ResourceLocation(Reference.MOD_ID, "crucible"));
        GameRegistry.registerTileEntity(ResearchTableTileEntity.class, new ResourceLocation(Reference.MOD_ID, "research_table"));
        GameRegistry.registerTileEntity(AltarTileEntity.class, new ResourceLocation(Reference.MOD_ID, "altar"));
        GameRegistry.registerTileEntity(GlassTubeTileEntity.class, new ResourceLocation(Reference.MOD_ID, "glass_tube"));
        GameRegistry.registerTileEntity(CisternTileEntity.class, new ResourceLocation(Reference.MOD_ID, "cistern"));
        GameRegistry.registerTileEntity(BrazierTileEntity.class, new ResourceLocation(Reference.MOD_ID, "brazier"));
        GameRegistry.registerTileEntity(StoneHandTileEntity.class, new ResourceLocation(Reference.MOD_ID, "stone_hand"));
        GameRegistry.registerTileEntity(NecroticFocusTileEntity.class, new ResourceLocation(Reference.MOD_ID, "necrotic_focus"));
        GameRegistry.registerTileEntity(OffertoryPlateTileEntity.class, new ResourceLocation(Reference.MOD_ID, "offertory_plate"));
        GameRegistry.registerTileEntity(SoulEnchanterTileEntity.class, new ResourceLocation(Reference.MOD_ID, "soul_enchanter"));
        GameRegistry.registerTileEntity(WoodenBrewingStandTileEntity.class, new ResourceLocation(Reference.MOD_ID, "wooden_brewing_stand"));
        GameRegistry.registerTileEntity(IncubatorTileEntity.class, new ResourceLocation(Reference.MOD_ID, "incubator"));
        GameRegistry.registerTileEntity(EffigyTileEntity.class, new ResourceLocation(Reference.MOD_ID, "effigy"));
        GameRegistry.registerTileEntity(GobletTileEntity.class, new ResourceLocation(Reference.MOD_ID, "goblet"));
    }
}
