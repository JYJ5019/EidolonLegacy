package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class CruciblePage extends Page {
    public CruciblePage(String textKey, ItemStack... displayStacks) {
        super(Kind.CRUCIBLE, textKey, textKey, stacks(displayStacks), null);
    }
}
