# Raven Cloak Mixin Replacement Audit

Source reference: `../eidolon-1.20x` `LocalPlayerMixin`, `LivingEntityMixin#isFallFlying`, and `RavenCloakRenderer`.

Audited on 2026-06-09. This 1.12 port does not add mixins; the behavior is approximated with client tick handling, armor-item state, packets, and an armor model.

## Parity Matrix

| Source behavior | 1.12 replacement | Status |
| --- | --- | --- |
| `LocalPlayerMixin#getJumpRidingScale` exposes a held-jump charge scale after 5 ticks | `RavenCloakControls` tracks jump key hold time client-side and treats a full 20-tick charge as dash intent | Covered, with no vanilla mount jump UI hook in 1.12 |
| `LocalPlayerMixin#isRidingJumpable` makes the jump UI available while charging in GUI state | No mixin hook; Raven Cloak uses direct key tracking instead of riding-jump UI state | Intentional 1.12 UI difference |
| `LivingEntityMixin#isFallFlying` returns true while the player data capability says the player is dashing | `RavenCloakItem` stores dash ticks on the chest item, drives dash motion in server tick logic, and exposes that state to rendering | Covered without changing vanilla Elytra fall-flying state |
| Client flap/dash input reaches server | `RavenCloakPacket` sends `ACTION_FLAP` or `ACTION_DASH`; server validates chest slot item before applying movement | Covered |
| Server state reaches third-person clients | `RavenCloakItem.syncState` sends `RavenCloakPacket.Sync` to the player and tracking clients; `RavenCloakRenderState` caches state per entity id | Covered |
| State cleanup | Ground contact resets flap charges and clears flying/dash state; render state clears on world change and expires stale entries | Covered |
| Curio renderer renders cloak model on the equipped entity | 1.12 Raven Cloak is chest armor and returns `RavenCloakModel` from `getArmorModel` | Covered as armor-renderer equivalent |
| Third-person shoulder raven render support | `PlayerLayerRegistry` adds `LayerRavenOnShoulder` to player renderers | Covered for shoulder raven layer; separate from cloak model |

## Notes

- The 1.12 implementation intentionally does not set vanilla `isFallFlying`, because doing that cleanly would require a mixin or broad motion hook. Dash motion and fall-distance reset are handled by `RavenCloakItem` instead.
- The client cannot reproduce the 1.20 riding-jump charge bar hook without a mixin. Holding jump still maps to flap or dash through `RavenCloakControls`.
- `RavenCloakRenderState` is render-only cache state. Authoritative movement state remains on the server-side chest item NBT.

## Suggested Manual Checks

- Equip Raven Cloak, jump off a ledge, tap jump before landing, and verify a flap consumes one charge.
- Hold jump until full charge while airborne, release, and verify dash starts server-side and renders as wings on nearby clients.
- Sneak while falling and verify descent is clamped and fall damage is prevented.
- Land and verify dash/flying state clears, charges refill, and the model returns from wings to cloak.
- Change worlds or reconnect and verify stale wing pose does not remain on other players.
