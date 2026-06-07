#!/usr/bin/env node
/* eslint-env node */

const fs = require("fs");
const path = require("path");

const legacyRoot = path.resolve(__dirname, "..");
const projectRoot = path.resolve(legacyRoot, "..");
const sourceLangFile = path.join(projectRoot, "eidolon-1.20x", "src", "main", "resources", "assets", "eidolon", "lang", "en_us.json");
const legacyLangFile = path.join(legacyRoot, "src", "main", "resources", "assets", "eidolon", "lang", "en_us.lang");
const legacyJava = path.join(legacyRoot, "src", "main", "java");

const generatedHeader = "# --- Synced from eidolon-1.20x by tools/sync_legacy_lang.js ---";

const legacyDefaults = {
  "tooltip.eidolon.research_use": "Right-click to learn this research",
  "tooltip.eidolon.research_known": "Known research",
  "tooltip.eidolon.altar_capacity": "Altar Capacity: %s",
  "tooltip.eidolon.altar_power": "Altar Power: %s",
  "tooltip.eidolon.bauble_slot": "Bauble Slot: %s",
  "tooltip.eidolon.bauble_slot.amulet": "Amulet",
  "tooltip.eidolon.bauble_slot.ring": "Ring",
  "tooltip.eidolon.bauble_slot.belt": "Belt",
  "tooltip.eidolon.bauble_slot.trinket": "Trinket",
  "tooltip.eidolon.bauble_slot.head": "Head",
  "tooltip.eidolon.bauble_slot.body": "Body",
  "tooltip.eidolon.bauble_slot.charm": "Charm",
  "tooltip.eidolon.wand_charge": "Charge: %s / %s",
  "tooltip.eidolon.soulfire_wand_use": "Right-click to fire soulfire",
  "tooltip.eidolon.bonechill_wand_use": "Right-click to fire bonechill",
  "tooltip.eidolon.summoning_staff_absorbed": "Absorbed undead: %s",
  "tooltip.eidolon.summoning_staff_selected": "Selected summon: %s",
  "tooltip.eidolon.summoning_staff_type": "%s: %s",
  "tooltip.eidolon.summoning_staff_cycle": "Sneak-right-click to cycle summon type",
  "tooltip.eidolon.summoning_staff_use": "Right-click to spend 1 absorbed undead and summon the selected type",
  "tooltip.eidolon.basic_amulet": "Reduces incoming damage slightly while worn.",
  "tooltip.eidolon.sanguine_amulet": "Stores excess healing at full health and spends it to heal you when injured.",
  "tooltip.eidolon.sanguine_amulet_charge": "Blood: %s / %s",
  "tooltip.eidolon.void_amulet": "Negates magic, explosion, or special projectile damage about every 10 seconds.",
  "tooltip.eidolon.void_amulet_cooldown": "Cooldown: about %s seconds",
  "tooltip.eidolon.soulbone_amulet": "Heals you and grants brief absorption when you kill a creature.",
  "tooltip.eidolon.basic_ring": "Reduces incoming damage slightly while worn.",
  "tooltip.eidolon.enervating_ring": "Briefly weakens and slows creatures you hit.",
  "tooltip.eidolon.basic_belt": "Reduces incoming damage slightly while worn.",
  "tooltip.eidolon.gravity_belt": "Slows falling and greatly reduces fall distance; sneak to fall normally.",
  "tooltip.eidolon.resolute_belt": "Reduces incoming damage and knocks attackers back.",
  "tooltip.eidolon.mind_shielding_plate": "Prevents nausea and preserves experience on death.",
  "tooltip.eidolon.glass_hand": "Deals double damage but greatly increases damage taken.",
  "tooltip.eidolon.angels_sight": "Fires an additional empowered arrow while drawing a bow.",
  "tooltip.eidolon.terminus_mirror": "Negates ordinary projectile damage.",
  "tooltip.eidolon.prestigious_palm": "Greatly increases interaction and attack reach.",
  "tooltip.eidolon.warlock_robes": "Warlock robes hold powerful enchantments and provide reliable light armor.",
  "tooltip.eidolon.bonelord_armor": "Maintains ethereal health while worn; damage drains ethereal health first.",
  "tooltip.eidolon.raven_cloak": "Glides and reduces fall damage; sneak in midair to dash forward.",
  "tooltip.eidolon.warded_mail": "Converts magic damage into ordinary damage, avoiding most magic effects.",
  "container.eidolon.soul_enchanter": "Soul Enchanter",
  "container.eidolon.incubator": "Incubator",
  "container.eidolon.research_table": "Research Table",
  "container.eidolon.worktable": "Worktable",
  "container.eidolon.soul_enchanter.cost": "%s  %s levels / 1 shard",
  "gui.eidolon.machine_info.title": "Machine Info",
  "gui.eidolon.machine.soul_enchanter.title": "Soul Enchanter",
  "gui.eidolon.machine.soul_enchanter.desc": "Place an enchantable item or book with a soul shard. Each operation consumes one soul shard and experience based on the enchantment level being added or upgraded.",
  "gui.eidolon.machine.wooden_brewing_stand.title": "Wooden Brewing Stand",
  "gui.eidolon.machine.wooden_brewing_stand.desc": "When placed above a heated, water-filled crucible, the wooden brewing stand processes potion recipes. Redstone and glowstone are not accepted as wooden brewing stand ingredients.",
  "entity.eidolon.necromancer_spell.name": "Necromancer Spell",
  "entity.eidolon:necromancer_spell.name": "Necromancer Spell",
  "entity.eidolon.angel_arrow.name": "Angel Arrow",
  "entity.eidolon:angel_arrow.name": "Angel Arrow",
  "entity.eidolon.chant_caster.name": "Chant Caster",
  "entity.eidolon:chant_caster.name": "Chant Caster",
  "potion.eidolon.decay": "Decay"
};

function main() {
  const source = readJson(sourceLangFile);
  const original = readText(legacyLangFile);
  const existing = readLangMap(original);
  const additions = new Map();

  addSourceLanguage(source, existing, additions);
  addLegacyRegistryNames(source, existing, additions);
  addRuntimeDefaults(existing, additions);
  addRuntimeFallbacks(existing, additions);

  if (additions.size === 0) {
    console.log("[lang-sync] en_us.lang already contains all generated keys");
    return;
  }

  const lines = [];
  lines.push("");
  lines.push(generatedHeader);
  for (const [key, value] of [...additions.entries()].sort((a, b) => a[0].localeCompare(b[0]))) {
    lines.push(`${key}=${value}`);
  }

  const suffix = original.endsWith("\n") ? "" : "\n";
  fs.writeFileSync(legacyLangFile, `${original}${suffix}${lines.join("\n")}\n`, "utf8");
  console.log(`[lang-sync] appended ${additions.size} keys to ${path.relative(legacyRoot, legacyLangFile).replace(/\\/g, "/")}`);
}

function addSourceLanguage(source, existing, additions) {
  for (const [key, value] of Object.entries(source)) {
    addIfMissing(additions, existing, key, value);
    const item = key.match(/^item\.eidolon\.([a-z0-9_]+)$/);
    if (item) {
      addIfMissing(additions, existing, `item.eidolon.${item[1]}.name`, value);
      continue;
    }
    const block = key.match(/^block\.eidolon\.([a-z0-9_]+)$/);
    if (block) {
      addIfMissing(additions, existing, `tile.eidolon.${block[1]}.name`, value);
      continue;
    }
    const entity = key.match(/^entity\.eidolon\.([a-z0-9_]+)$/);
    if (entity) {
      addIfMissing(additions, existing, `entity.eidolon.${entity[1]}.name`, value);
      addIfMissing(additions, existing, `entity.eidolon:${entity[1]}.name`, value);
      addIfMissing(additions, existing, `entity.eidolon:${entity[1]}`, value);
      continue;
    }
    const effect = key.match(/^effect\.eidolon\.([a-z0-9_]+)$/);
    if (effect) {
      addIfMissing(additions, existing, `potion.eidolon.${effect[1]}`, value);
      continue;
    }
    addPotionItemNameVariants(key, value, existing, additions);
  }
}

function addPotionItemNameVariants(key, value, existing, additions) {
  const match = key.match(/^item\.minecraft\.([a-z_]+)\.effect\.([a-z0-9_]+)$/);
  if (!match) {
    return;
  }
  addIfMissing(additions, existing, `item.minecraft.${match[1]}.effect.eidolon.${match[2]}`, value);
}

function addLegacyRegistryNames(source, existing, additions) {
  const modItems = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModItems.java"));
  const modBlocks = readText(path.join(legacyJava, "elucent", "eidolon", "registries", "ModBlocks.java"));

  for (const name of publicRegistryNames(modItems, "Item")) {
    addIfMissing(additions, existing, `item.eidolon.${name}.name`, source[`item.eidolon.${name}`] || titleCase(name));
  }
  for (const name of publicRegistryNames(modBlocks, "Block")) {
    if (!name.startsWith("double_")) {
      addIfMissing(additions, existing, `tile.eidolon.${name}.name`, source[`block.eidolon.${name}`] || titleCase(name));
    }
  }
}

function addRuntimeDefaults(existing, additions) {
  for (const [key, value] of Object.entries(legacyDefaults)) {
    addIfMissing(additions, existing, key, value);
  }
}

function addRuntimeFallbacks(existing, additions) {
  for (const key of collectRuntimeLanguageKeys()) {
    if (existing.has(key) || additions.has(key)) {
      continue;
    }
    const leaf = key.slice(key.lastIndexOf(".") + 1);
    addIfMissing(additions, existing, key, titleCase(leaf));
  }
}

function collectRuntimeLanguageKeys() {
  const keys = new Set();
  for (const file of listFiles(legacyJava, name => name.endsWith(".java"))) {
    const text = readText(path.join(legacyJava, file));
    for (const regex of [
      /I18n\.(?:format|translateToLocal|translateToLocalFormatted)\(\s*"([^"]+)"/g,
      /"((?:gui|tooltip|container|lore|entity|potion|research|research_source)\.eidolon[^"]+)"/g
    ]) {
      let match;
      while ((match = regex.exec(text)) !== null) {
        keys.add(match[1]);
      }
    }
  }
  return [...keys].sort();
}

function addIfMissing(additions, existing, key, value) {
  if (!key || existing.has(key) || additions.has(key) || value === undefined || value === null || value === "") {
    return;
  }
  additions.set(key, String(value).replace(/\r?\n/g, "\\n"));
}

function publicRegistryNames(text, typeName) {
  const names = [];
  const regex = new RegExp(`public\\s+static\\s+final\\s+${typeName}\\s+[A-Z0-9_]+\\s*=\\s*([^;]+);`, "g");
  let match;
  while ((match = regex.exec(text)) !== null) {
    const quoted = [...match[1].matchAll(/"([a-z0-9_]+)"/g)].map(value => value[1]);
    if (quoted.length > 0) {
      names.push(quoted[0]);
    }
  }
  return unique(names);
}

function readLangMap(text) {
  const keys = new Set();
  for (const line of text.split(/\r?\n/)) {
    const clean = line.trim();
    if (!clean || clean.startsWith("#") || !clean.includes("=")) {
      continue;
    }
    keys.add(clean.slice(0, clean.indexOf("=")).trim());
  }
  return keys;
}

function titleCase(value) {
  return value
    .split("_")
    .filter(Boolean)
    .map(part => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");
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

function readJson(file) {
  return JSON.parse(readText(file));
}

function readText(file) {
  if (!fs.existsSync(file)) {
    return "";
  }
  const text = fs.readFileSync(file, "utf8");
  return text.charCodeAt(0) === 0xFEFF ? text.slice(1) : text;
}

function unique(values) {
  return [...new Set(values.filter(value => value !== undefined && value !== null && value !== ""))].sort();
}

main();
