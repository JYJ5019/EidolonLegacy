package elucent.eidolon.compat.jei;

import elucent.eidolon.Eidolon;
import elucent.eidolon.item.AthameItem;
import elucent.eidolon.recipes.IncubatorRecipe;
import elucent.eidolon.recipes.IncubatorRecipes;
import elucent.eidolon.recipes.CrucibleRecipe;
import elucent.eidolon.recipes.CrucibleRecipes;
import elucent.eidolon.recipes.MachineInfoRecipe;
import elucent.eidolon.recipes.WorktableRecipe;
import elucent.eidolon.recipes.WorktableRecipes;
import elucent.eidolon.registries.ModBlocks;
import elucent.eidolon.spell.AltarRitual;
import elucent.eidolon.spell.AltarRituals;
import elucent.eidolon.gui.IncubatorContainer;
import elucent.eidolon.gui.WorktableContainer;
import elucent.eidolon.gui.WorktableGui;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.BlankModPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class EidolonJeiPlugin extends BlankModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(
                new WorktableRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new CrucibleRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new AltarRitualCategory(registry.getJeiHelpers().getGuiHelper()),
                new AthameHarvestCategory(registry.getJeiHelpers().getGuiHelper()),
                new SoulShardHarvestCategory(registry.getJeiHelpers().getGuiHelper()),
                new MachineInfoCategory(registry.getJeiHelpers().getGuiHelper()),
                new IncubatorRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
                new ChantRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(WorktableRecipe.class, WorktableRecipeWrapper::new, WorktableRecipeCategory.UID);
        registry.handleRecipes(CrucibleRecipe.class, CrucibleRecipeWrapper::new, CrucibleRecipeCategory.UID);
        registry.handleRecipes(AltarRitual.class, AltarRitualWrapper::new, AltarRitualCategory.UID);
        registry.handleRecipes(AthameItem.HarvestEntry.class, AthameHarvestWrapper::new, AthameHarvestCategory.UID);
        registry.handleRecipes(SoulShardHarvestRecipe.class, SoulShardHarvestWrapper::new, SoulShardHarvestCategory.UID);
        registry.handleRecipes(MachineInfoRecipe.class, MachineInfoWrapper::new, MachineInfoCategory.UID);
        registry.handleRecipes(IncubatorRecipe.class, IncubatorRecipeWrapper::new, IncubatorRecipeCategory.UID);
        registry.handleRecipes(ChantRecipeWrapper.class, recipe -> recipe, ChantRecipeCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Worktable recipes with HEI", WorktableRecipes.getRecipes().size());
        registry.addRecipes(WorktableRecipes.getRecipes(), WorktableRecipeCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Crucible recipes with HEI", CrucibleRecipes.getRecipes().size());
        registry.addRecipes(CrucibleRecipes.getRecipes(), CrucibleRecipeCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Altar Ritual recipes with HEI", AltarRituals.getRituals().size());
        registry.addRecipes(AltarRituals.getRituals(), AltarRitualCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Athame harvest recipes with HEI", AthameItem.getHarvestEntries().size());
        registry.addRecipes(AthameItem.getHarvestEntries(), AthameHarvestCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Soul Shard harvest recipes with HEI", SoulShardHarvestRecipe.getRecipes().size());
        registry.addRecipes(SoulShardHarvestRecipe.getRecipes(), SoulShardHarvestCategory.UID);
        registry.addRecipes(MachineInfoRecipe.getRecipes(), MachineInfoCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Incubator recipes with HEI", IncubatorRecipes.getRecipes().size());
        registry.addRecipes(IncubatorRecipes.getRecipes(), IncubatorRecipeCategory.UID);
        Eidolon.LOGGER.info("Registering {} Eidolon Chant recipes with HEI", ChantRecipeWrapper.getRecipes().size());
        registry.addRecipes(ChantRecipeWrapper.getRecipes(), ChantRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.WORKTABLE), WorktableRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.CRUCIBLE), CrucibleRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BRAZIER), AltarRitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.STONE_HAND), AltarRitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.NECROTIC_FOCUS), AltarRitualCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.SOUL_ENCHANTER), MachineInfoCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.WOODEN_BREWING_STAND), MachineInfoCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.INCUBATOR), IncubatorRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(elucent.eidolon.registries.ModItems.CODEX), ChantRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(elucent.eidolon.registries.ModItems.PARCHMENT), ChantRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.STRAW_EFFIGY), ChantRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(elucent.eidolon.registries.ModItems.ATHAME), AthameHarvestCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(elucent.eidolon.registries.ModItems.REAPER_SCYTHE), SoulShardHarvestCategory.UID);
        registry.addRecipeClickArea(WorktableGui.class, 129, 58, 24, 17, WorktableRecipeCategory.UID);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(WorktableContainer.class,
                WorktableRecipeCategory.UID, 1, 13, 14, 36);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(IncubatorContainer.class,
                IncubatorRecipeCategory.UID, 0, 2, 3, 36);
    }
}
