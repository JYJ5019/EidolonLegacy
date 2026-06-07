package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class WorktablePage extends Page {
    public WorktablePage(String textKey, ItemStack... displayStacks) {
        super(Kind.WORKTABLE, textKey, textKey, stacks(displayStacks), null);
    }
}
