package elucent.eidolon.codex;

import net.minecraft.item.ItemStack;

public class ChantPage extends Page {
    public ChantPage(String textKey, ItemStack... displayStacks) {
        super(Kind.CHANT, textKey, textKey, stacks(displayStacks), null);
    }
}
