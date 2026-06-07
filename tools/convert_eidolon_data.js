#!/usr/bin/env node
/* eslint-env node */

const fs = require("fs");
const path = require("path");

const legacyRoot = path.resolve(__dirname, "..");
const sourceRoot = path.resolve(legacyRoot, "..", "eidolon-1.20x", "src", "main", "resources", "data", "eidolon");
const assetsRoot = path.join(legacyRoot, "src", "main", "resources", "assets", "eidolon");
const docsRoot = path.join(legacyRoot, "docs");

const tagToOre = {
  "forge:bones": "bone",
  "forge:dusts/lead": "dustLead",
  "forge:dusts/redstone": "dustRedstone",
  "forge:dusts/silver": "dustSilver",
  "forge:dusts/sulfur": "dustSulfur",
  "forge:dyes/black": "dyeBlack",
  "forge:dyes/blue": "dyeBlue",
  "forge:dyes/red": "dyeRed",
  "forge:ender_pearls": "enderpearl",
  "forge:feathers": "feather",
  "forge:gems/diamond": "gemDiamond",
  "forge:gems/quartz": "gemQuartz",
  "forge:gems/shadow_gem": "gemShadow",
  "forge:ingots/arcane_gold": "ingotArcaneGold",
  "forge:ingots/gold": "ingotGold",
  "forge:ingots/iron": "ingotIron",
  "forge:ingots/lead": "ingotLead",
  "forge:ingots/pewter": "ingotPewter",
  "forge:ingots/silver": "ingotSilver",
  "forge:mushrooms": "cropMushroom",
  "forge:nuggets/arcane_gold": "nuggetArcaneGold",
  "forge:nuggets/gold": "nuggetGold",
  "forge:nuggets/lead": "nuggetLead",
  "forge:nuggets/pewter": "nuggetPewter",
  "forge:nuggets/silver": "nuggetSilver",
  "forge:ores/lead": "oreLead",
  "forge:ores/silver": "oreSilver",
  "forge:rods/wooden": "stickWood",
  "forge:storage_blocks/arcane_gold": "blockArcaneGold",
  "forge:storage_blocks/coal": "blockCoal",
  "forge:storage_blocks/diamond": "blockDiamond",
  "forge:storage_blocks/lapis": "blockLapis",
  "forge:storage_blocks/lead": "blockLead",
  "forge:storage_blocks/pewter": "blockPewter",
  "forge:storage_blocks/shadow_gem": "blockShadowGem",
  "forge:storage_blocks/silver": "blockSilver",
  "forge:string": "string",
  "forge:tallow": "tallow",
  "minecraft:planks": "plankWood",
  "minecraft:wooden_slabs": "slabWood"
};

const itemMap = {
  "minecraft:bone_meal": { item: "minecraft:dye", data: 15 },
  "minecraft:charcoal": { item: "minecraft:coal", data: 1 },
  "minecraft:chiseled_stone_bricks": { item: "minecraft:stonebrick", data: 3 },
  "minecraft:crimson_fungus": { item: "minecraft:red_mushroom" },
  "minecraft:crimson_roots": { item: "minecraft:nether_wart" },
  "minecraft:crying_obsidian": { item: "minecraft:obsidian" },
  "minecraft:glistering_melon_slice": { item: "minecraft:speckled_melon" },
  "minecraft:glow_berries": { item: "minecraft:speckled_melon" },
  "minecraft:glow_ink_sac": { item: "minecraft:glowstone_dust" },
  "minecraft:gray_wool": { item: "minecraft:wool", data: 7 },
  "minecraft:lapis_lazuli": { item: "minecraft:dye", data: 4 },
  "minecraft:melon_slice": { item: "minecraft:melon" },
  "minecraft:purple_carpet": { item: "minecraft:carpet", data: 10 },
  "minecraft:red_carpet": { item: "minecraft:carpet", data: 14 },
  "minecraft:skeleton_skull": { item: "minecraft:skull", data: 0 },
  "minecraft:smooth_stone": { item: "minecraft:stone", data: 0 },
  "minecraft:smooth_stone_slab": { item: "minecraft:stone_slab", data: 0 },
  "minecraft:stone": { item: "minecraft:stone", data: 0 },
  "minecraft:stone_slab": { item: "minecraft:stone_slab", data: 0 },
  "minecraft:warped_fungus": { item: "minecraft:brown_mushroom" },
  "minecraft:weeping_vines": { item: "minecraft:vine" },
  "minecraft:white_wool": { item: "minecraft:wool", data: 0 },
  "minecraft:wither_skeleton_skull": { item: "minecraft:skull", data: 1 }
};

const removedModernItems = new Set([]);

const coveredCraftingRecipes = {
  "arcane_gold_ingot.json": "arcane_gold_ingot_from_nuggets.json",
  "decompress_arcane_gold_block.json": "arcane_gold_ingot_from_block.json",
  "decompress_arcane_gold_ingot.json": "arcane_gold_nugget.json",
  "decompress_lead_block.json": "lead_ingot_from_block.json",
  "decompress_lead_ingot.json": "lead_nugget.json",
  "decompress_pewter_block.json": "pewter_ingot_from_block.json",
  "decompress_pewter_ingot.json": "pewter_nugget.json",
  "decompress_raw_lead_block.json": "raw_lead.json",
  "decompress_raw_silver_block.json": "raw_silver.json",
  "decompress_shadow_gem_block.json": "shadow_gem.json",
  "decompress_silver_block.json": "silver_ingot_from_block.json",
  "decompress_silver_ingot.json": "silver_nugget.json",
  "lead_ingot.json": "lead_ingot_from_nuggets.json",
  "pewter_ingot.json": "pewter_ingot_from_nuggets.json",
  "silver_ingot.json": "silver_ingot_from_nuggets.json"
};

const vanilla112Items = new Set([
  "minecraft:apple",
  "minecraft:arrow",
  "minecraft:beef",
  "minecraft:blaze_powder",
  "minecraft:bone",
  "minecraft:book",
  "minecraft:carrot",
  "minecraft:chainmail_boots",
  "minecraft:chainmail_chestplate",
  "minecraft:chainmail_helmet",
  "minecraft:chainmail_leggings",
  "minecraft:chicken",
  "minecraft:coal",
  "minecraft:diamond",
  "minecraft:dirt",
  "minecraft:dye",
  "minecraft:feather",
  "minecraft:flint",
  "minecraft:ghast_tear",
  "minecraft:glass",
  "minecraft:glass_bottle",
  "minecraft:glowstone_dust",
  "minecraft:gold_ingot",
  "minecraft:gold_nugget",
  "minecraft:golden_apple",
  "minecraft:golden_carrot",
  "minecraft:golden_chestplate",
  "minecraft:golden_helmet",
  "minecraft:golden_sword",
  "minecraft:gunpowder",
  "minecraft:iron_chestplate",
  "minecraft:iron_ingot",
  "minecraft:leather",
  "minecraft:melon",
  "minecraft:mutton",
  "minecraft:nether_wart",
  "minecraft:obsidian",
  "minecraft:paper",
  "minecraft:porkchop",
  "minecraft:rabbit",
  "minecraft:rotten_flesh",
  "minecraft:skull",
  "minecraft:slime_ball",
  "minecraft:soul_sand",
  "minecraft:speckled_melon",
  "minecraft:stick",
  "minecraft:string",
  "minecraft:vine",
  "minecraft:wheat",
  "minecraft:wheat_seeds"
]);

const vanilla112BlocksAsItems = new Set([
  "minecraft:bone_block",
  "minecraft:carpet",
  "minecraft:stone",
  "minecraft:stone_slab",
  "minecraft:stonebrick",
  "minecraft:wool"
]);

const report = {
  source: path.relative(legacyRoot, sourceRoot).replace(/\\/g, "/"),
  generatedAt: new Date().toISOString(),
  converted: {
    crafting: [],
    worktable: [],
    crucible: [],
    lootBlocks: [],
    lootChests: [],
    lootEntities: []
  },
  codeBacked: {
    smelting: [],
    conditionalOreSmelting: [],
    coveredCrafting: [],
    entityLootWiring: [],
    tags: [],
    worldgen: []
  },
  skipped: [],
  warnings: []
};

function main() {
  assertDirectory(sourceRoot);
  ensureDirectory(docsRoot);

  convertRecipes();
  convertLootTables();
  analyzeEntityLootWiring();
  analyzeTags();
  analyzeWorldgen();
  writeReports();

  const totalConverted = Object.values(report.converted)
    .reduce((sum, list) => sum + list.length, 0);
  console.log(`Converted ${totalConverted} data files; skipped ${report.skipped.length}.`);
  console.log("Report: docs/data-conversion-report.json");
}

function convertRecipes() {
  const recipeRoot = path.join(sourceRoot, "recipes");
  const files = listJsonFiles(recipeRoot);
  for (const file of files) {
    const source = readJson(path.join(recipeRoot, file));
    const name = stripJson(file);
    try {
      if ((source.type === "minecraft:crafting_shaped" || source.type === "minecraft:crafting_shapeless")
          && coveredCraftingRecipes[file]) {
        deleteConverted("recipes", file);
        report.codeBacked.coveredCrafting.push({
          file,
          coveredBy: `assets/eidolon/recipes/${coveredCraftingRecipes[file]}`,
          reason: "Source recipe is already represented by an existing Legacy 1.12 recipe with equivalent input/output."
        });
        continue;
      }
      switch (source.type) {
        case "minecraft:crafting_shaped":
          writeConverted("recipes", file, convertCrafting(source, true));
          report.converted.crafting.push({ file, type: source.type, target: `assets/eidolon/recipes/${file}` });
          break;
        case "minecraft:crafting_shapeless":
          writeConverted("recipes", file, convertCrafting(source, false));
          report.converted.crafting.push({ file, type: source.type, target: `assets/eidolon/recipes/${file}` });
          break;
        case "eidolon:worktable":
          writeConverted("worktable_recipes", file, convertSpecialRecipe(source));
          report.converted.worktable.push({ file, type: source.type, target: `assets/eidolon/worktable_recipes/${file}` });
          break;
        case "eidolon:crucible":
          writeConverted("crucible_recipes", file, convertSpecialRecipe(source));
          report.converted.crucible.push({ file, type: source.type, target: `assets/eidolon/crucible_recipes/${file}` });
          break;
        case "minecraft:smelting":
          report.codeBacked.smelting.push({
            file,
            reason: "1.12 smelting is registered through ModRecipes/GameRegistry instead of JSON.",
            summary: summarizeSmelting(source)
          });
          break;
        case "minecraft:blasting":
          if (file === "tallow.json") {
            report.codeBacked.smelting.push({
              file,
              reason: "1.12 has no blasting station; this one is registered as a furnace fallback in ModRecipes.",
              summary: summarizeSmelting(source)
            });
            break;
          }
          skip(file, source.type, "1.12 has no blasting station; equivalent ore/pewter smelting is handled by ModRecipes.");
          break;
        case "minecraft:stonecutting":
          skip(file, source.type, "1.12 has no stonecutter recipe type; existing shaped slab/stair/wall recipes cover the craftable outputs.");
          break;
        case "forge:conditional":
          analyzeConditionalRecipe(file, source);
          break;
        default:
          skip(file, source.type || "unknown", "Unsupported recipe type.");
          break;
      }
    } catch (error) {
      skip(file, source.type || "unknown", error.message);
    }
    void name;
  }
}

function convertCrafting(source, shaped) {
  const converted = {
    type: source.type
  };
  if (source.group) {
    converted.group = source.group;
  }
  if (shaped) {
    converted.pattern = source.pattern;
    converted.key = {};
    for (const [symbol, ingredient] of Object.entries(source.key || {})) {
      converted.key[symbol] = convertIngredientForForgeRecipe(ingredient);
    }
  } else {
    converted.ingredients = (source.ingredients || []).map(convertIngredientForForgeRecipe);
  }
  converted.result = convertResult(source.result);
  return converted;
}

function convertSpecialRecipe(source) {
  const converted = deepMap(source, value => {
    if (value && typeof value === "object" && !Array.isArray(value)) {
      if (typeof value.item === "string") {
        return convertItemObject(value);
      }
      if (typeof value.result === "string") {
        return { ...value, result: convertItemId(value.result).item };
      }
    }
    return value;
  });
  collectUnsupportedItems(converted);
  return converted;
}

function convertIngredientForForgeRecipe(ingredient) {
  if (Array.isArray(ingredient)) {
    throw new Error("Compound ingredient arrays need a custom loader in 1.12 and were not generated.");
  }
  if (!ingredient || typeof ingredient !== "object") {
    throw new Error("Invalid crafting ingredient.");
  }
  if (ingredient.tag) {
    const ore = tagToOre[ingredient.tag];
    if (!ore) {
      throw new Error(`No OreDictionary mapping for tag ${ingredient.tag}.`);
    }
    return { type: "forge:ore_dict", ore };
  }
  if (ingredient.item) {
    return convertItemObject(ingredient);
  }
  throw new Error(`Unsupported ingredient shape: ${JSON.stringify(ingredient)}`);
}

function convertResult(result) {
  if (typeof result === "string") {
    return convertItemId(result);
  }
  if (result && typeof result === "object" && result.item) {
    return convertItemObject(result);
  }
  throw new Error(`Unsupported result shape: ${JSON.stringify(result)}`);
}

function convertItemObject(object) {
  const mapped = convertItemId(object.item);
  const converted = { ...object, item: mapped.item };
  delete converted.tag;
  if (mapped.data !== undefined && converted.data === undefined) {
    converted.data = mapped.data;
  }
  if (converted.count !== undefined) {
    converted.count = normalizeNumber(converted.count);
  }
  validateItemId(converted.item);
  return converted;
}

function convertItemId(id) {
  if (itemMap[id]) {
    return { ...itemMap[id] };
  }
  validateItemId(id);
  return { item: id };
}

function validateItemId(id) {
  if (removedModernItems.has(id)) {
    throw new Error(`${id} does not exist in Minecraft 1.12; needs a replacement item or custom recipe branch.`);
  }
  if (id.startsWith("eidolon:")) {
    if (!legacyIdExists(id.slice("eidolon:".length))) {
      throw new Error(`${id} is not present in the current Legacy registry/assets.`);
    }
    return;
  }
  if (id.startsWith("minecraft:")) {
    if (!vanilla112Items.has(id) && !vanilla112BlocksAsItems.has(id)) {
      throw new Error(`${id} is not in the conservative Minecraft 1.12 item map.`);
    }
  }
}

function collectUnsupportedItems(object) {
  const unsupported = [];
  walkJson(object, value => {
    if (value && typeof value === "object" && typeof value.item === "string") {
      try {
        validateItemId(value.item);
      } catch (error) {
        unsupported.push(error.message);
      }
    }
    if (value && typeof value === "object" && typeof value.tag === "string" && !tagToOre[value.tag]) {
      unsupported.push(`No OreDictionary mapping for tag ${value.tag}.`);
    }
  });
  if (unsupported.length > 0) {
    throw new Error([...new Set(unsupported)].join("; "));
  }
}

function analyzeConditionalRecipe(file, source) {
  const recipes = Array.isArray(source.recipes) ? source.recipes : [];
  for (const entry of recipes) {
    const recipe = entry.recipe || {};
    if (recipe.type === "minecraft:smelting" && recipe.ingredient && recipe.ingredient.tag) {
      const ore = tagToOre[recipe.ingredient.tag];
      if (ore) {
        report.codeBacked.conditionalOreSmelting.push({
          file,
          tag: recipe.ingredient.tag,
          ore,
          result: recipe.result,
          reason: "Converted to ModRecipes OreDictionary smelting registration."
        });
        return;
      }
    }
  }
  skip(file, source.type, "Forge conditional recipes have no equivalent JSON condition system in 1.12.");
}

function summarizeSmelting(source) {
  return {
    ingredient: source.ingredient || null,
    result: source.result || null,
    experience: source.experience || 0
  };
}

function convertLootTables() {
  const lootRoot = path.join(sourceRoot, "loot_tables");
  for (const category of ["blocks", "chests", "entities"]) {
    const dir = path.join(lootRoot, category);
    if (!fs.existsSync(dir)) {
      continue;
    }
    for (const file of listJsonFiles(dir)) {
      const source = readJson(path.join(dir, file));
      try {
        const converted = convertLootTable(source, category, file);
        if (!converted) {
          continue;
        }
        writeConverted(path.join("loot_tables", category), file, converted);
        const key = category === "blocks" ? "lootBlocks" : category === "chests" ? "lootChests" : "lootEntities";
        report.converted[key].push({ file, target: `assets/eidolon/loot_tables/${category}/${file}` });
      } catch (error) {
        skip(`loot_tables/${category}/${file}`, `loot:${category}`, error.message);
      }
    }
  }
}

function analyzeEntityLootWiring() {
  const entityFiles = ["raven.json", "slimy_slug.json", "wraith.json", "zombie_brute.json"];
  report.codeBacked.entityLootWiring.push({
    files: entityFiles.map(file => `loot_tables/entities/${file}`),
    reason: "1.12 entities must return a ResourceLocation from getLootTable(); the Legacy entity classes now point to the generated tables."
  });
}

function convertLootTable(source, category, file) {
  if (category === "blocks" && hasComplexBlockLoot(source)) {
    const simple = simplifyBlockLoot(source, file);
    if (!simple) {
      skip(`loot_tables/blocks/${file}`, "loot:block", "Modern block loot conditions/functions could not be represented safely in 1.12.");
      return null;
    }
    report.warnings.push({
      file: `loot_tables/blocks/${file}`,
      warning: "Simplified modern block loot; silk touch, fortune, explosion decay, and block-state conditions are not represented in 1.12 JSON."
    });
    return simple;
  }

  const converted = deepMap(source, value => {
    if (typeof value === "string") {
      return stripMinecraftPrefix(value);
    }
    return value;
  });
  delete converted.type;
  removeUnsupportedLootKeys(converted);
  normalizeLootEntryCounts(converted);
  normalizeLootNumbers(converted);
  collectLootItems(converted);
  return converted;
}

function hasComplexBlockLoot(source) {
  let complex = false;
  walkJson(source, value => {
    if (!value || typeof value !== "object") {
      return;
    }
    if (value.type === "minecraft:alternatives" || value.condition === "minecraft:block_state_property" ||
        value.condition === "minecraft:match_tool" || value.function === "minecraft:apply_bonus" ||
        value.function === "minecraft:explosion_decay") {
      complex = true;
    }
  });
  return complex;
}

function simplifyBlockLoot(source, file) {
  const candidates = [];
  walkJson(source, value => {
    if (value && typeof value === "object" && value.type === "minecraft:item" && typeof value.name === "string") {
      if (!hasCondition(value, "minecraft:match_tool") && !hasCondition(value, "minecraft:block_state_property")) {
        candidates.push(stripMinecraftPrefix(value.name));
      }
    }
  });
  const name = candidates[0] || `eidolon:${stripJson(file)}`;
  validateItemId(name);
  return {
    pools: [
      {
        rolls: 1,
        entries: [
          {
            type: "item",
            name
          }
        ]
      }
    ]
  };
}

function hasCondition(value, condition) {
  return Array.isArray(value.conditions) && value.conditions.some(entry => entry.condition === condition);
}

function removeUnsupportedLootKeys(object) {
  walkJson(object, value => {
    if (value && typeof value === "object" && !Array.isArray(value)) {
      delete value.bonus_rolls;
      if (Array.isArray(value.conditions)) {
        const before = value.conditions.length;
        value.conditions = value.conditions.filter(condition => condition.condition !== "survives_explosion");
        if (value.conditions.length === 0) {
          delete value.conditions;
        }
        if (before !== (value.conditions ? value.conditions.length : 0)) {
          value.__removedSurvivesExplosion = true;
        }
      }
      if (value.count && typeof value.count === "object") {
        delete value.count.type;
      }
      if (value.rolls && typeof value.rolls === "object") {
        delete value.rolls.type;
      }
      if (value.type === "alternatives") {
        throw new Error("Loot alternatives need custom handling in 1.12.");
      }
      if (value.function === "apply_bonus" || value.function === "explosion_decay") {
        throw new Error(`Loot function ${value.function} has no safe 1.12 equivalent in generated JSON.`);
      }
      if (value.condition === "block_state_property" || value.condition === "match_tool") {
        throw new Error(`Loot condition ${value.condition} has no safe 1.12 equivalent in generated JSON.`);
      }
      delete value.__removedSurvivesExplosion;
    }
  });
}

function normalizeLootEntryCounts(object) {
  walkJson(object, value => {
    if (!value || typeof value !== "object" || Array.isArray(value)) {
      return;
    }
    if (value.type === "item" && value.name && value.count !== undefined) {
      const count = value.count;
      delete value.count;
      if (!Array.isArray(value.functions)) {
        value.functions = [];
      }
      value.functions.unshift({
        function: "set_count",
        count
      });
    }
  });
}

function normalizeLootNumbers(object) {
  walkJson(object, value => {
    if (!value || typeof value !== "object" || Array.isArray(value)) {
      return;
    }
    for (const key of ["rolls", "min", "max", "chance", "looting_multiplier", "weight"]) {
      if (value[key] !== undefined) {
        value[key] = normalizeNumber(value[key]);
      }
    }
  });
}

function collectLootItems(object) {
  walkJson(object, value => {
    if (value && typeof value === "object" && typeof value.name === "string") {
      validateItemId(value.name);
    }
  });
}

function stripMinecraftPrefix(value) {
  if (value.startsWith("minecraft:")) {
    const suffix = value.slice("minecraft:".length);
    const lootNames = new Set([
      "alternatives",
      "block",
      "chest",
      "entity",
      "item",
      "uniform",
      "set_count",
      "enchant_randomly",
      "looting_enchant",
      "killed_by_player",
      "random_chance",
      "random_chance_with_looting",
      "survives_explosion"
    ]);
    if (lootNames.has(suffix)) {
      return suffix;
    }
  }
  if (itemMap[value]) {
    return itemMap[value].item;
  }
  return value;
}

function analyzeTags() {
  const tagRoot = path.join(sourceRoot, "tags");
  for (const file of listJsonFiles(tagRoot)) {
    const source = readJson(path.join(tagRoot, file));
    if (file === path.join("items", "zombie_food.json")) {
      report.codeBacked.tags.push({
        file: `tags/${file.replace(/\\/g, "/")}`,
        reason: "No runtime tag loader exists in 1.12; values were analyzed for potential code hooks.",
        values: source.values || [],
        status: "No code usage found in Legacy during conversion, so no custom loader was added."
      });
    } else {
      report.codeBacked.tags.push({
        file: `tags/${file.replace(/\\/g, "/")}`,
        reason: "Worldgen biome tags are represented by ModWorldGen biome/structure code in 1.12."
      });
    }
  }
}

function analyzeWorldgen() {
  const worldgenRoot = path.join(sourceRoot, "worldgen");
  const files = listJsonFiles(worldgenRoot);
  const entry = {
    files: files.map(file => `worldgen/${file.replace(/\\/g, "/")}`),
    reason: "1.12 has no datapack worldgen loader; values are carried by elucent.eidolon.world.EidolonWorldGenerator/EidolonStructureGenerator.",
    mappedToCode: []
  };
  for (const file of files) {
    const source = readJson(path.join(worldgenRoot, file));
    if (file === path.join("placed_feature", "lead_ore.json") || file === path.join("placed_feature", "silver_ore.json")) {
      entry.mappedToCode.push({
        file: `worldgen/${file.replace(/\\/g, "/")}`,
        count: extractPlacementCount(source),
        height: extractPlacementHeight(source),
        note: "1.20 negative Y/deepslate split cannot be represented directly in 1.12; ore generation remains code-backed with normal/deep ranges."
      });
    }
    if (file === path.join("structure_set", "surface_structures.json")) {
      entry.mappedToCode.push({
        file: `worldgen/${file.replace(/\\/g, "/")}`,
        placement: source.placement,
        note: "Surface structure random-spread constants are mirrored in EidolonWorldGenerator."
      });
    }
  }
  report.codeBacked.worldgen.push(entry);
}

function extractPlacementCount(source) {
  const count = (source.placement || []).find(entry => entry.type === "minecraft:count");
  return count ? count.count : null;
}

function extractPlacementHeight(source) {
  const height = (source.placement || []).find(entry => entry.type === "minecraft:height_range");
  return height ? height.height : null;
}

function writeConverted(subdir, file, data) {
  const target = path.join(assetsRoot, subdir, file);
  ensureDirectory(path.dirname(target));
  writeJson(target, data);
}

function deleteConverted(subdir, file) {
  const target = path.join(assetsRoot, subdir, file);
  if (fs.existsSync(target)) {
    fs.unlinkSync(target);
  }
}

function writeReports() {
  ensureDirectory(docsRoot);
  writeJson(path.join(docsRoot, "data-conversion-report.json"), report);
  fs.writeFileSync(path.join(docsRoot, "data-conversion-report.md"), renderMarkdownReport(report), "utf8");
}

function renderMarkdownReport(data) {
  const lines = [];
  lines.push("# Eidolon Data Conversion Report");
  lines.push("");
  lines.push(`Source: \`${data.source}\``);
  lines.push(`Generated: \`${data.generatedAt}\``);
  lines.push("");
  lines.push("## Converted");
  for (const [key, list] of Object.entries(data.converted)) {
    lines.push(`- ${key}: ${list.length}`);
  }
  lines.push("");
  lines.push("## Code-backed");
  lines.push(`- smelting: ${data.codeBacked.smelting.length}`);
  lines.push(`- conditional ore smelting: ${data.codeBacked.conditionalOreSmelting.length}`);
  lines.push(`- covered crafting: ${data.codeBacked.coveredCrafting.length}`);
  lines.push(`- entity loot wiring: ${data.codeBacked.entityLootWiring.length}`);
  lines.push(`- tags: ${data.codeBacked.tags.length}`);
  lines.push(`- worldgen groups: ${data.codeBacked.worldgen.length}`);
  lines.push("");
  lines.push("## Skipped");
  if (data.skipped.length === 0) {
    lines.push("- none");
  } else {
    for (const item of data.skipped) {
      lines.push(`- ${item.file} (${item.type}): ${item.reason}`);
    }
  }
  lines.push("");
  lines.push("## Warnings");
  if (data.warnings.length === 0) {
    lines.push("- none");
  } else {
    for (const item of data.warnings) {
      lines.push(`- ${item.file}: ${item.warning}`);
    }
  }
  lines.push("");
  return lines.join("\n");
}

function listJsonFiles(root) {
  if (!fs.existsSync(root)) {
    return [];
  }
  const files = [];
  function walk(dir) {
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        walk(full);
      } else if (entry.isFile() && entry.name.endsWith(".json")) {
        files.push(path.relative(root, full));
      }
    }
  }
  walk(root);
  return files.sort();
}

function readJson(file) {
  return JSON.parse(fs.readFileSync(file, "utf8"));
}

function writeJson(file, data) {
  fs.writeFileSync(file, `${JSON.stringify(data, null, 2)}\n`, "utf8");
}

function ensureDirectory(dir) {
  fs.mkdirSync(dir, { recursive: true });
}

function assertDirectory(dir) {
  if (!fs.existsSync(dir) || !fs.statSync(dir).isDirectory()) {
    throw new Error(`Missing directory: ${dir}`);
  }
}

function stripJson(file) {
  return path.basename(file, ".json");
}

function normalizeNumber(value) {
  return typeof value === "number" && Number.isInteger(value) ? value : value;
}

function deepMap(value, mapper) {
  if (Array.isArray(value)) {
    return value.map(item => deepMap(item, mapper));
  }
  if (value && typeof value === "object") {
    const mapped = mapper(value);
    if (mapped !== value) {
      return mapped;
    }
    const copy = {};
    for (const [key, child] of Object.entries(value)) {
      copy[key] = deepMap(child, mapper);
    }
    return mapper(copy);
  }
  return mapper(value);
}

function walkJson(value, visitor) {
  visitor(value);
  if (Array.isArray(value)) {
    value.forEach(child => walkJson(child, visitor));
  } else if (value && typeof value === "object") {
    Object.values(value).forEach(child => walkJson(child, visitor));
  }
}

function skip(file, type, reason) {
  report.skipped.push({ file: file.replace(/\\/g, "/"), type, reason });
}

function legacyIdExists(id) {
  return fs.existsSync(path.join(assetsRoot, "models", "item", `${id}.json`)) ||
    fs.existsSync(path.join(assetsRoot, "models", "block", `${id}.json`)) ||
    sourceMentionsRegistryName(path.join(legacyRoot, "src", "main", "java", "elucent", "eidolon", "registries", "ModItems.java"), id) ||
    sourceMentionsRegistryName(path.join(legacyRoot, "src", "main", "java", "elucent", "eidolon", "registries", "ModBlocks.java"), id);
}

function sourceMentionsRegistryName(file, id) {
  if (!fs.existsSync(file)) {
    return false;
  }
  return fs.readFileSync(file, "utf8").includes(`"${id}"`);
}

main();
