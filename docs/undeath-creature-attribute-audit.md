# Undeath Creature Attribute Audit

Source reference: `../eidolon-1.20x/src/main/java/elucent/eidolon/mixin/LivingEntityMixin.java`.

## Summary

The 1.20 source uses a mixin on `LivingEntity#getMobType` so entities with the Undeath effect report `MobType.UNDEAD` unless source code explicitly asks for the true mob type. Legacy intentionally does not install mixins, so it uses `Eidolon.getCreatureAttribute(entity)` as the internal replacement and `Eidolon.getTrueCreatureAttribute(entity)` when native creature type is required.

## Current Internal Coverage

- `DeathbringerScytheItem` checks `Eidolon.getCreatureAttribute(target)` before applying Undeath.
- `ReaperScytheItem` checks `Eidolon.getCreatureAttribute(target)` for soul shard harvesting.
- `ModBlocks.EnchantedAshBlock` checks `Eidolon.getCreatureAttribute(entity)` so Undeath-affected entities are blocked by enchanted ash.
- `ActiveRituals.performCrystal` checks `Eidolon.getCreatureAttribute(entity)` so Undeath-affected entities are valid crystallization targets.
- `AltarRitual.getRitualHealthTargets` checks `Eidolon.getCreatureAttribute(entity) != UNDEAD` so Undeath-affected entities are excluded from non-undead health costs.
- `GameplayEvents.applyUndeathCreatureEnchantments` compensates the vanilla melee enchantment path by adding the undead enchantment bonus delta for Undeath targets.

## Intentional True Attribute Uses

- `Eidolon.getTrueCreatureAttribute(entity)` is the single direct wrapper around vanilla `EntityLivingBase#getCreatureAttribute()`.
- `GameplayEvents.applyUndeathCreatureEnchantments` uses true attribute to compute the native-vs-undead enchantment bonus delta.
- `AltarRitual.absorbNearbyUndead` uses true attribute so absorption stays limited to naturally undead entities, not temporarily Undeath-affected living entities.
- Entity overrides in `NecromancerEntity`, `WraithEntity`, and `ZombieBruteEntity` are native creature attributes and are not replacement call sites.

## Known No-Mixin Limit

Without a mixin, Legacy cannot globally override `EntityLivingBase#getCreatureAttribute()`. Vanilla internals and other mods that call the method directly will still see the native creature attribute. New Eidolon call sites that need Undeath semantics should use `Eidolon.getCreatureAttribute(entity)`; call sites that need native semantics should use `Eidolon.getTrueCreatureAttribute(entity)` and document why.
