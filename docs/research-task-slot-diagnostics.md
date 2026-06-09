# Research Task Slot Diagnostics

## Scope

Legacy 1.12 cannot use the 1.20x `AbstractContainerMenuMixin` invoker/accessors that dynamically add or remove research task item slots. The 1.12 port keeps nine pre-created `ResearchTaskSlot` instances in `ResearchTableContainer` and moves them on/off screen as tasks change.

## Current Audit

- 1.20x `ResearchTask.Items.modifyContainer` adds one menu slot per required item through the mixin-backed `callAddSlot`.
- Legacy `ResearchTask.ItemsTask` reports one slot per required item stack, and `ResearchTableContainer` maps those item slots onto a fixed limit of 9.
- Current default generated research steps create 3 tasks. The built-in task pool has `ScrivenerItems` with 1 item slot and `Xp` with 0 item slots, so the default maximum is 3 item slots.
- Runtime diagnostics now reports the Legacy slot limit, task-pool maximum, per-step audited item-slot counts, and any overflow entries.
- The container logs a warning if a live research note ever requires more item slots than the fixed Legacy slot array can display.

## Behavior Notes

- Shift-click keeps using vanilla `mergeItemStack` through the table, task, and player ranges. Active task slots still validate item identity, metadata, count limit, and tags through `ResearchTaskSlot.isItemValid`.
- Closing the container drops any remaining transient task-slot stacks server-side from the internal task inventory.
- Submitting a task consumes only the matching task input slots, starts progress, clears transient task inputs, and refreshes visible slots.
- Client and server both recompute tasks from research id, step, and seed. The remaining sync risk is stale client display if progress/seed/notes fields desync; the server-side submit path still revalidates completion before consuming inputs.

## Future Risk

If a future task factory or special task uses more than 9 total item slots in one research step, Legacy will still only display the first 9. The warning and runtime diagnostics should catch that case, but matching 1.20x behavior would require a deliberate non-mixin dynamic-slot design for 1.12.
