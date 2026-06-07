package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class CraftingPage extends Page {
    public CraftingPage(String textKey, ItemStack... displayStacks) {
        super(Kind.CRAFTING, textKey, textKey, stacks(displayStacks), null);
    }
}
