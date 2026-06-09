# Angel's Sight Arrow Behavior Matrix

Scope: audit the 1.12 no-mixin Angel's Sight path against the 1.20x
`AngelArrowEntity` plus `AbstractArrowMixin` design. This note is documentation
only; the closure rule is to keep the current no-mixin gameplay code path.

## Source Behavior

In 1.20x, Angel's Sight wraps a live `AbstractArrow` inside `AngelArrowEntity`.
The wrapper keeps the inner arrow ticking, serializes its entity type plus full
NBT, and uses `AbstractArrowMixin` invokers for protected `getPickupItem()` and
`onHitEntity(...)`. Vanilla and modded `AbstractArrow` subclasses can therefore
keep both normal public state and subclass hit behavior.

In Legacy 1.12, mixins are intentionally not used. The replacement path creates
the ammo's `ItemArrow#createArrow(...)` result as a temporary inner arrow, copies
supported state into an `AngelArrowEntity`, then lets that entity fly, seek, land,
and hit as an `EntityTippedArrow`. The inner arrow is not kept alive after the
copy step.

## Behavior Matrix

| Ammo type | Current Legacy behavior | Copied through public field/NBT path | Known gap |
| --- | --- | --- | --- |
| Vanilla arrow | Full low-risk parity target. It seeks, consumes one arrow unless infinity/creative applies, damages the bow, can be picked up as one normal arrow, and applies bow Power/Punch/Flame/critical state. | `ItemArrow#createArrow(...)`, inner arrow `writeEntityToNBT(...)` into outer `readEntityFromNBT(...)`, explicit `setDamage(...)`, `setIsCritical(...)`, public `pickupStatus`, outer `shoot(...)`, and custom `EidolonPickupStack`. | No live inner entity remains after copying, so only vanilla `EntityArrow` state is expected. |
| Tipped arrow | Treated as an `EntityTippedArrow` Angel arrow. Pickup returns one tipped-arrow stack and the potion payload is reapplied from ammo. | Base arrow NBT, `setPotionEffect(ammo)`, inherited tipped-arrow potion NBT, `EidolonPickupStack`, plus bow enchantment state. | Safe for vanilla tipped-arrow semantics, but not a delegate to a custom tipped-arrow subclass. |
| Spectral arrow | Pickup returns one spectral-arrow stack. On hit, Legacy explicitly applies Glowing for 200 ticks. | Base arrow NBT, bow enchantment state, `EidolonPickupStack`, and custom `EidolonSpectral=true`. | The original `EntitySpectralArrow` protected hit path is not called; this is a shim for the vanilla 200-tick glowing behavior. |
| Modded item extending `ItemArrow` that returns vanilla-like `EntityArrow` state | Supported to the extent the arrow item exposes behavior through the vanilla arrow state copied before spawn. Pickup returns one copy of the original ammo stack. | Ammo item stack, base `EntityArrow` NBT recognized by `EntityArrow`/`EntityTippedArrow`, explicit damage/crit/pickup fields, fire ticks if serialized by the base entity, and knockback if represented by vanilla arrow state. | Subclass-only tags are discarded unless the outer `EntityTippedArrow` knows how to read them. Custom `onUpdate`, collision, protected hit, or special render behavior is not preserved. |
| Modded arrow requiring custom `arrowHit(...)` or other protected methods | Not parity-complete under the no-mixin constraint. The Angel arrow calls `EntityTippedArrow#arrowHit(...)` and the local spectral shim only. | None beyond copied base state listed above. | Delegating this requires a mixin invoker, access transformer, or a public cooperation hook from the other mod. This task explicitly forbids adding that. |
| Modded bow custom-arrow hook | No additional Legacy hook is called in the current path after `ArrowLooseEvent` is canceled. | Bow enchantments handled explicitly: Power, Punch, Flame, full-charge crit, infinity/creative pickup and ammo consumption. | The 1.20x `BowItem#customArrow(...)` call has no equivalent in the current 1.12 closure path. |
| Non-arrow projectile ammo | Not in scope for the current Angel's Sight path. `findArrow(...)` accepts `ItemArrow`, `ItemTippedArrow`, or vanilla spectral arrows. | None. | A custom ammo item must participate as an arrow item to be picked by this handler. |

## Copyable State

The Legacy path can copy or rebuild these pieces without a mixin:

- `ItemArrow#createArrow(world, ammo, player)` is still honored as the source of
  initial vanilla arrow state.
- `EntityArrow` data recognized by the base 1.12 arrow readers can move through
  `writeEntityToNBT(...)` and `readEntityFromNBT(...)`.
- Damage, full-charge critical state, and public `pickupStatus` are copied
  explicitly after the NBT pass.
- Power, Punch, Flame, infinity/creative pickup, ammo shrink, bow durability,
  and bow use stat are handled in `CurioEvents#onArrowLoose`.
- The pickup item is rebuilt through `EidolonPickupStack`, storing a count-1 copy
  of the ammo stack so normal, tipped, spectral, and modded arrow items can drop
  the expected item where the outer entity's pickup status permits it.
- Tipped arrows reapply potion data from the ammo stack and persist through the
  inherited `EntityTippedArrow` NBT.
- Spectral arrows persist a local `EidolonSpectral` boolean and apply vanilla
  Glowing for 200 ticks on hit.

## Non-Copyable State

These are deliberate no-mixin limitations, not open low-risk code gaps:

- The outer entity is always `AngelArrowEntity extends EntityTippedArrow`; the
  original inner entity type is not serialized and recreated the way 1.20x does.
- Protected hit behavior is not delegated. In particular, subclass
  `arrowHit(...)` behavior from a custom arrow entity will not run.
- Subclass private/protected fields and custom NBT only survive if they are also
  understood by the base `EntityArrow`/`EntityTippedArrow` read path.
- Custom per-tick steering, drag, collision, piercing, or removal behavior from
  the inner arrow is not preserved because the inner arrow is only a temporary
  copy source.
- Client-side custom arrow visuals are not guaranteed for custom subclasses,
  because the spawned entity remains the Legacy Angel arrow.

## QA Checklist

Run these checks when touching Angel's Sight, arrow items, bow handling, or the
no-mixin compatibility boundary:

- Equip Angel's Sight in a Baubles slot, fire a normal bow with vanilla arrows in
  survival, and confirm seeking, damage, ammo consumption, bow durability, and
  one-arrow pickup from a landed arrow.
- Repeat with a full-charge shot and Power/Punch/Flame bows. Confirm critical
  state, increased damage, knockback, fire, and no duplicate vanilla arrow is
  spawned after the event is canceled.
- Test Infinity and creative mode. Confirm ammo is not consumed where expected
  and the Angel arrow pickup is creative-only.
- Fire tipped arrows with at least one visible potion effect. Confirm the target
  receives the potion effect, the landed arrow picks up as the same tipped stack,
  and the potion payload survives a save/reload before pickup.
- Fire spectral arrows. Confirm hit targets receive 200 ticks of Glowing and the
  landed arrow picks up as a spectral-arrow stack when pickup is allowed.
- If a modded `ItemArrow` is available, test one arrow whose behavior is encoded
  in vanilla arrow state and one arrow that depends on custom protected hit
  logic. The first should preserve copied base state; the second should be
  recorded as an expected no-mixin limitation.
- Shoot an Angel arrow into a block, save, reload, and pick it up. Confirm
  `EidolonPickupStack` and `EidolonSpectral` behavior still matches the ammo.
- Verify the source tree still has no SpongePowered mixin imports/annotations and
  no Gradle or mixin configuration changes.

## Closure Status

No gameplay code patch is required for this audit. The current Legacy
implementation already covers the low-risk, public-state/NBT-compatible subset.
Further parity for modded subclass hit behavior is outside this closure task
because it would require a mixin, an access transformer, or a public integration
hook.
