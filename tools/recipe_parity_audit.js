#!/usr/bin/env node
/* eslint-env node */

const fs = require("fs");
const path = require("path");

const legacyRoot = path.resolve(__dirname, "..");
const projectRoot = path.resolve(legacyRoot, "..");
const sourceRoot = path.join(projectRoot, "eidolon-1.20x");
const sourceRecipeRoot = path.join(sourceRoot, "src", "main", "resources", "data", "eidolon", "recipes");
const legacyAssets = path.join(legacyRoot, "src", "main", "resources", "assets", "eidolon");

const legacyRecipeRoots = [
  path.join(legacyAssets, "recipes"),
  path.join(legacyAssets, "worktable_recipes"),
  path.join(legacyAssets, "crucible_recipes"),
  path.join(legacyAssets, "incubator_recipes")
];

function main() {
  const source = collectRecipes(sourceRecipeRoot, "source");
  const legacy = [
    ...legacyRecipeRoots.flatMap(root => collectRecipes(root, "legacy")),
    ...collectLegacyCodeRecipes()
  ];
  const sourceByOutput = groupByOutput(source);
  const legacyByOutput = groupByOutput(legacy);
  const missing = [...sourceByOutput.keys()]
    .filter(output => output.startsWith("eidolon:") && !legacyByOutput.has(output))
    .sort();
  const legacyDuplicates = [...legacyByOutput.entries()]
    .filter(([output, recipes]) => output.startsWith("eidolon:") && recipes.length > 1)
    .sort(([a], [b]) => a.localeCompare(b));
  const both = [...sourceByOutput.keys()]
    .filter(output => output.startsWith("eidolon:") && legacyByOutput.has(output))
    .sort();

  console.log("== missing eidolon outputs in legacy ==");
  for (const output of missing) {
    console.log(output);
    printRecipes(sourceByOutput.get(output));
  }

  console.log("\n== duplicate eidolon outputs in legacy ==");
  for (const [output, recipes] of legacyDuplicates) {
    console.log(output);
    printRecipes(recipes);
    const sourceRecipes = sourceByOutput.get(output);
    if (sourceRecipes) {
      console.log("  source:");
      printRecipes(sourceRecipes, "    ");
    }
  }

  console.log("\n== source/legacy output count ==");
  console.log(`source eidolon outputs: ${[...sourceByOutput.keys()].filter(k => k.startsWith("eidolon:")).length}`);
  console.log(`legacy eidolon outputs: ${[...legacyByOutput.keys()].filter(k => k.startsWith("eidolon:")).length}`);
  console.log(`shared eidolon outputs: ${both.length}`);
  console.log(`missing eidolon outputs: ${missing.length}`);
  console.log(`duplicate legacy eidolon outputs: ${legacyDuplicates.length}`);
}

function collectLegacyCodeRecipes() {
  return [
    codeRecipe("ModRecipes.addSmelting(lead_ore)", "eidolon:lead_ingot", "item:eidolon:lead_ore"),
    codeRecipe("ModRecipes.addSmelting(deep_lead_ore)", "eidolon:lead_ingot", "item:eidolon:deep_lead_ore"),
    codeRecipe("ModRecipes.addSmelting(raw_lead)", "eidolon:lead_ingot", "item:eidolon:raw_lead"),
    codeRecipe("ModRecipes.addSmelting(silver_ore)", "eidolon:silver_ingot", "item:eidolon:silver_ore"),
    codeRecipe("ModRecipes.addSmelting(deep_silver_ore)", "eidolon:silver_ingot", "item:eidolon:deep_silver_ore"),
    codeRecipe("ModRecipes.addSmelting(raw_silver)", "eidolon:silver_ingot", "item:eidolon:raw_silver"),
    codeRecipe("ModRecipes.addSmelting(pewter_blend)", "eidolon:pewter_ingot", "item:eidolon:pewter_blend"),
    codeRecipe("ModRecipes.addOreSmelting(dustLead)", "eidolon:lead_ingot", "ore:dustLead"),
    codeRecipe("ModRecipes.addOreSmelting(dustSilver)", "eidolon:silver_ingot", "ore:dustSilver"),
    codeRecipe("ModRecipes.addSmelting(bone)", "eidolon:enchanted_ash", "item:minecraft:bone"),
    codeRecipe("ModRecipes.addSmelting(bone_block)", "eidolon:enchanted_ash", "item:minecraft:bone_block"),
    codeRecipe("ModRecipes.addSmelting(rotten_flesh)", "eidolon:tallow", "item:minecraft:rotten_flesh")
  ];
}

function codeRecipe(file, output, input) {
  return {
    side: "legacy",
    file,
    type: "code:smelting",
    outputs: [output],
    inputSig: `code:smelting input:${input}`
  };
}

function collectRecipes(root, side) {
  if (!fs.existsSync(root)) {
    return [];
  }
  const out = [];
  for (const file of walkJson(root)) {
    const json = readJson(file);
    if (!json) {
      continue;
    }
    const outputs = collectOutputs(json);
    if (outputs.length === 0) {
      continue;
    }
    out.push({
      side,
      file: path.relative(side === "source" ? sourceRoot : legacyRoot, file).replace(/\\/g, "/"),
      type: json.type || "",
      outputs: [...new Set(outputs.map(normalizeId))],
      inputSig: inputSignature(json)
    });
  }
  return out;
}

function collectOutputs(json) {
  const outputs = [];
  addItem(outputs, json.result);
  addItem(outputs, json.output);
  addItem(outputs, json.resultItem);
  if (Array.isArray(json.results)) {
    for (const result of json.results) {
      addItem(outputs, result);
    }
  }
  return outputs;
}

function addItem(outputs, value) {
  if (!value) {
    return;
  }
  if (typeof value === "string") {
    outputs.push(value);
    return;
  }
  if (typeof value.item === "string") {
    outputs.push(value.item);
  }
  if (typeof value.id === "string") {
    outputs.push(value.id);
  }
}

function inputSignature(json) {
  const parts = [json.type || ""];
  if (Array.isArray(json.pattern)) {
    parts.push(`pattern:${json.pattern.join("/")}`);
  }
  if (json.reagents) {
    parts.push(`reagents:${json.reagents}`);
  }
  if (json.key) {
    const keys = Object.keys(json.key).sort().map(key => `${key}:${ingredientSignature(json.key[key])}`);
    parts.push(`key:${keys.join("|")}`);
  }
  if (Array.isArray(json.ingredients)) {
    parts.push(`ingredients:${json.ingredients.map(ingredientSignature).sort().join("|")}`);
  }
  if (json.input) {
    parts.push(`input:${ingredientSignature(json.input)}`);
  }
  if (json.fluid) {
    parts.push(`fluid:${ingredientSignature(json.fluid)}`);
  }
  return parts.join(" ");
}

function ingredientSignature(value) {
  if (Array.isArray(value)) {
    return `[${value.map(ingredientSignature).sort().join(",")}]`;
  }
  if (!value || typeof value !== "object") {
    return String(value);
  }
  if (value.item) {
    return `item:${normalizeId(value.item)}`;
  }
  if (value.tag) {
    return `tag:${value.tag}`;
  }
  if (value.ore) {
    return `ore:${value.ore}`;
  }
  if (value.fluid) {
    return `fluid:${value.fluid}`;
  }
  return JSON.stringify(value);
}

function groupByOutput(recipes) {
  const map = new Map();
  for (const recipe of recipes) {
    for (const output of recipe.outputs) {
      if (!map.has(output)) {
        map.set(output, []);
      }
      map.get(output).push(recipe);
    }
  }
  return map;
}

function printRecipes(recipes, prefix = "  ") {
  for (const recipe of recipes) {
    console.log(`${prefix}${recipe.file} [${recipe.type}]`);
    console.log(`${prefix}  ${recipe.inputSig}`);
  }
}

function walkJson(root) {
  const out = [];
  for (const name of fs.readdirSync(root)) {
    const file = path.join(root, name);
    const stat = fs.statSync(file);
    if (stat.isDirectory()) {
      out.push(...walkJson(file));
    } else if (name.endsWith(".json")) {
      out.push(file);
    }
  }
  return out;
}

function readJson(file) {
  try {
    return JSON.parse(fs.readFileSync(file, "utf8"));
  } catch (err) {
    console.error(`[warn] ${file}: ${err.message}`);
    return null;
  }
}

function normalizeId(id) {
  if (!id.includes(":")) {
    return `minecraft:${id}`;
  }
  return id;
}

main();
