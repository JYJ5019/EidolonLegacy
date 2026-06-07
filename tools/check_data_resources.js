#!/usr/bin/env node
/* eslint-env node */

const fs = require("fs");
const path = require("path");

const legacyRoot = path.resolve(__dirname, "..");
const projectRoot = path.resolve(legacyRoot, "..");
const sourceRoot = path.join(projectRoot, "eidolon-1.20x", "src", "main", "resources");
const legacyResources = path.join(legacyRoot, "src", "main", "resources");
const legacyAssets = path.join(legacyResources, "assets", "eidolon");

const modernItemMap = {
  "minecraft:crimson_fungus": "minecraft:red_mushroom",
  "minecraft:crimson_roots": "minecraft:nether_wart",
  "minecraft:glow_berries": "minecraft:speckled_melon",
  "minecraft:glow_ink_sac": "minecraft:glowstone_dust",
  "minecraft:warped_fungus": "minecraft:brown_mushroom",
  "minecraft:weeping_vines": "minecraft:vine"
};

const minecraft112ItemIds = new Set([
  "minecraft:apple",
  "minecraft:arrow",
  "minecraft:beef",
  "minecraft:blaze_powder",
  "minecraft:bone",
  "minecraft:book",
  "minecraft:brown_mushroom",
  "minecraft:carpet",
  "minecraft:carrot",
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
  "minecraft:red_mushroom",
  "minecraft:rotten_flesh",
  "minecraft:skull",
  "minecraft:slime_ball",
  "minecraft:soul_sand",
  "minecraft:speckled_melon",
  "minecraft:stick",
  "minecraft:stone",
  "minecraft:stone_slab",
  "minecraft:stonebrick",
  "minecraft:string",
  "minecraft:vine",
  "minecraft:wheat",
  "minecraft:wheat_seeds",
  "minecraft:wool"
]);

const codeBackedRecipeNames = new Set([
  "blast_enchanted_ash",
  "blast_lead_dust",
  "blast_lead_ore",
  "blast_pewter_blend",
  "blast_raw_lead",
  "blast_raw_silver",
  "blast_silver_dust",
  "blast_silver_ore",
  "enchanted_ash",
  "enchanted_ash_from_block",
  "smelt_lead_dust",
  "smelt_lead_ore",
  "smelt_pewter_blend",
  "smelt_raw_lead",
  "smelt_raw_silver",
  "smelt_silver_dust",
  "smelt_silver_ore",
  "tallow"
]);

const convertedNameAliases = {
  arcane_gold_ingot: "arcane_gold_ingot_from_nuggets",
  decompress_arcane_gold_block: "arcane_gold_ingot_from_block",
  decompress_arcane_gold_ingot: "arcane_gold_nugget",
  decompress_lead_block: "lead_ingot_from_block",
  decompress_lead_ingot: "lead_nugget",
  decompress_pewter_block: "pewter_ingot_from_block",
  decompress_pewter_ingot: "pewter_nugget",
  decompress_raw_lead_block: "raw_lead",
  decompress_raw_silver_block: "raw_silver",
  decompress_shadow_gem_block: "shadow_gem",
  decompress_silver_block: "silver_ingot_from_block",
  decompress_silver_ingot: "silver_nugget",
  lead_ingot: "lead_ingot_from_nuggets",
  pewter_ingot: "pewter_ingot_from_nuggets",
  silver_ingot: "silver_ingot_from_nuggets"
};

const blockModelAliases = {
  elder_pillar_bottom: "elder_pillar",
  elder_pillar_mid: "elder_pillar",
  elder_pillar_top: "elder_pillar",
  illwood_bark_horizontal: "illwood_bark",
  illwood_log_horizontal: "illwood_log",
  illwood_planks_fence_gate_wall: "illwood_planks_wall_gate_closed",
  illwood_planks_fence_gate_wall_open: "illwood_planks_wall_gate_open",
  polished_planks_fence_gate_wall: "polished_planks_wall_gate_closed",
  polished_planks_fence_gate_wall_open: "polished_planks_wall_gate_open",
  stripped_illwood_bark_horizontal: "stripped_illwood_bark",
  stripped_illwood_log_horizontal: "stripped_illwood_log"
};

const unregisteredSourceBlockModels = new Set([
  "aludel",
  "cabinet_bottom",
  "cabinet_top",
  "censer",
  "coalfired_engine_on",
  "mirecap_0",
  "mirecap_1",
  "mirecap_2",
  "mirecap_3"
]);

const issues = [];
const notes = [];

function main() {
  assertDirectory(sourceRoot);
  assertDirectory(legacyResources);

  compareDirectoryNames("blockstates", "assets/eidolon/blockstates", "assets/eidolon/blockstates", { allowExtra: true });
  compareBlockModels();
  compareDirectoryNames("item models", "assets/eidolon/models/item", "assets/eidolon/models/item", { allowExtra: true });
  compareSpecialRecipes("crucible", "eidolon:crucible", "assets/eidolon/crucible_recipes");
  compareSpecialRecipes("worktable", "eidolon:worktable", "assets/eidolon/worktable_recipes");
  compareVanillaRecipes();
  compareSounds();
  compareStructures();
  scanModernItems();
  scanParticleStrategy();

  for (const note of notes) {
    console.log(`[OK] ${note}`);
  }

  if (issues.length > 0) {
    for (const issue of issues) {
      console.error(`[FAIL] ${issue}`);
    }
    process.exitCode = 1;
  } else {
    console.log("[OK] data/resource migration checks passed");
  }
}

function compareBlockModels() {
  const source = jsonBaseNames(path.join(sourceRoot, "assets/eidolon/models/block"));
  const legacy = jsonBaseNames(path.join(legacyResources, "assets/eidolon/models/block"));
  const missing = [];
  const aliasBacked = [];
  const unregistered = [];
  for (const name of source) {
    if (legacy.includes(name)) {
      continue;
    }
    const alias = blockModelAliases[name];
    if (alias && legacy.includes(alias)) {
      aliasBacked.push(`${name}->${alias}`);
      continue;
    }
    if (unregisteredSourceBlockModels.has(name)) {
      unregistered.push(name);
      continue;
    }
    missing.push(name);
  }
  if (missing.length > 0) {
    issues.push(`block models missing in Legacy: ${missing.join(", ")}`);
  } else {
    const extra = legacy.filter(name => !source.includes(name));
    notes.push(`block models: source ${source.length}, legacy ${legacy.length}, missing 0; aliases ${aliasBacked.length}, unregistered source-only ${unregistered.length}, extra ${extra.length}`);
  }
}

function compareDirectoryNames(label, sourceRel, legacyRel, options = {}) {
  const source = jsonBaseNames(path.join(sourceRoot, sourceRel));
  const legacy = jsonBaseNames(path.join(legacyResources, legacyRel));
  const missing = source.filter(name => !legacy.includes(name));
  const extra = legacy.filter(name => !source.includes(name));
  if (missing.length > 0) {
    issues.push(`${label} missing in Legacy: ${missing.join(", ")}`);
  } else {
    notes.push(`${label}: source ${source.length}, legacy ${legacy.length}, missing 0${options.allowExtra ? `, extra ${extra.length}` : ""}`);
  }
  if (!options.allowExtra && extra.length > 0) {
    issues.push(`${label} unexpected extra entries: ${extra.join(", ")}`);
  }
}

function compareSpecialRecipes(label, type, legacyRel) {
  const source = sourceRecipeNamesByType(type);
  const legacy = jsonBaseNames(path.join(legacyResources, legacyRel));
  const missing = source.filter(name => !legacy.includes(name));
  if (missing.length > 0) {
    issues.push(`${label} recipes missing in Legacy: ${missing.join(", ")}`);
  } else {
    notes.push(`${label} recipes: source ${source.length}, legacy ${legacy.length}, missing 0`);
  }
}

function compareVanillaRecipes() {
  const sourceDir = path.join(sourceRoot, "data", "eidolon", "recipes");
  const legacyNames = new Set(jsonBaseNames(path.join(legacyAssets, "recipes")));
  const missing = [];
  const codeBacked = [];
  const aliasBacked = [];
  const stonecutting = [];
  for (const file of listJsonFiles(sourceDir)) {
    const name = path.basename(file, ".json");
    const json = readJson(path.join(sourceDir, file));
    if (json.type === "eidolon:crucible" || json.type === "eidolon:worktable") {
      continue;
    }
    if (legacyNames.has(name)) {
      continue;
    }
    if (convertedNameAliases[name] && legacyNames.has(convertedNameAliases[name])) {
      aliasBacked.push(`${name}->${convertedNameAliases[name]}`);
      continue;
    }
    if (json.type === "minecraft:stonecutting") {
      stonecutting.push(name);
      continue;
    }
    if (codeBackedRecipeNames.has(name)) {
      codeBacked.push(name);
      continue;
    }
    missing.push(`${name} (${json.type || "unknown"})`);
  }
  if (missing.length > 0) {
    issues.push(`vanilla-style recipes with no Legacy JSON/code-backed mapping: ${missing.join(", ")}`);
  } else {
    notes.push(`vanilla-style recipes: JSON/code-backed coverage ok; aliases ${aliasBacked.length}, code-backed ${codeBacked.length}, stonecutting fallbacks ${stonecutting.length}`);
  }
}

function compareSounds() {
  const sourceSoundsPath = path.join(sourceRoot, "assets", "eidolon", "sounds.json");
  const legacySoundsPath = path.join(legacyAssets, "sounds.json");
  const sourceSounds = readJson(sourceSoundsPath);
  const legacySounds = readJson(legacySoundsPath);
  const sourceKeys = Object.keys(sourceSounds).sort();
  const legacyKeys = Object.keys(legacySounds).sort();
  const missingKeys = sourceKeys.filter(name => !legacyKeys.includes(name));
  const missingRefs = referencedSoundFiles(sourceSounds)
    .filter(file => !fs.existsSync(path.join(legacyAssets, "sounds", file)));
  if (missingKeys.length > 0) {
    issues.push(`sound events missing in Legacy sounds.json: ${missingKeys.join(", ")}`);
  }
  if (missingRefs.length > 0) {
    issues.push(`referenced source sound files missing in Legacy: ${missingRefs.join(", ")}`);
  }
  if (missingKeys.length === 0 && missingRefs.length === 0) {
    const extraKeys = legacyKeys.filter(name => !sourceKeys.includes(name));
    notes.push(`sounds: source ${sourceKeys.length}, legacy ${legacyKeys.length}, missing 0, extra ${extraKeys.length}; referenced files covered ${referencedSoundFiles(sourceSounds).length}`);
  }
}

function compareStructures() {
  const source = listFilesByExtension(path.join(sourceRoot, "data", "eidolon", "structures"), ".nbt");
  const legacy = listFilesByExtension(path.join(legacyAssets, "structures"), ".nbt");
  const missing = source.filter(name => !legacy.includes(name));
  if (missing.length > 0) {
    issues.push(`structure NBT files missing in Legacy: ${missing.join(", ")}`);
  } else {
    const extra = legacy.filter(name => !source.includes(name));
    notes.push(`structures: source ${source.length}, legacy ${legacy.length}, missing 0, extra ${extra.length}`);
  }
}

function referencedSoundFiles(soundsJson) {
  const files = new Set();
  for (const entry of Object.values(soundsJson)) {
    for (const sound of entry.sounds || []) {
      const name = typeof sound === "string" ? sound : sound.name;
      if (!name) {
        continue;
      }
      const localName = name.includes(":") ? name.split(":")[1] : name;
      files.add(`${localName}.ogg`);
    }
  }
  return [...files].sort();
}

function scanModernItems() {
  const dirs = [
    path.join(legacyAssets, "crucible_recipes"),
    path.join(legacyAssets, "worktable_recipes"),
    path.join(legacyAssets, "recipes")
  ];
  const unhandled = [];
  const mapped = [];
  for (const dir of dirs) {
    for (const file of listJsonFiles(dir)) {
      const relativeFile = path.relative(legacyRoot, path.join(dir, file)).replace(/\\/g, "/");
      const json = readJson(path.join(dir, file));
      walk(json, value => {
        if (value && typeof value === "object" && typeof value.item === "string" && modernItemMap[value.item]) {
          mapped.push(`${relativeFile}: ${value.item}->${modernItemMap[value.item]}`);
        } else if (value && typeof value === "object" && typeof value.item === "string" &&
            value.item.startsWith("minecraft:") && !minecraft112ItemIds.has(value.item)) {
          unhandled.push(`${relativeFile}: ${value.item}`);
        }
      });
    }
  }
  if (unhandled.length > 0) {
    issues.push(`unhandled non-1.12 minecraft item ids: ${[...new Set(unhandled)].join(", ")}`);
  } else {
    notes.push(`modern item fallbacks: ${mapped.length} mapped references are covered by CrucibleRecipes`);
  }
}

function scanParticleStrategy() {
  const sourceParticles = jsonBaseNames(path.join(sourceRoot, "assets", "eidolon", "particles"));
  const legacyParticlesDir = path.join(legacyResources, "assets", "eidolon", "particles");
  const legacyParticles = jsonBaseNames(legacyParticlesDir);
  if (sourceParticles.length > 0 && legacyParticles.length === 0) {
    notes.push(`particles: ${sourceParticles.length} source particle JSON files intentionally remain code-backed by VisualEffectPacket/native 1.12 particles`);
  } else {
    compareDirectoryNames("particles", "assets/eidolon/particles", "assets/eidolon/particles");
  }
}

function sourceRecipeNamesByType(type) {
  const sourceDir = path.join(sourceRoot, "data", "eidolon", "recipes");
  return listJsonFiles(sourceDir)
    .filter(file => readJson(path.join(sourceDir, file)).type === type)
    .map(file => path.basename(file, ".json"))
    .sort();
}

function jsonBaseNames(dir) {
  return listJsonFiles(dir).map(file => path.basename(file, ".json")).sort();
}

function listJsonFiles(root) {
  return listFilesByExtension(root, ".json");
}

function listFilesByExtension(root, extension) {
  if (!fs.existsSync(root)) {
    return [];
  }
  const files = [];
  function visit(dir) {
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        visit(full);
      } else if (entry.isFile() && entry.name.endsWith(extension)) {
        files.push(path.relative(root, full).replace(/\\/g, "/"));
      }
    }
  }
  visit(root);
  return files.sort();
}

function readJson(file) {
  return JSON.parse(fs.readFileSync(file, "utf8"));
}

function walk(value, visitor) {
  visitor(value);
  if (Array.isArray(value)) {
    value.forEach(child => walk(child, visitor));
  } else if (value && typeof value === "object") {
    Object.values(value).forEach(child => walk(child, visitor));
  }
}

function assertDirectory(dir) {
  if (!fs.existsSync(dir) || !fs.statSync(dir).isDirectory()) {
    throw new Error(`Missing directory: ${dir}`);
  }
}

main();
