#!/usr/bin/env node
/* eslint-env node */

const fs = require("fs");
const path = require("path");

const legacyRoot = path.resolve(__dirname, "..");
const projectRoot = path.resolve(legacyRoot, "..");
const sourceRoot = path.join(projectRoot, "eidolon-1.20x");
const sourceJava = path.join(sourceRoot, "src", "main", "java");
const sourceResources = path.join(sourceRoot, "src", "main", "resources");
const legacyJava = path.join(legacyRoot, "src", "main", "java");
const legacyResources = path.join(legacyRoot, "src", "main", "resources");
const sourceAssets = path.join(sourceResources, "assets", "eidolon");
const sourceData = path.join(sourceResources, "data", "eidolon");
const legacyAssets = path.join(legacyResources, "assets", "eidolon");
const docsRoot = path.join(legacyRoot, "docs");

const itemIdFallbacks = {
  "minecraft:bone_meal": "minecraft:dye",
  "minecraft:charcoal": "minecraft:coal",
  "minecraft:chiseled_stone_bricks": "minecraft:stonebrick",
  "minecraft:crimson_fungus": "minecraft:red_mushroom",
  "minecraft:crimson_roots": "minecraft:nether_wart",
  "minecraft:crying_obsidian": "minecraft:obsidian",
  "minecraft:glistering_melon_slice": "minecraft:speckled_melon",
  "minecraft:glow_berries": "minecraft:speckled_melon",
  "minecraft:glow_ink_sac": "minecraft:glowstone_dust",
  "minecraft:gray_wool": "minecraft:wool",
  "minecraft:lapis_lazuli": "minecraft:dye",
  "minecraft:melon_slice": "minecraft:melon",
  "minecraft:purple_carpet": "minecraft:carpet",
  "minecraft:red_carpet": "minecraft:carpet",
  "minecraft:skeleton_skull": "minecraft:skull",
  "minecraft:smooth_stone": "minecraft:stone",
  "minecraft:smooth_stone_slab": "minecraft:stone_slab",
  "minecraft:white_wool": "minecraft:wool",
  "minecraft:wither_skeleton_skull": "minecraft:skull",
  "minecraft:warped_fungus": "minecraft:brown_mushroom",
  "minecraft:weeping_vines": "minecraft:vine"
};

const report = {
  generatedAt: new Date().toISOString(),
  roots: {
    source: rel(sourceRoot),
    legacy: rel(legacyRoot)
  },
  summary: {},
  registries: {},
  recipes: {},
  loot: {},
  jei: {},
  language: {},
  resources: {},
  priorityFindings: []
};

function main() {
  assertDirectory(sourceRoot);
  assertDirectory(legacyRoot);
  ensureDirectory(docsRoot);

  const sourceRegistries = collectSourceRegistries();
  const legacyRegistries = collectLegacyRegistries();
  report.registries = compareRegistryGroups(sourceRegistries, legacyRegistries);

  report.resources = compareResourceCoverage(sourceRegistries, legacyRegistries);
  report.recipes = compareRecipeOutputs();
  report.loot = compareLootOutputs();
  report.jei = compareJeiCategories();
  report.language = compareLanguageKeys();
  report.priorityFindings = collectPriorityFindings();
  report.summary = summarizeReport();

  writeJson(path.join(docsRoot, "coverage-matrix.json"), report);
  fs.writeFileSync(path.join(docsRoot, "coverage-matrix.md"), renderMarkdown(report), "utf8");
  printConsoleSummary(report);
}

function collectSourceRegistries() {
  const registryJava = readText(path.join(sourceJava, "elucent", "eidolon", "Registry.java"));
  const entitiesJava = readText(path.join(sourceJava, "elucent", "eidolon", "registries", "Entities.java"));
  const potionsJava = readText(path.join(sourceJava, "elucent", "eidolon", "registries", "Potions.java"));
  const soundsJava = readText(path.join(sourceJava, "elucent", "eidolon", "registries", "Sounds.java"));

  return {
    items: unique(matches(registryJava, /addItem\("([^"]+)"/g)),
    blocks: unique(matches(registryJava, /addBlock\("([^"]+)"/g)),
    entities: unique(matches(entitiesJava, /addEntity\("([^"]+)"/g)),
    tileEntities: unique(matches(registryJava, /TILE_ENTITIES\.register\("([^"]+)"/g)),
    recipeSerializers: unique(matches(registryJava, /RECIPE_TYPES\.register\("([^"]+)"/g)),
    potions: unique(matches(potionsJava, /POTIONS\.register\("([^"]+)"/g)),
    potionTypes: unique(matches(potionsJava, /POTION_TYPES\.register\("([^"]+)"/g)),
    sounds: unique(matches(soundsJava, /addSound\("([^"]+)"/g))
  };
}

function collectLegacyRegistries() {
  const modItems = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModItems.java"));
  const modBlocks = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModBlocks.java"));
  const modEntities = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModEntities.java"));
  const modTiles = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModTileEntities.java"));
  const modPotions = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModPotions.java"));
  const modSounds = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModSounds.java"));
  const items = unique(publicRegistryNames(modItems, "Item"));

  return {
    items,
    blocks: unique(publicRegistryNames(modBlocks, "Block").filter(name => !name.startsWith("double_"))),
    allBlocks: unique(publicRegistryNames(modBlocks, "Block")),
    entities: unique([
      ...matches(modEntities, /register(?:Living)?\("([^"]+)"/g),
      ...items.filter(name => name.startsWith("spawn_")).map(name => name.slice("spawn_".length))
    ]),
    tileEntities: unique(matches(modTiles, /new ResourceLocation\(Reference\.MOD_ID,\s*"([^"]+)"/g)),
    recipeSerializers: unique(["crafting_json", "smelting_code", "worktable_json", "crucible_json", "altar_ritual_json", "incubator_json"]),
    potions: unique(publicRegistryNames(modPotions, "Potion")),
    potionTypes: unique(matches(modPotions, /potionType\("([^"]+)"/g)),
    sounds: unique(matches(modSounds, /sound\("([^"]+)"/g))
  };
}

function publicRegistryNames(text, typeName) {
  const names = [];
  const re = new RegExp(`public\\s+static\\s+final\\s+${typeName}\\s+[A-Z0-9_]+\\s*=\\s*([^;]+);`, "g");
  let match;
  while ((match = re.exec(text)) !== null) {
    const quoted = matches(match[1], /"([a-z0-9_]+)"/g);
    if (quoted.length > 0) {
      names.push(quoted[0]);
    }
  }
  return names;
}

function compareRegistryGroups(source, legacy) {
  const aliases = {
    tileEntities: {
      brazier_tile: "brazier",
      hand_tile: "stone_hand",
      pipe: "glass_tube"
    },
    recipeSerializers: {
      crucible: "crucible_json",
      worktable: "worktable_json"
    }
  };
  const groups = {};
  for (const key of Object.keys(source)) {
    const sourceList = source[key] || [];
    const legacyList = legacy[key] || [];
    groups[key] = compareSets(sourceList, legacyList, aliases[key] || {});
  }
  groups.legacyOnlyAllBlocks = legacy.allBlocks.filter(name => !(source.blocks || []).includes(name)).sort();
  return groups;
}

function compareResourceCoverage(sourceRegistries, legacyRegistries) {
  const sourceItemModels = jsonBaseNames(path.join(sourceAssets, "models", "item"));
  const legacyItemModels = jsonBaseNames(path.join(legacyAssets, "models", "item"));
  const sourceBlockstates = jsonBaseNames(path.join(sourceAssets, "blockstates"));
  const legacyBlockstates = jsonBaseNames(path.join(legacyAssets, "blockstates"));
  const sourceBlockModels = jsonBaseNames(path.join(sourceAssets, "models", "block"));
  const legacyBlockModels = jsonBaseNames(path.join(legacyAssets, "models", "block"));
  const registeredItems = legacyRegistries.items || [];
  const registeredBlocks = legacyRegistries.allBlocks || legacyRegistries.blocks || [];

  return {
    itemModels: compareSets(sourceItemModels, legacyItemModels),
    blockstates: compareSets(sourceBlockstates, legacyBlockstates),
    blockModels: compareSets(sourceBlockModels, legacyBlockModels),
    registeredItemsMissingItemModel: registeredItems.filter(name => !legacyItemModels.includes(name)).sort(),
    itemModelsWithoutRegisteredItemOrBlock: legacyItemModels
      .filter(name => !registeredItems.includes(name) && !registeredBlocks.includes(name))
      .sort(),
    registeredBlocksMissingBlockstate: registeredBlocks
      .filter(name => !name.startsWith("double_") && !legacyBlockstates.includes(name))
      .sort(),
    registeredBlocksMissingBlockModel: registeredBlocks
      .filter(name => !name.startsWith("double_") && blockHasMissingReferencedModels(name, legacyBlockModels))
      .sort()
  };
}

function compareRecipeOutputs() {
  const sourceRecipes = collectRecipeOutputs(path.join(sourceData, "recipes"), "source");
  const legacyRecipeDirs = [
    path.join(legacyAssets, "recipes"),
    path.join(legacyAssets, "worktable_recipes"),
    path.join(legacyAssets, "crucible_recipes"),
    path.join(legacyAssets, "altar_rituals"),
    path.join(legacyAssets, "incubator_recipes")
  ];
  const legacyRecipes = [];
  for (const dir of legacyRecipeDirs) {
    legacyRecipes.push(...collectRecipeOutputs(dir, "legacy"));
  }
  legacyRecipes.push(...collectCodeBackedRecipeOutputs());

  const sourceOutputs = unique(sourceRecipes.flatMap(recipe => recipe.outputs).map(normalizeItemId));
  const legacyOutputs = unique(legacyRecipes.flatMap(recipe => recipe.outputs).map(normalizeItemId));
  const sourceEidolonOutputs = sourceOutputs.filter(id => id.startsWith("eidolon:"));
  const legacyEidolonOutputs = legacyOutputs.filter(id => id.startsWith("eidolon:"));

  return {
    sourceRecipeCount: sourceRecipes.length,
    legacyRecipeCount: legacyRecipes.length,
    outputs: compareSets(sourceOutputs, legacyOutputs),
    eidolonOutputs: compareSets(sourceEidolonOutputs, legacyEidolonOutputs),
    sourceRecipesMissingOutputInLegacy: sourceRecipes
      .filter(recipe => recipe.outputs.some(id => !legacyOutputs.includes(normalizeItemId(id))))
      .map(recipe => ({ file: recipe.file, type: recipe.type, outputs: recipe.outputs.map(normalizeItemId) }))
      .sort((a, b) => a.file.localeCompare(b.file)),
    sourceRecipes,
    legacyRecipes
  };
}

function collectRecipeOutputs(root, side) {
  const recipes = [];
  for (const file of listJsonFiles(root)) {
    const full = path.join(root, file);
    const json = safeReadJson(full);
    if (!json) {
      continue;
    }
    const outputs = unique(extractRecipeOutputs(json).map(normalizeItemId)).filter(Boolean);
    recipes.push({
      side,
      file: path.relative(side === "source" ? sourceResources : legacyResources, full).replace(/\\/g, "/"),
      type: json.type || inferRecipeTypeFromPath(full),
      outputs
    });
  }
  return recipes;
}

function extractRecipeOutputs(json) {
  const outputs = [];
  if (typeof json.result === "string") {
    outputs.push(json.result);
  } else if (json.result && typeof json.result === "object") {
    if (typeof json.result.item === "string") {
      outputs.push(json.result.item);
    }
    if (typeof json.result.result === "string") {
      outputs.push(json.result.result);
    }
  }
  if (typeof json.output === "string") {
    outputs.push(json.output);
  } else if (json.output && typeof json.output === "object" && typeof json.output.item === "string") {
    outputs.push(json.output.item);
  }
  if (json.type === "forge:conditional" && Array.isArray(json.recipes)) {
    for (const entry of json.recipes) {
      outputs.push(...extractRecipeOutputs(entry.recipe || {}));
    }
  }
  return outputs;
}

function collectCodeBackedRecipeOutputs() {
  const modRecipes = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModRecipes.java"));
  const itemConstants = constantNameMap(readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModItems.java")), "Item");
  const blockConstants = constantNameMap(readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModBlocks.java")), "Block");
  const outputs = [];
  const re = /new ItemStack\((ModItems|ModBlocks)\.([A-Z0-9_]+)/g;
  let match;
  while ((match = re.exec(modRecipes)) !== null) {
    const map = match[1] === "ModItems" ? itemConstants : blockConstants;
    if (map[match[2]]) {
      outputs.push(`eidolon:${map[match[2]]}`);
    }
  }
  return unique(outputs).map(output => ({
    side: "legacy",
    file: "src/main/java/elucent/eidolon/registries/ModRecipes.java",
    type: "code-backed",
    outputs: [output]
  }));
}

function compareLootOutputs() {
  const sourceLoot = collectLootOutputs(path.join(sourceData, "loot_tables"), "source");
  const legacyLoot = collectLootOutputs(path.join(legacyAssets, "loot_tables"), "legacy");
  legacyLoot.push(...collectCodeBackedLootOutputs());
  const sourceOutputs = unique(sourceLoot.flatMap(table => table.outputs).map(normalizeItemId));
  const legacyOutputs = unique(legacyLoot.flatMap(table => table.outputs).map(normalizeItemId));
  const sourceEidolonOutputs = sourceOutputs.filter(id => id.startsWith("eidolon:"));
  const legacyEidolonOutputs = legacyOutputs.filter(id => id.startsWith("eidolon:"));
  return {
    sourceLootTableCount: sourceLoot.length,
    legacyLootTableCount: legacyLoot.length,
    outputs: compareSets(sourceOutputs, legacyOutputs),
    eidolonOutputs: compareSets(sourceEidolonOutputs, legacyEidolonOutputs),
    sourceTablesMissingOutputInLegacy: sourceLoot
      .filter(table => table.outputs.some(id => !legacyOutputs.includes(normalizeItemId(id))))
      .map(table => ({ file: table.file, outputs: table.outputs.map(normalizeItemId) }))
      .sort((a, b) => a.file.localeCompare(b.file)),
    sourceLoot,
    legacyLoot
  };
}

function collectCodeBackedLootOutputs() {
  const modBlocks = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModBlocks.java"));
  const itemConstants = constantNameMap(readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModItems.java")), "Item");
  const lootTables = [];
  const re = /public\s+static\s+final\s+Block\s+[A-Z0-9_]+\s*=\s*ore\("([a-z0-9_]+)",\s*ModItems\.([A-Z0-9_]+)\)/g;
  let match;
  while ((match = re.exec(modBlocks)) !== null) {
    const blockName = match[1];
    const dropName = itemConstants[match[2]];
    const outputs = [`eidolon:${blockName}`];
    if (dropName) {
      outputs.push(`eidolon:${dropName}`);
    }
    lootTables.push({
      side: "legacy",
      file: "src/main/java/elucent/eidolon/registries/ModBlocks.java",
      type: "code-backed-ore-loot",
      outputs
    });
  }
  return lootTables;
}

function collectLootOutputs(root, side) {
  const tables = [];
  for (const file of listJsonFiles(root)) {
    const full = path.join(root, file);
    const json = safeReadJson(full);
    if (!json) {
      continue;
    }
    const outputs = [];
    walk(json, value => {
      if (!value || typeof value !== "object") {
        return;
      }
      if (typeof value.name === "string") {
        outputs.push(value.name);
      }
      if (typeof value.item === "string") {
        outputs.push(value.item);
      }
    });
    tables.push({
      side,
      file: path.relative(side === "source" ? sourceResources : legacyResources, full).replace(/\\/g, "/"),
      outputs: unique(outputs.map(normalizeItemId)).filter(Boolean)
    });
  }
  return tables;
}

function compareJeiCategories() {
  const sourceDir = path.join(sourceJava, "elucent", "eidolon", "recipe", "jei");
  const legacyDir = path.join(legacyJava, "elucent", "eidolon", "compat", "jei");
  const sourceCategories = categoryClassNames(sourceDir);
  const legacyCategories = categoryClassNames(legacyDir);
  const sourceUids = extractJeiUids(sourceDir);
  const legacyUids = extractJeiUids(legacyDir);
  return {
    categories: compareSets(sourceCategories, legacyCategories, {
      RitualRecipeCategory: "AltarRitualCategory"
    }),
    uids: compareSets(sourceUids, legacyUids, {
      ritual: "altar_ritual"
    }),
    sourceCategories,
    legacyCategories,
    sourceUids,
    legacyUids
  };
}

function categoryClassNames(dir) {
  return listFiles(dir, file => file.endsWith("Category.java"))
    .map(file => path.basename(file, ".java"))
    .sort();
}

function extractJeiUids(dir) {
  const uids = [];
  for (const file of listFiles(dir, file => file.endsWith(".java"))) {
    const text = readText(path.join(dir, file));
    uids.push(...matches(text, /UID\s*=\s*new ResourceLocation\([^,]+,\s*"([^"]+)"/g));
    uids.push(...matches(text, /UID\s*=\s*"([^"]+)"/g));
    uids.push(...matches(text, /UID\s*=\s*Reference\.MOD_ID\s*\+\s*"\.([^"]+)"/g));
  }
  return unique(uids.map(normalizeJeiUid));
}

function normalizeJeiUid(uid) {
  return uid.replace(/^eidolon:/, "").replace(/^eidolon\./, "");
}

function compareLanguageKeys() {
  const source = readLangKeys(path.join(sourceAssets, "lang", "en_us.json"));
  const legacy = unique([
    ...readLangKeys(path.join(legacyAssets, "lang", "en_us.lang")),
    ...readLangKeys(path.join(legacyAssets, "lang", "en_us.json"))
  ]);
  const aliases = buildLanguageKeyAliases(source, legacy);
  return {
    enUs: compareSets(source, legacy, aliases),
    rawEnUs: compareSets(source, legacy),
    aliases,
    aliasCount: Object.keys(aliases).length,
    sourceCount: source.length,
    legacyCount: legacy.length
  };
}

function buildLanguageKeyAliases(source, legacy) {
  const legacySet = new Set(legacy);
  const aliases = {};
  for (const key of source) {
    const candidates = legacyLanguageKeyCandidates(key);
    const match = candidates.find(candidate => legacySet.has(candidate));
    if (match) {
      aliases[key] = match;
    }
  }
  return aliases;
}

function legacyLanguageKeyCandidates(key) {
  const item = key.match(/^item\.eidolon\.([a-z0-9_]+)$/);
  if (item) {
    return [`item.eidolon.${item[1]}.name`];
  }
  const block = key.match(/^block\.eidolon\.([a-z0-9_]+)$/);
  if (block) {
    return [`tile.eidolon.${block[1]}.name`];
  }
  const entity = key.match(/^entity\.eidolon\.([a-z0-9_]+)$/);
  if (entity) {
    return [
      `entity.eidolon.${entity[1]}.name`,
      `entity.eidolon:${entity[1]}.name`
    ];
  }
  const effect = key.match(/^effect\.eidolon\.([a-z0-9_]+)$/);
  if (effect) {
    return [`potion.eidolon.${effect[1]}`];
  }
  return [];
}

function readLangKeys(file) {
  if (!fs.existsSync(file)) {
    return [];
  }
  if (file.endsWith(".json")) {
    const json = safeReadJson(file);
    return json ? Object.keys(json).sort() : [];
  }
  return readText(file)
    .split(/\r?\n/)
    .map(line => line.trim())
    .filter(line => line && !line.startsWith("#") && line.includes("="))
    .map(line => line.slice(0, line.indexOf("=")).trim())
    .sort();
}

function collectPriorityFindings() {
  const findings = [];
  addFinding(findings, "registered_items_missing_item_model", report.resources.registeredItemsMissingItemModel, "Registered item has no Legacy item model.");
  addFinding(findings, "registered_blocks_missing_blockstate", report.resources.registeredBlocksMissingBlockstate, "Registered block has no Legacy blockstate.");
  addFinding(findings, "registered_blocks_missing_block_model", report.resources.registeredBlocksMissingBlockModel, "Registered block has no Legacy block model.");
  addFinding(findings, "resource_item_models_without_registration", report.resources.itemModelsWithoutRegisteredItemOrBlock, "Item model exists without a matching registered item/block.");
  addFinding(findings, "source_recipe_outputs_missing_in_legacy", report.recipes.sourceRecipesMissingOutputInLegacy, "A source recipe output has no Legacy recipe/code-backed output.");
  addFinding(findings, "source_loot_outputs_missing_in_legacy", report.loot.sourceTablesMissingOutputInLegacy, "A source loot output has no Legacy loot output.");
  addFinding(findings, "source_language_keys_missing_in_legacy", report.language.enUs.missing, "Source en_us language key has no direct or 1.12-equivalent Legacy en_us.lang key.");
  return findings;
}

function addFinding(findings, key, items, description) {
  if (items && items.length > 0) {
    findings.push({
      key,
      count: items.length,
      description,
      sample: items.slice(0, 20)
    });
  }
}

function summarizeReport() {
  return {
    registryMissingTotal: sumMissing(report.registries),
    resourceFindingsTotal: report.resources.registeredItemsMissingItemModel.length +
      report.resources.registeredBlocksMissingBlockstate.length +
      report.resources.registeredBlocksMissingBlockModel.length +
      report.resources.itemModelsWithoutRegisteredItemOrBlock.length,
    missingRecipeOutputCount: report.recipes.sourceRecipesMissingOutputInLegacy.length,
    missingLootOutputCount: report.loot.sourceTablesMissingOutputInLegacy.length,
    missingLanguageKeyCount: report.language.enUs.missing.length,
    priorityFindingCount: report.priorityFindings.length
  };
}

function sumMissing(groups) {
  let total = 0;
  for (const value of Object.values(groups)) {
    if (value && Array.isArray(value.missing)) {
      total += value.missing.length;
    }
  }
  return total;
}

function renderMarkdown(data) {
  const lines = [];
  lines.push("# Eidolon Source to Legacy Coverage Matrix");
  lines.push("");
  lines.push(`Generated: \`${data.generatedAt}\``);
  lines.push(`Source: \`${data.roots.source}\``);
  lines.push(`Legacy: \`${data.roots.legacy}\``);
  lines.push("");
  lines.push("## Summary");
  lines.push(`- Registry missing total: ${data.summary.registryMissingTotal}`);
  lines.push(`- Resource finding total: ${data.summary.resourceFindingsTotal}`);
  lines.push(`- Source recipe outputs missing in Legacy: ${data.summary.missingRecipeOutputCount}`);
  lines.push(`- Source loot outputs missing in Legacy: ${data.summary.missingLootOutputCount}`);
  lines.push(`- Source en_us language keys missing in Legacy after 1.12 key normalization: ${data.summary.missingLanguageKeyCount}`);
  lines.push("");
  lines.push("## Registries");
  for (const [key, value] of Object.entries(data.registries)) {
    if (!value || !Array.isArray(value.source)) {
      continue;
    }
    lines.push(`- ${key}: source ${value.source.length}, legacy ${value.legacy.length}, common ${value.common.length}, missing ${value.missing.length}, legacy-only ${value.extra.length}`);
  }
  lines.push("");
  lines.push("## Resource Checks");
  lines.push(`- Registered items missing item model: ${data.resources.registeredItemsMissingItemModel.length}`);
  lines.push(`- Registered blocks missing blockstate: ${data.resources.registeredBlocksMissingBlockstate.length}`);
  lines.push(`- Registered blocks missing block model: ${data.resources.registeredBlocksMissingBlockModel.length}`);
  lines.push(`- Item models without registered item/block: ${data.resources.itemModelsWithoutRegisteredItemOrBlock.length}`);
  lines.push("");
  lines.push("## Recipe And Loot Outputs");
  lines.push(`- Recipe outputs: source ${data.recipes.outputs.source.length}, legacy ${data.recipes.outputs.legacy.length}, missing ${data.recipes.outputs.missing.length}, legacy-only ${data.recipes.outputs.extra.length}`);
  lines.push(`- Eidolon recipe outputs: source ${data.recipes.eidolonOutputs.source.length}, legacy ${data.recipes.eidolonOutputs.legacy.length}, missing ${data.recipes.eidolonOutputs.missing.length}, legacy-only ${data.recipes.eidolonOutputs.extra.length}`);
  lines.push(`- Loot outputs: source ${data.loot.outputs.source.length}, legacy ${data.loot.outputs.legacy.length}, missing ${data.loot.outputs.missing.length}, legacy-only ${data.loot.outputs.extra.length}`);
  lines.push(`- Eidolon loot outputs: source ${data.loot.eidolonOutputs.source.length}, legacy ${data.loot.eidolonOutputs.legacy.length}, missing ${data.loot.eidolonOutputs.missing.length}, legacy-only ${data.loot.eidolonOutputs.extra.length}`);
  lines.push("");
  lines.push("## JEI And Language");
  lines.push(`- JEI categories: source ${data.jei.categories.source.length}, legacy ${data.jei.categories.legacy.length}, missing ${data.jei.categories.missing.length}, legacy-only ${data.jei.categories.extra.length}`);
  lines.push(`- JEI UIDs: source ${data.jei.uids.source.length}, legacy ${data.jei.uids.legacy.length}, missing ${data.jei.uids.missing.length}, legacy-only ${data.jei.uids.extra.length}`);
  lines.push(`- en_us language keys: source ${data.language.enUs.source.length}, legacy ${data.language.enUs.legacy.length}, missing ${data.language.enUs.missing.length}, legacy-only ${data.language.enUs.extra.length}`);
  lines.push(`- raw en_us key mismatch before normalization: missing ${data.language.rawEnUs.missing.length}, legacy-only ${data.language.rawEnUs.extra.length}`);
  lines.push(`- 1.20 -> 1.12 language key aliases: ${data.language.aliasCount}`);
  lines.push("");
  lines.push("## Priority Findings");
  if (data.priorityFindings.length === 0) {
    lines.push("- none");
  } else {
    for (const finding of data.priorityFindings) {
      lines.push(`- ${finding.key}: ${finding.count} - ${finding.description}`);
      for (const item of finding.sample.slice(0, 10)) {
        lines.push(`  - ${formatFindingItem(item)}`);
      }
      if (finding.count > 10) {
        lines.push(`  - ... ${finding.count - 10} more`);
      }
    }
  }
  lines.push("");
  return lines.join("\n");
}

function formatFindingItem(item) {
  if (typeof item === "string") {
    return item;
  }
  return JSON.stringify(item);
}

function printConsoleSummary(data) {
  console.log(`[coverage] registry missing total: ${data.summary.registryMissingTotal}`);
  console.log(`[coverage] resource finding total: ${data.summary.resourceFindingsTotal}`);
  console.log(`[coverage] missing recipe outputs: ${data.summary.missingRecipeOutputCount}`);
  console.log(`[coverage] missing loot outputs: ${data.summary.missingLootOutputCount}`);
  console.log(`[coverage] missing en_us keys after normalization: ${data.summary.missingLanguageKeyCount}`);
  console.log("[coverage] wrote docs/coverage-matrix.json and docs/coverage-matrix.md");
}

function constantNameMap(text, typeName) {
  const map = {};
  const re = new RegExp(`public\\s+static\\s+final\\s+${typeName}\\s+([A-Z0-9_]+)\\s*=\\s*([^;]+);`, "g");
  let match;
  while ((match = re.exec(text)) !== null) {
    const quoted = matches(match[2], /"([a-z0-9_]+)"/g);
    if (quoted.length > 0) {
      map[match[1]] = quoted[0];
    }
  }
  return map;
}

function compareSets(source, legacy, aliases = {}) {
  const sourceList = unique(source);
  const legacyList = unique(legacy);
  const aliasValues = new Set(Object.values(aliases));
  return {
    source: sourceList,
    legacy: legacyList,
    common: sourceList.filter(item => legacyList.includes(item) || legacyList.includes(aliases[item])),
    missing: sourceList.filter(item => !legacyList.includes(item) && !legacyList.includes(aliases[item])),
    extra: legacyList.filter(item => !sourceList.includes(item) && !aliasValues.has(item)),
    aliases: Object.entries(aliases)
      .filter(([sourceName, legacyName]) => sourceList.includes(sourceName) && legacyList.includes(legacyName))
      .map(([sourceName, legacyName]) => `${sourceName}->${legacyName}`)
  };
}

function blockHasMissingReferencedModels(blockName, legacyBlockModels) {
  const blockstate = safeReadJson(path.join(legacyAssets, "blockstates", `${blockName}.json`));
  if (!blockstate) {
    return true;
  }
  const refs = [];
  walk(blockstate, value => {
    if (value && typeof value === "object" && typeof value.model === "string") {
      refs.push(normalizeModelRef(value.model));
    }
  });
  if (refs.length === 0) {
    return !legacyBlockModels.includes(blockName);
  }
  return unique(refs).some(model => !legacyBlockModels.includes(model));
}

function normalizeModelRef(model) {
  let value = model;
  if (value.startsWith("eidolon:")) {
    value = value.slice("eidolon:".length);
  }
  if (value.startsWith("block/")) {
    value = value.slice("block/".length);
  }
  return value;
}

function normalizeItemId(id) {
  if (!id || typeof id !== "string") {
    return "";
  }
  const clean = id.includes(":") ? id : `minecraft:${id}`;
  return itemIdFallbacks[clean] || clean;
}

function inferRecipeTypeFromPath(file) {
  const normalized = file.replace(/\\/g, "/");
  if (normalized.includes("/worktable_recipes/")) {
    return "eidolon:worktable";
  }
  if (normalized.includes("/crucible_recipes/")) {
    return "eidolon:crucible";
  }
  if (normalized.includes("/altar_rituals/")) {
    return "eidolon:altar_ritual";
  }
  if (normalized.includes("/incubator_recipes/")) {
    return "eidolon:incubator";
  }
  return "unknown";
}

function jsonBaseNames(dir) {
  return listJsonFiles(dir).map(file => path.basename(file, ".json")).sort();
}

function listJsonFiles(root) {
  return listFiles(root, file => file.endsWith(".json"));
}

function listFiles(root, predicate) {
  if (!fs.existsSync(root)) {
    return [];
  }
  const files = [];
  function visit(dir) {
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        visit(full);
      } else if (entry.isFile() && predicate(entry.name)) {
        files.push(path.relative(root, full));
      }
    }
  }
  visit(root);
  return files.sort();
}

function safeReadJson(file) {
  try {
    return JSON.parse(stripBom(fs.readFileSync(file, "utf8")));
  } catch (error) {
    return null;
  }
}

function readText(file) {
  if (!fs.existsSync(file)) {
    return "";
  }
  return stripBom(fs.readFileSync(file, "utf8"));
}

function stripBom(text) {
  return text.charCodeAt(0) === 0xFEFF ? text.slice(1) : text;
}

function writeJson(file, data) {
  fs.writeFileSync(file, `${JSON.stringify(data, null, 2)}\n`, "utf8");
}

function walk(value, visitor) {
  visitor(value);
  if (Array.isArray(value)) {
    value.forEach(child => walk(child, visitor));
  } else if (value && typeof value === "object") {
    Object.values(value).forEach(child => walk(child, visitor));
  }
}

function matches(text, regex) {
  const values = [];
  let match;
  while ((match = regex.exec(text)) !== null) {
    values.push(match[1]);
  }
  return values;
}

function unique(values) {
  return [...new Set(values.filter(value => value !== undefined && value !== null && value !== ""))].sort();
}

function ensureDirectory(dir) {
  fs.mkdirSync(dir, { recursive: true });
}

function assertDirectory(dir) {
  if (!fs.existsSync(dir) || !fs.statSync(dir).isDirectory()) {
    throw new Error(`Missing directory: ${dir}`);
  }
}

function rel(file) {
  return path.relative(projectRoot, file).replace(/\\/g, "/");
}

main();
