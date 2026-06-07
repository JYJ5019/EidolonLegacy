const fs = require("fs");
const path = require("path");

const langDir = path.join("src", "main", "resources", "assets", "eidolon", "lang");
const zhFile = path.join(langDir, "zh_cn.lang");
const enFile = path.join(langDir, "en_us.lang");

function readLang(file) {
  if (!fs.existsSync(file)) {
    return { entries: [], map: new Map() };
  }
  const text = fs.readFileSync(file, "utf8").replace(/^\uFEFF/, "");
  const entries = [];
  const map = new Map();
  for (const rawLine of text.split(/\r?\n/)) {
    const line = rawLine.trimEnd();
    if (!line || line.trimStart().startsWith("#") || !line.includes("=")) {
      continue;
    }
    const idx = line.indexOf("=");
    const key = line.slice(0, idx);
    const value = line.slice(idx + 1);
    entries.push([key, value]);
    map.set(key, value);
  }
  return { entries, map };
}

const zh = readLang(zhFile);
const en = readLang(enFile);

const effectNames = {
  undeath: "亡灵化",
  vulnerable: "脆弱",
  reinforced: "加固",
  anchored: "锚定",
  chilled: "冻寒",
  decay: "衰朽"
};

const effects = Object.keys(effectNames);
const variants = ["", "long_", "strong_"];
const potionKinds = ["potion", "splash_potion", "lingering_potion", "tipped_arrow"];
const itemPrefixes = ["item.minecraft.", "item.", ""];
const namespaces = ["eidolon.", ""];

function isUnsafeBaseKey(key) {
  return /^item\.eidolon\.[a-z0-9_]+$/.test(key)
    || /^tile\.eidolon\.[a-z0-9_]+$/.test(key)
    || /^block\.eidolon\.[a-z0-9_]+$/.test(key);
}

function add(map, order, key, value) {
  if (!key || !value || isUnsafeBaseKey(key)) {
    return;
  }
  if (!map.has(key)) {
    order.push(key);
  }
  map.set(key, value);
}

function effectText(id) {
  if (id.startsWith("long_")) {
    return "长效" + (effectNames[id.slice("long_".length)] || id);
  }
  if (id.startsWith("strong_")) {
    return "强效" + (effectNames[id.slice("strong_".length)] || id);
  }
  return effectNames[id] || id;
}

function potionText(kind, id) {
  const effect = effectText(id);
  if (kind === "potion") {
    return effect + "药水";
  }
  if (kind === "splash_potion") {
    return "喷溅型" + effect + "药水";
  }
  if (kind === "lingering_potion") {
    return "滞留型" + effect + "药水";
  }
  return effect + "之箭";
}

function firstExisting(map, keys) {
  for (const key of keys) {
    const value = map.get(key);
    if (value) {
      return value;
    }
  }
  return null;
}

const output = new Map();
const order = [];

for (const source of [zh, en]) {
  for (const [key, value] of source.entries) {
    if (isUnsafeBaseKey(key)) {
      let match = key.match(/^item\.eidolon\.([a-z0-9_]+)$/);
      if (match) {
        add(output, order, `item.eidolon.${match[1]}.name`, value);
      }
      match = key.match(/^tile\.eidolon\.([a-z0-9_]+)$/);
      if (match) {
        add(output, order, `tile.eidolon.${match[1]}.name`, value);
      }
      match = key.match(/^block\.eidolon\.([a-z0-9_]+)$/);
      if (match) {
        add(output, order, `tile.eidolon.${match[1]}.name`, value);
      }
      continue;
    }
    add(output, order, key, value);
  }
}

for (const [key, value] of [...output.entries()]) {
  let match = key.match(/^item\.eidolon\.([a-z0-9_]+)\.name$/);
  if (match) {
    add(output, order, `item.eidolon.${match[1]}.name.name`, value);
  }

  match = key.match(/^tile\.eidolon\.([a-z0-9_]+)\.name$/);
  if (match) {
    add(output, order, `tile.eidolon.${match[1]}.name.name`, value);
    add(output, order, `item.eidolon.${match[1]}.name`, value);
  }

  match = key.match(/^entity\.eidolon[:.]([a-z0-9_]+)\.name$/);
  if (match) {
    add(output, order, `entity.eidolon.${match[1]}`, value);
    add(output, order, `entity.eidolon:${match[1]}`, value);
    add(output, order, `entity.${match[1]}.name`, value);
  }
}

for (const effect of effects) {
  const value = effectNames[effect];
  add(output, order, `effect.eidolon.${effect}`, value);
  add(output, order, `potion.eidolon.${effect}`, value);
  add(output, order, `potion.effect.eidolon.${effect}`, value);
}

for (const effect of effects) {
  for (const variant of variants) {
    const id = variant + effect;
    for (const kind of potionKinds) {
      const fallback = potionText(kind, id);
      const value = firstExisting(output, [
        `item.minecraft.${kind}.effect.eidolon.${id}`,
        `item.${kind}.effect.eidolon.${id}`,
        `${kind}.effect.eidolon.${id}`,
        `item.minecraft.${kind}.effect.${id}`,
        `item.${kind}.effect.${id}`,
        `${kind}.effect.${id}`
      ]) || fallback;

      for (const prefix of itemPrefixes) {
        for (const namespace of namespaces) {
          add(output, order, `${prefix}${kind}.effect.${namespace}${id}`, value);
        }
      }
    }
  }
}

const lines = [
  "# Eidolon Legacy Chinese language compatibility patch.",
  "# In Minecraft 1.12, item/tile base keys must stay untranslated; names live on .name keys.",
  "# en_us.lang mirrors zh_cn.lang so fallback never displays English."
];

for (const key of order) {
  lines.push(`${key}=${output.get(key)}`);
}
lines.push("");

const result = lines.join("\n");
fs.mkdirSync(langDir, { recursive: true });
fs.writeFileSync(zhFile, result, "utf8");
fs.writeFileSync(enFile, result, "utf8");

const unsafe = order.filter(isUnsafeBaseKey);
console.log(`[lang] wrote ${order.length} keys to zh_cn.lang and en_us.lang`);
console.log(`[lang] unsafe base keys kept: ${unsafe.length}`);
for (const key of unsafe.slice(0, 40)) {
  console.log(key);
}
