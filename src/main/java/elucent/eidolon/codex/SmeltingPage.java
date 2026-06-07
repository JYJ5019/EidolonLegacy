package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class SmeltingPage extends Page {
    public SmeltingPage(String textKey, ItemStack... displayStacks) {
        super(Kind.SMELTING, textKey, textKey, stacks(displayStacks), null);
    }
}
