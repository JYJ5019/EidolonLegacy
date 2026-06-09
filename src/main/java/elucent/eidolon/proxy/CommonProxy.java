package elucent.eidolon.proxy;

import elucent.eidolon.CommonConfig;
import elucent.eidolon.Eidolon;
import elucent.eidolon.diagnostics.RuntimeDiagnostics;
import elucent.eidolon.event.GameplayEvents;
import elucent.eidolon.gui.ModGuiHandler;
import elucent.eidolon.item.NotetakingToolsItem;
import elucent.eidolon.network.ModNetwork;
import elucent.eidolon.recipes.CrucibleRecipes;
import elucent.eidolon.recipes.IncubatorRecipes;
import elucent.eidolon.recipes.WorktableRecipes;
import elucent.eidolon.deity.Deities;
import elucent.eidolon.research.Researches;
import elucent.eidolon.registries.ModEntities;
import elucent.eidolon.registries.ModPotions;
import elucent.eidolon.registries.ModRecipes;
import elucent.eidolon.registries.ModOreDictionary;
import elucent.eidolon.registries.ModTileEntities;
import elucent.eidolon.spell.AltarEntries;
import elucent.eidolon.spell.AltarRituals;
import elucent.eidolon.spell.Runes;
import elucent.eidolon.spell.Spells;
import elucent.eidolon.world.ModWorldGen;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy implements IProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CommonConfig.init(event.getSuggestedConfigurationFile());
        ModEntities.init();
        ModTileEntities.init();
        ModNetwork.init();
        ModWorldGen.init();
        Researches.init();
        Deities.init();
        Runes.init();
        Spells.init();
        MinecraftForge.EVENT_BUS.register(NotetakingToolsItem.class);
        MinecraftForge.EVENT_BUS.register(new GameplayEvents());
        NetworkRegistry.INSTANCE.registerGuiHandler(Eidolon.instance, new ModGuiHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModOreDictionary.init();
        ModRecipes.init();
        WorktableRecipes.init();
        CrucibleRecipes.init();
        IncubatorRecipes.init();
        AltarEntries.init();
        AltarRituals.init();
        ModPotions.addBrewingRecipes();
        RuntimeDiagnostics.runIfRequested(null);
    }
}
